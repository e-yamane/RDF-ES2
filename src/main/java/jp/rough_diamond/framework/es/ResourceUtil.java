package jp.rough_diamond.framework.es;

import java.io.InputStream;

class ResourceUtil {
	public static InputStream getStream(String resourceName) {
		resourceName = resourceName.startsWith("/") ? resourceName : "/" + resourceName;
		return ResourceUtil.class.getResourceAsStream(resourceName);
	}
}
