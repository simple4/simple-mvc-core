package net.simpleframework.mvc.parser;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import net.simpleframework.common.AlgorithmUtils;
import net.simpleframework.common.ClassUtils;
import net.simpleframework.common.I18n;
import net.simpleframework.common.StringUtils;
import net.simpleframework.common.coll.KVMap;
import net.simpleframework.common.html.HtmlUtils;
import net.simpleframework.common.html.element.Meta;
import net.simpleframework.lib.org.jsoup.nodes.DataNode;
import net.simpleframework.lib.org.jsoup.nodes.Document;
import net.simpleframework.lib.org.jsoup.nodes.DocumentType;
import net.simpleframework.lib.org.jsoup.nodes.Element;
import net.simpleframework.lib.org.jsoup.nodes.Node;
import net.simpleframework.lib.org.jsoup.nodes.TextNode;
import net.simpleframework.lib.org.jsoup.select.Elements;
import net.simpleframework.mvc.AbstractMVCPage;
import net.simpleframework.mvc.IPageHandler;
import net.simpleframework.mvc.IPageHandler.PageSelector;
import net.simpleframework.mvc.MVCContextFactory;
import net.simpleframework.mvc.MVCException;
import net.simpleframework.mvc.MVCHtmlBuilder;
import net.simpleframework.mvc.PageDocument;
import net.simpleframework.mvc.PageParameter;
import net.simpleframework.mvc.UrlForward;
import net.simpleframework.mvc.component.AbstractComponentBean;

/**
 * 这是一个开源的软件，请在LGPLv3下合法使用、修改或重新发布。
 * 
 * @author 陈侃(cknet@126.com, 13910090885)
 *         http://code.google.com/p/simpleframework/
 *         http://www.simpleframework.net
 */
public final class PageParser extends AbstractPageParser {
	private final MVCHtmlBuilder htmlBuilder = MVCContextFactory.ctx().getPageHtmlBuilder();

	private Document htmlDocument;

	private Element headElement;

	public PageParser(final PageParameter pParameter) {
		super(pParameter);
	}

	public PageParser parser(final String responseString) {
		try {
			final PageParameter pParameter = getPageParameter();
			final IPageHandler pageHandle = pParameter.getPageHandler();
			if (pageHandle != null) {
				pageHandle.beforeComponentRender(pParameter);
			}

			beforeCreate(pParameter, responseString);

			final Map<String, AbstractComponentBean> oComponentBeans = pParameter.getComponentBeans();
			resourceBinding.doTag(pParameter, headElement, oComponentBeans);
			normaliseNode(pParameter, htmlDocument, oComponentBeans);
			htmlRender2Javascript.doTag(pParameter, headElement, oComponentBeans);
			javascriptRender.doTag(pParameter, headElement, oComponentBeans);

			// 转换UrlForward直接输出的代码
			final Elements elements = htmlDocument.select("." + UrlForward.URLFORWARD_CLASS);
			for (int i = 0; i < elements.size(); i++) {
				final Element element = elements.get(i);
				final Element parent = element.parent();
				final List<Node> nodes = ParserUtils.htmlToNodes(pParameter,
						new String(AlgorithmUtils.base64Decode(element.text())), headElement);
				parent.empty();
				for (final Node child : nodes) {
					parent.appendChild(child);
				}
			}

			// 执行handle
			if (pageHandle != null) {
				final KVMap dataBinding = new KVMap() {
					@Override
					public Object put(final String key, Object value) {
						if (value instanceof Enum) {
							value = ((Enum<?>) value).ordinal();
						}
						return super.put(key, value);
					}

					private static final long serialVersionUID = 971286039035769475L;
				};
				final PageSelector selector = new PageSelector();
				doPageLoad(pParameter, pageHandle, dataBinding, selector);
				pageLoaded.doTag(pParameter, headElement, dataBinding, selector);
			}
		} catch (final Exception e) {
			throw ParserRuntimeException.of(e);
		}
		return this;
	}

