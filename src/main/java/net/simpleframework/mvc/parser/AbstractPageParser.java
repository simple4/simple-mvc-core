package net.simpleframework.mvc.parser;

import net.simpleframework.mvc.PageParameter;

/**
 * 这是一个开源的软件，请在LGPLv3下合法使用、修改或重新发布。
 * 
 * @author 陈侃(cknet@126.com, 13910090885)
 *         http://code.google.com/p/simpleframework/
 *         http://www.simpleframework.net
 */
public abstract class AbstractPageParser extends AbstractParser implements IPageParser {
	private final PageParameter pParameter;

	protected AbstractPageParser(final PageParameter pParameter) {
		this.pParameter = pParameter;
	}

	@Override
	public PageParameter getPageParameter() {
		return pParameter;
	}
}
