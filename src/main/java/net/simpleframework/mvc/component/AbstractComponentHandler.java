package net.simpleframework.mvc.component;

import java.util.Map;

import net.simpleframework.common.bean.BeanUtils;
import net.simpleframework.common.coll.KVMap;
import net.simpleframework.mvc.AbstractMVCHandler;

/**
 * 这是一个开源的软件，请在LGPLv3下合法使用、修改或重新发布。
 * 
 * @author 陈侃(cknet@126.com, 13910090885)
 *         http://code.google.com/p/simpleframework/
 *         http://www.simpleframework.net
 */
public abstract class AbstractComponentHandler extends AbstractMVCHandler implements
		IComponentHandler {

	@Override
	public void handleCreated(final ComponentParameter cParameter) {
	}

	@Override
	public void beforeRender(final ComponentParameter cParameter) {
	}

	@Override
	public Map<String, Object> toJSON(final ComponentParameter cParameter) {
		return new KVMap();
	}

	@Override
	public Map<String, Object> getFormParameters(final ComponentParameter cParameter) {
		return new KVMap();
	}

	@Override
	public Object getBeanProperty(final ComponentParameter cParameter, final String beanProperty) {
		return BeanUtils.getProperty(cParameter.componentBean, beanProperty);
	}
}
