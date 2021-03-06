package org.jboss.windup.graph.model.resource;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import org.jboss.windup.graph.model.ArchiveModel;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.frames.Adjacency;
import com.tinkerpop.frames.Property;
import com.tinkerpop.frames.modules.javahandler.JavaHandler;
import com.tinkerpop.frames.modules.javahandler.JavaHandlerContext;
import com.tinkerpop.frames.modules.typedgraph.TypeValue;

@TypeValue("FileResource")
public interface FileResourceModel extends ResourceModel
{

    public static final String PROPERTY_FILE_PATH = "filePath";
    public static final String PROPERTY_IS_DIRECTORY = "isDirectory";

    @Property(PROPERTY_FILE_PATH)
    public String getFilePath();

    @Property(PROPERTY_IS_DIRECTORY)
    public boolean isDirectory();

    @Property("md5Hash")
    public String getMD5Hash();

    @Property("md5Hash")
    public void setMD5Hash(String md5Hash);

    @Property("sha1Hash")
    public String getSHA1Hash();

    @Property("sha1Hash")
    public void setSHA1Hash(String sha1Hash);

    /**
     * Indicates the archive that contained this file
     * 
     * @return
     */
    @Adjacency(label = "archiveFiles", direction = Direction.IN)
    public ArchiveModel getParentArchive();

    @Adjacency(label = "archiveFiles", direction = Direction.IN)
    public void setParentArchive(ArchiveModel parentArchive);

    // implemented via a handler that makes sure the isDirectory property is set as well
    @JavaHandler
    public void setFilePath(String filePath);

    @JavaHandler
    public File asFile() throws RuntimeException;

    @JavaHandler
    public InputStream asInputStream() throws RuntimeException;

    abstract class Impl implements FileResourceModel, ResourceModel, JavaHandlerContext<Vertex>
    {

        public void setFilePath(String filePath)
        {
            File file = new File(filePath);
            // set the isDirectory attribute
            it().setProperty(PROPERTY_IS_DIRECTORY, file.isDirectory());
            it().setProperty(PROPERTY_FILE_PATH, filePath);
        }

        @Override
        public InputStream asInputStream() throws RuntimeException
        {
            try
            {
                if (this.getFilePath() != null)
                {
                    File file = new File(getFilePath());
                    return new FileInputStream(file);
                }
                return null;
            }
            catch (Exception e)
            {
                throw new RuntimeException("Exception reading resource.", e);
            }
        }

        @Override
        public File asFile() throws RuntimeException
        {
            if (this.getFilePath() != null)
            {
                File file = new File(getFilePath());
                return file;
            }
            return null;
        }
    }
}
