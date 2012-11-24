package net.simpleframework.mvc;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import net.simpleframework.common.Convert;
import net.simpleframework.common.StringUtils;
import net.simpleframework.common.web.Browser;
import net.simpleframework.common.web.HttpUtils;
import net.simpleframework.mvc.common.GetSession;

/**
 * 这是一个开源的软件，请在LGPLv3下合法使用、修改或重新发布。
 * 
 * @author 陈侃(cknet@126.com, 13910090885)
 *         http://code.google.com/p/simpleframework/
 *         http://www.simpleframework.net
 */
public class UtilsFilterListener implements IFilterListener {

	@Override
	public boolean doFilter(final PageRequestResponse rRequest, final FilterChain filterChain)
			throws IOException {
		final HttpServletRequest httpRequest = rRequest.request;

		final String ieWarnUrl = MVCContextFactory.config().getIEWarnUrl();
		if (StringUtils.hasText(ieWarnUrl)) {
			final Browser browser = Browser.get(httpRequest);
			if (browser.isTrident() && browser.getVersion() < 8
					&& !Convert.toBool(HttpUtils.getCookie(httpRequest, "ie6_browser"))
					&& !httpRequest.getRequestURI().endsWith(ieWarnUrl)) {
				HttpUtils.loc(httpRequest, rRequest.response, ieWarnUrl);
				return false;
			}
		}

		final HttpSession httpSession = httpRequest.getSession();
		/* session cache */
		GetSession.setSession(httpSession);

		/* encoding */
		final String encoding = MVCContextFactory.config().getCharset();
		if (encoding != null) {
			rRequest.setRequestCharacterEncoding(encoding);
		}
		return true;
	}
}