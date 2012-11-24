package net.simpleframework.mvc;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Enumeration;

import javax.servlet.ServletContext;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.simpleframework.common.Convert;
import net.simpleframework.common.OsUtils;
import net.simpleframework.common.OsUtils.OsInfo;
import net.simpleframework.common.web.HttpUtils;

/**
 * 这是一个开源的软件，请在LGPLv3下合法使用、修改或重新发布。
 * 
 * @author 陈侃(cknet@126.com, 13910090885)
 *         http://code.google.com/p/simpleframework/
 *         http://www.simpleframework.net
 */
public class PageRequestResponse {

	public HttpServletRequest request;

	public HttpServletResponse response;

	public PageRequestResponse(final HttpServletRequest request, final HttpServletResponse response) {
		this.request = request;
		this.response = response;
	}

	private HttpServletRequest getPageRequest(HttpServletRequest request) {
		while (request instanceof HttpServletRequestWrapper) {
			if (!(request instanceof PageRequest)) {
				request = (HttpServletRequest) ((HttpServletRequestWrapper) request).getRequest();
			} else {
				return request;
			}
		}
		return request;
	}

	// Request Wrapper

	public String getParameter(final String key) {
		return request.getParameter(key);
	}

	public Enumeration<?> getParameterNames() {
		return request.getParameterNames();
	}

	public Object getRequestAttr(final String key) {
		return getPageRequest(request).getAttribute(key);
	}

	public void setRequestAttr(final String key, final Object value) {
		getPageRequest(request).setAttribute(key, value);
	}

	public void removeRequestAttr(final String key) {
		getPageRequest(request).removeAttribute(key);
	}

	public Enumeration<?> getRequestAttrNames() {
		return request.getAttributeNames();
	}

	public String getRequestURI() {
		return request.getRequestURI();
	}

	public Enumeration<?> getRequestHeaderNames() {
		return request.getHeaderNames();
	}

	public String getRequestHeader(final String key) {
		return request.getHeader(key);
	}

	public Enumeration<?> getRequestHeaders(final String name) {
		return request.getHeaders(name);
	}

	public int getServletPort() {
		return request.getServerPort();
	}

	public String getRequestScheme() {
		return request.getScheme();
	}

	public String getRequestCharacterEncoding() {
		return request.getCharacterEncoding();
	}

	public void setRequestCharacterEncoding(final String encoding)
			throws UnsupportedEncodingException {
		request.setCharacterEncoding(encoding);
	}

	public Cookie[] getRequestCookies() {
		return request.getCookies();
	}

	// Response Wrapper

	public PrintWriter getResponseWriter() throws IOException {
		return response.getWriter();
	}

	// Session Wrapper

	public HttpSession getSession() {
		return request.getSession(true);
	}

	public Object getSessionAttr(final String key) {
		return getSession().getAttribute(key);
	}

	public void setSessionAttr(final String key, final Object value) {
		getSession().setAttribute(key, value);
	}

	public void removeSessionAttr(final String key) {
		getSession().removeAttribute(key);
	}

	// ServletContext Wrapper

	public ServletContext getServletContext() {
		return getSession().getServletContext();
	}

	public String getContextPath() {
		return getServletContext().getContextPath();
	}

	// utils

	public boolean isHttpClientRequest() {
		return HttpUtils.getUserAgent(request).indexOf("HttpClient") > -1;
	}

	public boolean isAjaxRequest() {
		return "XMLHttpRequest".equals(request.getHeader("X-Requested-With"))
				|| Convert.toBool(getParameter("ajax_request"));
	}

	/**
	 * 获得请求客户端机器信息
	 * 
	 * @return OsInfo
	 */
	public OsInfo getOsInfo() {

		return OsUtils.get(request);
	}

	public boolean isHttpRequest() {
		return !isAjaxRequest() && !isHttpClientRequest();
	}

	public String wrapContextPath(final String url) {
		return HttpUtils.wrapContextPath(request, url);
	}

	public boolean loc(final String url) throws IOException {
		return HttpUtils.loc(request, response, url);
	}

	public OutputStream getBinaryOutputStream(final String filename) throws IOException {
		return getBinaryOutputStream(filename, 0);
	}

	public OutputStream getBinaryOutputStream(final String filename, final long filesize)
			throws IOException {
		return HttpUtils.getBinaryOutputStream(request, response, filename, filesize);
	}

	public static PageRequestResponse get(final HttpServletRequest request,
			final HttpServletResponse response) {
		final String key = PageRequestResponse.class.getName();
		PageRequestResponse rRequest = (PageRequestResponse) request.getAttribute(key);
		if (rRequest == null) {
			request.setAttribute(key, rRequest = new PageRequestResponse(request, response));
		}
		return rRequest;
	}
}
