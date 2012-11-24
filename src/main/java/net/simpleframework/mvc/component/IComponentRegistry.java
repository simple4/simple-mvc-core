package net.simpleframework.mvc.component;

import net.simpleframework.mvc.IPageResourceProvider;
import net.simpleframework.mvc.PageParameter;

/**
 * 提供给开发者开发自定义组件组件接口
 * 
 * 
 * 这是一个开源的软件，请在LGPLv3下合法使用、修改或重新发布。
 * 
 * @author 陈侃(cknet@126.com, 13910090885)
 *         http://code.google.com/p/simpleframework/
 *         http://www.simpleframework.net
 */
public interface IComponentRegistry {
	/**
	 * 组件的名称，这个值就是在XML描述中声明的组件标签，且必须唯一
	 */
	String getComponentName();

	/**
	 * 获取组件的渲染器实例
	 */
	IComponentRender getComponentRender();

	/**
	 * 获取组件的资源提供者实例
	 */
	IComponentResourceProvider getComponentResourceProvider();

	/**
	 * 创建组件的元信息定义实例,组件的元信息来自XML描述文件， 该实例将按XML中的定义来初始化Bean的属性
	 * 
	 * @param pParameter
	 * @param data
	 *           xml元素或bean对象
	 * @return
	 */
	AbstractComponentBean createComponentBean(PageParameter pParameter, Object data);

	/**
	 * 获得页面资源
	 */
	IPageResourceProvider getPageResourceProvider();
}
