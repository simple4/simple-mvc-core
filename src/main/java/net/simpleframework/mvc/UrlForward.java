package net.simpleframework.mvc;

import java.io.IOException;

import javax.servlet.http.Cookie;

import net.simpleframework.common.AlgorithmUtils;
import net.simpleframework.common.web.HttpUtils;
import net.simpleframework.lib.org.jsoup.Connection;
import net.simpleframework.lib.org.jsoup.Jsoup;

/**
 * 这是一个开源的软件，请在LGPLv3下合法使用、修改或重新发布。
 * 
 * @author 陈侃(cknet@126.com, 13910090885)
 *         http://code.google.com/p/simpleframework/
 *         http://www.simpleframework.net
 */
public class UrlForward extends AbstractUrlForward {

	public static String URLFORWARD_CLASS = "UrlForward_Base64_Code";

	public UrlForward(final String url, final String includeRequestData) {
		super(url, includeRequestData);
	}

	public UrlForward(final String url) {
		super(url);
	}

	@Override
	public String getResponseText(final PageRequestResponse rRequest) {
		final String url = getRequestUrl(rRequest);
		try {
			final Connection conn = Jsoup.connect(url)
					.userAgent("HttpClient-[" + HttpUtils.getUserAgent(rRequest.request) + "]")
					.timeout(0);
			final Cookie[] cookies = rRequest.getRequestCookies();
			if (cookies != null) {
				for (final Cookie cookie : cookies) {
					conn.cookie(cookie.getName(), cookie.getValue());
				}
			}
			return conn.execute().body();
		} catch (final IOException e) {
			throw convertRuntimeException(e, url);
		}
	}

	public static String getResponseText(final PageRequestResponse rRequest, final String url) {
		return new UrlForward(url).getResponseText(rRequest);
	}

	public static String encodeResponseText(final String responseText) {
		final StringBuilder sb = new StringBuilder();
		sb.append("<div class='").append(URLFORWARD_CLASS)
				.append("'>" + AlgorithmUtils.base64Encode(responseText.getBytes()) + "</div>");
		return sb.toString();
	}
}
