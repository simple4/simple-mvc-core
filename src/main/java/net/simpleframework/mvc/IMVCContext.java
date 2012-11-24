package net.simpleframework.mvc;

import java.io.IOException;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import net.simpleframework.common.Version;

/**
 * 这是一个开源的软件，请在LGPLv3下合法使用、修改或重新发布。
 * 
 * @author 陈侃(cknet@126.com, 13910090885)
 *         http://code.google.com/p/simpleframework/
 *         http://www.simpleframework.net
 */
public interface IMVCContext {

	/**
	 * 初始化
	 * 
	 * @param servletContext
	 * @throws Exception
	 */
	void doInit(ServletContext servletContext) throws Exception;

	ServletContext getServletContext();

	/**
	 * 扫描指定包名的类
	 * 
	 * @return
	 */
	String[] getScanPackageNames();

	/**
	 * 创建IMultipartPageRequest的实现
	 * 
	 * @param request
	 * @param maxUploadSize
	 * @return
	 * @throws IOException
	 */
	IMultipartPageRequest createMultipartPageRequest(HttpServletRequest request, int maxUploadSize)
			throws IOException;

	/**
	 * 后处理中，组合html的类
	 * 
	 * 通过覆盖，可以自定义自己的html
	 * 
	 * @return
	 */
	MVCHtmlBuilder getPageHtmlBuilder();

	/**
	 * 创建缺省的页面资源提供者
	 * 
	 * @return
	 */
	IPageResourceProvider getDefaultPageResourceProvider();

	/**
	 * 定义配置
	 * 
	 * @return
	 */
	MVCConfig getPageConfig();

	/**
	 * 包装HttpSession类的实现
	 * 
	 * @return
	 */
	HttpSession wrapHttpSession(HttpSession httpSession);

	/**
	 * 获取模板实例。模板开发者需要继承该接口，该接口完成app和模板的连接
	 * 
	 * @param page
	 *           当前使用该模板的page页
	 * @return
	 */
	IMVCTemplate getTemplate(AbstractMVCPage page);

	String getTitle();

	Version getVersion();

	/**
	 * 是否为系统url
	 * 
	 * @param request
	 * @return
	 */
	boolean isSystemUrl(PageRequestResponse rRequest);

	/**
	 * 获取系统请求参数
	 * 
	 * @return
	 */
	String[] getSystemParamKeys();
}
