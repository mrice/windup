package org.jboss.windup.engine.visitor;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.UUID;

import javax.inject.Inject;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.jboss.windup.engine.WindupContext;
import org.jboss.windup.engine.visitor.base.EmptyGraphVisitor;
import org.jboss.windup.engine.visitor.java.DecompilerAdapter;
import org.jboss.windup.engine.visitor.java.JadretroDecompilerAdapter;
import org.jboss.windup.graph.dao.ArchiveEntryDaoBean;
import org.jboss.windup.graph.dao.FileResourceDaoBean;
import org.jboss.windup.graph.dao.JavaClassDaoBean;
import org.jboss.windup.graph.model.resource.ArchiveEntryResource;
import org.jboss.windup.graph.model.resource.FileResource;
import org.jboss.windup.graph.model.resource.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * For all Java Class that are "customer packages" that need further investigation, this
 * visitor will decompile the Java class. 
 * 
 * @author bradsdavis@gmail.com
 *
 */
public class JavaDecompilerVisitor extends EmptyGraphVisitor {
	private static final Logger LOG = LoggerFactory.getLogger(JavaDecompilerVisitor.class);

	@Inject WindupContext context;
	
	@Inject
	private JavaClassDaoBean javaClassDao;
	
	@Inject
	private ArchiveEntryDaoBean archiveEntryDao;
	
	@Inject
	private FileResourceDaoBean fileDao;
	
	private final DecompilerAdapter decompiler;
	
	public JavaDecompilerVisitor() {
		decompiler = new JadretroDecompilerAdapter();
	}
	
	@Override
	public void run() {

		String[] packagePatterns = context.getPackagesToProfile().toArray(new String[]{});
		
		
		//add the ^[package name].* to the package name to create the regex for querying the Windup graph.
		for(int i=0; i<packagePatterns.length; i++) {
			packagePatterns[i] = "^"+packagePatterns[i] + ".*";
		}
		
		//count the iterations so that we can commit periodically.
		int count = 1;
		for(org.jboss.windup.graph.model.resource.JavaClass candidate : javaClassDao.findValueMatchingRegex("qualifiedName", packagePatterns)) {
			if(candidate.getSource() == null) {
				Iterator<Resource> resources = candidate.getResources().iterator();
				if(resources.hasNext()) {
					try {
						File fileReference = null;
						//check its type...
						Resource resource = resources.next();
						if(resource instanceof ArchiveEntryResource) {
							ArchiveEntryResource ae = archiveEntryDao.castToType(resource.asVertex());
							fileReference = ae.asFile();
						}
						else if(resource instanceof FileResource) {
							FileResource fr = fileDao.castToType(resource.asVertex());
							fileReference = fr.asFile();
						}
						
						LOG.info("Class File: "+fileReference);
						String fileName = UUID.randomUUID().toString();
						File output = new File(FileUtils.getTempDirectory(), fileName);
						
						LOG.info("Java Source: "+fileName);
						decompiler.decompile(candidate.getQualifiedName(), fileReference, output);
						
						File outputReference = new File(output, StringUtils.substringAfterLast(candidate.getQualifiedName(), ".")+".java");
						LOG.info("Output: "+outputReference.getAbsolutePath());
						
						if(!outputReference.exists()) {
							LOG.warn("Expected: "+outputReference.getAbsolutePath()+" but the file doesn't exist.");
						}
						
						FileResource fr = fileDao.create();
						fr.setFilePath(outputReference.getAbsolutePath());
						candidate.setSource(fr);
						
						if(count % 100 == 0) {
							fileDao.commit();
						}
					}
					catch(IOException e) {
						LOG.error("Unable to get resource for qualified: "+candidate.getQualifiedName());
					}
				}
			}
			else {
				LOG.warn("No resource associated with the Java class: "+candidate.getQualifiedName());
				continue;
			}
			count++;
		}
		
		fileDao.commit();
	}
	
}
