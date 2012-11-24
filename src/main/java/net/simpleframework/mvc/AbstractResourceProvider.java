package net.simpleframework.mvc;

import net.simpleframework.common.ObjectEx;
import net.simpleframework.common.StringUtils;
import net.simpleframework.mvc.common.DeployUtils;

/**
 * 这是一个开源的软件，请在LGPLv3下合法使用、修改或重新发布。
 * 
 * @author 陈侃(cknet@126.com, 13910090885)
 *         http://code.google.com/p/simpleframework/
 *         http://www.simpleframework.net
 */
public abstract class AbstractResourceProvider extends ObjectEx implements IResourceProvider {

	@Override
	public String getResourceHomePath() {
		return getResourceHomePath(getClass());
	}

	@Override
	public String getResourceHomePath(final Class<?> resourceClass) {
		return DeployUtils.getResourcePath(resourceClass);
	}

	@Override
	public String getCssResourceHomePath(final PageParameter pParameter) {
		return getCssResourceHomePath(pParameter, getClass());
	}

	@Override
	public String getCssResourceHomePath(final PageParameter pParameter, final Class<?> resourceClass) {
		return getResourceHomePath(resourceClass) + "/css/" + getSkin(pParameter);
	}

	@Override
	public String[] getJavascriptPath(final PageParameter pParameter) {
		return null;
	}

	@Override
	public String[] getCssPath(final PageParameter pParameter) {
		return null;
	}

	@Override
	public String[] getJarPath() {
		return null;
	}

	@Override
	public String getSkin(final PageParameter pParameter) {
		String skin = getSkin();
		if (StringUtils.hasText(skin)) {
			return skin;
		} else {
			skin = (String) pParameter.getSessionAttr(MVCUtils.SESSION_SKIN);
			if (StringUtils.hasText(skin)) {
				return skin;
			} else {
				return DEFAULT_SKIN;
			}
		}
	}

	private String skin;

	@Override
	public String getSkin() {
		return skin;
	}

	@Override
	public void setSkin(final String skin) {
		this.skin = skin;
	}

	public final static String DEFAULT_SKIN = "default";
}
