package net.simpleframework.mvc;

import java.io.IOException;
import java.util.Collection;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import net.simpleframework.common.ClassUtils;
import net.simpleframework.common.ClassUtils.IScanResourcesCallback;
import net.simpleframework.common.ConsoleThread;
import net.simpleframework.common.I18n;
import net.simpleframework.common.ObjectEx;
import net.simpleframework.common.StringUtils;
import net.simpleframework.common.Version;
import net.simpleframework.common.web.HttpUtils;
import net.simpleframework.ctx.ContextUtils;
import net.simpleframework.ctx.IModuleContextCallback;
import net.simpleframework.mvc.common.DeployUtils;
import net.simpleframework.mvc.component.AbstractComponentRegistry;
import net.simpleframework.mvc.ctx.permission.PermissionFilterListener;

/**
 * 这是一个开源的软件，请在LGPLv3下合法使用、修改或重新发布。
 * 
 * @author 陈侃(cknet@126.com, 13910090885)
 *         http://code.google.com/p/simpleframework/
 *         http://www.simpleframework.net
 */
public class MVCContext extends ObjectEx implements IMVCContext {
	private ServletContext servletContext;

	@Override
	public ServletContext getServletContext() {
		return servletContext;
	}

	@Override
	public void doInit(final ServletContext servletContext) throws Exception {
		this.servletContext = servletContext;

		MVCContextFactory.get().setContext(this);

		ConsoleThread.doInit();
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				shutdown();
			}
		});

		final Collection<IFilterListener> listeners = FilterUtils.getFilterListeners(servletContext);
		listeners.add(new UtilsFilterListener());
		listeners.add(new PermissionFilterListener());

		final MVCEventAdapter adapter = MVCEventAdapter.getInstance(servletContext);
		adapter.addListener(SessionCache.sessionListener);

		final String[] packageNames = getScanPackageNames();
		IScanResourcesCallback callback = I18n.getBasenamesCallback();
		for (final String packageName : packageNames) {
			ClassUtils.scanResources(packageName, callback);
		}
		callback = DeployUtils.newDeployResourcesCallback();
		for (final String packageName : packageNames) {
			ClassUtils.scanResources(packageName, callback);
		}
		callback = AbstractComponentRegistry.newComponentRegistryCallback();
		for (final String packageName : packageNames) {
			ClassUtils.scanResources(packageName, callback);
		}
	}

	protected void scanModuleContext(final IModuleContextCallback callback) throws Exception {
		ContextUtils.scanModuleContext(getScanPackageNames(), callback);
	}

	protected void shutdown() {
	}

	@Override
	public String[] getScanPackageNames() {
		return new String[] { "net.simpleframework" };
	}

	@Override
	public IPageResourceProvider getDefaultPageResourceProvider() {
		try {
			return (IPageResourceProvider) singleton(ClassUtils.forName(StringUtils.text(
					System.getProperty(IPageResourceProvider.DEFAULT_PAGERESOURCE_PROVIDER),
					"net.simpleframework.mvc.impl.DefaultPageResourceProvider")));
		} catch (final ClassNotFoundException e) {
			throw MVCException.of(e);
		}
	}

	@Override
	public IMultipartPageRequest createMultipartPageRequest(final HttpServletRequest request,
			final int maxUploadSize) throws IOException {
		return new MultipartPageRequest(request, maxUploadSize);
	}

	@Override
	public MVCHtmlBuilder getPageHtmlBuilder() {
		return singleton(MVCHtmlBuilder.class);
	}

	@Override
	public MVCConfig getPageConfig() {
		return singleton(MVCConfig.class);
	}

	@Override
	public HttpSession wrapHttpSession(final HttpSession httpSession) {
		return new PageSession(httpSession);
	}

	@Override
	public IMVCTemplate getTemplate(final AbstractMVCPage page) {
		/**
		 * 此方法由子类实现
		 */
		return null;
	}

	@Override
	public String getTitle() {
		return "simpleframework.net";
	}

	private Version version;

	@Override
	public Version getVersion() {
		if (version == null) {
			version = Version.getVersion("4.0");
		}
		return version;
	}

	@Override
	public boolean isSystemUrl(final PageRequestResponse rRequest) {
		final String requestURI = HttpUtils.getRequestURI(rRequest.request);
		if (requestURI.endsWith(".jsp")) {
			return true;
		}
		if (requestURI.indexOf(getPageConfig().getLoginUrl()) > -1) {
			return true;
		}
		return false;
	}

	private final String[] pKeys = new String[] { IForward.REQUEST_ID,
			PageDocument.XMLPATH_PARAMETER };

	@Override
	public String[] getSystemParamKeys() {
		return pKeys;
	}
}
