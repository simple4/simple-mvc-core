package net.simpleframework.mvc;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import net.simpleframework.common.IoUtils;
import net.simpleframework.common.coll.AbstractKVMap;
import net.simpleframework.common.script.MVEL2Template;
import net.simpleframework.common.web.HttpUtils;

/**
 * 这是一个开源的软件，请在LGPLv3下合法使用、修改或重新发布。
 * 
 * @author 陈侃(cknet@126.com, 13910090885)
 *         http://code.google.com/p/simpleframework/
 *         http://www.simpleframework.net
 */
public abstract class AbstractMVELTemplatePage extends AbstractMVCPage {

	/**
	 * 创建mvel预定义好的模板
	 * 
	 * html写法：$includeNamed{'key'}
	 * 
	 * @return
	 */
	protected NamedTemplate createNamedTemplates(final PageParameter pParameter) {
		return null;
	}

	@Override
	public IForward forward(final PageParameter pParameter) {
		final NamedTemplate nt = createNamedTemplates(pParameter);
		pParameter.setRequestAttr("NamedTemplate", nt);
		return super.forward(pParameter);
	}

	@Override
	protected String replaceExpr(final PageParameter pParameter, final InputStream htmlStream,
			final Map<String, Object> variables) throws IOException {
		final NamedTemplate nt = (NamedTemplate) pParameter.getRequestAttr("NamedTemplate");
		return MVEL2Template.replace(variables,
				IoUtils.getStringFromInputStream(htmlStream, getChartset()), nt == null ? null : nt);
	}

	public static class NamedTemplate extends AbstractKVMap<String, NamedTemplate> {
		private final PageParameter pParameter;

		public NamedTemplate(final PageParameter pParameter) {
			this.pParameter = pParameter;
		}

		public NamedTemplate add(final String key, final Class<? extends AbstractMVCPage> pageClass) {
			return add(key, UrlForward.encodeResponseText(new UrlForward(AbstractMVCPage
					.uriFor(pageClass) + "?referer=" + HttpUtils.getRequestURI(pParameter.request))
					.getResponseText(pParameter)));
		}

		private static final long serialVersionUID = 3291319165215636903L;
	}
}
