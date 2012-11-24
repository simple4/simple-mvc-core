package net.simpleframework.mvc;

import java.io.File;
import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import net.simpleframework.common.ClassUtils;
import net.simpleframework.common.StringUtils;
import net.simpleframework.common.coll.ParameterMap;
import net.simpleframework.common.web.HttpUtils;

/**
 * 这是一个开源的软件，请在LGPLv3下合法使用、修改或重新发布。
 * 
 * @author 陈侃(cknet@126.com, 13910090885)
 *         http://code.google.com/p/simpleframework/
 *         http://www.simpleframework.net
 */
public class PageDocumentFactory {

	/**
	 * 缓存查询路径下的类
	 */
	private static Map<String, Class<?>> pageClassCache;
	static {
		pageClassCache = new ConcurrentHashMap<String, Class<?>>();
	}

	public static PageDocument getPageDocument(final PageRequestResponse rRequest) {
		String xmlpath = rRequest.getParameter(PageDocument.XMLPATH_PARAMETER);
		if (StringUtils.hasText(xmlpath)) {
			return getPageDocumentAndCreate(new File(MVCUtils.getRealPath(xmlpath)), rRequest);
		} else {
			String lookupPath = HttpUtils.stripContextPath(rRequest.request,
					HttpUtils.getRequestURI(rRequest.request, false));
			int pos;
			if ((pos = lookupPath.indexOf(";")) > 0) {
				lookupPath = lookupPath.substring(0, pos); // strip jsessionid
			}

			final PageDocument pageDocument = getPageDocument(rRequest, lookupPath);
			if (pageDocument != null) {
				return pageDocument;
			} else {
				final int p = lookupPath.lastIndexOf('.');
				xmlpath = MVCUtils.getRealPath(((p <= 0) ? lookupPath : lookupPath.substring(0, p))
						+ ".xml");
				return getPageDocumentAndCreate(new File(xmlpath), rRequest);
			}
		}
	}

	private static PageDocument getPageDocument(final PageRequestResponse rRequest,
			final String lookupPath) {
		final String pagePath = MVCContextFactory.config().getPagePath();
		PageDocument pageDocument = null;
		if (StringUtils.hasText(pagePath) && lookupPath.startsWith(pagePath)) {
			Class<?> pageClass = pageClassCache.get(lookupPath);
			if (pageClass == null) {
				String clazzName = lookupPath.substring(pagePath.length());
				if (!StringUtils.hasText(clazzName) || "/".equals(clazzName)) {
					final String homeUrl = MVCContextFactory.config().getHomeUrl();
					if (homeUrl.startsWith(pagePath)) {
						clazzName = homeUrl.substring(pagePath.length());
					}
				}
				if (StringUtils.hasText(clazzName)) {
					String pagePackage = null;
					int pos;
					if ((pos = clazzName.lastIndexOf("/") + 1) > 0) {
						final ParameterMap packages = MVCContextFactory.config().getPagePackages();
						if (packages != null) {
							pagePackage = packages.get(clazzName.substring(0, pos - 1));
						}
						clazzName = clazzName.substring(pos);
					}
					clazzName = StringUtils.replace(clazzName, "-", ".");
					if (pagePackage != null) {
						clazzName = pagePackage + "." + clazzName;
					}
					try {
						pageClass = ClassUtils.forName(clazzName);
					} catch (final ClassNotFoundException e) {
					}
				}
			}
			if (pageClass != null) {
				pageDocument = getPageDocumentAndCreate(pageClass, rRequest);
				pageClassCache.put(lookupPath, pageClass);
			}
			AbstractMVCPage abstractMVCPage;
			if (pageDocument != null
					&& (abstractMVCPage = PageParameter.get(rRequest, pageDocument).getPage()) != null) {
				abstractMVCPage.setLookupPath(lookupPath);
			}
		}
		return pageDocument;
	}

	private static Map<String, PageDocument> documentMap = new ConcurrentHashMap<String, PageDocument>();

	static PageDocument getPageDocument(final String docHash) {
		return docHash != null ? documentMap.get(docHash) : null;
	}

	public synchronized static PageDocument getPageDocumentAndCreate(Object sourceObject,
			final PageRequestResponse rRequest) {
		if (sourceObject == null) {
			return null;
		}

		String docHash = null;
		if (sourceObject instanceof File && ((File) sourceObject).exists()) {
			docHash = StringUtils.hash(sourceObject);
		} else if (sourceObject instanceof Object[]) {
			docHash = (String) ((Object[]) sourceObject)[1];
			sourceObject = ((Object[]) sourceObject)[0];
		} else if (sourceObject instanceof Class<?>) {
			docHash = ((Class<?>) sourceObject).getName();
		}

		if (docHash == null) {
			return null;
		}
		PageDocument document = documentMap.get(docHash);
		if (document != null && document.isModified()) {
			document = null;
		}
		if (document == null) {
			try {
				if (sourceObject instanceof File) {
					documentMap.put(docHash, document = new PageDocument((File) sourceObject, rRequest));
				} else if (sourceObject instanceof Class<?>) {
					document = AbstractMVCPage.createPageDocument((Class<?>) sourceObject, rRequest);
					if (document != null) {
						documentMap.put(docHash, document);
					}
				} else if (sourceObject instanceof InputStream) {
					documentMap.put(docHash, document = new PageDocument((InputStream) sourceObject,
							rRequest));
				}
			} catch (final Exception e) {
				throw MVCException.of(e);
			}
		} else {
			document.setFirstCreated(false);
		}
		return document;
	}

	public static PageDocument getPageDocumentByPath(final PageRequestResponse rRequest,
			final String xmlPath) {
		return PageDocumentFactory.getPageDocumentAndCreate(new File(MVCUtils.getRealPath(xmlPath)),
				rRequest);
	}
}
