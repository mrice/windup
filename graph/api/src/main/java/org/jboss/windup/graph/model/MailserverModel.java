package org.jboss.windup.graph.model;

import com.tinkerpop.frames.Property;
import com.tinkerpop.frames.modules.typedgraph.TypeValue;

@TypeValue("MailserverMeta")
public interface MailserverModel extends WindupVertexFrame
{

    @Property("name")
    public String getName();

    @Property("name")
    public void setName(String name);

    @Property("serverURI")
    public String getServerURI();

    @Property("serverURI")
    public void setServerURI(String uri);

}
