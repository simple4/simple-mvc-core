package net.simpleframework.mvc;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import net.simpleframework.common.ClassUtils;
import net.simpleframework.common.Convert;
import net.simpleframework.common.StringUtils;
import net.simpleframework.common.coll.KVMap;
import net.simpleframework.common.coll.ParameterMap;
import net.simpleframework.common.html.element.EInputType;
import net.simpleframework.common.html.element.InputElement;
import net.simpleframework.common.html.element.Meta;
import net.simpleframework.common.web.HttpUtils;
import net.simpleframework.mvc.component.AbstractComponentBean;
import net.simpleframework.mvc.component.ComponentHandleException;
import net.simpleframework.mvc.component.IComponentHandler;

/**
 * 这是一个开源的软件，请在LGPLv3下合法使用、修改或重新发布。
 * 
 * @author 陈侃(cknet@126.com, 13910090885)
 *         http://code.google.com/p/simpleframework/
 *         http://www.simpleframework.net
 */
public abstract class AbstractMVCPage extends AbstractMVCHandler {

	protected PageDocument pageDocument;

	private String lookupPath;

	public AbstractMVCPage() {
		this.enableAttributes();
	}

	/**
	 * 页面创建时运行
	 * 
	 * @param pParameter
	 */
	protected void onCreate(final PageParameter pParameter) {
	}

	/**
	 * page类的初始化方法，每次调用该页面时运行
	 * 
	 * @param pageParameter
	 */
	protected void onInit(final PageParameter pParameter) {
	}

	public PageBean getPageBean() {
		return pageDocument.getPageBean();
	}

	private final Map<Class<?>, ParameterMap> htmlViewVariables;
	{
		htmlViewVariables = new HashMap<Class<?>, ParameterMap>();
	}

	/**
	 * 添加模板中定义的html文件变量
	 * 
	 * @param pageClass
	 * @param variable
	 * @param htmlFilename
	 */
	protected void addHtmlViewVariable(final Class<?> pageClass, final String variable,
			final String htmlFilename) {
		ParameterMap htmlViews = htmlViewVariables.get(pageClass);
		if (htmlViews == null) {
			htmlViewVariables.put(pageClass, htmlViews = new ParameterMap());
		}
		for (final ParameterMap m : htmlViewVariables.values()) {
			if (m.remove(variable) != null) {
				break;
			}
		}
		htmlViews.put(variable, htmlFilename);
	}

	protected void addHtmlViewVariable(final Class<?> pageClass, final String variable) {
		addHtmlViewVariable(pageClass, variable, pageClass.getSimpleName() + ".html");
	}

	protected String getMethod(final PageParameter pParameter) {
		return pParameter.getParameter("method");
	}

	public IForward forward(final PageParameter pParameter) {
		onInit(pParameter);
		final String methodStr = getMethod(pParameter);
		if (StringUtils.hasText(methodStr)) {
			try {
				return (IForward) ClassUtils.invoke(getClass()
						.getMethod(methodStr, PageParameter.class), this, pParameter);
			} catch (final NoSuchMethodException e) {
				log.warn(e);
			}
		}
		try {
			return new TextForward(getPageForward(pParameter, getClass(), createVariables(pParameter)));
		} catch (final IOException e) {
			log.warn(e);
		}
		return null;
	}

	/**
	 * 子类继承，输出page的html
	 * 
	 * @param pParameter
	 * @param pageClass
	 * @param variables
	 * @param currentVariable
	 * @return
	 * @throws IOException
	 */
	protected String toHtml(final PageParameter pParameter,
			final Class<? extends AbstractMVCPage> pageClass, final Map<String, Object> variables,
			final String currentVariable) throws IOException {
		if (getClass().equals(pageClass)) {
			return toHtml(pParameter, variables, currentVariable);
		}
		return null;
	}

	protected String toHtml(final PageParameter pParameter, final Map<String, Object> variables,
			final String currentVariable) throws IOException {
		return null;
	}

