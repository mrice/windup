package org.jboss.windup.engine.visitor.reporter.html.renderer;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.commons.io.FileUtils;
import org.jboss.windup.engine.WindupContext;
import org.jboss.windup.engine.visitor.base.EmptyGraphVisitor;
import org.jboss.windup.engine.visitor.reporter.html.model.ApplicationContext;
import org.jboss.windup.engine.visitor.reporter.html.model.ClassloaderReport;
import org.jboss.windup.engine.visitor.reporter.html.model.ClassloaderReport.ClassLoaderReportRow;
import org.jboss.windup.engine.visitor.reporter.html.model.ClassloaderReport.ClassReference;
import org.jboss.windup.engine.visitor.reporter.html.model.Name;
import org.jboss.windup.graph.dao.JavaClassDaoBean;
import org.jboss.windup.graph.model.resource.JavaClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import freemarker.template.Configuration;
import freemarker.template.Template;

public class DuplicateClassloaderReportRenderer extends EmptyGraphVisitor {
	private static final Logger LOG = LoggerFactory.getLogger(DuplicateClassloaderReportRenderer.class);
	
	@Inject
	private WindupContext context;
	
	@Inject
	private JavaClassDaoBean javaClassDao;
	
	@Inject
	private NamingUtility namingUtility;
	
	private Configuration cfg;
	private File runDirectory;
	private File reportReference;
	
	@PostConstruct
	private void postConstruct() throws IOException {
		cfg = new Configuration();
        cfg.setTemplateUpdateDelay(500);
        cfg.setClassForTemplateLoading(this.getClass(), "/");
        
		runDirectory = context.getRunDirectory();
		File archiveReportDirectory = new File(runDirectory, "applications");
		File archiveDirectory = new File(archiveReportDirectory, "application");
		FileUtils.forceMkdir(archiveDirectory);
		reportReference = new File(archiveDirectory, "classloader-duplicate.html");
	}

	@Override
	public void run() {
		try {
			Template template = cfg.getTemplate("/reports/templates/classloader.ftl");
			
			ApplicationContext appCtx = new ApplicationContext(namingUtility.getApplicationName()); 
		
			ClassloaderReport report = new ClassloaderReport("Duplicates", "Duplicate", "Leveraged By");
			
			//for each class leveraging a blacklist...
			for(JavaClass clz : javaClassDao.getAllDuplicateClasses()) {
				Name name = namingUtility.getReportJavaResource(runDirectory, reportReference.getParentFile(), clz);
	
				//get reference...
				ClassLoaderReportRow row = new ClassLoaderReportRow(name);
				addAll(row.getReferences(), clz.providesForJavaClass());
				
				report.getClasses().add(row);
			}
			
			Map<String, Object> objects = new HashMap<String, Object>();
			objects.put("application", appCtx);
			objects.put("classloader", report);
			
			template.process(objects, new FileWriter(reportReference));
		}
		catch(Exception e) {
			throw new RuntimeException("Exception processing report.", e);
		}
		
	}
	
	public void addAll(Collection<ClassReference> references, Iterable<JavaClass> clzs) {
		for(JavaClass clz : clzs) {
			Name name = namingUtility.getReportJavaResource(runDirectory, reportReference.getParentFile(), clz);
			ClassReference clzRef = new ClassReference("", name);
			references.add(clzRef);
		}
	}
}
