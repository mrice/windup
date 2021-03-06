package org.jboss.windup.config.graphsearch;

import org.jboss.windup.graph.model.WindupVertexFrame;

import com.thinkaurelius.titan.core.attribute.Text;
import com.tinkerpop.frames.FramedGraphQuery;
import com.tinkerpop.frames.modules.typedgraph.TypeValue;

class GraphSearchCriterionType implements GraphSearchCriterion
{
    private String typeValue;

    public GraphSearchCriterionType(Class<? extends WindupVertexFrame> clazz)
    {
        TypeValue typeValueAnnotation = clazz.getAnnotation(TypeValue.class);
        if (typeValueAnnotation == null)
        {
            throw new IllegalArgumentException("Class " + clazz.getCanonicalName() + " lacks a @TypeValue annotation");
        }
        else
        {
            this.typeValue = typeValueAnnotation.value();
        }
    }

    @Override
    public void query(FramedGraphQuery q)
    {
        q.has("type", Text.CONTAINS, typeValue);
    }
}