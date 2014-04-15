package org.jboss.windup.graph.dao;

import java.util.Iterator;

import org.jboss.windup.graph.model.meta.JarManifest;
import org.jboss.windup.graph.model.resource.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.gremlin.java.GremlinPipeline;

public class JarManifestDaoBean extends BaseDaoBean<JarManifest> {

	private static Logger LOG = LoggerFactory.getLogger(JarManifestDaoBean.class);
	
	public JarManifestDaoBean() {
		super(JarManifest.class);
	}
	
	public boolean isManifestResource(Resource resource) {
		return (new GremlinPipeline<Vertex, Vertex>(resource.asVertex())).out("manifestFacet").iterator().hasNext();
	}
	
	public JarManifest getManifestFromResource(Resource resource) {
		Iterator<Vertex> v = (new GremlinPipeline<Vertex, Vertex>(resource.asVertex())).out("manifestFacet").iterator();
		if(v.hasNext()) {
			return context.getFramed().frame(v.next(), JarManifest.class);
		}
		
		return null;
	}
}
