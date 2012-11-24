package net.simpleframework.mvc.ctx.permission;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpSession;

import net.simpleframework.common.AlgorithmUtils;
import net.simpleframework.common.StringUtils;
import net.simpleframework.common.web.HttpUtils;
import net.simpleframework.ctx.permission.IPermissionHandler;
import net.simpleframework.mvc.IFilterListener;
import net.simpleframework.mvc.LocalSessionCache;
import net.simpleframework.mvc.MVCContextFactory;
import net.simpleframework.mvc.MVCUtils;
import net.simpleframework.mvc.PageDocument;
import net.simpleframework.mvc.PageParameter;
import net.simpleframework.mvc.PageRequestResponse;

/**
 * 这是一个开源的软件，请在LGPLv3下合法使用、修改或重新发布。
 * 
 * @author 陈侃(cknet@126.com, 13910090885)
 *         http://code.google.com/p/simpleframework/
 *         http://www.simpleframework.net
 */
public class PermissionFilterListener implements IFilterListener {

	@Override
	public boolean doFilter(final PageRequestResponse rRequest, final FilterChain filterChain)
			throws IOException {
		final HttpSession httpSession = rRequest.getSession();
		if (LocalSessionCache.get(httpSession, SESSION_THROWABLE) != null) {
			return true;
		}

		String role = null;
		if (rRequest instanceof PageParameter) {
			final PageParameter pParameter = (PageParameter) rRequest;
			role = (String) pParameter.getBeanProperty("role");
			if (rRequest.isAjaxRequest() && !StringUtils.hasText(role)) {
				final PageDocument pageDocument2 = MVCUtils.getBrowserLocationPageDocument(httpSession);
				if (pageDocument2 != null && !pageDocument2.equals(pParameter.getPageDocument())) {
					final PageParameter pParameter2 = PageParameter.get(rRequest, pageDocument2);
					role = (String) pParameter2.getBeanProperty("role");
				}
			}
		}

		final IPagePermissionHandler permission = MVCContextFactory.permission();
		String rUrl;
		if (permission != null
				&& StringUtils.hasText(rUrl = permission.getLoginRedirectUrl(rRequest, role))) {
			rRequest.loc(rUrl);
			return false;
		}

		if (StringUtils.hasText(role) && !IPermissionHandler.sj_anonymous.equals(role)) {
			if (!permission.isMember(permission.getLoginId(rRequest), role)) {
				final String v = AlgorithmUtils.base64Encode(HttpUtils.getRequestAndQueryStringUrl(
						rRequest.request).getBytes());
				rRequest.loc(MVCUtils.getPageResourcePath() + "/jsp/role_http_access.jsp?v=" + v
						+ "&role=" + role);
				return false;
			}
		}
		return true;
	}
}
