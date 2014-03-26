package org.jboss.windup.engine.visitor.reporter.html.renderer;

import java.io.File;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import org.jboss.windup.engine.WindupContext;
import org.jboss.windup.engine.visitor.base.EmptyGraphVisitor;
import org.jboss.windup.engine.visitor.reporter.html.model.SourceReport;
import org.jboss.windup.graph.dao.ArchiveDaoBean;
import org.jboss.windup.graph.dao.JavaClassDaoBean;
import org.jboss.windup.graph.dao.XmlResourceDaoBean;
import org.jboss.windup.graph.model.resource.ArchiveResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import freemarker.template.Configuration;
import freemarker.template.Template;

public class SourceRenderer extends EmptyGraphVisitor {
	private static final Logger LOG = LoggerFactory.getLogger(SourceRenderer.class);
	
	@Inject
	private JavaClassDaoBean javaClassDao;
	
	@Inject
	private XmlResourceDaoBean xmlResources;
	
	@Inject
	private ArchiveDaoBean archiveDao;
	
	@Inject
	private WindupContext context;
	
	private final Configuration cfg;
	
	public SourceRenderer() {
		cfg = new Configuration();
        cfg.setTemplateUpdateDelay(500);
        cfg.setClassForTemplateLoading(this.getClass(), "/");
	}
	
	@Override
	public void run() {
		try {
			Template template = cfg.getTemplate("/reports/templates/source.ftl");
			
			Map<String, Object> objects = new HashMap<String, Object>();
			objects.put("source", generageReports());
			File runDirectory = context.getRunDirectory();
			File overviewReport = new File(runDirectory, "source.html");
			template.process(objects, new FileWriter(overviewReport));
			
			LOG.info("Wrote overview report: "+overviewReport.getAbsolutePath());
		} catch (Exception e) {
			throw new RuntimeException("Exception writing report.", e);
		}
	}
	
	protected SourceReport generageReports() {
		SourceReport sourceReport = new SourceReport();
		sourceReport.setSourceBody("Hello world.");
		sourceReport.setSourceName("Example.xml");
		return sourceReport;
	}
}
