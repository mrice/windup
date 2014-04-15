package org.jboss.windup.graph.dao;

import org.jboss.windup.graph.model.meta.report.SourceReport;
import org.jboss.windup.graph.model.resource.FileResource;
import org.jboss.windup.graph.model.resource.Resource;

import com.tinkerpop.blueprints.Direction;

public class SourceReportDao extends BaseDaoBean<SourceReport> {

	public SourceReportDao() {
		super(SourceReport.class);
	}
	
	
	public boolean hasSourceReport(Resource resource) {
		return resource.asVertex().getVertices(Direction.OUT, "sourceReport").iterator().hasNext();
	}
	
	public FileResource getResourceReport(Resource resource) {
		if(hasSourceReport(resource)) {
			SourceReport report = context.getFramed().frame(resource.asVertex().getVertices(Direction.OUT, "sourceReport").iterator().next(), SourceReport.class);
			return report.getReportFile();
		}
		return null;
	}
	
	

}
