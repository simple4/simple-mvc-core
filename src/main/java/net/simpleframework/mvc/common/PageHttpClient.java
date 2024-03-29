package net.simpleframework.mvc.common;

import java.io.IOException;
import java.util.Map;

import net.simpleframework.common.StringUtils;
import net.simpleframework.common.web.HttpClient;
import net.simpleframework.mvc.AbstractMVCPage;

/**
 * 这是一个开源的软件，请在LGPLv3下合法使用、修改或重新发布。
 * 
 * @author 陈侃(cknet@126.com, 13910090885)
 *         http://code.google.com/p/simpleframework/
 *         http://www.simpleframework.net
 */
public class PageHttpClient {

	private final HttpClient httpClient;

	public PageHttpClient(final HttpClient httpClient) {
		this.httpClient = httpClient;
	}

	// AbstractMVCPage

	public Map<String, Object> get(final Class<? extends AbstractMVCPage> pageClass,
			final String method, final Map<String, Object> data) throws IOException {
		final String queryString = StringUtils.hasText(method) ? "method=" + method : null;
		return httpClient.get(AbstractMVCPage.uriFor(pageClass, queryString), data);
	}

	public Map<String, Object> get(final Class<? extends AbstractMVCPage> pageClass,
			final Map<String, Object> data) throws IOException {
		return get(pageClass, null, data);
	}

	public Map<String, Object> get(final Class<? extends AbstractMVCPage> pageClass,
			final String method) throws IOException {
		return get(pageClass, method, null);
	}

	public Map<String, Object> get(final Class<? extends AbstractMVCPage> pageClass)
			throws IOException {
		return get(pageClass, null, null);
	}

	public Map<String, Object> post(final Class<? extends AbstractMVCPage> pageClass,
			final String method, final Map<String, Object> data) throws IOException {
		final String queryString = StringUtils.hasText(method) ? "method=" + method : null;
		return httpClient.post(AbstractMVCPage.uriFor(pageClass, queryString), data);
	}
}
