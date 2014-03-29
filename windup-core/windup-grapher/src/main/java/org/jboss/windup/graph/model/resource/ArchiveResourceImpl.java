package org.jboss.windup.graph.model.resource;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipFile;

import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.frames.modules.javahandler.JavaHandlerContext;

public abstract class ArchiveResourceImpl implements ArchiveResource, JavaHandlerContext<Vertex> {

	@Override
	public InputStream asInputStream() throws IOException {
		return getFileResource().asInputStream();
	}
	
	@Override
	public File asFile() throws IOException {
		return getFileResource().asFile();
	}
	
	@Override
	public ZipFile asZipFile() throws IOException {
		return new ZipFile(this.asFile());
	}
}
