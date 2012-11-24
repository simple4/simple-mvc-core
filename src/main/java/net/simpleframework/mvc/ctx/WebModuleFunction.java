package net.simpleframework.mvc.ctx;

import net.simpleframework.ctx.ModuleFunction;
import net.simpleframework.mvc.AbstractMVCPage;

/**
 * 这是一个开源的软件，请在LGPLv3下合法使用、修改或重新发布。
 * 
 * @author 陈侃(cknet@126.com, 13910090885)
 *         http://code.google.com/p/simpleframework/
 *         http://www.simpleframework.net
 */
public class WebModuleFunction extends ModuleFunction {
	/* 功能的主操作页面的地址 */
	private String url;

	private Class<? extends AbstractMVCPage> pageClass;

	/* 显示的16px图标 */
	private String icon16;

	public WebModuleFunction() {
	}

	public WebModuleFunction(final Class<? extends AbstractMVCPage> pageClass) {
		this.pageClass = pageClass;
		setUrl(AbstractMVCPage.uriFor(pageClass));
	}

	public String getUrl() {
		return url;
	}

	public WebModuleFunction setUrl(final String url) {
		this.url = url;
		return this;
	}

	public Class<? extends AbstractMVCPage> getPageClass() {
		return pageClass;
	}

	public String getIcon16() {
		return icon16;
	}

	public WebModuleFunction setIcon16(final String icon16) {
		this.icon16 = icon16;
		return this;
	}

	private static final long serialVersionUID = -7070465803484944460L;
}
