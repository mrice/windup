package org.jboss.windup.engine.provider;

import java.util.LinkedList;
import java.util.List;

import javax.enterprise.inject.Produces;
import javax.inject.Inject;

import org.jboss.windup.engine.WindupContext;
import org.jboss.windup.engine.qualifier.ListenerChainQualifier;
import org.jboss.windup.engine.visitor.ArchiveEntryIndexVisitor;
import org.jboss.windup.engine.visitor.ArchiveHashVisitor;
import org.jboss.windup.engine.visitor.ArchiveTypingVisitor;
import org.jboss.windup.engine.visitor.BasicVisitor;
import org.jboss.windup.engine.visitor.CustomerPackageVisitor;
import org.jboss.windup.engine.visitor.DebugVisitor;
import org.jboss.windup.engine.visitor.EjbConfigurationVisitor;
import org.jboss.windup.engine.visitor.HibernateConfigurationVisitor;
import org.jboss.windup.engine.visitor.HibernateMappingVisitor;
import org.jboss.windup.engine.visitor.JavaASTVisitor;
import org.jboss.windup.engine.visitor.BlacklistCandidateVisitor;
import org.jboss.windup.engine.visitor.JavaClassVisitor;
import org.jboss.windup.engine.visitor.JavaDecompilerVisitor;
import org.jboss.windup.engine.visitor.ManifestVisitor;
import org.jboss.windup.engine.visitor.MavenFacetVisitor;
import org.jboss.windup.engine.visitor.SpringConfigurationVisitor;
import org.jboss.windup.engine.visitor.WebConfigurationVisitor;
import org.jboss.windup.engine.visitor.XmlResourceVisitor;
import org.jboss.windup.engine.visitor.ZipArchiveGraphVisitor;
import org.jboss.windup.engine.visitor.base.GraphVisitor;
import org.jboss.windup.engine.visitor.reporter.ArchiveDependsOnReporter;
import org.jboss.windup.engine.visitor.reporter.ArchiveProvidesReporter;
import org.jboss.windup.engine.visitor.reporter.ArchiveTransitiveDependsOnReporter;
import org.jboss.windup.engine.visitor.reporter.ClassNotFoundReporter;
import org.jboss.windup.engine.visitor.reporter.DuplicateClassReporter;
import org.jboss.windup.engine.visitor.reporter.EjbConfigurationReporter;
import org.jboss.windup.engine.visitor.reporter.GraphRenderReporter;
import org.jboss.windup.engine.visitor.reporter.HibernateConfigurationReporter;
import org.jboss.windup.engine.visitor.reporter.HibernateEntityReporter;
import org.jboss.windup.engine.visitor.reporter.JarManifestReporter;
import org.jboss.windup.engine.visitor.reporter.MavenPomReporter;
import org.jboss.windup.engine.visitor.reporter.NamespacesFoundReporter;
import org.jboss.windup.engine.visitor.reporter.WriteGraphToDotReporter;
import org.jboss.windup.engine.visitor.reporter.WriteGraphToGraphMLReporter;
import org.jboss.windup.engine.visitor.reporter.html.renderer.ApplicationReportRenderer;
import org.jboss.windup.engine.visitor.reporter.html.renderer.CssJsResourceRenderer;
import org.jboss.windup.engine.visitor.reporter.html.renderer.EJBReportRenderer;
import org.jboss.windup.engine.visitor.reporter.html.renderer.HibernateReportRenderer;
import org.jboss.windup.engine.visitor.reporter.html.renderer.OverviewReportRenderer;
import org.jboss.windup.engine.visitor.reporter.html.renderer.ServerResourceReportRenderer;
import org.jboss.windup.engine.visitor.reporter.html.renderer.SpringReportRenderer;
import org.jboss.windup.graph.model.meta.EnvironmentReference;

public class ListenerChainProvider {

	@Inject
	private WindupContext context;
	
	@Inject
	private BasicVisitor basic;
	
	@Inject
	private ZipArchiveGraphVisitor zipArchive;
	
	@Inject
	private ArchiveTypingVisitor archiveTypeVisitor;
	
	@Inject
	private ArchiveEntryIndexVisitor archiveEntryIndexingVisitor;
	
	@Inject
	private JavaClassVisitor javaClassVisitor;
	
	@Inject
	private JavaASTVisitor javaAstVisitor;
	
	@Inject
	private JavaDecompilerVisitor javaDecompilerVisitor;
	
	@Inject
	private CustomerPackageVisitor customerPackageVisitor;
	
	@Inject
	private BlacklistCandidateVisitor blacklistClassVisitor;
	
	@Inject
	private WebConfigurationVisitor webConfigurationVisitor;
	
	@Inject
	private XmlResourceVisitor xmlResourceVisitor;

	@Inject
	private MavenFacetVisitor mavenFacetVisitor;
	
	@Inject
	private SpringConfigurationVisitor springConfigurationVisitor;
	
	@Inject
	private ClassNotFoundReporter classNotFoundReporter;
	
	@Inject
	private DuplicateClassReporter duplicateClassReporter;
	
	@Inject
	private ArchiveProvidesReporter archiveProvidesReporter;
	
