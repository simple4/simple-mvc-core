package net.simpleframework.mvc;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.simpleframework.common.ClassUtils;
import net.simpleframework.common.Convert;
import net.simpleframework.common.JsonUtils;
import net.simpleframework.common.ObjectEx;
import net.simpleframework.common.StringUtils;
import net.simpleframework.common.ThrowableUtils;
import net.simpleframework.common.coll.ParameterMap;
import net.simpleframework.common.html.js.JavascriptUtils;
import net.simpleframework.common.web.HttpUtils;
import net.simpleframework.mvc.parser.PageParser;

/**
 * 这是一个开源的软件，请在LGPLv3下合法使用、修改或重新发布。
 * 
 * @author 陈侃(cknet@126.com, 13910090885)
 *         http://code.google.com/p/simpleframework/
 *         http://www.simpleframework.net
 */
public class MVCFilter extends ObjectEx implements Filter {

	public static final String PAGELOAD_TIME = "pageload_time";

	@Override
	public void init(final FilterConfig filterConfig) throws ServletException {
		try {
			final String initializerHandle = filterConfig.getInitParameter("pageContext");
			final IMVCContext mVCContext = StringUtils.hasText(initializerHandle) ? (IMVCContext) ClassUtils
					.newInstance(initializerHandle) : new MVCContext();
			mVCContext.doInit(filterConfig.getServletContext());
		} catch (final Exception ex) {
			throw new ServletException(ex);
		}
	}

	@Override
	public void doFilter(final ServletRequest request, final ServletResponse response,
			final FilterChain filterChain) throws IOException, ServletException {
		if (request instanceof HttpServletRequest && response instanceof HttpServletResponse) {
			final HttpServletResponse httpResponse = (HttpServletResponse) response;
			final PageRequest pageRequest = new PageRequest((HttpServletRequest) request);
			final PageRequestResponse rRequest = new PageRequestResponse(pageRequest, httpResponse);
			try {
				final HttpSession httpSession = pageRequest.getSession();
				final boolean bHttpRequest = rRequest.isHttpRequest();
				if (bHttpRequest) {
					httpSession.setAttribute(PAGELOAD_TIME, System.currentTimeMillis());
				}
				/* page document */
				final PageDocument pageDocument = PageDocumentFactory
						.getPageDocument(new PageRequestResponse(pageRequest, httpResponse));
				if (pageDocument == null) {
					if (!doFilter(rRequest, filterChain)) {
						return;
					}
					filterChain.doFilter(pageRequest, httpResponse);
				} else {
					final PageResponse pageResponse = new PageResponse(httpResponse, isGzip(rRequest));
					rRequest.response = pageResponse;
					final PageParameter pParameter = PageParameter.get(rRequest, pageDocument);

					if (bHttpRequest) {
						MVCUtils.setBrowserLocationPageDocument(httpSession, pageDocument);
					}

					/* after process */
					if (!doFilter(pParameter, filterChain)) {
						return;
					}

					final AbstractMVCPage abstractMVCPage = pParameter.getPage();
					final IForward forward = abstractMVCPage != null ? abstractMVCPage
							.forward(pParameter) : null;
					if (forward == null) {
						filterChain.doFilter(pageRequest, pageResponse);
					}
					if (pageResponse.isCommitted()) {
						return;
					}
					String responseString = forward != null ? forward.getResponseText(pParameter)
							: pageResponse.toString();
					if (!(forward instanceof JsonForward || forward instanceof JavascriptForward)) {
						final String contentType;
						if ((contentType = pageResponse.getContentType()) != null
								&& contentType.toLowerCase().startsWith("text/html")) {
							responseString = new PageParser(pParameter).parser(responseString).toHtml(
									pParameter);
						}
					}
					write(pParameter, responseString);
				}
			} catch (final Throwable e) {
				log.error(e);
				doThrowable(e, rRequest);
			}
		} else {
			filterChain.doFilter(request, response);
		}
	}

	private boolean isGzip(final PageRequestResponse rRequest) {
		final String browserEncodings = rRequest.getRequestHeader("accept-encoding");
		return ((browserEncodings != null) && (browserEncodings.indexOf("gzip") != -1))
				&& MVCContextFactory.config().isGzipResponse() && !rRequest.isHttpClientRequest();
	}

	private boolean doFilter(final PageRequestResponse rRequest, final FilterChain filterChain)
			throws Exception {
		for (final IFilterListener listener : FilterUtils.getFilterListeners(rRequest
				.getServletContext())) {
			if (!listener.doFilter(rRequest, filterChain)) {
				return false;
			}
		}
		initResponse(rRequest);
		return true;
	}

	private String getResponseCharset(final PageRequestResponse rRequest) {
		String encoding = null;
		if (rRequest instanceof PageParameter) {
			encoding = (String) ((PageParameter) rRequest)
					.getBeanProperty("responseCharacterEncoding");
		}
		if (!StringUtils.hasText(encoding)) {
			encoding = MVCContextFactory.config().getCharset();
		}
		return encoding;
	}

	private void initResponse(final PageRequestResponse rRequest) {
		final HttpServletResponse response = rRequest.response;
		final String encoding = getResponseCharset(rRequest);
		response.setCharacterEncoding(encoding);
		response.setContentType("text/html;charset=" + encoding);
		// WebUtils.setNoCache(httpResponse);
		if (rRequest.isHttpRequest()) {
			final Long l = (Long) rRequest.getSessionAttr(PAGELOAD_TIME);
			if (l != null) {
				HttpUtils.addCookie(response, PAGELOAD_TIME,
						(System.currentTimeMillis() - l.longValue()) / 1000d);
			}
		}
	}

	private void write(final PageRequestResponse rRequest, final String html) throws IOException {
		initResponse(rRequest);
		// resetBuffer() 只会清掉內容的部份(Body)，而不会去清 status code 和 header
		final HttpServletResponse response = rRequest.response;
		response.resetBuffer();
		final PrintWriter out = new PrintWriter(new OutputStreamWriter(response.getOutputStream(),
				getResponseCharset(rRequest)));
		if (response instanceof PageResponse) {
			((PageResponse) response).initOutputStream();
		}
		out.write(html);
		out.close();
	}

	private void doThrowable(Throwable th, final PageRequestResponse rRequest) throws IOException {
		th = ThrowableUtils.convertThrowable(th);
		if (rRequest.isAjaxRequest()) {
			final ParameterMap json = new ParameterMap();
			json.put("title", ThrowableUtils.getThrowableMessage(th));
			final String detail = Convert.toString(th);
			json.put("detail", detail);
			json.put("hash", StringUtils.hash(detail));
			write(rRequest, JavascriptUtils.wrapScriptTag("$error(" + JsonUtils.toJSON(json) + ");"));
		} else if (rRequest
				.loc(MVCUtils.getPageResourcePath() + "/jsp/error_template.jsp?systemErrorPage="
						+ MVCContextFactory.config().getErrorUrl())) {
			LocalSessionCache.put(rRequest.getSession(), IFilterListener.SESSION_THROWABLE, th);
		}
	}

	@Override
	public void destroy() {
	}
}