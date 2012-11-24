package net.simpleframework.mvc;

import java.util.Map;

import net.simpleframework.common.bean.BeanUtils;
import net.simpleframework.common.coll.ArrayUtils;

/**
 * 这是一个开源的软件，请在LGPLv3下合法使用、修改或重新发布。
 * 
 * @author 陈侃(cknet@126.com, 13910090885)
 *         http://code.google.com/p/simpleframework/
 *         http://www.simpleframework.net
 */
public class DefaultPageHandler extends AbstractMVCHandler implements IPageHandler {

	@Override
	public void pageLoad(final PageParameter pParameter, final Map<String, Object> dataBinding,
			final PageSelector selector) {
	}

	@Override
	public String getRole(final PageParameter pParameter) {
		return pParameter.getPageBean().getRole();
	}

	@Override
	public String getTitle(final PageParameter pParameter) {
		return pParameter.getPageBean().getTitle();
	}

	@Override
	public Object getBeanProperty(final PageParameter pParameter, final String beanProperty) {
		if ("role".equals(beanProperty)) {
			return getRole(pParameter);
		} else if ("title".equals(beanProperty)) {
			return getTitle(pParameter);
		}
		return BeanUtils.getProperty(pParameter.getPageBean(), beanProperty);
	}

	protected String[] addImportPage(final PageParameter pParameter, final String[] importPage) {
		final PageDocument pageDocument = pParameter.getPageDocument();
		final String[] importPage2;
		if (pageDocument == null
				|| (importPage2 = pageDocument.getPageBean().getImportPage()) == null
				|| importPage2.length == 0) {
			return importPage;
		}
		return ArrayUtils.add(importPage2, importPage);
	}

	@Override
	public void beforeComponentRender(final PageParameter pParameter) {
	}
}
