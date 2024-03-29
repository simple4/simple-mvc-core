package net.simpleframework.mvc.component;

import static net.simpleframework.common.I18n.$m;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Modifier;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import net.simpleframework.common.ClassUtils.IScanResourcesCallback;
import net.simpleframework.common.ClassUtils.ScanClassResourcesCallback;
import net.simpleframework.common.Convert;
import net.simpleframework.common.ObjectEx;
import net.simpleframework.common.bean.BeanUtils;
import net.simpleframework.common.script.IScriptEval;
import net.simpleframework.common.xml.XmlElement;
import net.simpleframework.mvc.IPageResourceProvider;
import net.simpleframework.mvc.PageDocument;
import net.simpleframework.mvc.PageParameter;
import net.simpleframework.mvc.PageResourceProviderRegistry;

/**
 * 这是一个开源的软件，请在LGPLv3下合法使用、修改或重新发布。
 * 
 * @author 陈侃(cknet@126.com, 13910090885)
 *         http://code.google.com/p/simpleframework/
 *         http://www.simpleframework.net
 */
public abstract class AbstractComponentRegistry extends ObjectEx implements IComponentRegistry {

	static Map<Class<?>, AbstractComponentRegistry> componentRegistryCache;
	static {
		componentRegistryCache = new ConcurrentHashMap<Class<?>, AbstractComponentRegistry>();
	}

	public static IComponentRegistry getComponentRegistry(
			final Class<? extends AbstractComponentBean> beanClass) {
		Class<?> beanClass2 = beanClass;
		while (true) {
			final IComponentRegistry componentRegistry = componentRegistryCache.get(beanClass2);
			if (componentRegistry != null) {
				return componentRegistry;
			}
			beanClass2 = beanClass2.getSuperclass();
			if (Modifier.isAbstract(beanClass2.getModifiers())) {
				break;
			}
		}
		return null;
	}

	public static IScanResourcesCallback newComponentRegistryCallback() {
		System.out.println($m("AbstractComponentRegistry.0"));
		final ComponentRegistryFactory factory = ComponentRegistryFactory.get();
		return new ScanClassResourcesCallback() {
			@Override
			public void doResources(final String filepath, final boolean isDirectory)
					throws IOException {
				final IComponentRegistry registry = newInstance(loadClass(filepath),
						IComponentRegistry.class);
				if (registry != null) {
					factory.registered(registry);
				}
			}
		};
	}

	public AbstractComponentRegistry() {
		final Class<? extends AbstractComponentBean> beanClass = getBeanClass();
		if (beanClass != null) {
			componentRegistryCache.put(beanClass, this);
		}
	}

	private <A extends Annotation> A getAnnotation(final Class<?> registryClass,
			final Class<A> annotationClass) {
		if (registryClass.equals(AbstractComponentRegistry.class)) {
			return null;
		}
		A a = registryClass.getAnnotation(annotationClass);
		if (a == null) {
			a = getAnnotation(registryClass.getSuperclass(), annotationClass);
		}
		return a;
	}

	@Override
	public String getComponentName() {
		final ComponentName a = getAnnotation(getClass(), ComponentName.class);
		return a != null ? a.value() : null;
	}

	protected Class<? extends AbstractComponentBean> getBeanClass() {
		final ComponentBean a = getAnnotation(getClass(), ComponentBean.class);
		return a != null ? a.value() : null;
	}

	protected Class<? extends IComponentRender> getRenderClass() {
		final ComponentRender a = getAnnotation(getClass(), ComponentRender.class);
		return a != null ? a.value() : null;
	}

	protected Class<? extends IComponentResourceProvider> getResourceProviderClass() {
		final ComponentResourceProvider a = getAnnotation(getClass(), ComponentResourceProvider.class);
		Class<? extends IComponentResourceProvider> provider = null;
		if (a != null) {
			provider = a.value();
		}
		return provider != null ? provider : DefaultComponentResourceProvider.class;
	}

	private IComponentRender componentRender;

	@Override
	public IComponentRender getComponentRender() {
		if (componentRender == null) {
			try {
				componentRender = getRenderClass().getConstructor(IComponentRegistry.class)
						.newInstance(this);
			} catch (final Exception e) {
				throw ComponentException.of(e);
			}
		}
		return componentRender;
	}

	private IComponentResourceProvider componentResourceProvider;

	@Override
	public IComponentResourceProvider getComponentResourceProvider() {
		if (componentResourceProvider == null) {
			try {
				componentResourceProvider = getResourceProviderClass().getConstructor(
						IComponentRegistry.class).newInstance(this);
			} catch (final Exception e) {
				throw ComponentException.of(e);
			}
		}
		return componentResourceProvider;
	}

	@Override
	public AbstractComponentBean createComponentBean(final PageParameter pParameter,
			final Object data) {
		AbstractComponentBean componentBean;
		try {
			componentBean = getBeanClass().getConstructor(PageDocument.class, XmlElement.class)
					.newInstance(pParameter.getPageDocument(), data instanceof XmlElement ? data : null);
		} catch (final Exception e) {
			throw ComponentException.of(e);
		}
		final IScriptEval scriptEval = pParameter.getScriptEval();
		if (scriptEval != null) {
			scriptEval.putVariable("bean", componentBean);
		}
		if (data instanceof XmlElement) {
			componentBean.parseElement(scriptEval);
		} else if (data instanceof Map) {
			for (final Map.Entry<?, ?> e : ((Map<?, ?>) data).entrySet()) {
				BeanUtils.setProperty(componentBean, Convert.toString(e.getKey()), e.getValue());
			}
		}
		return componentBean;
	}

	protected void initComponentBean(final PageParameter pParameter,
			final AbstractComponentBean componentBean) {
	}

	@Override
	public IPageResourceProvider getPageResourceProvider() {
		return PageResourceProviderRegistry.get().getPageResourceProvider(null);
	}

	public static String getLoadingContent() {
		return $m("AbstractComponentRegistry.loadingContent.0");
	}
}
