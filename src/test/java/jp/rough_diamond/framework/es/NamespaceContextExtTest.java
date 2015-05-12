package jp.rough_diamond.framework.es;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.w3c.dom.Document;

public class NamespaceContextExtTest {
	@Test
	public void test() {
		Document doc = XMLUtil.getDocumentFromClassPath("mule/mule-common-config.xml");
		NamespaceContextExt nc = new NamespaceContextExt(doc);
		assertThat(nc.namespaceMap.size(), is(6));
	}
	
	@Test
	public void getNamespaceName() {
		Document doc = XMLUtil.getDocumentFromClassPath("mule/mule-common-config.xml");
		assertThat(NamespaceContextExt.getNamespaceName(doc, "xmlns:context"), is("context"));
		assertThat(NamespaceContextExt.getNamespaceName(doc, "xmlns"), is("mule"));
	}
	
	@Test
	public void getNamespaceURI() {
		Document doc = XMLUtil.getDocumentFromClassPath("mule/mule-common-config.xml");
		NamespaceContextExt nc = new NamespaceContextExt(doc);
		assertThat(nc.getNamespaceURI("context"), is("http://www.springframework.org/schema/context"));
	}
	
	@Test
	public void getPrefix() {
		Document doc = XMLUtil.getDocumentFromClassPath("mule/mule-common-config.xml");
		NamespaceContextExt nc = new NamespaceContextExt(doc);
		assertThat(nc.getPrefix("http://www.mulesource.org/schema/mule/cxf/2.1"), is("cxf"));
	}
}
