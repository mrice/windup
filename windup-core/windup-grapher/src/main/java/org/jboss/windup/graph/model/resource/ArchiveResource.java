package org.jboss.windup.graph.model.resource;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipFile;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.frames.Adjacency;
import com.tinkerpop.frames.Property;
import com.tinkerpop.frames.modules.javahandler.JavaHandler;
import com.tinkerpop.frames.modules.javahandler.JavaHandlerContext;
import com.tinkerpop.frames.modules.typedgraph.TypeValue;

@TypeValue("ArchiveResource")
public interface ArchiveResource extends Resource {

	@Adjacency(label="fileReference", direction=Direction.OUT)
	public FileResource getFileResource();
	
	@Adjacency(label="fileReference", direction=Direction.OUT)
	public void setFileResource(FileResource file);
	
	@Property("md5Hash")
	public String getMD5Hash();
	
	@Property("md5Hash")
	public void setMD5Hash(String md5Hash);
	
	@Property("sha1Hash")
	public String getSHA1Hash();
	
	@Property("sha1Hash")
	public void setSHA1Hash(String sha1Hash);
	
	@Property("archiveName")
	public String getArchiveName();
	
	@Property("archiveName")
	public void setArchiveName(String archiveName);
	
	@Adjacency(label="childArchive", direction=Direction.OUT)
	public Iterable<ArchiveResource> getChildrenArchive();
	
	@Adjacency(label="childArchive", direction=Direction.OUT)
	public void addChildArchive(final ArchiveResource resource);
	
	@Adjacency(label="childArchive", direction=Direction.IN)
	public ArchiveResource getParentArchive();
	
	
	@Adjacency(label="childArchiveEntry", direction=Direction.OUT)
	public Iterable<ArchiveEntryResource> getChildrenArchiveEntries();
	
	@Adjacency(label="childArchiveEntry", direction=Direction.OUT)
	public void addChildrenArchiveEntries(final ArchiveEntryResource resource);

	
	
	@JavaHandler
	public File asFile() throws IOException;

	@JavaHandler
	public ZipFile asZipFile() throws IOException;
	
	@JavaHandler
	public InputStream asInputStream() throws IOException;
	
	abstract class Impl implements ArchiveResource, JavaHandlerContext<Vertex> {

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
}