	/**
	 * 模板的核心方法，用户覆盖此函数可以设置不同的模板引擎
	 * 
	 * @param htmlStream
	 * @param variables
	 * @return
	 * @throws IOException
	 */
	protected abstract String replaceExpr(final PageParameter pParameter,
			final InputStream htmlStream, final Map<String, Object> variables) throws IOException;

	private static final String $html = "$html$";

	private String getPageForward(final PageParameter pParameter,
			final Class<? extends AbstractMVCPage> pageClass, final Map<String, Object> variables)
			throws IOException {
		@SuppressWarnings("unchecked")
		final Class<? extends AbstractMVCPage> superPageClass = (Class<? extends AbstractMVCPage>) pageClass
				.getSuperclass();
		if (!isExtend(pParameter, pageClass) || superPageClass.equals(AbstractMVCPage.class)) {
			final InputStream htmlStream = getResource(pageClass, ".html");
			if (htmlStream != null) {
				return replaceExpr(pParameter, htmlStream, variables);
			} else {
				final String html = toHtml(pParameter, pageClass, variables, null);
				return StringUtils.hasText(html) ? html : (String) variables.get($html);
			}
		} else {
			final ParameterMap htmlViews = htmlViewVariables.get(pageClass);
			if (htmlViews == null || htmlViews.size() == 0) {
				final InputStream htmlStream = getResource(pageClass, ".html");
				final String html = htmlStream != null ? replaceExpr(pParameter, htmlStream, variables)
						: toHtml(pParameter, pageClass, variables, null);
				if (html != null) {
					variables.put($html, html);
				}
			} else {
				for (final Map.Entry<String, String> entry : htmlViews.entrySet()) {
					final String key = entry.getKey();
					final String filename = entry.getValue();
					final InputStream htmlStream = getResource(pageClass, filename);
					if (htmlStream != null) {
						variables.put(key, replaceExpr(pParameter, htmlStream, variables));
					} else {
						final String text = toHtml(pParameter, pageClass, variables, key);
						if (StringUtils.hasText(text)) {
							variables.put(key, text);
						}
					}
					if (!variables.containsKey(key)) {
						variables.put(key, "");
					}
				}
			}
			return getPageForward(pParameter, superPageClass, variables);
		}
	}

	public String getResourceHomePath() {
		return getResourceHomePath(getClass());
	}

	public String getResourceHomePath(final Class<?> pageClass) {
		return pageDocument.getPageResourceProvider().getResourceHomePath(pageClass);
	}

	public String getCssResourceHomePath(final PageParameter pParameter) {
		return getCssResourceHomePath(pParameter, getClass());
	}

	public String getCssResourceHomePath(final PageParameter pParameter, final Class<?> pageClass) {
		return pageDocument.getPageResourceProvider().getCssResourceHomePath(pParameter, pageClass);
	}

	public String getInitJavascriptCode(final PageParameter pParameter) {
		return null;
	}

	/**
	 * 定义模板中可见的变量
	 * 
	 * @param pageParameter
	 * @return
	 */
	public Map<String, Object> createVariables(final PageParameter pParameter) {
		return new KVMap().setNullVal("").add("page", this).add("parameter", pParameter)
				.add("pagePath", getPagePath()).add("StringUtils", StringUtils.class)
				.add("Convert", Convert.class);
	}

	public static <T extends AbstractMVCPage> T get(final Class<T> pageClass) {
		return singleton(pageClass);
	}

	@SuppressWarnings("unchecked")
	public static <T extends AbstractMVCPage> T get(final PageParameter pParameter) {
		return (T) pParameter.getPage();
	}

	private static final Map<Class<? extends AbstractMVCPage>, String> urlsCache = new ConcurrentHashMap<Class<? extends AbstractMVCPage>, String>();

