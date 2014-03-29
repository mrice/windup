package org.jboss.windup.engine.visitor;

import javax.inject.Inject;

import org.jboss.windup.engine.WindupContext;
import org.jboss.windup.engine.visitor.base.EmptyGraphVisitor;
import org.jboss.windup.graph.dao.JavaClassDaoBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * For all Java Class entries in the zip, this sets up the JavaClass graph entries. 
 * 
 * @author bradsdavis@gmail.com
 *
 */
public class JavaDecompilerVisitor extends EmptyGraphVisitor {
	private static final Logger LOG = LoggerFactory.getLogger(JavaDecompilerVisitor.class);

	@Inject WindupContext context;
	
	@Inject
	private JavaClassDaoBean javaClassDao;
	
	@Override
	public void run() {

		String[] packagePatterns = context.getPackagesToProfile().toArray(new String[]{});
		
		
		//add the ^[package name].* to the package name to create the regex for querying the Windup graph.
		for(int i=0; i<packagePatterns.length; i++) {
			packagePatterns[i] = "^"+packagePatterns[i] + ".*";
		}
		
		for(org.jboss.windup.graph.model.resource.JavaClass candidate : javaClassDao.findValueMatchingRegex("qualifiedName", packagePatterns)) {
			if(candidate.getSource() == null) {
				//TODO: Add preconditions to determine if the class makes use of any Windup Rule
				LOG.info("Must decompile: "+candidate.getQualifiedName());
			}
		}
	}
	
}
