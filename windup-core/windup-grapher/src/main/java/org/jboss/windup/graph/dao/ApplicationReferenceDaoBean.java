package org.jboss.windup.graph.dao;

import org.jboss.windup.graph.model.meta.ApplicationReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ApplicationReferenceDaoBean extends BaseDaoBean<ApplicationReference> {

	private static Logger LOG = LoggerFactory.getLogger(ApplicationReferenceDaoBean.class);
	
	public ApplicationReferenceDaoBean() {
		super(ApplicationReference.class);
	}
}
