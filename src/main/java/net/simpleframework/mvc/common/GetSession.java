package net.simpleframework.mvc.common;

import java.lang.ref.WeakReference;

import javax.servlet.http.HttpSession;

/**
 * 这是一个开源的软件，请在LGPLv3下合法使用、修改或重新发布。
 * 
 * @author 陈侃(cknet@126.com, 13910090885)
 *         http://code.google.com/p/simpleframework/
 *         http://www.simpleframework.net
 */
public abstract class GetSession {
	private static final ThreadLocal<WeakReference<HttpSession>> SESSION_LOCAL = new ThreadLocal<WeakReference<HttpSession>>();

	public static HttpSession getSession() {
		final WeakReference<HttpSession> ref = SESSION_LOCAL.get();
		if (ref != null) {
			return ref.get();
		} else {
			return null;
		}
	}

	public static void setSession(final HttpSession session) {
		if (session != null) {
			SESSION_LOCAL.set(new WeakReference<HttpSession>(session));
		} else {
			SESSION_LOCAL.set(null);
		}
	}
}
