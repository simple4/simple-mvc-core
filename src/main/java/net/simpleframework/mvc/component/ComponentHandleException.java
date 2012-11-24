package net.simpleframework.mvc.component;

import net.simpleframework.common.SimpleRuntimeException;

/**
 * 这是一个开源的软件，请在LGPLv3下合法使用、修改或重新发布。
 * 
 * @author 陈侃(cknet@126.com, 13910090885)
 *         http://code.google.com/p/simpleframework/
 *         http://www.simpleframework.net
 */
public class ComponentHandleException extends SimpleRuntimeException {
	private static final long serialVersionUID = 7623116181965540895L;

	public ComponentHandleException(final String msg, final Throwable cause) {
		super(msg, cause);
	}

	public static ComponentHandleException of(final String msg) {
		return _of(ComponentHandleException.class, msg, null);
	}

	public static ComponentHandleException of(final Throwable throwable) {
		return _of(ComponentHandleException.class, null, throwable);
	}
}
