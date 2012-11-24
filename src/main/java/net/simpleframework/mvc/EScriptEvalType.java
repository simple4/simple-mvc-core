package net.simpleframework.mvc;

/**
 * 这是一个开源的软件，请在LGPLv3下合法使用、修改或重新发布。
 * 
 * @author 陈侃(cknet@126.com, 13910090885)
 *         http://code.google.com/p/simpleframework/
 *         http://www.simpleframework.net
 */
public enum EScriptEvalType {
	/**
	 * 不执行脚本
	 */
	none,

	/**
	 * 第一次运行的时候执行
	 */
	single,

	/**
	 * 每次运行的时候都执行
	 */
	multiple
}
