package net.simpleframework.mvc;

import java.io.Serializable;
import java.util.Enumeration;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

/**
 * 这是一个开源的软件，请在LGPLv3下合法使用、修改或重新发布。
 * 
 * @author 陈侃(cknet@126.com, 13910090885)
 *         http://code.google.com/p/simpleframework/
 *         http://www.simpleframework.net
 */
public class SessionCache {
	private static ISessionAttribute sAttribute;

	public static void registSessionAttribute(final ISessionAttribute sAttribute) {
		SessionCache.sAttribute = sAttribute;
	}

	private static ISessionAttribute getSessionAttribute() {
		if (sAttribute == null) {
			sAttribute = LocalSessionCache.lsAttribute;
		}
		return sAttribute;
	}

	public static HttpSessionListener sessionListener = new HttpSessionListener() {

		@Override
		public void sessionCreated(final HttpSessionEvent httpSessionEvent) {
		}

		@Override
		public void sessionDestroyed(final HttpSessionEvent httpSessionEvent) {
			final String sessionId = httpSessionEvent.getSession().getId();
			// System.out.println("remove objects from session: " + sessionId);
			LocalSessionCache.lsAttribute.sessionDestroyed(sessionId);
			final ISessionAttribute sAttribute = getSessionAttribute();
			if (sAttribute != LocalSessionCache.lsAttribute) {
				sAttribute.sessionDestroyed(sessionId);
			}
		}
	};

	public static void put(final HttpSession httpSession, final Serializable key,
			final Serializable value) {
		getSessionAttribute().put(httpSession.getId(), key, value);
	}

	public static Object get(final HttpSession httpSession, final Serializable key) {
		return getSessionAttribute().get(httpSession.getId(), key);
	}

	public static Object remove(final HttpSession httpSession, final Serializable key) {
		return getSessionAttribute().remove(httpSession.getId(), key);
	}

	public static Enumeration<?> getAttributeNames(final HttpSession httpSession) {
		return getSessionAttribute().getAttributeNames(httpSession.getId());
	}
}
