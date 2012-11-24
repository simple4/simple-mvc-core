package net.simpleframework.mvc.ctx.permission;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import net.simpleframework.common.ID;
import net.simpleframework.common.ImageUtils;
import net.simpleframework.common.IoUtils;
import net.simpleframework.common.StringUtils;
import net.simpleframework.ctx.permission.DefaultPermissionHandler;
import net.simpleframework.mvc.IForward;
import net.simpleframework.mvc.MVCContextFactory;
import net.simpleframework.mvc.MVCUtils;
import net.simpleframework.mvc.PageRequestResponse;
import net.simpleframework.mvc.UrlForward;
import net.simpleframework.mvc.component.ComponentParameter;

/**
 * 这是一个开源的软件，请在LGPLv3下合法使用、修改或重新发布。
 * 
 * @author 陈侃(cknet@126.com, 13910090885)
 *         http://code.google.com/p/simpleframework/
 *         http://www.simpleframework.net
 */
public class DefaultPagePermissionHandler extends DefaultPermissionHandler implements
		IPagePermissionHandler {

	@Override
	public String getLoginRedirectUrl(final PageRequestResponse rRequest, final String role) {
		// 已经登录
		if (getLoginId(rRequest) != null) {
			return null;
		}
		// 不存在角色或角色是匿名
		if (!StringUtils.hasText(role) || sj_anonymous.equals(role)) {
			return null;
		}

		final String rPath = MVCUtils.getPageResourcePath();
		if (rRequest.getRequestURI().contains(rPath + "/jsp/login_redirect")) {
			return null;
		}

		if (rRequest.isHttpRequest()) {
			return rPath + "/jsp/login_redirect_template.jsp?login_redirect="
					+ rRequest.wrapContextPath(MVCContextFactory.config().getLoginUrl());
		} else {
			return rPath + "/jsp/login_win_redirect.jsp";
		}
	}

	@Override
	public IForward accessForward(final ComponentParameter cParameter, final Object role) {
		final String roleName = getRole(role).getName();
		final String redirectUrl = getLoginRedirectUrl(cParameter, roleName);
		if (StringUtils.hasText(redirectUrl)) {
			return new UrlForward(redirectUrl);
		} else if (StringUtils.hasText(roleName)) {
			if (!isMember(getLoginId(cParameter), role)) {
				return new UrlForward(MVCUtils.getPageResourcePath() + "/jsp/role_ajax_access.jsp?v="
						+ cParameter.getBeanProperty("name") + "&role=" + roleName);
			}
		}
		return null;
	}

	@Override
	public ID getLoginId(final PageRequestResponse rRequest) {
		return null;
	}

	@Override
	public void login(final PageRequestResponse rRequest, final String login, final String password,
			final Map<String, Object> params) {
	}

	@Override
	public String getPhotoUrl(final PageRequestResponse rRequest, final Object user,
			final int width, final int height) {
		final StringBuilder sb = new StringBuilder();
		String path = MVCUtils.getPageResourcePath() + "/images";
		final InputStream inputStream = getUser(user).getPhotoStream();
		if (inputStream == null) {
			sb.append(path).append("/none_user.gif");
		} else {
			path += "/photo-cache/";
			final File photoCache = new File(MVCUtils.getRealPath(path));
			if (!photoCache.exists()) {
				IoUtils.createDirectoryRecursively(photoCache);
			}
			final String filename = getUser(user).getId() + "_" + width + "_" + height + ".png";
			final File photoFile = new File(photoCache.getAbsolutePath() + File.separator + filename);
			if (!photoFile.exists() || photoFile.length() == 0) {
				try {
					ImageUtils.thumbnail(inputStream, width, height, new FileOutputStream(photoFile));
				} catch (final IOException e) {
					log.warn(e);
				}
			}
			sb.append(path).append(filename);
		}
		return sb.toString();
	}

	@Override
	public String getPhotoUrl(final PageRequestResponse rRequest, final Object user) {
		return getPhotoUrl(rRequest, user, 128, 128);
	}
}
