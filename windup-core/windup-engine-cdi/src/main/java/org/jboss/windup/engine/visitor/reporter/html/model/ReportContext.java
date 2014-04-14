package org.jboss.windup.engine.visitor.reporter.html.model;

import java.io.File;

import org.apache.commons.lang.StringUtils;

public class ReportContext {
	private final String relativeTo;
	private final String relativeFrom;
	
	
	public ReportContext(File base, File report) {
		String relativeTo = base.toURI().relativize(report.toURI()).getPath();
		String relativeFrom = report.toURI().relativize(base.toURI()).getPath();
		
		if(StringUtils.equals(relativeTo, "/")) {
			relativeTo = "";
		}
		if(StringUtils.equals(relativeFrom, "/")) {
			relativeFrom = "";
		}
		
		this.relativeTo = relativeTo;
		this.relativeFrom = relativeFrom;
	}
	
	public String getRelativeFrom() {
		return relativeFrom;
	}
	
	public String getRelativeTo() {
		return relativeTo;
	}
}
