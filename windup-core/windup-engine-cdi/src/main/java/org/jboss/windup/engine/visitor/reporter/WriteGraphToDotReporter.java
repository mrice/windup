package org.jboss.windup.engine.visitor.reporter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.inject.Inject;

import org.apache.commons.io.IOUtils;
import org.jboss.windup.engine.WindupContext;
import org.jboss.windup.engine.visitor.base.EmptyGraphVisitor;
import org.jboss.windup.graph.renderer.dot.DotWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Writes Windup graph to GraphML file.
 * 
 * @author bradsdavis@gmail.com
 *
 */
public class WriteGraphToDotReporter extends EmptyGraphVisitor {

	private static final Logger LOG = LoggerFactory.getLogger(WriteGraphToDotReporter.class);
	
	@Inject
	private WindupContext context;
	
	@Override
	public void run() {
		DotWriter writer = new DotWriter(context.getGraphContext().getGraph());
		File graphFile = new File(context.getRunDirectory(), "graph.dot");
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(graphFile.getAbsoluteFile());
			writer.writeGraph(fos);
			LOG.info("Wrote graph to: "+graphFile.getAbsolutePath());
		} catch (IOException e) {
			IOUtils.closeQuietly(fos);
			throw new RuntimeException("Exception writing graph to: "+graphFile.getAbsolutePath(), e);
		}
	}
}
