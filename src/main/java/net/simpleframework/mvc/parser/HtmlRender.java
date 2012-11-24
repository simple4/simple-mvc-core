package net.simpleframework.mvc.parser;

import java.util.List;
import java.util.Map;

import net.simpleframework.common.StringUtils;
import net.simpleframework.lib.org.jsoup.nodes.Element;
import net.simpleframework.lib.org.jsoup.nodes.Node;
import net.simpleframework.mvc.PageParameter;
import net.simpleframework.mvc.component.AbstractComponentBean;
import net.simpleframework.mvc.component.AbstractContainerBean;
import net.simpleframework.mvc.component.ComponentParameter;
import net.simpleframework.mvc.component.IComponentHtmlRender;
import net.simpleframework.mvc.component.IComponentRender;

/**
 * 这是一个开源的软件，请在LGPLv3下合法使用、修改或重新发布。
 * 
 * @author 陈侃(cknet@126.com, 13910090885)
 *         http://code.google.com/p/simpleframework/
 *         http://www.simpleframework.net
 */
public class HtmlRender extends AbstractParser {
	void doTag(final PageParameter pParameter, final Element htmlHead, final Element element,
			final Map<String, AbstractComponentBean> componentBeans) {
		for (final Map.Entry<String, AbstractComponentBean> entry : componentBeans.entrySet()) {
			final AbstractComponentBean componentBean = entry.getValue();
			if (!(componentBean instanceof AbstractContainerBean)) {
				continue;
			}
			final IComponentRender render = componentBean.getComponentRegistry().getComponentRender();
			if (!(render instanceof IComponentHtmlRender)) {
				continue;
			}

			final ComponentParameter cParameter = ComponentParameter.get(pParameter, componentBean);
			if (!((Boolean) cParameter.getBeanProperty("runImmediately"))) {
				continue;
			}

			final String tagId = element.attr("id");
			if (!StringUtils.hasText(tagId)) {
				continue;
			}
			if (!tagId.equals(cParameter.getBeanProperty("containerId"))) {
				continue;
			}

			doBeforeRender(cParameter);
			final String html = ((IComponentHtmlRender) render).getHtml(cParameter);
			if (!StringUtils.hasText(html)) {
				return;
			}

			final List<Node> nodes = ParserUtils.htmlToNodes(pParameter, html, htmlHead);

			element.empty();

			for (final Node child : nodes) {
				element.appendChild(child);
			}
		}
	}
}
