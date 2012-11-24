package net.simpleframework.mvc;

/**
 * 这是一个开源的软件，请在LGPLv3下合法使用、修改或重新发布。
 * 
 * @author 陈侃(cknet@126.com, 13910090885)
 *         http://code.google.com/p/simpleframework/
 *         http://www.simpleframework.net
 */
public class TextForward implements IForward {

	protected final StringBuilder builder = new StringBuilder();

	public TextForward() {
	}

	public TextForward(final String responseText) {
		append(responseText);
	}

	public TextForward append(final Object javascript) {
		if (javascript != null) {
			builder.append(javascript);
		}
		return this;
	}

	@Override
	public String getResponseText(final PageRequestResponse rRequest) {
		return toString();
	}

	@Override
	public String toString() {
		return builder.toString();
	}
}
