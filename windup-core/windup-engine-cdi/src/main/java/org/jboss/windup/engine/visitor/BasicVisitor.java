package org.jboss.windup.engine.visitor;

import java.io.File;

import javax.inject.Inject;

import org.jboss.windup.engine.visitor.base.EmptyGraphVisitor;
import org.jboss.windup.graph.dao.FileResourceDaoBean;

public class BasicVisitor extends EmptyGraphVisitor {

	@Inject
	private FileResourceDaoBean fileDao;
	
	@Override
	public void run() {
		File r1 = new File("//Users/bradsdavis/Windup/Presentation/jee-example-app-1.0.0.ear");
		org.jboss.windup.graph.model.resource.FileResource r1g = fileDao.getByFilePath(r1.getAbsolutePath());
		///Users/bradsdavis/Windup/Presentation/jee-example-app-1.0.0.ear
		
		//File r2 = new File("/Users/bradsdavis/Projects/migrations/inputs/WindupConfigurations.jar");
		//org.jboss.windup.graph.model.resource.FileResource r2g = fileDao.getByFilePath(r2.getAbsolutePath());
	}
}
