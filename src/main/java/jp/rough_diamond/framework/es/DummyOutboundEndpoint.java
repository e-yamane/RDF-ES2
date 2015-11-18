package jp.rough_diamond.framework.es;

import java.io.Serializable;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.mule.api.MuleContext;
import org.mule.api.MuleEvent;
import org.mule.api.MuleMessage;
import org.mule.api.endpoint.EndpointURI;
import org.mule.api.endpoint.OutboundEndpoint;
import org.mule.api.lifecycle.InitialisationException;
import org.mule.api.retry.RetryPolicyTemplate;
import org.mule.api.routing.filter.Filter;
import org.mule.api.security.EndpointSecurityFilter;
import org.mule.api.transaction.TransactionConfig;
import org.mule.api.transport.Connector;
import org.mule.api.transport.DispatchException;

@SuppressWarnings("serial")
class DummyOutboundEndpoint implements OutboundEndpoint, Serializable {
	private EndpointURI uri;
	DummyOutboundEndpoint(String uri) {
		this.uri = new DummyEndpointURI(uri);
	}
	
	@Override
	public Connector getConnector() {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getEncoding() {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getEndpointBuilderName() {
		throw new UnsupportedOperationException();
	}

	@Override
	public EndpointURI getEndpointURI() {
		return uri;
	}

	@Override
	public Filter getFilter() {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getInitialState() {
		throw new UnsupportedOperationException();
	}

	@Override
	public MuleContext getMuleContext() {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getName() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Map getProperties() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Object getProperty(Object arg0) {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getProtocol() {
		throw new UnsupportedOperationException();
	}

	@Override
	public int getRemoteSyncTimeout() {
		throw new UnsupportedOperationException();
	}

	@Override
	public List getResponseTransformers() {
		throw new UnsupportedOperationException();
	}

	@Override
	public RetryPolicyTemplate getRetryPolicyTemplate() {
		throw new UnsupportedOperationException();
	}

	@Override
	public EndpointSecurityFilter getSecurityFilter() {
		throw new UnsupportedOperationException();
	}

	@Override
	public TransactionConfig getTransactionConfig() {
		throw new UnsupportedOperationException();
	}

	@Override
	public List getTransformers() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isDeleteUnacceptedMessages() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isReadOnly() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isRemoteSync() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isSynchronous() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void dispatch(MuleEvent arg0) throws DispatchException {
		throw new UnsupportedOperationException();
	}

	@Override
	public MuleMessage send(MuleEvent arg0) throws DispatchException {
		throw new UnsupportedOperationException();
	}
	
	static class DummyEndpointURI implements EndpointURI, Serializable {
		private String address;
		DummyEndpointURI(String address) {
			this.address = address;
		}
		
		@Override
		public void initialise() throws InitialisationException {
			System.out.println("initialise");
		}

		@Override
		public String getAddress() {
			return address;
		}

		@Override
		public String getAuthority() {
			throw new UnsupportedOperationException();
		}

		@Override
		public String getConnectorName() {
			return "DUMMY";
		}

		@Override
		public String getEndpointName() {
			return "DUMMY";
		}

		@Override
		public String getFilterAddress() {
			throw new UnsupportedOperationException();
		}

		@Override
		public String getFullScheme() {
			throw new UnsupportedOperationException();
		}

		@Override
		public String getHost() {
			throw new UnsupportedOperationException();
		}

		@Override
		public Properties getParams() {
			return new Properties();
		}

		@Override
		public String getPassword() {
			throw new UnsupportedOperationException();
		}

		@Override
		public String getPath() {
			throw new UnsupportedOperationException();
		}

		@Override
		public int getPort() {
			throw new UnsupportedOperationException();
		}

		@Override
		public String getQuery() {
			throw new UnsupportedOperationException();
		}

		@Override
		public String getResourceInfo() {
			throw new UnsupportedOperationException();
		}

		@Override
		public String getResponseTransformers() {
			return "DUMMY";
		}

		@Override
		public String getScheme() {
			throw new UnsupportedOperationException();
		}

		@Override
		public String getSchemeMetaInfo() {
			throw new UnsupportedOperationException();
		}

		@Override
		public String getTransformers() {
			return "DUMMY";
		}

		@Override
		public URI getUri() {
			throw new UnsupportedOperationException();
		}

		@Override
		public String getUser() {
			throw new UnsupportedOperationException();
		}

		@Override
		public String getUserInfo() {
			throw new UnsupportedOperationException();
		}

		@Override
		public Properties getUserParams() {
			throw new UnsupportedOperationException();
		}

		@Override
		public void setEndpointName(String arg0) {
			throw new UnsupportedOperationException();
		}
	}
}
