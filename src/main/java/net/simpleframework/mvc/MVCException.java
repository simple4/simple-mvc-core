package net.simpleframework.mvc;

import net.simpleframework.common.SimpleRuntimeException;

/**
 * 这是一个开源的软件，请在LGPLv3下合法使用、修改或重新发布。
 * 
 * @author 陈侃(cknet@126.com, 13910090885)
 *         http://code.google.com/p/simpleframework/
 *         http://www.simpleframework.net
 */
public class MVCException extends SimpleRuntimeException {
	private static final long serialVersionUID = 7838430650458772788L;

	public MVCException(final String msg, final Throwable cause) {
		super(msg, cause);
	}

	public static MVCException of(final Throwable throwable) {
		return _of(MVCException.class, null, throwable);
	}
}
