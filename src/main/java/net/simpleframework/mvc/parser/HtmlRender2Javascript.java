package net.simpleframework.mvc.parser;

import java.util.Map;

import net.simpleframework.common.StringUtils;
import net.simpleframework.common.html.js.JavascriptUtils;
import net.simpleframework.lib.org.jsoup.nodes.Element;
import net.simpleframework.mvc.PageParameter;
import net.simpleframework.mvc.component.AbstractComponentBean;
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
public class HtmlRender2Javascript extends AbstractParser {

	void doTag(final PageParameter pParameter, final Element htmlHead,
			final Map<String, AbstractComponentBean> componentBeans) {
		for (final Map.Entry<String, AbstractComponentBean> entry : componentBeans.entrySet()) {
			final AbstractComponentBean componentBean = entry.getValue();

			final IComponentRender render = componentBean.getComponentRegistry().getComponentRender();
			if (!(render instanceof IComponentHtmlRender)) {
				continue;
			}

			final ComponentParameter cParameter = ComponentParameter.get(pParameter, componentBean);
			final String js = ((IComponentHtmlRender) render).getHtmlJavascriptCode(cParameter);
			if (StringUtils.hasText(js)) {
				// 立即执行，此处不用wrapWhenReady
				// 该js要优先于HttpClient产生的jscode
				ParserUtils.addScriptText(htmlHead, JavascriptUtils.wrapFunction(js));
			}
		}
	}
}
