package net.simpleframework.mvc;

import java.util.Collections;
import java.util.Enumeration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.HttpSession;

/**
 * 这是一个开源的软件，请在LGPLv3下合法使用、修改或重新发布。
 * 
 * @author 陈侃(cknet@126.com, 13910090885)
 *         http://code.google.com/p/simpleframework/
 *         http://www.simpleframework.net
 */
public class LocalSessionCache {
	static LocalSessionAttribute lsAttribute = new LocalSessionAttribute();

	public static void put(final HttpSession httpSession, final Object key, final Object value) {
		lsAttribute.put(httpSession.getId(), key, value);
	}

	public static Object get(final HttpSession httpSession, final Object key) {
		return lsAttribute.get(httpSession.getId(), key);
	}

	public static Object remove(final HttpSession httpSession, final Object key) {
		return lsAttribute.remove(httpSession.getId(), key);
	}

	public static class LocalSessionAttribute implements ISessionAttribute {
		final Map<String, Map<Object, Object>> sObjects = new ConcurrentHashMap<String, Map<Object, Object>>();

		private Map<Object, Object> getAttributes(final String sessionId) {
			Map<Object, Object> attributes = sObjects.get(sessionId);
			if (attributes == null) {
				sObjects.put(sessionId, attributes = new ConcurrentHashMap<Object, Object>());
			}
			return attributes;
		}

		@Override
		public void put(final String sessionId, final Object key, final Object value) {
			if (key != null) {
				getAttributes(sessionId).put(key, value);
			}
		}

		@Override
		public Object get(final String sessionId, final Object key) {
			return key == null ? null : getAttributes(sessionId).get(key);
		}

		@Override
		public Object remove(final String sessionId, final Object key) {
			return key == null ? null : getAttributes(sessionId).remove(key);
		}

		@Override
		public void sessionDestroyed(final String sessionId) {
			sObjects.remove(sessionId);
		}

		@Override
		public Enumeration<?> getAttributeNames(final String sessionId) {
			return Collections.enumeration(getAttributes(sessionId).keySet());
		}
	}
}
