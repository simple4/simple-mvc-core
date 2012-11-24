package net.simpleframework.mvc;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import net.simpleframework.common.ObjectEx;
import net.simpleframework.common.StringUtils;

/**
 * 这是一个开源的软件，请在LGPLv3下合法使用、修改或重新发布。
 * 
 * @author 陈侃(cknet@126.com, 13910090885)
 *         http://code.google.com/p/simpleframework/
 *         http://www.simpleframework.net
 */
public class PageResourceProviderRegistry extends ObjectEx {

	public static PageResourceProviderRegistry get() {
		return singleton(PageResourceProviderRegistry.class);
	}

	private IPageResourceProvider defaultProvider;

	public PageResourceProviderRegistry() {
	}

	private final Map<String, IPageResourceProvider> providers = new ConcurrentHashMap<String, IPageResourceProvider>();

	public IPageResourceProvider getPageResourceProvider(final String name) {
		IPageResourceProvider provider;
		if (StringUtils.hasText(name) && (provider = providers.get(name)) != null) {
			return provider;
		}
		if (defaultProvider == null) {
			registered(defaultProvider = MVCContextFactory.ctx().getDefaultPageResourceProvider());
		}
		return defaultProvider;
	}

	public void registered(final IPageResourceProvider pageResourceProvider) {
		final String name = pageResourceProvider.getName();
		if (StringUtils.hasText(name)) {
			providers.put(name, pageResourceProvider);
		}
	}
}
