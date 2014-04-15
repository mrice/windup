package org.jboss.windup.engine.visitor;

import java.io.InputStream;
import java.util.Properties;

import javax.inject.Inject;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.jboss.windup.engine.WindupContext;
import org.jboss.windup.engine.visitor.base.EmptyGraphVisitor;
import org.jboss.windup.graph.dao.ArchiveEntryDaoBean;
import org.jboss.windup.graph.dao.PropertiesDaoBean;
import org.jboss.windup.graph.model.meta.PropertiesMeta;
import org.jboss.windup.graph.model.resource.ArchiveEntryResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Extracts manifest information to graph. 
 * 
 * @author bradsdavis@gmail.com
 *
 */
public class PropertiesVisitor extends EmptyGraphVisitor {
	private static final Logger LOG = LoggerFactory.getLogger(PropertiesVisitor.class);

	@Inject
	private WindupContext context;
	
	@Inject
	private ArchiveEntryDaoBean archiveEntryDao;
	
	@Inject
	private PropertiesDaoBean propertiesDao;
	
	@Override
	public void run() {
		for(ArchiveEntryResource resource : archiveEntryDao.findArchiveEntryWithExtension("properties")) {
			visitArchiveEntry(resource);
		}
		archiveEntryDao.commit();
	}
	
	@Override
	public void visitArchiveEntry(ArchiveEntryResource entry) {
		PropertiesMeta properties = propertiesDao.create();
		properties.setResource(entry);
		
		InputStream is = null; 
		try {
			is = entry.asInputStream();
			Properties props = new Properties();
			props.load(is);
			
			for(Object key : props.keySet()) {
				String property = StringUtils.trim(key.toString());
				String propertyValue = StringUtils.trim(props.get(key).toString());
				properties.setProperty(property, propertyValue);
			}
		}
		catch(Exception e) {
			LOG.warn("Exception reading manifest.", e);
		}
		finally {
			IOUtils.closeQuietly(is);
		}
		
		
	}
}
