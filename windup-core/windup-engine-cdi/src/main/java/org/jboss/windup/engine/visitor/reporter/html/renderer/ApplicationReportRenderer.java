package org.jboss.windup.engine.visitor.reporter.html.renderer;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

import javax.annotation.PostConstruct;
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
import org.jboss.windup.graph.dao.ArchiveDaoBean;
import org.jboss.windup.graph.dao.EJBConfigurationDaoBean;
import org.jboss.windup.graph.dao.JarManifestDaoBean;
import org.jboss.windup.graph.dao.JavaClassDaoBean;
import org.jboss.windup.graph.dao.WebConfigurationDaoBean;
import org.jboss.windup.graph.dao.XmlResourceDaoBean;
import org.jboss.windup.graph.model.meta.ApplicationReference;
import org.jboss.windup.graph.model.meta.JarManifest;
import org.jboss.windup.graph.model.meta.xml.EjbConfigurationFacet;
import org.jboss.windup.graph.model.meta.xml.WebConfigurationFacet;
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
	private ArchiveDaoBean archiveDao;
	
	@Inject
	private NamingUtility namingUtility;
	
	@Inject
	private WebConfigurationDaoBean webConfigurationDao;
	
	@Inject
	private EJBConfigurationDaoBean ejbConfigurationDao;
	
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
		reportReference = new File(archiveDirectory, "index.html");
	}
	
	
	@Override
	public void run() {
		try {
			ApplicationReport applicationReport = new ApplicationReport();
			for(ApplicationReference app : appRefDao.getAll()) {
				ArchiveResource reference = app.getArchive();
				applicationReport.setApplicationName(reference.getArchiveName());
				recurseArchive(applicationReport, app.getArchive());
			}
			
			
		
		
			Template template = cfg.getTemplate("/reports/templates/application.ftl");
			
			java.util.Map<String, Object> objects = new HashMap<String, Object>();
			objects.put("application", applicationReport);
			
			template.process(objects, new FileWriter(reportReference));
			LOG.info("Wrote report: "+reportReference.getAbsolutePath());
			
		} catch (Exception e) {
			throw new RuntimeException("Exception writing report.", e);
		}
	}
	
	protected void recurseArchive(ApplicationReport report, ArchiveResource resource) {
		ArchiveReport archiveReport = new ArchiveReport();
		
		String name = null;
		if(resource.getResource() instanceof ArchiveEntryResource) {
			ArchiveEntryResource parentEntry = context.getGraphContext().getFramed().frame(resource.getResource().asVertex(), ArchiveEntryResource.class);
			name = namingUtility.buildFullPath(parentEntry);
		}
		else {
			name = resource.getArchiveName();
		}
		
		
		
		
		archiveReport.setApplicationPath(name);
		
		for(ArchiveEntryResource entry : resource.getChildrenArchiveEntries()) {
			//check to see about facets.
			processEntry(archiveReport, entry);
		}
		
		for(ArchiveResource childResource : resource.getChildrenArchive()) {
			recurseArchive(report, childResource);
		}
		
		report.getArchives().add(archiveReport);
	}
	
	protected void processEntry(ArchiveReport report, ArchiveEntryResource entry) {
		ResourceReportRow reportRow = new ResourceReportRow();
		
		//see if the resource is a java class...
		if(javaDao.isJavaClass(entry)) {
			JavaClass clz = javaDao.getJavaClassFromResource(entry);
			if(!clz.isCustomerPackage()) {
				return;
			}
			reportRow.setResourceName(namingUtility.getReportJavaResource(runDirectory, reportReference, clz));
			reportRow.getTechnologyTags().add(new Tag("Java", Level.SUCCESS));
			
			report.getResources().add(reportRow);
			return;
		}

		//check if it is a XML resource...
		if(xmlDao.isXmlResource(entry)) 
		{
			XmlResource resource = xmlDao.getXmlFromResource(entry);
			reportRow.setResourceName(namingUtility.getReportXmlResource(runDirectory, reportReference, resource));
			reportRow.getTechnologyTags().add(new Tag("XML", Level.SUCCESS));
					
			//check the XML for some tags...
			if(ejbConfigurationDao.isEJBConfiguration(resource)) {
				EjbConfigurationFacet ejbConfiguration = ejbConfigurationDao.getEjbConfigurationFromResource(resource);
				reportRow.getTechnologyTags().add(new Tag("EJB "+ejbConfiguration.getSpecificationVersion()+" Configuration", Level.SUCCESS));
			}
			
			if(webConfigurationDao.isWebConfiguration(resource)) {
				WebConfigurationFacet webConfiguration = webConfigurationDao.getWebConfigurationFromResource(resource);
				reportRow.getTechnologyTags().add(new Tag("Web "+webConfiguration.getSpecificationVersion()+" Configuration", Level.SUCCESS));
			}
			
			
			report.getResources().add(reportRow);
			return;
		}
		
		//check if it is a manifest...
		if(manifestDao.isManifestResource(entry))
		{
			JarManifest resource = manifestDao.getManifestFromResource(entry);
			reportRow.setResourceName(namingUtility.getReportManifestResource(runDirectory, reportReference, resource));
			reportRow.getTechnologyTags().add(new Tag("Manifest", Level.SUCCESS));
					
			report.getResources().add(reportRow);
			return;
		}
		
		if(archiveDao.isArchiveResource(entry)) {
			//skip.
			return;
		}
		
		
		reportRow.setResourceName(new SimpleName(entry.getArchiveEntry()));
		reportRow.getIssueTags().add(new Tag("Unknown Type", Level.WARNING));
			
		report.getResources().add(reportRow);
		return;
		
	}
	
}