	private void beforeCreate(final PageParameter pParameter, final String responseString) {
		if (pParameter.isHttpRequest()) {
			htmlDocument = HtmlUtils.createHtmlDocument(responseString, true);
			headElement = htmlDocument.head();

			final Collection<Meta> coll = htmlBuilder.meta(pParameter);
			if (coll != null) {
				for (final Meta attri : coll) {
					headElement.prepend(attri.toString());
				}
			}

			final PageDocument pageDocument = pParameter.getPageDocument();
			final String title = pageDocument.getTitle(pParameter);
			if (StringUtils.hasText(title)) {
				htmlDocument.title(title);
			}

			String favicon = pageDocument.getPageBean().getFavicon();
			if (!StringUtils.hasText(favicon)) {
				favicon = pageDocument.getPageResourceProvider().getCssResourceHomePath(pParameter)
						+ "/images/favicon.png";
			}
			final Element link = htmlDocument.head().appendElement("link");
			link.attr("href", pParameter.wrapContextPath(favicon));
			link.attr("rel", "SHORTCUT ICON");

			headElement.append(htmlBuilder.headStyle(pParameter));
		} else {
			htmlDocument = HtmlUtils.createHtmlDocument(responseString, false);
			headElement = htmlDocument.select("head").first();
			if (headElement == null) {
				headElement = htmlDocument.createElement("head");
				htmlDocument.prependChild(headElement);
			}
			headElement.attr("move", "true");
		}
	}

	private static final String[] i18nAttributes = new String[] { "value", "title" };

	private void normaliseNode(final PageParameter pParameter, final Element element,
			final Map<String, AbstractComponentBean> componentBeans) {
		for (final Node child : element.childNodes()) {
			if (child instanceof Element) {
				final String id = child.attr("id");
				if (StringUtils.hasText(id)) {
					htmlRender.doTag(pParameter, htmlDocument.head(), (Element) child, componentBeans);
				}

				for (final String attribute : i18nAttributes) {
					final String value = child.attr(attribute);
					if (StringUtils.hasText(value)) {
						child.attr(attribute, I18n.replaceI18n(value));
					}
				}

				final String nodeName = child.nodeName();
				if ("a".equalsIgnoreCase(nodeName)) {
					child.attr("hidefocus", "hidefocus");
					final String href = child.attr("href");
					if (!StringUtils.hasText(href)) {
						child.attr("href", "javascript:void(0);");
					} else {
						child.attr("href", pParameter.wrapContextPath(href));
					}
				} else if ("form".equalsIgnoreCase(nodeName)) {
					if (!StringUtils.hasText(child.attr("action"))) {
						child.attr("action", "javascript:void(0);");
					}
				} else if ("img".equalsIgnoreCase(nodeName)) {
					final String src = child.attr("src");
					if (StringUtils.hasText(src)) {
						child.attr("src", pParameter.wrapContextPath(src));
					}
				}
				normaliseNode(pParameter, (Element) child, componentBeans);
			} else if (child instanceof TextNode) {
				final String text = ((TextNode) child).getWholeText();
				if (StringUtils.hasText(text)) {
					((TextNode) child).text(I18n.replaceI18n(text));
				}
			} else if (child instanceof DataNode) {
				final String text = ((DataNode) child).getWholeData();
				if (StringUtils.hasText(text)) {
					child.attr("data", I18n.replaceI18n(text));
				}
			}
		}
	}

	private void doPageLoad(final PageParameter pParameter, final IPageHandler pageHandle,
			final Map<String, Object> dataBinding, final PageSelector selector) {
		final String handleMethod = pParameter.getPageDocument().getPageBean().getHandleMethod();
		if (StringUtils.hasText(handleMethod) && !(pageHandle instanceof AbstractMVCPage.PageLoad)) {
			try {
				final Method methodObject = pageHandle.getClass().getMethod(handleMethod,
						PageParameter.class, Map.class, PageSelector.class);
				ClassUtils.invoke(methodObject, pageHandle, pParameter, dataBinding, selector);
			} catch (final NoSuchMethodException e) {
				throw MVCException.of(e);
			}
		} else {
			pageHandle.pageLoad(pParameter, dataBinding, selector);
		}
	}

	public String toHtml(final PageParameter pParameter) {
		if (htmlDocument == null) {
			return "";
		}
		String html = htmlDocument.html();
		if (pParameter.isHttpRequest()) {
			boolean doctype = false;
			for (final Node child : htmlDocument.childNodes()) {
				if (child instanceof DocumentType) {
					doctype = true;
					break;
				}
			}
			if (!doctype) {
				html = htmlBuilder.doctype(getPageParameter()) + html;
			}
		}
		return html;
	}

	private static ResourceBinding resourceBinding = new ResourceBinding();
	private static HtmlRender htmlRender = new HtmlRender();
	private static JavascriptRender javascriptRender = new JavascriptRender();
	private static HtmlRender2Javascript htmlRender2Javascript = new HtmlRender2Javascript();
	private static PageLoaded pageLoaded = new PageLoaded();
}
