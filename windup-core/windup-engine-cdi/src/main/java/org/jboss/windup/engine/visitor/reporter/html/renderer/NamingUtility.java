package org.jboss.windup.engine.visitor.reporter.html.renderer;

import java.io.File;
import java.io.IOException;

import javax.inject.Inject;

import org.jboss.windup.engine.WindupContext;
import org.jboss.windup.engine.visitor.reporter.html.model.LinkName;
import org.jboss.windup.engine.visitor.reporter.html.model.Name;
import org.jboss.windup.engine.visitor.reporter.html.model.ReportContext;
import org.jboss.windup.engine.visitor.reporter.html.model.SimpleName;
import org.jboss.windup.graph.dao.SourceReportDao;
import org.jboss.windup.graph.model.meta.JarManifest;
import org.jboss.windup.graph.model.resource.ArchiveEntryResource;
import org.jboss.windup.graph.model.resource.FileResource;
import org.jboss.windup.graph.model.resource.JavaClass;
import org.jboss.windup.graph.model.resource.XmlResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NamingUtility {
	private static final Logger LOG = LoggerFactory.getLogger(NamingUtility.class);
	
	@Inject
	private WindupContext context;
	
	@Inject
	private SourceReportDao sourceReportDao;
	
	protected Name getReportJavaResource(File thisReport, JavaClass clz) {
		if(!sourceReportDao.hasSourceReport(clz)) {
			return new SimpleName(clz.getQualifiedName());
		}
		
		FileResource reportLocation = sourceReportDao.getResourceReport(clz);
		
		try {
			ReportContext context = new ReportContext(thisReport, reportLocation.asFile());
			LOG.info("Relative: "+context.getRelativeFrom()+" , "+context.getRelativeTo());
			Name linked = new LinkName(context.getRelativeFrom(), clz.getQualifiedName());
			return linked;
		} catch (IOException e) {
			LOG.error("Exception reading report file.", e);
		}
		return null;
	}
	
	protected Name getReportXmlResource(File thisReport, XmlResource xml) {
		if(!sourceReportDao.hasSourceReport(xml)) {
			return new SimpleName(getXmlResourceName(xml));
		}
		
		FileResource reportLocation = sourceReportDao.getResourceReport(xml);
		
		try {
			ReportContext context = new ReportContext(thisReport, reportLocation.asFile());
			LOG.info("Relative: "+context.getRelativeFrom()+" , "+context.getRelativeTo());
			Name linked = new LinkName(context.getRelativeFrom(), getXmlResourceName(xml));
			return linked;
		} catch (IOException e) {
			LOG.error("Exception reading report file.", e);
		}
		
		return null;
	}
	
	protected Name getReportManifestResource(File thisReport, JarManifest manifest) {
		if(!sourceReportDao.hasSourceReport(manifest)) {
			return new SimpleName(getManifestResourceName(manifest));
		}
		
		FileResource reportLocation = sourceReportDao.getResourceReport(manifest);
		
		try {
			ReportContext context = new ReportContext(thisReport, reportLocation.asFile());
			LOG.info("Relative: "+context.getRelativeFrom()+" , "+context.getRelativeTo());
			Name linked = new LinkName(context.getRelativeFrom(), getManifestResourceName(manifest));
			return linked;
		} catch (IOException e) {
			LOG.error("Exception reading report file.", e);
		}
		
		return null;
	}
	
	
	protected String getManifestResourceName(JarManifest manifest) {
		if(manifest.getResource() instanceof ArchiveEntryResource) {
			ArchiveEntryResource resource = context.getGraphContext().getFramed().frame(manifest.getResource().asVertex(), ArchiveEntryResource.class);
			return resource.getArchiveEntry();
		}
		else if(manifest.getResource() instanceof FileResource) { 
			FileResource resource = context.getGraphContext().getFramed().frame(manifest.getResource().asVertex(), FileResource.class);
			return resource.getFilePath();
		}
		LOG.info("Link is null.");
		return null;
	}
	
	protected String getXmlResourceName(XmlResource xml) {
		if(xml.getResource() instanceof ArchiveEntryResource) {
			ArchiveEntryResource resource = context.getGraphContext().getFramed().frame(xml.getResource().asVertex(), ArchiveEntryResource.class);
			return resource.getArchiveEntry();
		}
		else if(xml.getResource() instanceof FileResource) { 
			FileResource resource = context.getGraphContext().getFramed().frame(xml.getResource().asVertex(), FileResource.class);
			return resource.getFilePath();
		}
		LOG.info("Link is null.");
		return null;
	}
}
