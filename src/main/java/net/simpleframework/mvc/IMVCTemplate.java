package net.simpleframework.mvc;

/**
 * 这是一个开源的软件，请在LGPLv3下合法使用、修改或重新发布。
 * 
 * @author 陈侃(cknet@126.com, 13910090885)
 *         http://code.google.com/p/simpleframework/
 *         http://www.simpleframework.net
 */
public interface IMVCTemplate {

	/**
	 * 获取模板头部页面
	 * 
	 * @return
	 */
	Class<? extends AbstractMVCPage> getHeaderPage();

	/**
	 * 获取模板尾部页面
	 * 
	 * @return
	 */
	Class<? extends AbstractMVCPage> getFooterPage();
}
