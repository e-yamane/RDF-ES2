package jp.rough_diamond.framework.es;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.Arrays;
import java.util.stream.Stream;

import jp.rough_diamond.commons.di.DIContainerFactory;
import jp.rough_diamond.sample.esb.service.Request;
import jp.rough_diamond.sample.esb.service.Response;
import jp.rough_diamond.sample.esb.service.ResponseRecord;
import jp.rough_diamond.sample.esb.service.SampleService;
import jp.rough_diamond.sample.esb.service.SampleService2;

import org.junit.Before;
import org.junit.Test;

public class ServiceBus2Test {
	private ServiceBus2 target;
	
	@Before
	public void setupDI() {
		target = (ServiceBus2)DIContainerFactory.getDIContainer().getObject("serviceBus");
		assertThat("DI設定が誤っています。", target.getClass().getName(), is(ServiceBus2.class.getName()));
		System.out.println(target.config);
	}
	
	@Test
	public void configs() {
		Stream<String> ret = ServiceBus2.configs("abc def\tghi　jkl\nmno\rpqr");
		assertThat(ret.count(), is(6L));
		ret = target.configs();
		ret.forEach(System.out::println);
		
	}

	@Test
	public void makeConfig() {
		target.makeConfig();
		assertThat("プレースホルダ数が誤っています。", target.placeHolder.size(), is(5));
		assertThat("サーバ設定数が誤っています。", target.servers.size(), is(2));
		assertThat("endpointがきちんと差し替わってます。", target.servers.get(0).endpoint, is("http://localhost:10080/services/SampleService"));
		assertThat("endpointがきちんと差し替わってます。", target.servers.get(1).endpoint, is("http://localhost:10080/services/SampleService2"));
		assertThat("クライアント設定数が誤っています。", target.clients.size(), is(3));
	}

	public static void main(String[] args) throws Exception {
		ServiceBus2Test test = new ServiceBus2Test();
		test.setupDI();
		test.target.init();
		try {
			ServiceFinder2 finder2 = new ServiceFinder2();
			SampleService service = finder2.getService(SampleService.class, SampleService.class);
			service.foo();
			System.out.println(service.sayHello("Eiji Yamane."));
			SampleService2 service2 = finder2.getService(SampleService2.class, SampleService2.class);
			Request req = new Request();
			req.setName("山根英次");
			req.setScore(Arrays.asList(90, 80, 75));
			Request req2 = new Request();
			req2.setName("安藤");
			req2.setScore(Arrays.asList(30, 45, 27));
			Response ret = service2.calculate(Arrays.asList(req, req2));
			System.out.println(ret.getRecords().size());
			for(ResponseRecord res : ret.getRecords()) {
				System.out.printf("%s:%d\n", res.getName(), res.getTotal());
			}
			try {
				test.target.addProperty(DynamicEndPointRouter.ENDPOINT_KEY, "http://localhost:12345/SampleService2");
				service.foo();
				System.out.println("例外が発生してません。");
			} catch(Exception e) {
				e.printStackTrace(System.out);
			}
			System.in.read();
		} finally {
			test.target.dispose();
		}
	}
}
