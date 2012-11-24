package net.simpleframework.mvc.component;

import net.simpleframework.common.ObjectEx;
import net.simpleframework.mvc.UrlForward;

/**
 * 这是一个开源的软件，请在LGPLv3下合法使用、修改或重新发布。
 * 
 * @author 陈侃(cknet@126.com, 13910090885)
 *         http://code.google.com/p/simpleframework/
 *         http://www.simpleframework.net
 */
public abstract class AbstractComponentRender extends ObjectEx implements IComponentRender {
	private final IComponentRegistry componentRegistry;

	public AbstractComponentRender(final IComponentRegistry componentRegistry) {
		this.componentRegistry = componentRegistry;
	}

	@Override
	public IComponentRegistry getComponentRegistry() {
		return componentRegistry;
	}

	public static abstract class ComponentJavascriptRender extends AbstractComponentRender implements
			IComponentJavascriptRender {

		public ComponentJavascriptRender(final IComponentRegistry componentRegistry) {
			super(componentRegistry);
		}
	}

	public static abstract class ComponentHtmlRender extends AbstractComponentRender implements
			IComponentHtmlRender {
		public ComponentHtmlRender(final IComponentRegistry componentRegistry) {
			super(componentRegistry);
		}

		@Override
		public String getHtml(final ComponentParameter cParameter) {
			return new UrlForward(getResponseUrl(cParameter),
					(String) cParameter.getBeanProperty("includeRequestData"))
					.getResponseText(cParameter);
		}

		public String getResponseUrl(final ComponentParameter cParameter) {
			return ComponentUtils.getResourceHomePath(cParameter.componentBean.getClass())
					+ getRelativePath(cParameter);
		}

		protected abstract String getRelativePath(final ComponentParameter cParameter);
	}
}