package net.simpleframework.mvc;

import java.io.Serializable;
import java.util.Enumeration;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionContext;

/**
 * 这是一个开源的软件，请在LGPLv3下合法使用、修改或重新发布。
 * 
 * @author 陈侃(cknet@126.com, 13910090885)
 *         http://code.google.com/p/simpleframework/
 *         http://www.simpleframework.net
 */
@SuppressWarnings("deprecation")
public class PageSession implements HttpSession {
	private final HttpSession httpSession;

	public PageSession(final HttpSession httpSession) {
		this.httpSession = httpSession;
	}

	@Override
	public String getId() {
		return httpSession.getId();
	}

	@Override
	public Object getAttribute(final String key) {
		return SessionCache.get(httpSession, key);
	}

	@Override
	public void setAttribute(final String key, final Object val) {
		SessionCache.put(httpSession, key, (Serializable) val);
	}

	@Override
	public void removeAttribute(final String key) {
		SessionCache.remove(httpSession, key);
	}

	@Override
	public Enumeration<?> getAttributeNames() {
		return SessionCache.getAttributeNames(httpSession);
	}

	@Override
	public void invalidate() {
		httpSession.invalidate();
	}

	@Override
	public boolean isNew() {
		return httpSession.isNew();
	}

	@Override
	public long getCreationTime() {
		return httpSession.getCreationTime();
	}

	@Override
	public long getLastAccessedTime() {
		return httpSession.getLastAccessedTime();
	}

	@Override
	public int getMaxInactiveInterval() {
		return httpSession.getMaxInactiveInterval();
	}

	@Override
	public void setMaxInactiveInterval(final int interval) {
		httpSession.setMaxInactiveInterval(interval);
	}

	@Override
	public ServletContext getServletContext() {
		return httpSession.getServletContext();
	}

	@Override
	public HttpSessionContext getSessionContext() {
		return httpSession.getSessionContext();
	}

	@Override
	public Object getValue(final String key) {
		return httpSession.getValue(key);
	}

	@Override
	public void putValue(final String key, final Object val) {
		httpSession.putValue(key, val);
	}

	@Override
	public void removeValue(final String key) {
		httpSession.removeValue(key);
	}

	@Override
	public String[] getValueNames() {
		return httpSession.getValueNames();
	}
}
