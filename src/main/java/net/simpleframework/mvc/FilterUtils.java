package net.simpleframework.mvc;

import java.util.ArrayList;
import java.util.Collection;

import javax.servlet.ServletContext;

/**
 * 这是一个开源的软件，请在LGPLv3下合法使用、修改或重新发布。
 * 
 * @author 陈侃(cknet@126.com, 13910090885)
 *         http://code.google.com/p/simpleframework/
 *         http://www.simpleframework.net
 */
public abstract class FilterUtils {
	private static final String FILTER_LISTENERS = "_filter_listeners";

	@SuppressWarnings("unchecked")
	public static Collection<IFilterListener> getFilterListeners(final ServletContext servletContext) {
		Collection<IFilterListener> coll = (Collection<IFilterListener>) servletContext
				.getAttribute(FILTER_LISTENERS);
		if (coll == null) {
			servletContext.setAttribute(FILTER_LISTENERS, coll = new ArrayList<IFilterListener>());
		}
		return coll;
	}
}