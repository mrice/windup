package org.jboss.windup.graph.model.resource;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.frames.Adjacency;
import com.tinkerpop.frames.Property;
import com.tinkerpop.frames.modules.javahandler.JavaHandler;
import com.tinkerpop.frames.modules.javahandler.JavaHandlerContext;
import com.tinkerpop.frames.modules.typedgraph.TypeValue;

@TypeValue("ArchiveEntryResource")
public interface ArchiveEntryResource extends Resource {

	@Property("archiveEntry")
	public String getArchiveEntry();

	@Property("archiveEntry")
	public void setArchiveEntry(String archiveEntry);
	
	@Adjacency(label="child", direction=Direction.IN)
	public ArchiveResource getArchive();
	
	@Adjacency(label="child", direction=Direction.IN)
	public void setArchive(ArchiveResource archive);

	@JavaHandler
	public InputStream asInputStream() throws IOException;
	
	@JavaHandler
	public File asFile() throws IOException;
	
	@JavaHandler
	public ZipEntry asZipEntry() throws IOException;
	
	abstract class Impl implements ArchiveEntryResource, JavaHandlerContext<Vertex> {
		@Override
		public InputStream asInputStream() throws IOException {
			ZipFile file = getArchive().asZipFile();
			ZipEntry entry = file.getEntry(this.getArchiveEntry());
			return file.getInputStream(entry);
		}
		
		@Override
		public File asFile() throws IOException {
			if(this.it().getProperty("tempFile") != null) {
				File filePath = new File(this.it().getProperty("tempFile").toString());
				return filePath;
			}
			else {
				File temp = org.jboss.windup.engine.util.ZipUtil.unzipToTemp(this.getArchive().asZipFile(), this.asZipEntry());
				this.it().setProperty("tempFile", temp.getAbsolutePath());
				return temp;
			}
		}
		
		@Override
		public ZipEntry asZipEntry() throws IOException {
			ZipEntry ze = new ZipEntry(this.getArchiveEntry());
			return ze;
		}
	}
}
