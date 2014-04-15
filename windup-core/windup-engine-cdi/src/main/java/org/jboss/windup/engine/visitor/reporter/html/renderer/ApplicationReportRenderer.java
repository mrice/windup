package org.jboss.windup.engine.visitor.reporter.html.renderer;

import java.io.File;
import java.io.FileWriter;
import java.util.HashMap;

import javax.inject.Inject;

import org.apache.commons.io.FileUtils;
import org.jboss.windup.engine.WindupContext;
import org.jboss.windup.engine.visitor.base.EmptyGraphVisitor;
import org.jboss.windup.engine.visitor.reporter.html.model.ApplicationReport;
import org.jboss.windup.engine.visitor.reporter.html.model.ArchiveReport;
import org.jboss.windup.engine.visitor.reporter.html.model.ArchiveReport.ResourceReportRow;
import org.jboss.windup.engine.visitor.reporter.html.model.Level;
import org.jboss.windup.engine.visitor.reporter.html.model.SimpleName;
import org.jboss.windup.engine.visitor.reporter.html.model.Tag;
import org.jboss.windup.graph.dao.ApplicationReferenceDaoBean;
import org.jboss.windup.graph.dao.JarManifestDaoBean;
import org.jboss.windup.graph.dao.JavaClassDaoBean;
import org.jboss.windup.graph.dao.XmlResourceDaoBean;
import org.jboss.windup.graph.model.meta.ApplicationReference;
import org.jboss.windup.graph.model.meta.JarManifest;
import org.jboss.windup.graph.model.resource.ArchiveEntryResource;
import org.jboss.windup.graph.model.resource.ArchiveResource;
import org.jboss.windup.graph.model.resource.JavaClass;
import org.jboss.windup.graph.model.resource.XmlResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import freemarker.template.Configuration;
import freemarker.template.Template;

public class ApplicationReportRenderer extends EmptyGraphVisitor {
	private static final Logger LOG = LoggerFactory.getLogger(ApplicationReportRenderer.class);
	
	@Inject
	private WindupContext context;
	
	@Inject
	private ApplicationReferenceDaoBean appRefDao;

	@Inject
	private JavaClassDaoBean javaDao;
	
	@Inject
	private XmlResourceDaoBean xmlDao;
	
	@Inject
	private JarManifestDaoBean manifestDao;
	
	@Inject
	private NamingUtility namingUtility;
	
	private final Configuration cfg;
	
	public ApplicationReportRenderer() {
		cfg = new Configuration();
        cfg.setTemplateUpdateDelay(500);
        cfg.setClassForTemplateLoading(this.getClass(), "/");
	}
	
	
	@Override
	public void run() {
		try {
			File runDirectory = context.getRunDirectory();
			File archiveReportDirectory = new File(runDirectory, "applications");
			File archiveDirectory = new File(archiveReportDirectory, "application");
			FileUtils.forceMkdir(archiveDirectory);
			File archiveReport = new File(archiveDirectory, "index.html");
		
		
			ApplicationReport applicationReport = new ApplicationReport();
			for(ApplicationReference app : appRefDao.getAll()) {
				ArchiveResource reference = app.getArchive();
				applicationReport.setApplicationName(reference.getArchiveName());
				recurseArchive(archiveReport, applicationReport, app.getArchive());
			}
			
			
		
		
			Template template = cfg.getTemplate("/reports/templates/application.ftl");
			
			java.util.Map<String, Object> objects = new HashMap<String, Object>();
			objects.put("application", applicationReport);
			
			template.process(objects, new FileWriter(archiveReport));
			
			LOG.info("Wrote report: "+archiveReport.getAbsolutePath());
			
		} catch (Exception e) {
			throw new RuntimeException("Exception writing report.", e);
		}
	}
	
	protected void recurseArchive(File reportPath, ApplicationReport report, ArchiveResource resource) {
		ArchiveReport archiveReport = new ArchiveReport();
		archiveReport.setApplicationPath(resource.getArchiveName());
		
		for(ArchiveEntryResource entry : resource.getChildrenArchiveEntries()) {
			//check to see about facets.
			processEntry(archiveReport, reportPath, entry);
		}
		
		for(ArchiveResource childResource : resource.getChildrenArchive()) {
			recurseArchive(reportPath, report, childResource);
		}
		
		report.getArchives().add(archiveReport);
	}
	
	protected void processEntry(ArchiveReport report, File reportPath, ArchiveEntryResource entry) {
		ResourceReportRow reportRow = new ResourceReportRow();
		
		//see if the resource is a java class...
		if(javaDao.isJavaClass(entry)) {
			JavaClass clz = javaDao.getJavaClassFromResource(entry);
			if(!clz.isCustomerPackage()) {
				return;
			}
			reportRow.setResourceName(namingUtility.getReportJavaResource(reportPath, clz));
			reportRow.getTechnologyTags().add(new Tag("Java", Level.SUCCESS));
			
			report.getResources().add(reportRow);
			return;
		}

		//check if it is a XML resource...
		if(xmlDao.isXmlResource(entry)) 
		{
			XmlResource resource = xmlDao.getXmlFromResource(entry);
			reportRow.setResourceName(namingUtility.getReportXmlResource(reportPath, resource));
			reportRow.getTechnologyTags().add(new Tag("XML", Level.SUCCESS));
					
			report.getResources().add(reportRow);
			return;
		}
		
		//check if it is a manifest...
		if(manifestDao.isManifestResource(entry))
		{
			JarManifest resource = manifestDao.getManifestFromResource(entry);
			reportRow.setResourceName(namingUtility.getReportManifestResource(reportPath, resource));
			reportRow.getTechnologyTags().add(new Tag("Manifest", Level.SUCCESS));
					
			report.getResources().add(reportRow);
			return;
		}
		
		
		reportRow.setResourceName(new SimpleName(entry.getArchiveEntry()));
		reportRow.getIssueTags().add(new Tag("Unknown Type", Level.WARNING));
			
		report.getResources().add(reportRow);
		return;
		
	}
	
}
