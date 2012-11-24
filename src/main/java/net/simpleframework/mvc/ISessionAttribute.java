package net.simpleframework.mvc;

import java.util.Enumeration;

/**
 * 这是一个开源的软件，请在LGPLv3下合法使用、修改或重新发布。
 * 
 * @author 陈侃(cknet@126.com, 13910090885)
 *         http://code.google.com/p/simpleframework/
 *         http://www.simpleframework.net
 */
public interface ISessionAttribute {

	/**
	 * @param sessionId
	 * @param key
	 * @param value
	 */
	void put(String sessionId, Object key, Object value);

	/**
	 * @param sessionId
	 * @param key
	 * @return
	 */
	Object get(String sessionId, Object key);

	/**
	 * @param sessionId
	 * @param key
	 * @return
	 */
	Object remove(String sessionId, Object key);

	/**
	 * 获取所有属性的名称
	 * 
	 * @param sessionId
	 * @return
	 */
	Enumeration<?> getAttributeNames(String sessionId);

	/**
	 * 当session销毁时触发
	 * 
	 * @param sessionId
	 */
	void sessionDestroyed(String sessionId);
}
