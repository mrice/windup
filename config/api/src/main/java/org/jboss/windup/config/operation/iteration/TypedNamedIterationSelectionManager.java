/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.windup.config.operation.iteration;

import org.jboss.windup.config.GraphRewrite;
import org.jboss.windup.config.selectables.SelectionFactory;
import org.jboss.windup.graph.model.WindupVertexFrame;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public class TypedNamedIterationSelectionManager implements IterationSelectionManager
{

    private final Class<? extends WindupVertexFrame> sourceType;
    private final String source;

    public TypedNamedIterationSelectionManager(Class<? extends WindupVertexFrame> sourceType, String source)
    {
        this.sourceType = sourceType;
        this.source = source;
    }

    @Override
    public Iterable<WindupVertexFrame> getFrames(GraphRewrite event, SelectionFactory factory)
    {
        // TODO verify type
        return factory.findVariable(source);
    }

}
