package org.jboss.windup.engine.visitor.reporter.html.model;

import java.util.LinkedList;
import java.util.List;

public class ArchiveReport {

	private String applicationPath;
	private Level level = Level.PRIMARY;
	
	private final List<ResourceReportRow> resources = new LinkedList<>();

	public String getApplicationPath() {
		return applicationPath;
	}

	public void setApplicationPath(String applicationPath) {
		this.applicationPath = applicationPath;
	}

	public Level getLevel() {
		return level;
	}

	public void setLevel(Level level) {
		this.level = level;
	}

	public List<ResourceReportRow> getResources() {
		return resources;
	}


	public static class ResourceReportRow {
	
		private Name resourceName;
		
		private final List<Tag> technologyTags = new LinkedList<>();
		private Effort effort;
		private final List<Tag> issueTags = new LinkedList<>();
		
		public Name getResourceName() {
			return resourceName;
		}
		public void setResourceName(Name resourceName) {
			this.resourceName = resourceName;
		}
		public List<Tag> getTechnologyTags() {
			return technologyTags;
		}
		public Effort getEffort() {
			return effort;
		}
		public void setEffort(Effort effort) {
			this.effort = effort;
		}
		public List<Tag> getIssueTags() {
			return issueTags;
		}
		
	}
}
