package net.simpleframework.mvc.parser;

import java.util.ArrayList;
import java.util.List;

import net.simpleframework.common.StringUtils;
import net.simpleframework.common.html.HtmlUtils;
import net.simpleframework.common.html.js.JavascriptUtils;
import net.simpleframework.common.web.HttpUtils;
import net.simpleframework.lib.org.jsoup.nodes.DataNode;
import net.simpleframework.lib.org.jsoup.nodes.Document;
import net.simpleframework.lib.org.jsoup.nodes.Element;
import net.simpleframework.lib.org.jsoup.nodes.Node;
import net.simpleframework.lib.org.jsoup.select.Elements;
import net.simpleframework.mvc.MVCContextFactory;
import net.simpleframework.mvc.PageParameter;

/**
 * 这是一个开源的软件，请在LGPLv3下合法使用、修改或重新发布。
 * 
 * @author 陈侃(cknet@126.com, 13910090885)
 *         http://code.google.com/p/simpleframework/
 *         http://www.simpleframework.net
 */
public abstract class ParserUtils {
	private final static String SCRIPT_TYPE = "text/javascript";

	static Element addScriptSRC(final PageParameter pParameter, final Element element, String src) {
		if (!StringUtils.hasText(src)) {
			return null;
		}
		final Elements scripts;
		int size;
		final int p = src.indexOf("?");
		if (p > 0) { // 含有参数
			scripts = element.select("script[src=" + src + "]"); // 全部相等
			size = scripts.size();
		} else {
			scripts = element.select("script[src^=" + src + "]");
			size = scripts.size();
			if (size == 0) {
				src = HttpUtils.addParameters(src, "v=" + MVCContextFactory.ctx().getVersion());
			}
		}
		if (size == 0) {
			return element.appendElement("script").attr("type", SCRIPT_TYPE).attr("src", src);
		} else {
			return scripts.first();
		}
	}

	static Element addStylesheet(final PageParameter pParameter, final Element element, String href) {
		if (!StringUtils.hasText(href)) {
			return null;
		}
		final Elements links;
		int size;
		final int p = href.indexOf("?");
		if (p > 0) {
			links = element.select("link[href=" + href + "]");
			size = links.size();
		} else {
			links = element.select("link[href^=" + href + "]");
			size = links.size();
			if (size == 0) {
				href = HttpUtils.addParameters(href, "v=" + MVCContextFactory.ctx().getVersion());
			}
		}
		if (size == 0) {
			return element.appendElement("link").attr("rel", "stylesheet").attr("type", "text/css")
					.attr("href", href);
		} else {
			return links.first();
		}
	}

	static Element addScriptText(final Element element, final String js) {
		return addScriptText(element, js, true);
	}

	static Element addScriptText(final Element element, String js, final boolean compress) {
		js = StringUtils.blank(js);
		return element
				.appendElement("script")
				.attr("type", SCRIPT_TYPE)
				.appendChild(
						new DataNode(compress ? JavascriptUtils.jsCompress(js) : js, element.baseUri()));
	}

	static List<Node> htmlToNodes(final PageParameter pParameter, final String html,
			final Element htmlHead) {
		final Document htmlDocument = HtmlUtils.createHtmlDocument(html, false);
		for (final Element moveHead : htmlDocument.select("head[move]")) {
			for (final Element link : moveHead.select("link[href], link[rel=stylesheet]")) {
				addStylesheet(pParameter, htmlHead, link.attr("href"));
				link.remove();
			}
			final StringBuilder jsCode = new StringBuilder();
			for (final Element script : moveHead.select("script")) {
				final String src = script.attr("src");
				if (StringUtils.hasText(src)) {
					addScriptSRC(pParameter, htmlHead, src);
				} else {
					jsCode.append(StringUtils.blank(script.data()));
				}
				script.remove();
			}
			if (jsCode.length() > 0) {
				addScriptText(htmlHead, jsCode.toString());
			}
			if (moveHead.children().size() == 0) {
				moveHead.remove();
			}
		}
		return new ArrayList<Node>(htmlDocument.childNodes());
	}
}