	@Inject
	private NamespacesFoundReporter namespacesFoundReporter;
	
	@Inject
	private GraphRenderReporter graphRenderReporter;
	
	@Inject
	private ArchiveDependsOnReporter archiveDependsOnReport;
	
	@Inject
	private HibernateConfigurationVisitor hibernateConfigurationVisitor;
	
	@Inject
	private HibernateMappingVisitor hibernateMappingVisitor;
	
	@Inject
	private EjbConfigurationVisitor ejbConfigurationVisitor;
	
	
	@Inject
	private ArchiveHashVisitor archiveHashVisitor;
	
	@Inject
	private WriteGraphToGraphMLReporter exportToMLreporter;
	
	@Inject
	private WriteGraphToDotReporter exportToDotReporter;
	
	
	@Inject
	private MavenPomReporter mavenPomReporter;
	
	@Inject
	private ManifestVisitor manifestVisitor;
	
	@Inject
	private JarManifestReporter manifestReporter;
	
	@Inject
	private HibernateConfigurationReporter hibernateConfigurationReporter;
	
	@Inject
	private HibernateEntityReporter hibernateEntityReporter;
	
	@Inject
	private EjbConfigurationReporter ejbConfigurationReporter;
	
	@Inject
	private ArchiveTransitiveDependsOnReporter archiveTransitiveReporter;
	
	//
	// Report Renderers
	//
	@Inject
	private CssJsResourceRenderer resourceRenderer;
	
	@Inject
	private OverviewReportRenderer overviewRenderer;
	
	@Inject
	private ApplicationReportRenderer appReportRenderer;
	
	@Inject
	private EJBReportRenderer ejbRenderer;
	
	@Inject
	private HibernateReportRenderer hibernateRenderer;
	
	@Inject
	private SpringReportRenderer springRenderer;
	
	@Inject
	private ServerResourceReportRenderer serverResourceRenderer;
	
	
	
	@ListenerChainQualifier
	@Produces
	public List<GraphVisitor> produceListenerChain() {
		List<GraphVisitor> listenerChain = new LinkedList<GraphVisitor>();
		listenerChain.add(basic);
		listenerChain.add(zipArchive); //recurses zip entries to expand
		listenerChain.add(archiveEntryIndexingVisitor); //indexes all entries to the graph
		listenerChain.add(archiveHashVisitor);
		listenerChain.add(archiveTypeVisitor);  //sets the archive to a sub-type
		listenerChain.add(manifestVisitor); //extracts manifest data.
		
		listenerChain.add(javaClassVisitor); //loads java class information (imports / extends) to the graph
		listenerChain.add(customerPackageVisitor);
		
		listenerChain.add(blacklistClassVisitor); //walks the source for Java Syntax Tree
		listenerChain.add(javaDecompilerVisitor); //looks for classes without source, and decompiles the class.
		listenerChain.add(javaAstVisitor); //walks the source for Java Syntax Tree
		
		
		listenerChain.add(xmlResourceVisitor); //loads xml resource information to the graph
		
		listenerChain.add(webConfigurationVisitor);
		//listenerChain.add(archiveTransitiveReporter);
		listenerChain.add(ejbConfigurationVisitor);
		//listenerChain.add(ejbConfigurationReporter);
		
		//listenerChain.add(hibernateConfigurationVisitor); //loads hibernate configurations and processes
		//listenerChain.add(hibernateConfigurationReporter); //reports on hibernate configurations found
		
		//listenerChain.add(hibernateMappingVisitor); //loads hibernate entity mappings and processes
		//listenerChain.add(hibernateEntityReporter);
		
		
		
		//listenerChain.add(manifestReporter); //reports on hibernate configurations found
		
	//	listenerChain.add(new DebugVisitor(context, NamespaceMeta.class)); //extract Maven information to facet.
	//	listenerChain.add(new DebugVisitor(context, DoctypeMeta.class)); //extract Maven information to facet.
		
		listenerChain.add(new DebugVisitor(context, EnvironmentReference.class)); //extract Maven information to facet.
		
		
		
		//listenerChain.add(mavenFacetVisitor); //extract Maven information to facet.
		//listenerChain.add(springConfigurationVisitor);
		//listenerChain.add(new DebugVisitor(context, SpringConfigurationFacet.class)); //extract Maven information to facet.

		//listenerChain.add(archiveDependsOnReport);
		//listenerChain.add(exportToMLreporter);
		//listenerChain.add(mavenPomReporter);
		//listenerChain.add(duplicateClassReporter); //reports all classes found multiple times on the classpath.
		//listenerChain.add(classNotFoundReporter); //reports all classes not found on the classpath.
		listenerChain.add(graphRenderReporter);
		listenerChain.add(namespacesFoundReporter);
		listenerChain.add(exportToDotReporter);
		
		listenerChain.add(resourceRenderer);
		listenerChain.add(overviewRenderer);
		listenerChain.add(appReportRenderer);
		listenerChain.add(ejbRenderer);
		listenerChain.add(hibernateRenderer);
		listenerChain.add(springRenderer);
		listenerChain.add(serverResourceRenderer);

		return listenerChain;
	}
}
