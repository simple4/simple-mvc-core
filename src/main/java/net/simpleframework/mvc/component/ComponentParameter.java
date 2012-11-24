package net.simpleframework.mvc.component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.simpleframework.common.bean.BeanUtils;
import net.simpleframework.mvc.PageDocument;
import net.simpleframework.mvc.PageParameter;
import net.simpleframework.mvc.PageRequestResponse;

/**
 * 这是一个开源的软件，请在LGPLv3下合法使用、修改或重新发布。
 * 
 * @author 陈侃(cknet@126.com, 13910090885)
 *         http://code.google.com/p/simpleframework/
 *         http://www.simpleframework.net
 */
public final class ComponentParameter extends PageParameter {
	private PageDocument documentRef;

	public AbstractComponentBean componentBean;

	public ComponentParameter(final HttpServletRequest request, final HttpServletResponse response,
			final AbstractComponentBean componentBean) {
		super(request, response, null);
		this.componentBean = componentBean;
	}

	@Override
	public PageDocument getPageDocument() {
		PageDocument pageDocument = super.getPageDocument();
		if (pageDocument == null) {
			pageDocument = componentBean.getPageDocument();
		}
		return pageDocument;
	}

	public PageDocument getDocumentRef() {
		return documentRef;
	}

	public IComponentHandler getComponentHandler() {
		return componentBean != null ? componentBean.getComponentHandler(this) : null;
	}

	@Override
	public String hashId() {
		return componentBean.hashId();
	}

	@Override
	public Object getBeanProperty(final String beanProperty) {
		final IComponentHandler handle = getComponentHandler();
		if (handle != null) {
			return handle.getBeanProperty(this, beanProperty);
		} else {
			return BeanUtils.getProperty(componentBean, beanProperty);
		}
	}

	public static ComponentParameter get(final HttpServletRequest request,
			final HttpServletResponse response, final String beanId) {
		return get(new PageRequestResponse(request, response), beanId);
	}

	public static ComponentParameter get(final PageRequestResponse rRequest, final String beanId) {
		return get(rRequest,
				ComponentUtils.getComponentBeanByHashId(rRequest, rRequest.getParameter(beanId)));
	}

	public static ComponentParameter get(final PageRequestResponse rRequest,
			final AbstractComponentBean componentBean) {
		ComponentParameter cParameter;
		if (rRequest instanceof ComponentParameter
				&& (cParameter = (ComponentParameter) rRequest).componentBean == componentBean) {
			return cParameter;
		}
		cParameter = get(rRequest.request, rRequest.response, componentBean);
		if (rRequest instanceof ComponentParameter) {
			cParameter.documentRef = ((ComponentParameter) rRequest).documentRef;
		} else if (rRequest instanceof PageParameter) {
			cParameter.documentRef = ((PageParameter) rRequest).getPageDocument();
		}
		return cParameter;
	}

	public static ComponentParameter get(final HttpServletRequest request,
			final HttpServletResponse response, final AbstractComponentBean componentBean) {
		if (componentBean == null) {
			return new ComponentParameter(request, response, null);
		}
		final String key = "$cp_" + componentBean.hashId();
		ComponentParameter cParameter = (ComponentParameter) request.getAttribute(key);
		if (cParameter == null) {
			cParameter = new ComponentParameter(request, response, componentBean);
			request.setAttribute(key, cParameter);
		}
		cParameter.set(request, response);
		return cParameter;
	}

	public static ComponentParameter getByAttri(final ComponentParameter cParameter,
			final String attri) {
		return get(cParameter, (AbstractComponentBean) cParameter.componentBean.getAttr(attri));
	}
}
