package jp.rough_diamond.framework.es;

import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;

class XMLUtil {
	public static Document getDocumentFromClassPath(String resourceName) {
		return getDocumentFromClassPath(resourceName, true);
	}
	
	
	public static Document getDocumentFromClassPath(String resourceName, boolean namespaceAware) {
		return getDocumentFromStream(ResourceUtil.getStream(resourceName), namespaceAware);
	}


	public static Document getDocumentFromStream(InputStream is, boolean namespaceAware) {
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			dbf.setNamespaceAware(namespaceAware);
			DocumentBuilder builder = dbf.newDocumentBuilder();
			Document ret = builder.parse(is);
			return ret;
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
	}
}
