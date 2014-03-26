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
import org.jboss.windup.engine.visitor.reporter.html.model.Effort;
import org.jboss.windup.engine.visitor.reporter.html.model.Level;
import org.jboss.windup.engine.visitor.reporter.html.model.Link;
import org.jboss.windup.engine.visitor.reporter.html.model.Tag;
import org.jboss.windup.graph.dao.JarArchiveDaoBean;
import org.jboss.windup.graph.model.resource.ArchiveResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import freemarker.template.Configuration;
import freemarker.template.Template;

public class ApplicationReportRenderer extends EmptyGraphVisitor {
	private static final Logger LOG = LoggerFactory.getLogger(ApplicationReportRenderer.class);
	
	@Inject
	private WindupContext context;
	

	@Inject
	private JarArchiveDaoBean jarDao;
	
	private final Configuration cfg;
	
	public ApplicationReportRenderer() {
		cfg = new Configuration();
        cfg.setTemplateUpdateDelay(500);
        cfg.setClassForTemplateLoading(this.getClass(), "/");
	}
	
	@Override
	public void run() {
		for(ArchiveResource entry : jarDao.getAll()) {
			visitArchive(entry);
		}
	}

	@Override
	public void visitArchive(ArchiveResource entry) {
		try {
			Template template = cfg.getTemplate("/reports/templates/application.ftl");
			
			Map<String, Object> objects = new HashMap<String, Object>();
			objects.put("application", generageReports());
			
			File runDirectory = context.getRunDirectory();
			File archiveReportDirectory = new File(runDirectory, "applications");
			File archiveDirectory = new File(archiveReportDirectory, "x");
			LOG.info("Archive Name: "+entry.getArchiveName());
			FileUtils.forceMkdir(archiveDirectory);
			File archiveReport = new File(archiveDirectory, "index.html");
			
			template.process(objects, new FileWriter(archiveReport));
			
			LOG.info("Wrote report: "+archiveReport.getAbsolutePath());
			
		} catch (Exception e) {
			throw new RuntimeException("Exception writing report.", e);
		}
	}
	
	
	protected ApplicationReport generageReports() {
		ApplicationReport applicationReport = new ApplicationReport();
		applicationReport.setApplicationName("Example Application");
		
		for(int i=0; i<10; i++) {
			ArchiveReport report = new ArchiveReport();
			applicationReport.getArchives().add(report);
			report.setApplicationPath("Test Application "+i);
			report.setLevel(Level.INFO);
			
			for(int j=0; j<10; j++) {
				ResourceReportRow resourceReport = new ResourceReportRow();
				report.getResources().add(resourceReport);
				resourceReport.setEffort(Effort.HIGH);
				resourceReport.setResourceLink(new Link("#", "Resource "+j));
				resourceReport.getIssueTags().add(new Tag("Test Danger 1.x", Level.DANGER));
				resourceReport.getTechnologyTags().add(new Tag("Test Info 1.x", Level.INFO));
			}
		}
		
		return applicationReport;
	}
}
