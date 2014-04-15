package org.jboss.windup.graph.dao;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.inject.Inject;

import org.jboss.windup.graph.model.meta.xml.NamespaceMeta;
import org.jboss.windup.graph.model.resource.JavaClass;
import org.jboss.windup.graph.model.resource.Resource;
import org.jboss.windup.graph.model.resource.XmlResource;

import com.google.common.collect.Iterables;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.gremlin.java.GremlinPipeline;

public class XmlResourceDaoBean extends BaseDaoBean<XmlResource> {

	public XmlResourceDaoBean() {
		super(XmlResource.class);
	}

	@Inject
	private ArchiveEntryDaoBean archiveEntryDao;
	
	@Inject
	private NamespaceDaoBean namespaceDao;

	public Iterable<XmlResource> containsNamespaceURI(String namespaceURI) {
		
		List<Iterable<XmlResource>> result = new LinkedList<Iterable<XmlResource>>();
		for(NamespaceMeta resource : namespaceDao.findByURI(namespaceURI)) {
			result.add(resource.getXmlResources());
		}
		
		//now, check thether it is null.
		if(result == null || result.size() == 0) {
			return new LinkedList<XmlResource>();
		}
		return Iterables.concat(result);
	}
	
	public Iterable<XmlResource> findByRootTag(String rootTagName) {
		return getByProperty("rootTagName", rootTagName);
	}
	
	
	public boolean isXmlResource(Resource resource) {
		return (new GremlinPipeline<Vertex, Vertex>(resource.asVertex())).out("xmlResourceFacet").iterator().hasNext();
	}
	
	public XmlResource getXmlFromResource(Resource resource) {
		Iterator<Vertex> v = (new GremlinPipeline<Vertex, Vertex>(resource.asVertex())).out("xmlResourceFacet").iterator();
		if(v.hasNext()) {
			return context.getFramed().frame(v.next(), XmlResource.class);
		}
		
		return null;
	}
}
