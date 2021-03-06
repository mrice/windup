package org.jboss.windup.graph.dao.impl;

import javax.inject.Singleton;

import org.jboss.windup.graph.dao.MessageDrivenDao;
import org.jboss.windup.graph.model.meta.javaclass.MessageDrivenBeanFacetModel;

@Singleton
public class MessageDrivenDaoImpl extends BaseDaoImpl<MessageDrivenBeanFacetModel> implements MessageDrivenDao {
	public MessageDrivenDaoImpl() {
		super(MessageDrivenBeanFacetModel.class);
	}
}