	/**
	 * 生成page类的访问url
	 * 
	 * @param clazz
	 * @param queryString
	 * @return
	 */
	public static String uriFor(final Class<? extends AbstractMVCPage> clazz,
			final String queryString) {
		final MVCConfig pConfig = MVCContextFactory.config();
		String url = urlsCache.get(clazz);
		if (!StringUtils.hasText(url)) {
			url = pConfig != null ? pConfig.getPagePath() : "/";
			if (!url.endsWith("/")) {
				url += "/";
			}
			final ParameterMap pagePackages = pConfig != null ? pConfig.getPagePackages() : null;
			final String className = clazz.getName();
			String val = null;
			if (pagePackages != null) {
				for (final Map.Entry<String, String> entry : pagePackages.entrySet()) {
					val = entry.getValue();
					if (className.startsWith(val)) {
						final String key = entry.getKey();
						url += key.startsWith("/") ? key.substring(1) : key;
						if (!url.endsWith("/")) {
							url += "/";
						}
						break;
					} else {
						val = null;
					}
				}
			}
			urlsCache.put(
					clazz,
					url += StringUtils.replace(val != null ? className.substring(val.length() + 1)
							: className, ".", "-"));
		}
		if (StringUtils.hasText(queryString)) {
			url += "?" + queryString;
		}
		return url;
	}

	public static String uriFor(final Class<? extends AbstractMVCPage> clazz) {
		return uriFor(clazz, null);
	}

	protected boolean isExtend(final PageParameter pParameter,
			final Class<? extends AbstractMVCPage> pageClass) {
		return true;
	}

	protected String getPagePath() {
		return MVCContextFactory.config().getPagePath();
	}

	protected String getChartset() {
		return MVCContextFactory.config().getCharset();
	}

	public String getLookupPath() {
		return lookupPath;
	}

	public void setLookupPath(final String lookupPath) {
		this.lookupPath = lookupPath;
	}

	public void addImportPage(final Class<? extends AbstractMVCPage>... pageClass) {
		if (pageClass == null || pageClass.length == 0) {
			return;
		}
		final LinkedHashSet<String> l = new LinkedHashSet<String>();
		final PageBean pageBean = getPageBean();
		final String[] oImportPage = pageBean.getImportPage();
		if (oImportPage != null) {
			l.addAll(Arrays.asList(oImportPage));
		}
		for (int i = 0; i < pageClass.length; i++) {
			l.add(pageClass[i].getName().replace(".", "/") + ".xml");
		}
		pageBean.setImportPage(l.toArray(new String[l.size()]));
	}

	public void addImportJavascript(final String... importJavascript) {
		if (importJavascript == null || importJavascript.length == 0) {
			return;
		}
		final LinkedHashSet<String> l = new LinkedHashSet<String>();
		final PageBean pageBean = getPageBean();
		final String[] oImportJavascript = pageBean.getImportJavascript();
		if (oImportJavascript != null) {
			l.addAll(Arrays.asList(oImportJavascript));
		}
		for (final String js : importJavascript) {
			l.add(js);
		}
		pageBean.setImportJavascript(importJavascript);
	}

	public void addImportCSS(final String... importCSS) {
		if (importCSS == null || importCSS.length == 0) {
			return;
		}
		final LinkedHashSet<String> l = new LinkedHashSet<String>();
		final PageBean pageBean = getPageBean();
		final String[] oImportCSS = pageBean.getImportCSS();
		if (oImportCSS != null) {
			l.addAll(Arrays.asList(oImportCSS));
		}
		for (final String css : importCSS) {
			l.add(css);
		}
		pageBean.setImportCSS(l.toArray(new String[l.size()]));
	}

	/**
	 * 设置页面的meta
	 * 
	 * @param pParameter
	 * @return
	 */
	public Collection<Meta> meta(final PageParameter pParameter) {
		final String redirectUrl = getRedirectUrl(pParameter);
		if (StringUtils.hasText(redirectUrl)) {
			return Arrays.asList(new Meta("refresh", "0;url="
					+ HttpUtils.wrapContextPath(pParameter.request, redirectUrl)));
		}
		return null;
	}

	protected String getRedirectUrl(final PageParameter pParameter) {
		return null;
	}

	public String getRole(final PageParameter pParameter) {
		return null;
	}

	public String getTitle(final PageParameter pParameter) {
		return null;
	}

	/*--------------------------------- Component Utils   ---------------------------------*/

