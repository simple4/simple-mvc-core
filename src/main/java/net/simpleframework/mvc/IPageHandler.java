package net.simpleframework.mvc;

import java.util.Map;

/**
 * 这是一个开源的软件，请在LGPLv3下合法使用、修改或重新发布。
 * 
 * @author 陈侃(cknet@126.com, 13910090885)
 *         http://code.google.com/p/simpleframework/
 *         http://www.simpleframework.net
 */
public interface IPageHandler {

	/**
	 * @param pParameter
	 * @param beanProperty
	 * @return
	 */
	Object getBeanProperty(PageParameter pParameter, String beanProperty);

	/**
	 * 获取页面的角色
	 * 
	 * @param pParameter
	 * @return
	 */
	String getRole(PageParameter pParameter);

	/**
	 * 获取页面的标题
	 * 
	 * @param pParameter
	 * @return
	 */
	String getTitle(PageParameter pParameter);

	/**
	 * 
	 * @param pParameter
	 * @param dataBinding
	 * @param selector
	 */
	void pageLoad(PageParameter pParameter, Map<String, Object> dataBinding, PageSelector selector);

	/**
	 * 
	 * @param pParameter
	 */
	void beforeComponentRender(PageParameter pParameter);

	public static class PageSelector {
		public String visibleToggleSelector;

		public String readonlySelector;

		public String disabledSelector;
	}
}
