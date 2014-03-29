package org.jboss.windup.graph.model.resource;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.frames.modules.javahandler.JavaHandlerContext;

public abstract class FileResourceImpl implements FileResource, JavaHandlerContext<Vertex> {

	@Override
	public InputStream asInputStream() throws IOException {
		if(this.getFilePath() != null) {
			File file = new File(getFilePath());
			return new FileInputStream(file);
		}
		return null;
	}
	
	public File asFile() throws IOException {
		if(this.getFilePath() != null) {
			File file = new File(getFilePath());
			return file;
		}
		return null; 
	}
}
