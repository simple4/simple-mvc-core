package net.simpleframework.mvc.component;

import net.simpleframework.common.Convert;
import net.simpleframework.common.xml.XmlElement;
import net.simpleframework.mvc.PageDocument;

/**
 * 这是一个开源的软件，请在LGPLv3下合法使用、修改或重新发布。
 * 
 * @author 陈侃(cknet@126.com, 13910090885)
 *         http://code.google.com/p/simpleframework/
 *         http://www.simpleframework.net
 */
public abstract class AbstractContainerBean extends AbstractComponentBean {
	private String containerId;

	private String width, height;

	public AbstractContainerBean(final PageDocument pageDocument, final XmlElement xmlElement) {
		super(pageDocument, xmlElement);
	}

	public String getContainerId() {
		return containerId;
	}

	public AbstractContainerBean setContainerId(final String containerId) {
		this.containerId = containerId;
		return this;
	}

	public String getWidth() {
		return width;
	}

	public AbstractContainerBean setWidth(final String width) {
		final int w = Convert.toInt(width);
		this.width = w > 0 ? w + "px" : width;
		return this;
	}

	public String getHeight() {
		return height;
	}

	public AbstractContainerBean setHeight(final String height) {
		final int h = Convert.toInt(height);
		this.height = h > 0 ? h + "px" : height;
		return this;
	}
}
