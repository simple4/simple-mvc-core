package net.simpleframework.mvc.parser;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import net.simpleframework.common.StringUtils;
import net.simpleframework.lib.org.jsoup.nodes.Element;
import net.simpleframework.mvc.IPageResourceProvider;
import net.simpleframework.mvc.MVCContextFactory;
import net.simpleframework.mvc.PageDocument;
import net.simpleframework.mvc.PageParameter;
import net.simpleframework.mvc.component.AbstractComponentBean;
import net.simpleframework.mvc.component.ComponentRegistryFactory;
import net.simpleframework.mvc.component.IComponentRegistry;
import net.simpleframework.mvc.component.IComponentResourceProvider;

/**
 * 这是一个开源的软件，请在LGPLv3下合法使用、修改或重新发布。
 * 
 * @author 陈侃(cknet@126.com, 13910090885)
 *         http://code.google.com/p/simpleframework/
 *         http://www.simpleframework.net
 */
public class ResourceBinding extends AbstractParser {

	public void doTag(final PageParameter pParameter, final Element htmlHead,
			final Map<String, AbstractComponentBean> componentBeans) {
		final PageDocument pageDocument = pParameter.getPageDocument();
		final IPageResourceProvider prp = pageDocument.getPageResourceProvider();
		final String pageHome = prp.getResourceHomePath();

		if (pParameter.isHttpRequest()) {
			final StringBuilder sb = new StringBuilder();
			sb.append("window.CONTEXT_PATH=\"").append(pParameter.getContextPath()).append("\";");
			sb.append("window.HOME_PATH=\"").append(pageHome).append("\";");
			sb.append("window.SKIN=\"")
					.append(pageDocument.getPageResourceProvider().getSkin(pParameter)).append("\";");
			sb.append("window.IS_EFFECTS=");
			sb.append(MVCContextFactory.config().isEffect(pParameter)).append(";");
			ParserUtils.addScriptText(htmlHead, sb.toString());
		}

		if (pParameter.isHttpRequest()) {
			// base
			String[] jsArr, cssArr;
			cssArr = prp.getCssPath(pParameter);
			if (cssArr != null) {
				for (final String css : cssArr) {
					ParserUtils.addStylesheet(pParameter, htmlHead, css);
				}
			}
			jsArr = prp.getJavascriptPath(pParameter);
			if (jsArr != null) {
				for (final String js : jsArr) {
					ParserUtils.addScriptSRC(pParameter, htmlHead, js);
				}
			}
		}

		final Collection<String> cssColl = pageDocument.getImportCSS(pParameter);
		if (cssColl != null) {
			for (String css : cssColl) {
				if (!css.startsWith("/")) {
					css = pageHome + "/" + css;
				} else {
					css = pParameter.wrapContextPath(css);
				}
				ParserUtils.addStylesheet(pParameter, htmlHead, css);
			}
		}

		// page
		final Collection<String> jsColl = pageDocument.getImportJavascript(pParameter);
		if (jsColl != null) {
			for (String js : jsColl) {
				if (!js.startsWith("/")) {
					js = pageHome + "/" + js;
				} else {
					js = pParameter.wrapContextPath(js);
				}
				ParserUtils.addScriptSRC(pParameter, htmlHead, js);
			}
		}

		final String javascriptCode = prp.getInitJavascriptCode(pParameter);
		if (StringUtils.hasText(javascriptCode)) {
			ParserUtils.addScriptText(htmlHead, javascriptCode);
		}

		// component
		final Set<String> keys = new LinkedHashSet<String>();
		for (final AbstractComponentBean componentBean : componentBeans.values()) {
			keys.add(componentBean.getComponentRegistry().getComponentName());
			// final String key = ;
			// Collection<AbstractComponentBean> coll = components.get(key);
			// if (coll == null) {
			// components.put(key, coll = new ArrayList<AbstractComponentBean>());
			// }
			// coll.add(componentBean);
		}

		final ComponentRegistryFactory factory = ComponentRegistryFactory.get();
		for (final String componentName : keys) {
			final IComponentRegistry registry = factory.getComponentRegistry(componentName);
			final IComponentResourceProvider crp = registry.getComponentResourceProvider();
			if (crp == null) {
				continue;
			}

			final String[] dependents = crp.getDependentComponents(pParameter);
			if (dependents != null && dependents.length > 0) {
				for (final String dependent : dependents) {
					final IComponentRegistry registry2 = factory.getComponentRegistry(dependent);
					if (registry2 == null) {
						continue;
					}
					final IComponentResourceProvider crp2 = registry2.getComponentResourceProvider();
					if (crp2 == null) {
						continue;
					}
					doComponentResource(htmlHead, pParameter, crp2, pageHome);
				}
			}
			doComponentResource(htmlHead, pParameter, crp, pageHome);
		}
	}

	private void doComponentResource(final Element htmlHead, final PageParameter pParameter,
			final IComponentResourceProvider crp, final String pageHome) {
		final String[] cssArr = crp.getCssPath(pParameter);
		if (cssArr != null) {
			for (final String css : cssArr) {
				ParserUtils.addStylesheet(pParameter, htmlHead, css);
			}
		}
		final String[] jsArr = crp.getJavascriptPath(pParameter);
		if (jsArr != null) {
			for (final String js : jsArr) {
				ParserUtils.addScriptSRC(pParameter, htmlHead, js);
			}
		}
	}
}
