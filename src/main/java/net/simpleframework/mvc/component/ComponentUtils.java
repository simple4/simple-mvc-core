package net.simpleframework.mvc.component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import net.simpleframework.common.ClassUtils;
import net.simpleframework.common.StringUtils;
import net.simpleframework.common.coll.KVMap;
import net.simpleframework.mvc.AbstractMVCPage;
import net.simpleframework.mvc.LocalSessionCache;
import net.simpleframework.mvc.MVCContextFactory;
import net.simpleframework.mvc.PageDocument;
import net.simpleframework.mvc.PageDocumentFactory;
import net.simpleframework.mvc.PageParameter;
import net.simpleframework.mvc.PageRequestResponse;

/**
 * 这是一个开源的软件，请在LGPLv3下合法使用、修改或重新发布。
 * 
 * @author 陈侃(cknet@126.com, 13910090885)
 *         http://code.google.com/p/simpleframework/
 *         http://www.simpleframework.net
 */
public abstract class ComponentUtils {

	public static String getResourceHomePath(
			final Class<? extends AbstractComponentBean> componentBeanClass) {
		return AbstractComponentRegistry.getComponentRegistry(componentBeanClass)
				.getComponentResourceProvider().getResourceHomePath();
	}

	public static String getCssResourceHomePath(final ComponentParameter cParameter) {
		final Class<? extends AbstractComponentBean> componentClass = cParameter.componentBean
				.getClass();
		return AbstractComponentRegistry.getComponentRegistry(componentClass)
				.getComponentResourceProvider().getCssResourceHomePath(cParameter, componentClass);
	}

	public static String getCssResourceHomePath(final PageParameter pParameter,
			final Class<? extends AbstractComponentBean> componentBeanClass) {
		return AbstractComponentRegistry.getComponentRegistry(componentBeanClass)
				.getComponentResourceProvider().getCssResourceHomePath(pParameter);
	}

	public static Map<String, AbstractComponentBean> allComponentsCache;
	static {
		allComponentsCache = new ConcurrentHashMap<String, AbstractComponentBean>();
	}

	public static AbstractComponentBean getComponent(final String hashId) {
		return allComponentsCache.get(hashId);
	}

	public static void putComponent(final AbstractComponentBean componentBean) {
		allComponentsCache.put(componentBean.hashId(), componentBean);
	}

	public static void removeComponent(final String hashId) {
		allComponentsCache.remove(hashId);
	}

	public static AbstractComponentBean getComponentBeanByHashId(final PageRequestResponse rRequest,
			final String hashId) {
		if (hashId == null) {
			return null;
		}
		final AbstractComponentBean componentBean = getComponent(hashId);
		if (componentBean != null) {
			return componentBean;
		}
		return (AbstractComponentBean) LocalSessionCache.get(rRequest.getSession(), hashId);
	}

	public static String getComponentHashByName(final PageDocument pageDocument,
			final String componentName) {
		return StringUtils.hash(pageDocument.hashId() + componentName);
	}

	public static AbstractComponentBean getComponentBeanByName(final PageRequestResponse rRequest,
			final String xmlpath, final String componentName) {
		final PageDocument pageDocument = PageDocumentFactory
				.getPageDocumentByPath(rRequest, xmlpath);
		return pageDocument != null ? PageParameter.get(rRequest, pageDocument)
				.getComponentBeanByName(componentName) : null;
	}

	/*--------------------------------- handle -----------------------------------*/

	public static String REQUEST_HANDLE_KEY = "@handleClass_";

	public static Map<String, Object> toFormParameters(final ComponentParameter cParameter) {
		final KVMap parameters = new KVMap();
		Map<String, Object> parameters2;
		final IComponentHandler hdl = cParameter.getComponentHandler();
		if (hdl != null && (parameters2 = hdl.getFormParameters(cParameter)) != null) {
			Object val;
			for (final String k : MVCContextFactory.ctx().getSystemParamKeys()) {
				parameters2.remove(k);
			}
			for (final Map.Entry<String, Object> entry : parameters2.entrySet()) {
				if ((val = entry.getValue()) != null) {
					parameters.put(entry.getKey(), val);
				}
			}
		}
		return parameters;
	}

	private static Map<String, IComponentHandler> handleMap = new ConcurrentHashMap<String, IComponentHandler>();

	public static IComponentHandler getComponentHandler(final PageRequestResponse rRequest,
			final AbstractComponentBean componentBean) {
		String stringClass = componentBean.getHandleClass();
		if (!StringUtils.hasText(stringClass)) {
			return null;
		}
		Class<?> handleClass;
		try {
			handleClass = ClassUtils.forName(stringClass);
		} catch (final ClassNotFoundException e) {
			throw ComponentHandleException.of(e);
		}

		if (AbstractMVCPage.class.isAssignableFrom(handleClass)) {
			stringClass += "#" + componentBean.getComponentRegistry().getComponentName();
		}
		IComponentHandler componentHandle = null;
		final EComponentHandlerScope handleScope = componentBean.getHandleScope();
		if (handleScope == EComponentHandlerScope.singleton) {
			componentHandle = handleMap.get(stringClass);
			if (componentHandle == null) {
				componentHandle = createComponentHandler(rRequest, componentBean, handleClass);
				handleMap.put(stringClass, componentHandle);
			}
		} else if (handleScope == EComponentHandlerScope.prototype) {
			stringClass = REQUEST_HANDLE_KEY + stringClass;
			componentHandle = (IComponentHandler) rRequest.getRequestAttr(stringClass);
			if (componentHandle == null) {
				componentHandle = createComponentHandler(rRequest, componentBean, handleClass);
				rRequest.setRequestAttr(stringClass, componentHandle);
			}
		}
		return componentHandle;
	}

	private static IComponentHandler createComponentHandler(final PageRequestResponse rRequest,
			final AbstractComponentBean componentBean, final Class<?> handleClass) {
		IComponentHandler componentHandle = null;
		if (AbstractMVCPage.class.isAssignableFrom(handleClass)) {
			componentHandle = ((AbstractMVCPage) ClassUtils.newInstance(handleClass))
					.createComponentHandler(componentBean);
		} else {
			try {
				componentHandle = (IComponentHandler) ClassUtils.invoke(
						handleClass.getMethod("getInstance"), null);
			} catch (final NoSuchMethodException e1) {
			}
			if (componentHandle == null) {
				try {
					componentHandle = (IComponentHandler) handleClass.newInstance();
				} catch (final Exception e) {
					throw ComponentHandleException.of(e);
				}
			}
			if (componentHandle != null) {
				componentHandle.handleCreated(ComponentParameter.get(rRequest, componentBean));
			}
		}
		return componentHandle;
	}
}
