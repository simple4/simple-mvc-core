package net.simpleframework.mvc;

import net.simpleframework.common.Convert;

/**
 * 这是一个开源的软件，请在LGPLv3下合法使用、修改或重新发布。
 * 
 * @author 陈侃(cknet@126.com, 13910090885)
 *         http://code.google.com/p/simpleframework/
 *         http://www.simpleframework.net
 */
public interface IPageResourceProvider extends IResourceProvider {

	static final String DEFAULT_PAGERESOURCE_PROVIDER = "default_pageresource_provider";

	/**
	 * 页面资源提供者的唯一名称
	 * 
	 * @return
	 */
	String getName();

	/**
	 * 输出到浏览器的初始化代码
	 * 
	 * @param pageParameter
	 * @return
	 */
	String getInitJavascriptCode(final PageParameter pParameter);

	public static abstract class AbstractPageResourceProvider extends AbstractResourceProvider
			implements IPageResourceProvider {

		@Override
		public boolean equals(final Object obj) {
			return getName().equalsIgnoreCase(Convert.toString(obj));
		}

		@Override
		public String toString() {
			return getName();
		}
	}
}
