package org.springframework.social.config.xml;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.NamespaceHandler;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class SocialNamespaceHandler implements NamespaceHandler {

    private final Map<String, BeanDefinitionParser> parsers = new HashMap<String, BeanDefinitionParser>();

	public void init() {
		loadParsers();
	}

	public BeanDefinition parse(Element element, ParserContext parserContext) {
		String name = parserContext.getDelegate().getLocalName(element);
        BeanDefinitionParser parser = parsers.get(name);
        if (parser == null) {
            loadParsers();
        }
        
        if(parser == null) {
        	// if missing classes from other modules (Facebook, Web, etc) report that separately    
        	// their parsers should be supplied by the individual modules
        	reportUnsupportedNodeType(name, parserContext, element);
        	return null;
        }
        
        return parser.parse(element, parserContext);
	}

	public BeanDefinitionHolder decorate(Node source, BeanDefinitionHolder definition, ParserContext parserContext) {
		return definition;
	}
	
    private void reportUnsupportedNodeType(String name, ParserContext parserContext, Node node) {
        parserContext.getReaderContext().fatal("Social namespace does not support decoration of " +
                (node instanceof Element ? "element" : "attribute") + " [" + name + "]", node);
    }

	private void loadParsers() {
		parsers.put("config", new SocialConfigBeanDefinitionParser());
	}

}
