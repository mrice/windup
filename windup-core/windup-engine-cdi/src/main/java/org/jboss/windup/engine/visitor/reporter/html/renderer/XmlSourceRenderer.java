package org.jboss.windup.engine.visitor.reporter.html.renderer;

import java.io.File;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.ivy.plugins.repository.file.FileRepository;
import org.jboss.windup.engine.WindupContext;
import org.jboss.windup.engine.visitor.base.EmptyGraphVisitor;
import org.jboss.windup.engine.visitor.reporter.html.model.ReportContext;
import org.jboss.windup.engine.visitor.reporter.html.model.SourceReport;
import org.jboss.windup.engine.visitor.reporter.html.model.SourceReport.SourceLineAnnotations;
import org.jboss.windup.graph.dao.FileResourceDaoBean;
import org.jboss.windup.graph.dao.SourceReportDao;
import org.jboss.windup.graph.dao.XmlResourceDaoBean;
import org.jboss.windup.graph.model.resource.ArchiveEntryResource;
import org.jboss.windup.graph.model.resource.FileResource;
import org.jboss.windup.graph.model.resource.JavaClass;
import org.jboss.windup.graph.model.resource.XmlResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import freemarker.template.Configuration;
import freemarker.template.Template;

public class XmlSourceRenderer extends EmptyGraphVisitor {
	private static final Logger LOG = LoggerFactory.getLogger(XmlSourceRenderer.class);
	
	@Inject
	private XmlResourceDaoBean xmlResources;
	
	@Inject
	private FileResourceDaoBean fileResourceDao;
	
	@Inject
	private SourceReportDao sourceReportDao;
	
	@Inject
	private WindupContext context;
	
	@Inject
	private NamingUtility namingUtility;
	
	private final Configuration cfg;
	
	public XmlSourceRenderer() {
		cfg = new Configuration();
        cfg.setTemplateUpdateDelay(500);
        cfg.setClassForTemplateLoading(this.getClass(), "/");
	}
	
	@Override
	public void run() {
		try {
			for(XmlResource entry : xmlResources.getAll()) {
				visitXmlResource(entry);
			}
		} catch (Exception e) {
			throw new RuntimeException("Exception writing report.", e);
		}
	}
	
	@Override
	public void visitXmlResource(XmlResource entry) {
		
		try {
			Template template = cfg.getTemplate("/reports/templates/source.ftl");
			
			Map<String, Object> objects = new HashMap<String, Object>();
			
			SourceReport report = new SourceReport();
			
			report.setSourceBody(IOUtils.toString(entry.asInputStream()));
			
			//create block settings.
			report.setSourceType("xml");
			
			report.setSourceBlock(createBlockSettings(report.getSourceLineAnnotations()));
			objects.put("source", report);
			

			//create report context.
			File runDirectory = context.getRunDirectory();
			
			File archiveReportDirectory = new File(runDirectory, "applications");
			File archiveDirectory = new File(archiveReportDirectory, "application");
			FileUtils.forceMkdir(archiveDirectory);
			
			File resourceDirectory = new File(archiveDirectory, "resources");
			FileUtils.forceMkdir(resourceDirectory);
			
			
			String fullName = null;
			String name = null;
			if(entry.getResource() instanceof ArchiveEntryResource) {
				ArchiveEntryResource resource = context.getGraphContext().getFramed().frame(entry.getResource().asVertex(), ArchiveEntryResource.class);
				name = resource.getArchiveEntry();
				name = StringUtils.substringAfterLast(name, "/");
				
				fullName = namingUtility.buildFullPath(resource);
			}
			else if(entry.getResource() instanceof FileResource) {
				//TODO: fix this for non-archive XML.  
				//This should recurse back to find the root directory.
				FileResource resource = context.getGraphContext().getFramed().frame(entry.getResource().asVertex(), FileResource.class);
				name = resource.asFile().getName(); 
				fullName = name;
			}
			report.setSourceName(name);
			
			
			File reportRef = new File(resourceDirectory, fullName+".html");
			FileUtils.forceMkdir(reportRef.getParentFile());
			
			ReportContext reportContext = new ReportContext(runDirectory, reportRef.getParentFile());
			objects.put("report", reportContext);
			template.process(objects, new FileWriter(reportRef));
			
			LOG.info("Wrote overview report: "+reportRef.getAbsolutePath());
			
			persistReportReference(entry, reportRef);
		}
		catch(Exception e) {
			LOG.error("Exception writing Report: "+e.getMessage());
		}
	}
	
	private void persistReportReference(XmlResource xmlResource, File reportLocation) {
		//persist the file resource & reference to Java Class.
		FileResource fileReference = fileResourceDao.create();
		fileReference.setFilePath(reportLocation.getAbsolutePath());
		
		org.jboss.windup.graph.model.meta.report.SourceReport sourceReport = sourceReportDao.create();
		sourceReport.setReportFile(fileReference);
		sourceReport.setResource(xmlResource);
		
		sourceReportDao.commit();
	}
	
	
	public String createBlockSettings(Set<SourceLineAnnotations> lines) {
		StringBuilder builder = new StringBuilder();
		
		boolean first = true;
		for(SourceLineAnnotations line : lines) {
			if(!first) {
				builder.append(",");
			}
			builder.append(line.getLineNumber());
			
			if(first) {
				first = false;
			}
		}
		
		return builder.toString();
	}
}
