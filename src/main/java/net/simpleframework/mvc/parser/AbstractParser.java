package net.simpleframework.mvc.parser;

import net.simpleframework.common.ObjectEx;
import net.simpleframework.mvc.component.ComponentParameter;
import net.simpleframework.mvc.component.IComponentHandler;

/**
 * 这是一个开源的软件，请在LGPLv3下合法使用、修改或重新发布。
 * 
 * @author 陈侃(cknet@126.com, 13910090885)
 *         http://code.google.com/p/simpleframework/
 *         http://www.simpleframework.net
 */
public abstract class AbstractParser extends ObjectEx {

	protected void doBeforeRender(final ComponentParameter cParameter) {
		final IComponentHandler handle = cParameter.getComponentHandler();
		if (handle != null) {
			handle.beforeRender(cParameter);
		}
	}
}
