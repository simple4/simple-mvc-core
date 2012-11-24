package net.simpleframework.mvc;

import net.simpleframework.common.ObjectEx;
import net.simpleframework.mvc.ctx.permission.IPagePermissionHandler;

/**
 * 这是一个开源的软件，请在LGPLv3下合法使用、修改或重新发布。
 * 
 * @author 陈侃(cknet@126.com, 13910090885)
 *         http://code.google.com/p/simpleframework/
 *         http://www.simpleframework.net
 */
public abstract class AbstractMVCHandler extends ObjectEx {

	protected IPagePermissionHandler permission() {
		return MVCContextFactory.permission();
	}
}
