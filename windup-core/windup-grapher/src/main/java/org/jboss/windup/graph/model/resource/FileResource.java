package org.jboss.windup.graph.model.resource;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.frames.Property;
import com.tinkerpop.frames.modules.javahandler.JavaHandler;
import com.tinkerpop.frames.modules.javahandler.JavaHandlerContext;
import com.tinkerpop.frames.modules.typedgraph.TypeValue;

@TypeValue("FileResource")
public interface FileResource extends Resource {
	
	@Property("filePath")
	public String getFilePath();
	
	@Property("filePath")
	public void setFilePath(String filePath);
	
	@JavaHandler
	public File asFile() throws IOException;
	
	@JavaHandler
	public InputStream asInputStream() throws IOException;

	abstract class Impl implements FileResource, Resource, JavaHandlerContext<Vertex> {
		
		@Override
		public InputStream asInputStream() throws IOException {
			if(this.getFilePath() != null) {
				File file = new File(getFilePath());
				return new FileInputStream(file);
			}
			return null;
		}
		
		@Override
		public File asFile() throws IOException {
			if(this.getFilePath() != null) {
				File file = new File(getFilePath());
				return file;
			}
			return null; 
		}
	}
}
