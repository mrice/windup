package org.jboss.windup.engine.visitor.java;

public class Example {
	public static void main(String[] args) {
		for(String key : System.getenv().keySet()) {
			System.out.println(key+" -> "+System.getenv().get(key));
		}
	}

}
