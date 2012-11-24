package net.simpleframework.mvc;

import static net.simpleframework.common.I18n.$m;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.simpleframework.common.ObjectEx;
import net.simpleframework.common.StringUtils;
import net.simpleframework.common.bean.BeanUtils;
import net.simpleframework.common.coll.KVMap;
import net.simpleframework.common.script.IScriptEval;
import net.simpleframework.common.script.ScriptEvalFactory;
import net.simpleframework.mvc.component.AbstractComponentBean;
import net.simpleframework.mvc.component.AbstractComponentRegistry;
import net.simpleframework.mvc.component.ComponentException;
import net.simpleframework.mvc.component.ComponentParameter;
import net.simpleframework.mvc.component.ComponentUtils;
import net.simpleframework.mvc.component.IComponentHandler;

/**
 * 这是一个开源的软件，请在LGPLv3下合法使用、修改或重新发布。
 * 
 * @author 陈侃(cknet@126.com, 13910090885)
 *         http://code.google.com/p/simpleframework/
 *         http://www.simpleframework.net
 */
public class PageParameter extends PageRequestResponse {

	private IScriptEval scriptEval;

	private final PageDocument pageDocument;

	public PageParameter(final HttpServletRequest request, final HttpServletResponse response,
			final PageDocument pageDocument) {
		super(request, response);
		this.pageDocument = pageDocument;
	}

	public PageDocument getPageDocument() {
		return pageDocument;
	}

	public IScriptEval getScriptEval() {
		return scriptEval;
	}

	public IScriptEval createScriptEval() {
		scriptEval = ScriptEvalFactory.createDefaultScriptEval(MVCUtils.createVariables(this));
		return scriptEval;
	}

	public Map<String, AbstractComponentBean> getComponentBeans() {
		return getPageDocument().getComponentBeans(this);
	}

	public void addComponentBean(final AbstractComponentBean componentBean) {
		final String componentName = (String) ComponentParameter.get(this, componentBean)
				.getBeanProperty("name");
		if (!StringUtils.hasText(componentName)) {
			throw ComponentException.of($m("PageDocument.1"));
		}
		getComponentBeans().put(componentName, componentBean);
	}

	public void removeComponentBean(final AbstractComponentBean componentBean) {
		final String componentName = (String) ComponentParameter.get(this, componentBean)
				.getBeanProperty("name");
		if (StringUtils.hasText(componentName)) {
			getComponentBeans().remove(componentName);
		}
	}

	public <T extends AbstractComponentBean> T addComponentBean(final Map<String, Object> attris,
			final Class<T> componentClass) {
		@SuppressWarnings("unchecked")
		final T t = (T) AbstractComponentRegistry.getComponentRegistry(componentClass)
				.createComponentBean(this, attris);
		addComponentBean(t);
		return t;
	}

	public <T extends AbstractComponentBean> T addComponentBean(final String name,
			final Class<T> componentClass) {
		return addComponentBean(new KVMap().add("name", name), componentClass);
	}

	public <T extends AbstractComponentBean> T addComponentBean(final Class<T> beanClass,
			final Class<? extends IComponentHandler> handleClass) {
		final T t = addComponentBean(handleClass.getSimpleName(), beanClass);
		t.setHandleClass(handleClass);
		return t;
	}

	public AbstractComponentBean getComponentBeanByName(final String name) {
		if (!StringUtils.hasText(name)) {
			return null;
		}
		AbstractComponentBean componentBean = getComponentBeans().get(name);
		if (componentBean == null) {
			componentBean = (AbstractComponentBean) LocalSessionCache.get(getSession(),
					ComponentUtils.getComponentHashByName(getPageDocument(), name));
		}
		return componentBean;
	}

	public IPageHandler getPageHandler() {
		return getPageDocument().getPageHandler(this);
	}

	public PageBean getPageBean() {
		return getPageDocument().getPageBean();
	}

	public AbstractMVCPage getPage() {
		return (AbstractMVCPage) ObjectEx.singleton(getPageDocument().getPageClass());
	}

	public String hashId() {
		return getPageDocument().hashId();
	}

	public Object getBeanProperty(final String beanProperty) {
		final IPageHandler pageHandle = getPageHandler();
		if (pageHandle != null) {
			return pageHandle.getBeanProperty(this, beanProperty);
		} else {
			return BeanUtils.getProperty(getPageBean(), beanProperty);
		}
	}

	protected void set(final HttpServletRequest request, final HttpServletResponse response) {
		this.request = request;
		this.response = response;
	}

	public static PageParameter get(final PageRequestResponse rRequest,
			final PageDocument pageDocument) {
		return get(rRequest.request, rRequest.response, pageDocument);
	}

	public static PageParameter get(final HttpServletRequest request,
			final HttpServletResponse response, final PageDocument pageDocument) {
		final String key = "$pp_" + pageDocument.hashId();
		PageParameter pParameter = (PageParameter) request.getAttribute(key);
		if (pParameter == null) {
			pParameter = new PageParameter(request, response, pageDocument);
			request.setAttribute(key, pParameter);
		}
		pParameter.set(request, response);
		return pParameter;
	}
}
