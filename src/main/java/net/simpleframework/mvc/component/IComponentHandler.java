package net.simpleframework.mvc.component;

import java.util.Map;

/**
 * 这是一个开源的软件，请在LGPLv3下合法使用、修改或重新发布。
 * 
 * @author 陈侃(cknet@126.com, 13910090885)
 *         http://code.google.com/p/simpleframework/
 *         http://www.simpleframework.net
 */
public interface IComponentHandler {

	void handleCreated(ComponentParameter cParameter);

	void beforeRender(ComponentParameter cParameter);

	/**
	 * 目的是通过该函数动态装载属性，在组件开发中需要通过调用该函数，否则使用者覆盖此类没有效果
	 * 
	 * @param compParameter
	 * @param beanProperty
	 * @return
	 */
	Object getBeanProperty(ComponentParameter cParameter, String beanProperty);

	/**
	 * 
	 * @param cParameter
	 * @return
	 */
	Map<String, Object> toJSON(ComponentParameter cParameter);

	/**
	 * 
	 * @param cParameter
	 * @return
	 */
	Map<String, Object> getFormParameters(ComponentParameter cParameter);
}