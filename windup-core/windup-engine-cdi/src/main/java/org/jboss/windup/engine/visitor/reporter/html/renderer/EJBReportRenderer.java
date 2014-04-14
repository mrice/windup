package org.jboss.windup.engine.visitor.reporter.html.renderer;

import java.io.File;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import org.apache.commons.io.FileUtils;
import org.jboss.windup.engine.WindupContext;
import org.jboss.windup.engine.visitor.base.EmptyGraphVisitor;
import org.jboss.windup.engine.visitor.reporter.html.model.EJBReport;
import org.jboss.windup.engine.visitor.reporter.html.model.EJBReport.EJBRow;
import org.jboss.windup.engine.visitor.reporter.html.model.EJBReport.MDBRow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import freemarker.template.Configuration;
import freemarker.template.Template;

public class EJBReportRenderer extends EmptyGraphVisitor {
	private static final Logger LOG = LoggerFactory.getLogger(EJBReportRenderer.class);
	
	@Inject
	private WindupContext context;
	

	private final Configuration cfg;
	
	public EJBReportRenderer() {
		cfg = new Configuration();
        cfg.setTemplateUpdateDelay(500);
        cfg.setClassForTemplateLoading(this.getClass(), "/");
	}
	
	@Override
	public void run() {
		try {
			Template template = cfg.getTemplate("/reports/templates/ejb.ftl");
			
			Map<String, Object> objects = new HashMap<String, Object>();
			objects.put("ejbs", generageReports());
			
			File runDirectory = context.getRunDirectory();
			File archiveReportDirectory = new File(runDirectory, "applications");
			File archiveDirectory = new File(archiveReportDirectory, "application");
			FileUtils.forceMkdir(archiveDirectory);
			File archiveReport = new File(archiveDirectory, "ejbs.html");
			
			template.process(objects, new FileWriter(archiveReport));
			
			LOG.info("Wrote report: "+archiveReport.getAbsolutePath());
			
		} catch (Exception e) {
			throw new RuntimeException("Exception writing report.", e);
		}
	}
	
	
	protected EJBReport generageReports() {
		EJBReport applicationReport = new EJBReport();
		
		for(int i=0; i<10; i++) {
			EJBRow row = new EJBRow("ejbName"+i, "com.example.bean.ejb.ExampleName"+i, "java:/exampleName"+i);
			applicationReport.getStatefulBeans().add(row);
			applicationReport.getStatelessBeans().add(row);
		}
		
		for(int i=0; i<5; i++) {
			MDBRow row = new MDBRow("mdbName"+i, "com.example.bean.mdb.ExampleMDB"+i, "java:/queue/Example"+i);
			applicationReport.getMdbs().add(row);
		}
		return applicationReport;
	}
}
