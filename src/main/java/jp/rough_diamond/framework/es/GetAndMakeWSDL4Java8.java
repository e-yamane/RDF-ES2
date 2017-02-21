package jp.rough_diamond.framework.es;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.ServletOutputStream;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;

import jp.rough_diamond.commons.di.DIContainer;
import jp.rough_diamond.commons.di.DIContainerFactory;
import jp.rough_diamond.commons.di.SpringFramework;

public class GetAndMakeWSDL4Java8 {
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void main(String[] args) throws Exception {
		try {
			System.out.println("GetAndMakeWSDL4Java8!!!");
			System.out.println(args.length);
			String wsdlStorageDir = (args.length == 0) ? "etc/wsdl" : args[0];
			System.out.println(wsdlStorageDir);
			Class cl = (args.length < 2) ? SpringFramework.class : Class.forName(args[1]);
			System.out.println(cl.getName());
			String config = (args.length < 3) ? null : args[2];
			DIContainer di;
			if(config == null) {
				di = (DIContainer)cl.newInstance();
			} else {
				di = (DIContainer)cl.getConstructor(String.class).newInstance(config);
			}
			DIContainerFactory.setDIContainer(di);
			ServiceBus2.getInstance();
			try {
				GetAndMakeWSDL4Java8 t = new GetAndMakeWSDL4Java8(new File(wsdlStorageDir));
				t.execute();
			} finally {
				ServiceBus2.getInstance().dispose();
			}
		} catch(Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	private File wsdlStorageDir;

	public GetAndMakeWSDL4Java8(File wsdlStorageDir) {
		this.wsdlStorageDir = wsdlStorageDir;
	}

	private void execute() throws Exception {
		Set<String> urlSet = getUrlSet();
		HttpClient client = new HttpClient();
		System.out.println("XXXXX:" + urlSet);
		for(String url : urlSet) {
			System.out.println(url);
			GetMethod method = new GetMethod(url + "?wsdl");
			String wsdlName = getWsdlName(url);
			int status = client.executeMethod(method);
			System.out.println(status + ":" + HttpStatus.SC_OK);
			if(status == HttpStatus.SC_OK) {
                saveWSDL(wsdlName, method.getResponseBody());
			}
		}
	}

	private void saveWSDL(String wsdlName, byte[] responseBody) throws Exception {
		File f = new File(wsdlStorageDir, wsdlName);
		BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(f));
		IOException ioe = null;
		try {
			//なぜかnamespaceがxsdとなるところがxsになっていているので強制保管
			responseBody = new String(responseBody).replaceAll("xs:", "xsd:").getBytes();
			bos.write(responseBody);
		} catch(IOException e) {
			ioe = e;
		} finally {
			try {
				bos.close();
			} catch(IOException e) {
				if(ioe == null) {
					throw e;
				} else {
					e.printStackTrace();
					throw ioe;
				}
			}
		}
	}

	private String getWsdlName(String url) {
		String[] split = url.split("/");
		return split[split.length - 1] + ".wsdl";
	}

	private Set<String> getUrlSet() {
		System.out.println(ServiceBus2.getInstance().getClass().getName());
		return ServiceBus2.getInstance().getServerEndPoints();
	}
}
