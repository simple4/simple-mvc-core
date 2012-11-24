package net.simpleframework.mvc;

import java.io.File;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import net.simpleframework.common.StringUtils;
import net.simpleframework.common.bean.BeanUtils;
import net.simpleframework.common.coll.KVMap;
import net.simpleframework.common.logger.Log;
import net.simpleframework.common.logger.LogFactory;
import net.simpleframework.common.web.HttpUtils;
import net.simpleframework.mvc.common.DeployUtils;
import net.simpleframework.mvc.component.ComponentHandleException;

/**
 * 这是一个开源的软件，请在LGPLv3下合法使用、修改或重新发布。
 * 
 * @author 陈侃(cknet@126.com, 13910090885)
 *         http://code.google.com/p/simpleframework/
 *         http://www.simpleframework.net
 */
public abstract class MVCUtils {

	public static KVMap createVariables(final PageParameter pParameter) {
		final KVMap variable = new KVMap();
		variable.add("parameter", pParameter);
		variable.add("request", pParameter.request);
		variable.add("response", pParameter.response);
		final HttpSession session = pParameter.getSession();
		variable.add("session", session);
		variable.add("application", session.getServletContext());
		variable.add("pagePath", MVCContextFactory.config().getPagePath());

		final PageDocument pageDocument = pParameter.getPageDocument();
		if (pageDocument != null) {
			variable.add("document", pageDocument);
			final IPageResourceProvider prp = pageDocument.getPageResourceProvider();
			variable.add("skin", prp.getSkin(pParameter));
			variable.add("resourcePath", prp.getResourceHomePath());

			final AbstractMVCPage pageView = pParameter.getPage();
			if (pageView != null) {
				variable.putAll(pageView.createVariables(pParameter));
			}
		}
		return variable;
	}

	private final static String SESSION_PAGE_DOCUMENT = "$$page_document";

	public static void setBrowserLocationPageDocument(final HttpSession httpSession,
			final PageDocument pageDocument) {
		httpSession.setAttribute(SESSION_PAGE_DOCUMENT, pageDocument.hashId());
	}

	public static PageDocument getBrowserLocationPageDocument(final HttpSession httpSession) {
		final String hashId = (String) httpSession.getAttribute(SESSION_PAGE_DOCUMENT);
		return PageDocumentFactory.getPageDocument(hashId);
	}

	public static String getRealPath(String url) {
		final ServletContext ctx = MVCContextFactory.servlet();
		final String contextPath = ctx.getContextPath();
		if (StringUtils.hasText(contextPath) && url.startsWith(contextPath)) {
			url = url.substring(contextPath.length());
		}
		return ctx.getRealPath(url);
	}

	public static String getPageResourcePath() {
		return DeployUtils.getResourcePath(MVCUtils.class);
	}

	public static String getCssResourcePath(final PageParameter pParameter) {
		return MVCContextFactory.ctx().getDefaultPageResourceProvider()
				.getCssResourceHomePath(pParameter, MVCUtils.class);
	}

	public static String getLocationPath() {
		return getPageResourcePath() + "/jsp/location.jsp";
	}

	public final static String SESSION_SKIN = "$$skin";

	public static void setSessionSkin(final HttpSession httpSession, final String skin) {
		httpSession.setAttribute(SESSION_SKIN, skin);
	}

	public static String doPageUrl(final PageParameter pParameter, final String url) {
		final PageDocument pageDocument = pParameter.getPageDocument();
		if (StringUtils.hasText(url) && !HttpUtils.isAbsoluteUrl(url) && !url.startsWith("/")) {
			final File documentFile = pageDocument.getDocumentFile();
			if (documentFile != null) {
				String lookupPath = documentFile.getAbsolutePath().substring(getRealPath("/").length())
						.replace(File.separatorChar, '/');
				final int pos = lookupPath.lastIndexOf("/");
				if (pos > -1) {
					lookupPath = lookupPath.substring(0, pos + 1) + url;
					return lookupPath.charAt(0) == '/' ? lookupPath : "/" + lookupPath;
				}
			} else {
				AbstractMVCPage abstractMVCPage;
				if ((abstractMVCPage = pParameter.getPage()) != null) {
					final String lookupPath = abstractMVCPage.getLookupPath();
					if (lookupPath != null) {
						return lookupPath.substring(0, lookupPath.lastIndexOf("/") + 1) + url;
					}
				}
			}
		}
		return url;
	}

	public static void setObjectFromRequest(final Object object, final HttpServletRequest request,
			final String prefix, final String[] properties) {
		if (object == null || properties == null) {
			return;
		}
		for (final String property : properties) {
			String value = request.getParameter(StringUtils.blank(prefix) + property);
			if ("".equals(value)) {
				value = null;
			}
			try {
				BeanUtils.setProperty(object, property, value);
			} catch (final Exception e) {
				throw ComponentHandleException.of(e);
			}
		}
	}

	static Log log = LogFactory.getLogger(MVCUtils.class);
}
