package net.simpleframework.mvc.component;

import static net.simpleframework.common.I18n.$m;
import net.simpleframework.common.SimpleRuntimeException;

/**
 * 这是一个开源的软件，请在LGPLv3下合法使用、修改或重新发布。
 * 
 * @author 陈侃(cknet@126.com, 13910090885)
 *         http://code.google.com/p/simpleframework/
 *         http://www.simpleframework.net
 */
public class ComponentException extends SimpleRuntimeException {

	public ComponentException(final String msg, final Throwable cause) {
		super(msg, cause);
	}

	public static ComponentException of(final Throwable cause) {
		return _of(ComponentException.class, null, cause);
	}

	public static ComponentException of(final String message) {
		return _of(ComponentException.class, message);
	}

	public static ComponentException wrapException_ComponentRef(final String ref) {
		return new ComponentException($m("ComponentException.0", ref), null);
	}

	private static final long serialVersionUID = 5936937563262430751L;
}
