package org.jboss.windup.graph.dao.impl;

import javax.inject.Singleton;

import org.jboss.windup.graph.dao.SpringBeanDao;
import org.jboss.windup.graph.model.meta.javaclass.SpringBeanFacetModel;

@Singleton
public class SpringBeanDaoImpl extends BaseDaoImpl<SpringBeanFacetModel> implements SpringBeanDao {

	public SpringBeanDaoImpl() {
		super(SpringBeanFacetModel.class);
	}
}
