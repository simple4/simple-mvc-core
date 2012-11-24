package net.simpleframework.mvc;

/**
 * 这是一个开源的软件，请在LGPLv3下合法使用、修改或重新发布。
 * 
 * @author 陈侃(cknet@126.com, 13910090885)
 *         http://code.google.com/p/simpleframework/
 *         http://www.simpleframework.net
 */
public interface IForwardCallback<T extends IForward> {

	/**
	 * IForward的回调接口
	 * 
	 * @param t
	 */
	void doAction(T t);

	public static interface IJsonForwardCallback extends IForwardCallback<JsonForward> {
	}

	public static interface IJavascriptForwardCallback extends IForwardCallback<JavascriptForward> {
	}
}
