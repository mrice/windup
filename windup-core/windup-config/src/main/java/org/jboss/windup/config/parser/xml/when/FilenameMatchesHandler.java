package org.jboss.windup.config.parser.xml.when;

import static org.joox.JOOX.$;

import org.jboss.windup.config.condition.FilenameMatchesCondition;
import org.jboss.windup.config.parser.ElementHandler;
import org.jboss.windup.config.parser.NamespaceElementHandler;
import org.jboss.windup.config.parser.ParserContext;
import org.w3c.dom.Element;

@NamespaceElementHandler(elementName = "filename", namespace = "http://windup.jboss.org/v1/xml")
public class FilenameMatchesHandler implements ElementHandler<FilenameMatchesCondition>
{
   @Override
   public FilenameMatchesCondition processElement(ParserContext handlerManager, Element element)
   {
	   	return null;
   }

}
