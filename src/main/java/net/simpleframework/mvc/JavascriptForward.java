package net.simpleframework.mvc;

/**
 * 这是一个开源的软件，请在LGPLv3下合法使用、修改或重新发布。
 * 
 * @author 陈侃(cknet@126.com, 13910090885)
 *         http://code.google.com/p/simpleframework/
 *         http://www.simpleframework.net
 */
public class JavascriptForward extends TextForward {
	public JavascriptForward(final String javascript) {
		super(javascript);
	}

	public JavascriptForward() {
		this(null);
	}

	@Override
	public JavascriptForward append(final Object javascript) {
		return (JavascriptForward) super.append(javascript);
	}
}
