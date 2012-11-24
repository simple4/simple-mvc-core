package net.simpleframework.mvc;

import java.io.File;
import java.util.ArrayList;

import net.simpleframework.common.IoUtils;
import net.simpleframework.common.coll.ParameterMap;
import net.simpleframework.common.web.Browser;

/**
 * 这是一个开源的软件，请在LGPLv3下合法使用、修改或重新发布。
 * 
 * @author 陈侃(cknet@126.com, 13910090885)
 *         http://code.google.com/p/simpleframework/
 *         http://www.simpleframework.net
 */
public class MVCConfig {

	public MVCConfig() {
		final ParameterMap packages = getPagePackages();
		if (packages != null) {
			for (final String key : new ArrayList<String>(packages.keySet())) {
				if (!key.endsWith("/")) {
					packages.put(key + "/", packages.remove(key));
				}
			}
		}
	}

	/**
	 * 获取系统的临时目录
	 * 
	 * @return
	 */
	public File getTmpdir() {
		File tmpdir = new File(System.getProperty("java.io.tmpdir"));
		if (!tmpdir.exists()) {
			tmpdir = new File(MVCUtils.getRealPath("/$upload_temp/"));
			IoUtils.createDirectoryRecursively(tmpdir);
		}
		return tmpdir;
	}

	/**
	 * 获取系统的字符集
	 * 
	 * @return
	 */
	public String getCharset() {
		return "UTF-8";
	}

	/**
	 * 资源是否压缩，js和css
	 * 
	 * @return
	 */
	public boolean isResourceCompress() {
		return false;
	}

	public String getErrorUrl() {
		return MVCUtils.getPageResourcePath() + "/jsp/error.jsp";
	}

	private static final String IE_VERSION_PAGE = "/jsp/ie_version_alert.jsp";

	public String getIEWarnUrl() {
		return MVCUtils.getPageResourcePath() + IE_VERSION_PAGE;
	}

	public String getLoginUrl() {
		return null;
	}

	public String getHomeUrl() {
		return null;
	}

	/**
	 * response是否压缩
	 * 
	 * @return
	 */
	public boolean isGzipResponse() {
		return false;
	}

	public boolean isEffect(final PageRequestResponse rRequest) {
		final Browser browser = Browser.get(rRequest.request);
		return !browser.isTrident() || browser.getVersion() > 8;
	}

	public int getServletPort(final PageRequestResponse rRequest) {
		return rRequest.getServletPort();
	}

	public String getDefaultRole() {
		return "sys_anonymous";
	}

	/*----------------------------- AbstractMVCPage -----------------------------*/

	public String getPagePath() {
		return "/app";
	}

	public ParameterMap getPagePackages() {
		return null;
	}
}
