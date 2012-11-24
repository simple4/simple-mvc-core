package net.simpleframework.mvc;

import javax.servlet.ServletContext;

import net.simpleframework.common.ObjectEx;
import net.simpleframework.ctx.permission.PermissionFactory;
import net.simpleframework.mvc.ctx.permission.IPagePermissionHandler;

/**
 * 这是一个开源的软件，请在LGPLv3下合法使用、修改或重新发布。
 * 
 * @author 陈侃(cknet@126.com, 13910090885)
 *         http://code.google.com/p/simpleframework/
 *         http://www.simpleframework.net
 */
public class MVCContextFactory extends ObjectEx {

	public static IMVCContext ctx() {
		return get().getContext();
	}

	public static MVCConfig config() {
		final IMVCContext ctx = ctx();
		return ctx != null ? ctx.getPageConfig() : null;
	}

	public static IPagePermissionHandler permission() {
		return (IPagePermissionHandler) PermissionFactory.get();
	}

	public static ServletContext servlet() {
		return ctx().getServletContext();
	}

	public static MVCContextFactory get() {
		return singleton(MVCContextFactory.class);
	}

	private IMVCContext mVCContext;

	public IMVCContext getContext() {
		return mVCContext;
	}

	public void setContext(final IMVCContext mVCContext) {
		this.mVCContext = mVCContext;
	}
}
