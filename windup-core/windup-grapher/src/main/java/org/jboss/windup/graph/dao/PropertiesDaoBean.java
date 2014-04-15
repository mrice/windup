package org.jboss.windup.graph.dao;

import java.util.Iterator;

import org.jboss.windup.graph.model.meta.PropertiesMeta;
import org.jboss.windup.graph.model.resource.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.gremlin.java.GremlinPipeline;

public class PropertiesDaoBean extends BaseDaoBean<PropertiesMeta> {

	private static Logger LOG = LoggerFactory.getLogger(PropertiesDaoBean.class);
	
	public PropertiesDaoBean() {
		super(PropertiesMeta.class);
	}
	
	public boolean isPropertiesResource(Resource resource) {
		return (new GremlinPipeline<Vertex, Vertex>(resource.asVertex())).out("propertiesFacet").iterator().hasNext();
	}
	
	public PropertiesMeta getPropertiesFromResource(Resource resource) {
		Iterator<Vertex> v = (new GremlinPipeline<Vertex, Vertex>(resource.asVertex())).out("propertiesFacet").iterator();
		if(v.hasNext()) {
			return context.getFramed().frame(v.next(), PropertiesMeta.class);
		}
		
		return null;
	}
}
