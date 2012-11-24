package net.simpleframework.mvc;

import java.util.ArrayList;
import java.util.Collection;

import javax.servlet.http.HttpServletRequest;

import net.simpleframework.common.ObjectEx;
import net.simpleframework.common.OsUtils;
import net.simpleframework.common.OsUtils.OS;
import net.simpleframework.common.OsUtils.OsInfo;
import net.simpleframework.common.Version;
import net.simpleframework.common.html.element.Meta;
import net.simpleframework.common.web.Browser;

/**
 * 这是一个开源的软件，请在LGPLv3下合法使用、修改或重新发布。
 * 
 * @author 陈侃(cknet@126.com, 13910090885)
 *         http://code.google.com/p/simpleframework/
 *         http://www.simpleframework.net
 */
public class MVCHtmlBuilder extends ObjectEx {

	public final static String HTML5_DOC_TYPE = "<!DOCTYPE HTML>";

	public final static String HTML401_DOC_TYPE = "<!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\" \"http://www.w3.org/TR/html4/loose.dtd\">";

	public final static String XHTML10_DOC_TYPE = "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">";

	public String doctype(final PageParameter pParameter) {
		return HTML5_DOC_TYPE;
	}

	public String headStyle(final PageParameter pParameter) {
		final HttpServletRequest request = pParameter.request;
		final StringBuilder style = new StringBuilder();
		style.append("<style type=\"text/css\">body * { font-family:");
		if (Browser.get(request).isPresto()) {
			style.append("'Microsoft YaHei','微软雅黑'");
		} else {
			style.append("Verdana,");
			// !HttpUtils.isIE(request) || HttpUtils.getIEVersion(request) >= 8
			if (gtWindowXP(request)) {
				style.append("'Microsoft YaHei',");
			} else {
				style.append("SimSun,");
			}
			style.append("Sans-Serif,Tahoma,Arial");
		}
		style.append("; }</style>");
		return style.toString();
	}

	public Collection<Meta> meta(final PageParameter pParameter) {
		final ArrayList<Meta> al = new ArrayList<Meta>();
		al.add(new Meta("Content-Type", "text/html; charset="
				+ MVCContextFactory.config().getCharset()));
		if (Browser.get(pParameter.request).isTrident()) {
			// EmulateIE7
			al.add(new Meta("X-UA-Compatible", "IE=edge,chrome=1"));
		}
		final AbstractMVCPage page = pParameter.getPage();
		if (page != null) {
			final Collection<Meta> meta2 = page.meta(pParameter);
			if (meta2 != null) {
				al.addAll(meta2);
			}
		}
		return al;
	}

	protected boolean gtWindowXP(final HttpServletRequest request) {
		Boolean gt = (Boolean) request.getSession().getAttribute("__gtWindowXP");
		if (gt != null) {
			return gt.booleanValue();
		}
		try {
			final OsInfo osInfo = OsUtils.get(request);
			if (osInfo.os.equals(OS.Windows)) {
				request.getSession().setAttribute("__gtWindowXP",
						gt = osInfo.v.complies(Version.getVersion(String.valueOf(6.0f))));
			}
			return gt;
		} catch (final Exception e) {
		}
		return false;
	}
}
