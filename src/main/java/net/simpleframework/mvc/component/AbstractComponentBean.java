package net.simpleframework.mvc.component;

import java.io.IOException;

import net.simpleframework.common.StringUtils;
import net.simpleframework.common.xml.AbstractElementBean;
import net.simpleframework.common.xml.XmlElement;
import net.simpleframework.mvc.MVCContextFactory;
import net.simpleframework.mvc.PageDocument;
import net.simpleframework.mvc.PageRequestResponse;

/**
 * 这是一个开源的软件，请在LGPLv3下合法使用、修改或重新发布。
 * 
 * @author 陈侃(cknet@126.com, 13910090885)
 *         http://code.google.com/p/simpleframework/
 *         http://www.simpleframework.net
 */
public abstract class AbstractComponentBean extends AbstractElementBean {
	public static final String FORM_PREFIX = "form_";

	private final PageDocument pageDocument;

	protected String name;

	private boolean runImmediately = true;

	private String handleClass;

	private EComponentHandlerScope handleScope;

	protected String selector;

	private String includeRequestData; // p=parameter a=attribute h=header

	private String parameters;

	private boolean effects = true;

	public AbstractComponentBean(final PageDocument pageDocument, final XmlElement xmlElement) {
		super(xmlElement);
		this.pageDocument = pageDocument;
	}

	public void saveToFile() throws IOException {
		syncElement();
		final PageDocument pageDocument = getPageDocument();
		pageDocument.saveToFile(pageDocument.getDocumentFile());
	}

	public String getName() {
		return name;
	}

	public AbstractComponentBean setName(final String name) {
		this.name = name;
		return this;
	}

	public boolean isRunImmediately() {
		return runImmediately;
	}

	public AbstractComponentBean setRunImmediately(final boolean runImmediately) {
		this.runImmediately = runImmediately;
		return this;
	}

	public String getHandleClass() {
		return handleClass;
	}

	public AbstractComponentBean setHandleClass(final String handleClass) {
		this.handleClass = handleClass;
		return this;
	}

	public AbstractComponentBean setHandleClass(final Class<?> hClass) {
		return setHandleClass(hClass.getName());
	}

	public EComponentHandlerScope getHandleScope() {
		return handleScope != null ? handleScope : EComponentHandlerScope.singleton;
	}

	public AbstractComponentBean setHandleScope(final EComponentHandlerScope handleScope) {
		this.handleScope = handleScope;
		return this;
	}

	public String getSelector() {
		final StringBuilder sb = new StringBuilder();
		if (StringUtils.hasText(selector)) {
			// sb.append("#").append(AbstractComponentBean.FORM_PREFIX).append(hashId());
			// sb.append(", ");
			sb.append(selector);
		} else {
			sb.append("#").append(AbstractComponentBean.FORM_PREFIX).append(hashId());
		}
		return sb.toString();
	}

	public AbstractComponentBean setSelector(final String selector) {
		this.selector = selector;
		return this;
	}

	public boolean isEffects() {
		return effects;
	}

	public AbstractComponentBean setEffects(final boolean effects) {
		this.effects = effects;
		return this;
	}

	public String getParameters() {
		return parameters;
	}

	public AbstractComponentBean setParameters(final String parameters) {
		this.parameters = parameters;
		return this;
	}

	public String getIncludeRequestData() {
		return includeRequestData;
	}

	public AbstractComponentBean setIncludeRequestData(final String includeRequestData) {
		this.includeRequestData = includeRequestData;
		return this;
	}

	public IComponentHandler getComponentHandler(final PageRequestResponse rRequest) {
		return ComponentUtils.getComponentHandler(rRequest, this);
	}

	public PageDocument getPageDocument() {
		return pageDocument;
	}

	private String _hashId;

	public String hashId() {
		if (_hashId == null) {
			final XmlElement xmlElement = getBeanElement();
			final String name = xmlElement != null ? xmlElement.attributeValue("name") : getName();
			if (StringUtils.hasText(name)) {
				_hashId = ComponentUtils.getComponentHashByName(pageDocument, name);
			} else {
				_hashId = StringUtils.hash(this);
			}
		}
		return _hashId;
	}

	public IComponentRegistry getComponentRegistry() {
		return AbstractComponentRegistry.getComponentRegistry(getClass());
	}

	protected String default_role = MVCContextFactory.config().getDefaultRole();
}
