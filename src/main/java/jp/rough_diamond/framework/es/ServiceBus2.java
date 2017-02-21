package jp.rough_diamond.framework.es;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import jp.rough_diamond.commons.di.DIContainerFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.cxf.endpoint.Server;
import org.apache.cxf.frontend.ServerFactoryBean;
import org.apache.cxf.transport.http_jetty.JettyHTTPDestination;
import org.apache.cxf.transport.http_jetty.JettyHTTPServerEngine;
import org.mule.api.MuleMessage;
import org.mule.api.routing.CouldNotRouteOutboundMessageException;
import org.mule.routing.outbound.ChainingRouter;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class ServiceBus2 extends ServiceBus {
	private final static Log log = LogFactory.getLog(ServiceBus2.class);
	Map<String, String> placeHolder = new HashMap<>();
	List<ServerConfig> servers = new ArrayList<>();
	List<ClientConfig> clients = new ArrayList<>();
	private boolean isInit = false;

	@Override
	synchronized public void init() {
		if(!isInit) {
			makeConfig();
			publish();
			isInit = true;
		}
	}

	public void dispose() {
		servers.forEach(config -> {
			Server server = config.server;
			server.stop();
			JettyHTTPDestination dest = (JettyHTTPDestination)server.getDestination();
			JettyHTTPServerEngine engine = (JettyHTTPServerEngine)dest.getEngine();
			engine.shutdown();
		});
	}

	Set<String> getServerEndPoints() {
		return servers.stream().map(config -> config.endpoint).collect(Collectors.toSet());
	}

	String getEndpoint(String serviceName, final MuleMessage msg) {
//		Map<String, Object> params = ServiceBus.getInstance().popParameters();
//		if(params.containsKey(DynamicEndPointRouter.ENDPOINT_KEY)) {
//			return (String)params.get(DynamicEndPointRouter.ENDPOINT_KEY);
//		} else {
			return clients.stream().filter(config -> config.serviceName.equals(serviceName)
									).map(config -> getEndpoint(config, msg)).findFirst().get();
//		}
	}

	private String getEndpoint(ClientConfig config, MuleMessage msg) {
		try {
			if(config.router != null) {
				return config.router.getEndpoint(0, msg).getEndpointURI().getAddress();
			} else {
				return config.defaultEndpoint;
			}
		} catch (CouldNotRouteOutboundMessageException e) {
			throw new RuntimeException(e);
		}
	}

	void publish() {
		servers.forEach(config -> {
			try {
				ServerFactoryBean factory = new ServerFactoryBean();
				factory.setServiceClass(config.serviceInterface);
				factory.setServiceBean(config.serviceImpl.newInstance());
				factory.setAddress(config.endpoint);
//				factory.getInInterceptors().add(new LoggingInInterceptor());
//				factory.getOutInterceptors().add(new LoggingOutInterceptor());
				config.server = factory.create();
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		});
	}

	void makeConfig() {
		placeHolder = new HashMap<>();
		servers = new ArrayList<>();
		clients = new ArrayList<>();
		configs().forEach(this::makeConfig);
	}

	void makeConfig(String resourceName) {
		try {
			log.debug(resourceName);
			Document doc = XMLUtil.getDocumentFromClassPath(resourceName);
			XPath xpath = XPathFactory.newInstance().newXPath();
			xpath.setNamespaceContext(new NamespaceContextExt(doc));
//			System.out.println(doc.getDocumentElement().getNamespaceURI());
//			System.out.println(doc.getDocumentElement().getPrefix());
//			System.out.println(doc.getDocumentElement().getTagName());
//			System.out.println(doc.getDocumentElement().getNodeName());

			makePlaceHolder(xpath, doc);
			serverConfig(xpath, doc);
			clientConfig(xpath, doc);
		} catch(XPathExpressionException | IOException | ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	private void clientConfig(XPath xpath, Document doc) throws XPathExpressionException {
		if(!hasNamespacePrefix(xpath, "cxf")) {
			return;
		}
		NodeList list = (NodeList)xpath.evaluate("//cxf:outbound-endpoint", doc, XPathConstants.NODESET);
//		System.out.println(list.getLength());
		for(int i = 0 ; i < list.getLength() ; i++) {
			Element el = (Element)list.item(i);
			ClientConfig config = new ClientConfig();
			config.defaultEndpoint = replace(el.getAttribute("address"));
			config.mtomEnabled = Boolean.valueOf(replace(el.getAttribute("mtomEnabled")));
			Element serviceTag = (Element)el.getParentNode().getParentNode().getParentNode();
			config.serviceName = replace(serviceTag.getAttribute("name"));
			Node parent = el.getParentNode();
			if("custom-outbound-router".equals(parent.getNodeName())) {
				config.router = makeRouter(parent.getAttributes().getNamedItem("class").getNodeValue(), config.defaultEndpoint);
			}
			clients.add(config);
		}
	}

	private ChainingRouter makeRouter(String clsName, String address) {
		try {
			ChainingRouter router = (ChainingRouter) Class.forName(clsName).newInstance();
			router.addEndpoint(new DummyOutboundEndpoint(address));
			return router;
		} catch (Exception e) {
			throw new RuntimeException("OutboundRouterの生成に失敗しました：" + clsName, e);
		}
	}

	private void serverConfig(XPath xpath, Document doc) throws XPathExpressionException, IOException, ClassNotFoundException {
		if(!hasNamespacePrefix(xpath, "cxf")) {
			return;
		}
		NodeList list = (NodeList)xpath.evaluate("//cxf:inbound-endpoint", doc, XPathConstants.NODESET);
		for(int i = 0 ; i < list.getLength() ; i++) {
			Element el = (Element)list.item(i);
			ServerConfig config = new ServerConfig();
			config.endpoint = replace(el.getAttribute("address"));
			config.mtomEnabled = Boolean.valueOf(replace(el.getAttribute("mtomEnabled")));
			config.serviceInterface = Class.forName(replace(el.getAttribute("serviceClass")));
//			System.out.println(el.getParentNode().getParentNode().getNamespaceURI());
			Element component = (Element)xpath.evaluate("mule:component", el.getParentNode().getParentNode(), XPathConstants.NODE);
			config.serviceImpl = Class.forName(replace(component.getAttribute("class")));
			servers.add(config);
		}
//		System.out.println(list.getLength());
	}

	private boolean hasNamespacePrefix(XPath xpath, String prefix) {
		try {
			xpath.getNamespaceContext().getNamespaceURI(prefix);
			return true;
		} catch(IllegalArgumentException e) {
			return false;
		}
	}

	private String replace(String value) {
		for(String key :placeHolder.keySet()) {
			value = value.replaceAll("\\$\\{" + key + "\\}", placeHolder.get(key));
		}
		return value;
	}

	private void makePlaceHolder(XPath xpath, Document doc) throws XPathExpressionException, IOException {
		NodeList list = (NodeList)xpath.evaluate("//context:property-placeholder", doc, XPathConstants.NODESET);
		for(int i = 0 ; i < list.getLength() ; i++) {
			Element el = (Element)list.item(i);
//			System.out.println(node.getNamespaceURI());
//			System.out.println(node.getPrefix());
//			System.out.println(node.getTagName());
//			System.out.println(node.getNodeName());
			String location = el.getAttribute("location");
//			System.out.println(location);
			Properties prop = new Properties();
			prop.load(ResourceUtil.getStream(location));
			prop.forEach((key, value) -> {
				String keyText = (String)key;
				if(!placeHolder.containsKey(keyText)) {
					placeHolder.put((String)key, (String)value);
				}
			});
		}
//		System.out.println(list.getLength());
	}

	Stream<String> configs() {
		return configs(config);
	}

	static Stream<String> configs(String config) {
		return Stream.<String>of(config.split("\\s|　"));
	}

	static class ServerConfig {
		Server server;
		String endpoint;
		boolean mtomEnabled;
		Class<?> serviceInterface;
		Class<?> serviceImpl;
	}

	static class ClientConfig {
		String serviceName;
		String defaultEndpoint;
		boolean mtomEnabled;
		ChainingRouter router;
	}

	//Injection Value
	String config;
	public void setConfig(String config) {
		this.config = config;
	}

	public static ServiceBus2 getInstance() {
		ServiceBus2 bus = (ServiceBus2)DIContainerFactory.getDIContainer().getObject("serviceBus");
		bus.init();
		return bus;
	}
}
