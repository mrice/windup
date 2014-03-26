package org.jboss.windup.engine.visitor.reporter.html.model;

import java.util.LinkedList;
import java.util.List;

public class ApplicationReport {

	private String applicationName;
	private final List<ArchiveReport> archives = new LinkedList<>();
	
	public void setApplicationName(String applicationName) {
		this.applicationName = applicationName;
	}
	public String getApplicationName() {
		return applicationName;
	}
	public List<ArchiveReport> getArchives() {
		return archives;
	}
}
