package org.jboss.windup.graph.model.blacklist;

import org.jboss.windup.graph.model.resource.JavaClass;
import org.jboss.windup.graph.model.resource.Resource;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.frames.Adjacency;

public interface BlacklistJavaClass {

	@Adjacency(label="blacklist", direction=Direction.IN)
	public void setJavaClass(JavaClass javaClass);
}