	public String buildInputHidden(final PageParameter pParameter, final String... names) {
		final StringBuilder sb = new StringBuilder();
		if (names != null) {
			for (final String name : names) {
				sb.append(new InputElement().setInputType(EInputType.hidden).setName(name)
						.setText(pParameter.getParameter(name)));
			}
		}
		return sb.toString();
	}

	/*--------------------------------- static ---------------------------------*/

	protected static InputStream getResource(final Class<?> resourceClass, final String filename) {
		return ClassUtils.getResourceAsStream(resourceClass,
				filename.startsWith(".") ? resourceClass.getSimpleName() + filename : filename);
	}

	public static final String NULL_PAGEDOCUMENT = "@null_pagedocument";

	static PageDocument createPageDocument(final Class<?> pageClass,
			final PageRequestResponse rRequest) {
		PageDocument pageDocument = null;
		InputStream inputStream = getResource(pageClass, ".xml");
		if (inputStream == null) {
			rRequest.setRequestAttr(NULL_PAGEDOCUMENT, Boolean.TRUE);
			inputStream = getResource(AbstractMVCPage.class, "page-null.xml");
		}
		try {
			pageDocument = new PageDocument(pageClass, inputStream, rRequest);
		} catch (final IOException e) {
		}
		return pageDocument;
	}

	/*--------------------------------- components wrapper  ---------------------------------*/

	protected <T extends AbstractComponentBean> T addComponentBean(final PageParameter pParameter,
			final Map<String, Object> attris, final Class<T> beanClass) {
		return pParameter.addComponentBean(attris, beanClass);
	}

	protected <T extends AbstractComponentBean> T addComponentBean(final PageParameter pParameter,
			final String name, final Class<T> beanClass) {
		return pParameter.addComponentBean(name, beanClass);
	}

	@SuppressWarnings("unchecked")
	protected <T extends AbstractComponentBean> T addComponentBean(final PageParameter pParameter,
			final Class<T> beanClass, final Class<? extends IComponentHandler> handleClass) {
		return (T) addComponentBean(pParameter, handleClass.getSimpleName(), beanClass)
				.setHandleClass(handleClass);
	}

	/**
	 * 
	 * @param pageParameter
	 * @param beanProperty
	 * @return
	 */
	public Object getBeanProperty(final PageParameter pParameter, final String beanProperty) {
		return null;
	}

	public static class PageLoad extends DefaultPageHandler {

		@Override
		public Object getBeanProperty(final PageParameter pParameter, final String beanProperty) {
			final Object property = AbstractMVCPage.get(pParameter).getBeanProperty(pParameter,
					beanProperty);
			return property != null ? property : super.getBeanProperty(pParameter, beanProperty);
		}

		@Override
		public String getRole(final PageParameter pParameter) {
			final String role = AbstractMVCPage.get(pParameter).getRole(pParameter);
			return role != null ? role : super.getRole(pParameter);
		}

		@Override
		public String getTitle(final PageParameter pParameter) {
			final String title = AbstractMVCPage.get(pParameter).getTitle(pParameter);
			return title != null ? title : super.getTitle(pParameter);
		}

		@Override
		@SuppressWarnings("unchecked")
		public void pageLoad(final PageParameter pParameter, final Map<String, Object> dataBinding,
				final PageSelector selector) {
			final String handleClass = (String) pParameter.getBeanProperty("handleClass");
			if (!StringUtils.hasText(handleClass)) {
				return;
			}
			try {
				((Class<AbstractMVCPage>) ClassUtils.forName(handleClass)).getMethod(
						(String) pParameter.getBeanProperty("handleMethod"), PageParameter.class,
						Map.class, PageSelector.class).invoke(AbstractMVCPage.get(pParameter),
						pParameter, dataBinding, selector);
			} catch (final Exception e) {
				throw ComponentHandleException.of(e);
			}
		}
	}

	public IComponentHandler createComponentHandler(final AbstractComponentBean componentBean) {
		return null;
	}

	protected boolean isInheritedPage(final Class<?> pageClass) {
		final Class<?> pageClass2 = getClass();
		return !(Modifier.isAbstract(pageClass2.getModifiers()) || pageClass2.equals(pageClass));
	}
}
