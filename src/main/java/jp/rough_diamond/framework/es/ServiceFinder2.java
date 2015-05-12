package jp.rough_diamond.framework.es;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import jp.rough_diamond.framework.service.Service;
import jp.rough_diamond.framework.service.ServiceFinder;

import org.apache.cxf.frontend.ClientProxyFactoryBean;

public class ServiceFinder2 implements ServiceFinder {

	@Override
	public <T extends Service> T getService(Class<T> cl, Class<? extends T> defaultClass) {
		if(!isTarget(defaultClass)) {
			return null;
		}
		@SuppressWarnings("unchecked")
		T ret = (T)Proxy.newProxyInstance(defaultClass.getClassLoader(), new Class[]{defaultClass}, getInvocationHandler(defaultClass));
		return ret;
	}

	private InvocationHandler getInvocationHandler(final Class<?> proxyType) {
		return new InvocationHandler() {
			@Override
			public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
//				System.out.println(method.getName());
				ServiceConnecter sc = method.getAnnotation(ServiceConnecter.class);
				String serviceName = sc.serviceName();
//				System.out.println(serviceName);
				ServiceBus2 bus = ServiceBus2.getInstance();
				String endpoint = bus.getEndpoint(serviceName);
				
//				System.out.println(proxyType.getName());
				
				ClientProxyFactoryBean factory = new ClientProxyFactoryBean();
				factory.setServiceClass(proxyType);
				factory.setAddress(endpoint);
//				factory.getInInterceptors().add(new LoggingInInterceptor());
//				factory.getOutInterceptors().add(new LoggingOutInterceptor());
				return method.invoke(factory.create(), args);
			}
		};
	}

	static <T extends Service> boolean isTarget(Class<T> cl) {
		if(EnterpriseService.class.isAssignableFrom(cl) && cl.isInterface()) {
			return true;
		}
		return false;
	}
}
