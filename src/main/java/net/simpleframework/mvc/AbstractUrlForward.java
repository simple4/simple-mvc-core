package net.simpleframework.mvc;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import net.simpleframework.common.Convert;
import net.simpleframework.common.ObjectEx;
import net.simpleframework.common.SimpleRuntimeException;
import net.simpleframework.common.StringUtils;
import net.simpleframework.common.web.HttpUtils;
import net.simpleframework.mvc.component.AbstractComponentBean;
import net.simpleframework.mvc.component.ComponentUtils;

/**
 * 这是一个开源的软件，请在LGPLv3下合法使用、修改或重新发布。
 * 
 * @author 陈侃(cknet@126.com, 13910090885)
 *         http://code.google.com/p/simpleframework/
 *         http://www.simpleframework.net
 */
public abstract class AbstractUrlForward extends ObjectEx implements IForward {

	private String url;

	private final String includeRequestData;

	public AbstractUrlForward(final String url, final String includeRequestData) {
		this.url = url;
		this.includeRequestData = includeRequestData;
	}

	public AbstractUrlForward(final String url) {
		this(url, null);
	}

	protected String getRequestUrl(final PageRequestResponse rRequest) {
		final StringBuilder sb = new StringBuilder();
		sb.append(getLocalhostUrl(rRequest));
		final Map<String, String> qMap = HttpUtils.toQueryParams(putRequestData(rRequest,
				includeRequestData));
		final String url = getUrl();
		final int qp = url.indexOf("?");
		if (qp > -1) {
			sb.append(rRequest.wrapContextPath(url.substring(0, qp)));
			qMap.putAll(HttpUtils.toQueryParams(url.substring(qp + 1)));
		} else {
			sb.append(rRequest.wrapContextPath(url));
		}
		sb.append(";jsessionid=").append(rRequest.getSession().getId());
		sb.append("?").append(HttpUtils.toQueryString(qMap));
		return sb.toString();
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(final String url) {
		this.url = url;
	}

	public String getIncludeRequestData() {
		return includeRequestData;
	}

	protected RuntimeException convertRuntimeException(final Exception ex, final String url) {
		log.warn(ex);
		return SimpleRuntimeException._of(MVCException.class, "url: " + url, ex);
	}

	private static final String KEEP_REQUESTDATA_CACHE = "keep_requestdata_cache";

	public static String putRequestData(final PageRequestResponse rRequest,
			final String includeRequestData) {
		return putRequestData(rRequest, includeRequestData, false);
	}

	static Object lock = new Object();

	static long COUNTER = 0;

	public static String putRequestData(final PageRequestResponse rRequest,
			final String includeRequestData, final boolean keepCache) {
		String requestId;
		synchronized (lock) {
			requestId = String.valueOf(COUNTER++);
		}
		LocalSessionCache.put(rRequest.getSession(), requestId, new RequestData(rRequest,
				includeRequestData));
		String p = IForward.REQUEST_ID + "=" + requestId;
		if (keepCache) {
			p += "&" + KEEP_REQUESTDATA_CACHE + "=true";
		}
		return p;
	}

	public static RequestData getRequestDataByRequest(final HttpServletRequest request) {
		final String requestId = request.getParameter(IForward.REQUEST_ID);
		if (Convert.toBool(request.getParameter(KEEP_REQUESTDATA_CACHE))) {
			return (RequestData) LocalSessionCache.get(request.getSession(), requestId);
		} else {
			return (RequestData) LocalSessionCache.remove(request.getSession(), requestId);
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static class RequestData {
		final Map parameters = new HashMap();

		final Map attributes = new HashMap();

		final Map headers = new HashMap();

		public RequestData(final PageRequestResponse rRequest, String includeRequestData) {
			includeRequestData = StringUtils.text(includeRequestData, "p").toLowerCase();

			// parameters
			if (includeRequestData.contains("p")) {
				parameters.putAll(rRequest.request.getParameterMap());
				for (final String k : MVCContextFactory.ctx().getSystemParamKeys()) {
					parameters.remove(k);
				}
			}

			// attributes
			final Enumeration<?> attributeNames = rRequest.getRequestAttrNames();
			while (attributeNames.hasMoreElements()) {
				final String name = (String) attributeNames.nextElement();
				if (includeRequestData.contains("a")
						|| name.startsWith(ComponentUtils.REQUEST_HANDLE_KEY)) {
					final Object value = rRequest.getRequestAttr(name);
					if (value != null) {
						attributes.put(name, value);
					}
				}
			}

			// headers
			if (includeRequestData.contains("h")) {
				final Enumeration<?> headerNames = rRequest.getRequestHeaderNames();
				while (headerNames.hasMoreElements()) {
					final String name = (String) headerNames.nextElement();
					final Enumeration e = rRequest.getRequestHeaders(name);
					if (e != null) {
						headers.put(name, e);
					}
				}
			}
		}
	}

	public static String getLocalhostUrl(final PageRequestResponse rRequest) {
		final StringBuilder sb = new StringBuilder();
		sb.append(rRequest.getRequestScheme()).append("://localhost:")
				.append(MVCContextFactory.config().getServletPort(rRequest));
		return sb.toString();
	}

	public static AbstractUrlForward componentUrl(
			final Class<? extends AbstractComponentBean> beanClass, final String url) {
		return new UrlForward(ComponentUtils.getResourceHomePath(beanClass) + url);
	}
}
