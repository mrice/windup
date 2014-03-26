package org.jboss.windup.engine.visitor.reporter.html.model;

import java.util.LinkedList;
import java.util.List;


public class SourceReport {

	private String sourceName;
	private String sourceBody;
	
	private final List<SourceLineAnnotations> sourceLineAnnotations = new LinkedList<>();

	public String getSourceName() {
		return sourceName;
	}

	public void setSourceName(String sourceName) {
		this.sourceName = sourceName;
	}

	public String getSourceBody() {
		return sourceBody;
	}

	public void setSourceBody(String sourceBody) {
		this.sourceBody = sourceBody;
	}

	public List<SourceLineAnnotations> getSourceLineAnnotations() {
		return sourceLineAnnotations;
	}
	
	
	public static class SourceLineAnnotations {

		private int lineNumber;
		private String annotationTitle;
		private String annotationBody;
		
		public int getLineNumber() {
			return lineNumber;
		}
		public void setLineNumber(int lineNumber) {
			this.lineNumber = lineNumber;
		}
		public String getAnnotationTitle() {
			return annotationTitle;
		}
		public void setAnnotationTitle(String annotationTitle) {
			this.annotationTitle = annotationTitle;
		}
		public String getAnnotationBody() {
			return annotationBody;
		}
		public void setAnnotationBody(String annotationBody) {
			this.annotationBody = annotationBody;
		}
		
		
	}

}
