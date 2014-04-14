package org.jboss.windup.engine.visitor.reporter.html.renderer;

import java.io.File;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import org.apache.commons.io.FileUtils;
import org.jboss.windup.engine.WindupContext;
import org.jboss.windup.engine.visitor.base.EmptyGraphVisitor;
import org.jboss.windup.engine.visitor.reporter.html.model.ApplicationReport;
import org.jboss.windup.engine.visitor.reporter.html.model.ArchiveReport;
import org.jboss.windup.engine.visitor.reporter.html.model.ArchiveReport.ResourceReportRow;
import org.jboss.windup.engine.visitor.reporter.html.model.Level;
import org.jboss.windup.engine.visitor.reporter.html.model.Link;
import org.jboss.windup.graph.dao.ApplicationReferenceDaoBean;
import org.jboss.windup.graph.model.meta.ApplicationReference;
import org.jboss.windup.graph.model.resource.ArchiveEntryResource;
import org.jboss.windup.graph.model.resource.ArchiveResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tinkerpop.blueprints.Vertex;

import freemarker.template.Configuration;
import freemarker.template.Template;

public class ApplicationReportRenderer extends EmptyGraphVisitor {
	private static final Logger LOG = LoggerFactory.getLogger(ApplicationReportRenderer.class);
	
	@Inject
	private WindupContext context;
	
	@Inject
	private ApplicationReferenceDaoBean appRefDao;

	private final Configuration cfg;
	
	public ApplicationReportRenderer() {
		cfg = new Configuration();
        cfg.setTemplateUpdateDelay(500);
        cfg.setClassForTemplateLoading(this.getClass(), "/");
	}
	
	@Override
	public void run() {
		ApplicationReport applicationReport = new ApplicationReport();
		applicationReport.setApplicationName("Application Name");
		
		for(ApplicationReference app : appRefDao.getAll()) {
			Vertex reference = app.getMetaReference();
			LOG.info("Vertex: "+reference);
		}
		
		try {
			Template template = cfg.getTemplate("/reports/templates/application.ftl");
			
			Map<String, Object> objects = new HashMap<String, Object>();
			objects.put("application", applicationReport);
			
			File runDirectory = context.getRunDirectory();
			File archiveReportDirectory = new File(runDirectory, "applications");
			File archiveDirectory = new File(archiveReportDirectory, "application");
			FileUtils.forceMkdir(archiveDirectory);
			File archiveReport = new File(archiveDirectory, "index.html");
			
			template.process(objects, new FileWriter(archiveReport));
			
			LOG.info("Wrote report: "+archiveReport.getAbsolutePath());
			
		} catch (Exception e) {
			throw new RuntimeException("Exception writing report.", e);
		}
	}
	
	protected ArchiveReport generageReports(ArchiveResource entry) {
		
		ArchiveReport report = new ArchiveReport();
		report.setApplicationPath(entry.getArchiveName());
		report.setLevel(Level.PRIMARY);
		
		for(ArchiveResource resource : entry.getChildren()) {
			if (resource instanceof ArchiveEntryResource) {
				ArchiveEntryResource archiveEntry = (ArchiveEntryResource) resource;
				
					String type = resource.asVertex().getProperty("type");
					
					ResourceReportRow resourceReport = new ResourceReportRow();
					resourceReport.setResourceLink(new Link("#", ((ArchiveEntryResource) resource).getArchiveEntry()));
					report.getResources().add(resourceReport);
			}
			
			
		}
		
		return report;
	}
}
