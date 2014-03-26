package org.jboss.windup.engine.visitor.reporter.html.model;

import java.util.LinkedList;
import java.util.List;

public class EJBReport {

	private final List<EJBRow> statefulBeans = new LinkedList<>();
	private final List<EJBRow> statelessBeans = new LinkedList<>();
	private final List<MDBRow> mdbs = new LinkedList<>();
	
	public List<MDBRow> getMdbs() {
		return mdbs;
	}
	
	public List<EJBRow> getStatefulBeans() {
		return statefulBeans;
	}
	
	public List<EJBRow> getStatelessBeans() {
		return statelessBeans;
	}
	
	public static class EJBRow {
		private String name;
		private String qualifiedName;
		private String jndiName;
		
		public EJBRow() {

		}
		
		public EJBRow(String name, String qualifiedName, String jndiName) {
			this.name = name;
			this.qualifiedName = qualifiedName;
			this.jndiName = jndiName;
		}

		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public String getQualifiedName() {
			return qualifiedName;
		}
		public void setQualifiedName(String qualifiedName) {
			this.qualifiedName = qualifiedName;
		}
		public String getJndiName() {
			return jndiName;
		}
		public void setJndiName(String jndiName) {
			this.jndiName = jndiName;
		}
		
	}
	
	public static class MDBRow {

		private String name;
		private String qualifiedName;
		private String queueName;
		

		public MDBRow() {

		}
		
		public MDBRow(String name, String qualifiedName, String queueName) {
			this.name = name;
			this.qualifiedName = qualifiedName;
			this.queueName = queueName;
		}
		
		
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public String getQualifiedName() {
			return qualifiedName;
		}
		public void setQualifiedName(String qualifiedName) {
			this.qualifiedName = qualifiedName;
		}
		public String getQueueName() {
			return queueName;
		}
		public void setQueueName(String queueName) {
			this.queueName = queueName;
		}
	}
	
}
