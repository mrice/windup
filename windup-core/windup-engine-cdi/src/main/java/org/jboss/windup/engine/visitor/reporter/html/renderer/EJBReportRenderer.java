package org.jboss.windup.engine.visitor.reporter.html.renderer;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.jboss.windup.engine.WindupContext;
import org.jboss.windup.engine.visitor.base.EmptyGraphVisitor;
import org.jboss.windup.engine.visitor.reporter.html.model.EJBReport;
import org.jboss.windup.engine.visitor.reporter.html.model.EJBReport.EJBRow;
import org.jboss.windup.engine.visitor.reporter.html.model.EJBReport.MDBRow;
import org.jboss.windup.engine.visitor.reporter.html.model.LinkName;
import org.jboss.windup.engine.visitor.reporter.html.model.Name;
import org.jboss.windup.engine.visitor.reporter.html.model.ReportContext;
import org.jboss.windup.engine.visitor.reporter.html.model.SimpleName;
import org.jboss.windup.graph.dao.EJBEntityDaoBean;
import org.jboss.windup.graph.dao.EJBSessionBeanDaoBean;
import org.jboss.windup.graph.dao.MessageDrivenDaoBean;
import org.jboss.windup.graph.dao.SourceReportDao;
import org.jboss.windup.graph.model.meta.javaclass.EjbSessionBeanFacet;
import org.jboss.windup.graph.model.meta.javaclass.MessageDrivenBeanFacet;
import org.jboss.windup.graph.model.resource.FileResource;
import org.jboss.windup.graph.model.resource.JavaClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import freemarker.template.Configuration;
import freemarker.template.Template;

public class EJBReportRenderer extends EmptyGraphVisitor {
	private static final Logger LOG = LoggerFactory.getLogger(EJBReportRenderer.class);
	
	@Inject
	private WindupContext context;
	
	@Inject
	private EJBSessionBeanDaoBean sessionDao;
	
	@Inject
	private EJBEntityDaoBean entityDao;
	
	@Inject
	private SourceReportDao sourceReportDao;
	
	@Inject
	private MessageDrivenDaoBean messageDrivenDao;

	@Inject
	private NamingUtility reportUtility;
	
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
			
			
			File runDirectory = context.getRunDirectory();
			File archiveReportDirectory = new File(runDirectory, "applications");
			File archiveDirectory = new File(archiveReportDirectory, "application");
			FileUtils.forceMkdir(archiveDirectory);
			File ejbReport = new File(archiveDirectory, "ejbs.html");
			objects.put("ejbs", generageReports(ejbReport));
			
			template.process(objects, new FileWriter(ejbReport));
			
			
			LOG.info("Wrote report: "+ejbReport.getAbsolutePath());
			
		} catch (Exception e) {
			throw new RuntimeException("Exception writing report.", e);
		}
	}
	
	
	protected EJBReport generageReports(File ejbReport) {
		EJBReport applicationReport = new EJBReport();
		
		for(EjbSessionBeanFacet session : sessionDao.getAll()) {
			Name name = reportUtility.getReportJavaResource(ejbReport, session.getJavaClassFacet());

			EJBRow ejbRow = new EJBRow(session.getSessionBeanName(), name, session.getSessionType());
			
			String type = session.getSessionType();
			
			if(StringUtils.equals("Stateless", type)) {
				applicationReport.getStatelessBeans().add(ejbRow);
			}
			else {
				applicationReport.getStatefulBeans().add(ejbRow);
			}
		}
		
		for(MessageDrivenBeanFacet mdf : messageDrivenDao.getAll()) {
			String name = mdf.getMessageDrivenBeanName();
			if(StringUtils.isBlank(name)) {
				name = mdf.getJavaClassFacet().getQualifiedName();
			}
			Name qualifiedName = reportUtility.getReportJavaResource(ejbReport, mdf.getJavaClassFacet());
			
			MDBRow row = new MDBRow(name, qualifiedName, "");
			applicationReport.getMdbs().add(row);
		}
		return applicationReport;
	}

}
