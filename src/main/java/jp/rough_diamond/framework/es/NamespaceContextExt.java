package jp.rough_diamond.framework.es;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.xml.namespace.NamespaceContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;

public class NamespaceContextExt implements NamespaceContext {
	private final static Log log = LogFactory.getLog(NamespaceContextExt.class);
	
	final Map<String, String> namespaceMap = new HashMap<>();
	public NamespaceContextExt(Document base) {
		NamedNodeMap map = base.getDocumentElement().getAttributes();
		for(int i = 0 ; i < map.getLength() ; i++) {
			String attrName = map.item(i).getNodeName();
			if(!attrName.startsWith("xmlns")) {
				continue;
			}
			String namespaceName = getNamespaceName(base, attrName);
			namespaceMap.put(namespaceName, map.item(i).getNodeValue());
			log.debug(map.item(i).getNodeName() + "#" + map.item(i).getNodeValue());
		}
	}
	
	static String getNamespaceName(Document doc, String attrName) {
		String[] split = attrName.split(":");
		return (split.length > 1) ? split[1] : doc.getDocumentElement().getNodeName();
	}

	@Override
	public String getNamespaceURI(String prefix) {
		log.debug("getNamespaceURI:" + prefix);
		if(namespaceMap.containsKey(prefix)) {
			return namespaceMap.get(prefix);
		} else {
			throw new IllegalArgumentException();
		}
	}

	@Override
	public String getPrefix(String namespaceURI) {
		Iterator<String> prefixes = getPrefixes(namespaceURI);
		if(prefixes.hasNext()) {
			return prefixes.next();
		} else {
			throw new IllegalArgumentException();
		}
	}

	@Override
	public Iterator<String> getPrefixes(final String namespaceURI) {
		return namespaceMap.entrySet().stream(
				).filter(e -> e.getValue().equals(namespaceURI)
				).map(e -> e.getKey()).iterator();
	}
}
