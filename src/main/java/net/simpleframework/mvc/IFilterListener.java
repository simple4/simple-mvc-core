package net.simpleframework.mvc;

import java.io.IOException;
import java.util.EventListener;

import javax.servlet.FilterChain;

/**
 * 这是一个开源的软件，请在LGPLv3下合法使用、修改或重新发布。
 * 
 * @author 陈侃(cknet@126.com, 13910090885)
 *         http://code.google.com/p/simpleframework/
 *         http://www.simpleframework.net
 */
public interface IFilterListener extends EventListener {

	final static String SESSION_THROWABLE = "$$throwable";

	/**
	 * 过滤器
	 * 
	 * @param rRequest
	 * @param filterChain
	 * @return
	 * @throws IOException
	 */
	boolean doFilter(PageRequestResponse rRequest, FilterChain filterChain) throws IOException;
}