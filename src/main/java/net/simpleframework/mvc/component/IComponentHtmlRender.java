package net.simpleframework.mvc.component;

/**
 * 这是一个开源的软件，请在LGPLv3下合法使用、修改或重新发布。
 * 
 * @author 陈侃(cknet@126.com, 13910090885)
 *         http://code.google.com/p/simpleframework/
 *         http://www.simpleframework.net
 */
public interface IComponentHtmlRender extends IComponentRender {

	/**
	 * 生成组件的html代码
	 * 
	 * @param compParameter
	 * @return
	 */
	String getHtml(ComponentParameter cParameter);

	/**
	 * 生成执行的js代码
	 * 
	 * @param compParameter
	 * @return
	 */
	String getHtmlJavascriptCode(ComponentParameter cParameter);
}
