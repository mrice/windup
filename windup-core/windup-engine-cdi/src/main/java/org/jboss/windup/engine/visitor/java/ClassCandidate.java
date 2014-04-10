package org.jboss.windup.engine.visitor.java;

public class ClassCandidate {

	private final int lineNumber;
	private final String qualifiedName;
	
	public ClassCandidate(int lineNumber, String qualifiedName) {
		this.lineNumber = lineNumber;
		this.qualifiedName = qualifiedName;
	}

	public int getLineNumber() {
		return lineNumber;
	}

	public String getQualifiedName() {
		return qualifiedName;
	}

	@Override
	public String toString() {
		return "ClassCandidate [lineNumber=" + lineNumber + ", qualifiedName="
				+ qualifiedName + "]";
	}
	
	
}
