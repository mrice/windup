package org.jboss.windup.graph.dao.impl;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.inject.Singleton;

import org.jboss.windup.graph.dao.JavaClassDao;
import org.jboss.windup.graph.model.resource.JavaClassModel;
import org.jboss.windup.graph.model.resource.ResourceModel;

import com.thinkaurelius.titan.core.attribute.Text;
import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.gremlin.java.GremlinPipeline;
import com.tinkerpop.pipes.PipeFunction;

@Singleton
public class JavaClassDaoImpl extends BaseDaoImpl<JavaClassModel> implements JavaClassDao
{

    public JavaClassDaoImpl()
    {
        super(JavaClassModel.class);
    }

    public synchronized JavaClassModel createJavaClass(String qualifiedName)
    {
        JavaClassModel clz = getByUniqueProperty("qualifiedName", qualifiedName);

        if (clz == null)
        {
            clz = (JavaClassModel) this.create(null);
            clz.setQualifiedName(qualifiedName);
        }

        return clz;
    }

    public Iterable<JavaClassModel> findByJavaClassPattern(String regex)
    {
        return super.findValueMatchingRegex("qualifiedName", regex);
    }

    public Iterable<JavaClassModel> findByJavaPackage(String packageName)
    {
        return getContext().getFramed().query().has("type", Text.CONTAINS, typeValueForSearch).has("packageName", packageName).vertices(type);
    }

    public Iterable<JavaClassModel> findByJavaVersion(JavaVersion version)
    {
        return getContext().getFramed().query().has("type", Text.CONTAINS, typeValueForSearch).has("majorVersion", version.getMajor())
                    .has("minorVersion", version.getMinor()).vertices(type);
    }

    public Iterable<JavaClassModel> getAllClassNotFound()
    {

        // iterate through all vertices
        Iterable<Vertex> pipeline = new GremlinPipeline<Vertex, Vertex>(context
                    .getGraph().query().has("type", Text.CONTAINS, typeValueForSearch).vertices())

                    // check to see whether there is an edge coming in that links to the resource providing the java
                    // class model.
                    .filter(new PipeFunction<Vertex, Boolean>()
                    {
                        public Boolean compute(Vertex argument)
                        {
                            if (argument.getEdges(Direction.IN, "javaClassFacet").iterator().hasNext())
                            {
                                return false;
                            }
                            // allow it through if there are no edges coming in that provide the java class model.
                            return true;
                        }
                    });
        return context.getFramed().frameVertices(pipeline, JavaClassModel.class);
    }

    public Iterable<JavaClassModel> getAllDuplicateClasses()
    {
        // iterate through all vertices
        Iterable<Vertex> pipeline = new GremlinPipeline<Vertex, Vertex>(context
                    .getGraph().query().has("type", Text.CONTAINS, typeValueForSearch).vertices())

                    // check to see whether there is an edge coming in that links to the resource providing the java
                    // class model.
                    .filter(new PipeFunction<Vertex, Boolean>()
                    {
                        public Boolean compute(Vertex argument)
                        {
                            Iterator<Edge> edges = argument.getEdges(Direction.IN, "javaClassFacet").iterator();
                            if (edges.hasNext())
                            {
                                edges.next();
                                if (edges.hasNext())
                                {
                                    return true;
                                }
                            }
                            // if there aren't two edges, return false.
                            return false;
                        }
                    });
        return context.getFramed().frameVertices(pipeline, JavaClassModel.class);
    }

    public boolean isJavaClass(ResourceModel resource)
    {
        return (new GremlinPipeline<Vertex, Vertex>(resource.asVertex())).out("javaClassFacet").iterator().hasNext();
    }

    public JavaClassModel getJavaClassFromResource(ResourceModel resource)
    {
        Iterator<Vertex> v = (new GremlinPipeline<Vertex, Vertex>(resource.asVertex())).out("javaClassFacet")
                    .iterator();
        if (v.hasNext())
        {
            return context.getFramed().frame(v.next(), JavaClassModel.class);
        }

        return null;
    }

    public void markAsBlacklistCandidate(JavaClassModel clz)
    {
        clz.asVertex().setProperty("blacklistCandidate", true);
    }

    public void markAsCustomerPackage(JavaClassModel clz)
    {
        clz.asVertex().setProperty("customerPackage", true);
    }

    public Iterable<JavaClassModel> findClassesWithSource()
    {
        // iterate through all vertices
        Iterable<Vertex> pipeline = new GremlinPipeline<Vertex, Vertex>(context
                    .getGraph().query().has("type", Text.CONTAINS, typeValueForSearch).vertices())

                    // check to see whether there is an edge coming in that links to the resource providing the java
                    // class model.
                    .filter(new PipeFunction<Vertex, Boolean>()
                    {
                        public Boolean compute(Vertex argument)
                        {
                            return argument.getEdges(Direction.OUT, "source").iterator().hasNext();
                        }
                    });
        return context.getFramed().frameVertices(pipeline, JavaClassModel.class);
    }

    public Iterable<JavaClassModel> findCandidateBlacklistClasses()
    {
        // for all java classes
        Iterable<Vertex> pipeline = new GremlinPipeline<Vertex, Vertex>(context
                    .getGraph().query().has("type", Text.CONTAINS, typeValueForSearch).vertices())
                    .as("clz").has("blacklistCandidate").back("clz").cast(Vertex.class);
        return context.getFramed().frameVertices(pipeline, JavaClassModel.class);
    }

    public Iterable<JavaClassModel> findClassesLeveragingCandidateBlacklists()
    {
        Iterable<Vertex> pipeline = new GremlinPipeline<Vertex, Vertex>(context
                    .getGraph().query().has("type", Text.CONTAINS, typeValueForSearch).vertices())
                    .has("blacklistCandidate")
                    .in("extends", "imports", "implements")
                    .dedup();
        return context.getFramed().frameVertices(pipeline, JavaClassModel.class);
    }

    public Iterable<JavaClassModel> findLeveragedCandidateBlacklists(JavaClassModel clz)
    {
        Set<JavaClassModel> results = new HashSet<JavaClassModel>();
        for (JavaClassModel javaClz : clz.dependsOnJavaClass())
        {
            if (javaClz.isBlacklistCandidate())
            {
                results.add(javaClz);
            }
        }

        return results;
    }

    public Iterable<JavaClassModel> findCustomerPackageClasses()
    {
        // for all java classes
        Iterable<Vertex> pipeline = new GremlinPipeline<Vertex, Vertex>(context
                    .getGraph().query().has("type", Text.CONTAINS, typeValueForSearch).vertices())
                    .has("customerPackage").V();
        return context.getFramed().frameVertices(pipeline, JavaClassModel.class);
    }
}
