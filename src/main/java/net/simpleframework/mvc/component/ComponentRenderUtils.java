package net.simpleframework.mvc.component;

import java.util.Map;

import net.simpleframework.common.Convert;
import net.simpleframework.common.JsonUtils;
import net.simpleframework.common.StringUtils;
import net.simpleframework.common.html.element.EInputType;
import net.simpleframework.common.html.element.InputElement;
import net.simpleframework.common.html.js.JavascriptUtils;
import net.simpleframework.mvc.AbstractUrlForward;

/**
 * 这是一个开源的软件，请在LGPLv3下合法使用、修改或重新发布。
 * 
 * @author 陈侃(cknet@126.com, 13910090885)
 *         http://code.google.com/p/simpleframework/
 *         http://www.simpleframework.net
 */
public abstract class ComponentRenderUtils {

	public static String genActionWrapper(final ComponentParameter cParameter, final String wrapCode) {
		return genActionWrapper(cParameter, wrapCode, null);
	}

	public static String genActionWrapper(final ComponentParameter cParameter,
			final String wrapCode, final String execCode) {
		return genActionWrapper(cParameter, wrapCode, execCode, true,
				(Boolean) cParameter.getBeanProperty("runImmediately"));
	}

	public static String genActionWrapper(final ComponentParameter cParameter,
			final String wrapCode, final String execCode, final boolean genJSON,
			final boolean runImmediately) {
		final StringBuilder sb = new StringBuilder();
		final String actionFunc = actionFunc(cParameter);
		sb.append("var ").append(actionFunc).append("=function() {");
		sb.append(StringUtils.blank(wrapCode)).append("return true;");
		sb.append("};");

		String json;
		if (genJSON && (json = genJSON(cParameter, actionFunc)) != null) {
			sb.append(json);
		}

		if (StringUtils.hasText(execCode)) {
			sb.append(JavascriptUtils.wrapFunction(execCode));
		}

		sb.append("$Actions[\"").append(cParameter.getBeanProperty("name")).append("\"]=")
				.append(actionFunc).append(";");
		if (runImmediately) {
			sb.append(JavascriptUtils.wrapWhenReady(actionFunc + "();"));
		}
		return sb.toString();
	}

	public static String genJSON(final ComponentParameter cParameter, final String actionFunc) {
		final Map<String, Object> json;
		final IComponentHandler handle = cParameter.getComponentHandler();
		if (handle != null && (json = handle.toJSON(cParameter)) != null) {
			final StringBuilder sb = new StringBuilder();
			sb.append(actionFunc).append(".json=").append(JsonUtils.toJSON(json)).append(";");
			return sb.toString();
		}
		return null;
	}

	public static String actionFunc(final ComponentParameter cParameter) {
		return "f_" + cParameter.hashId();
	}

	public static String VAR_CONTAINER = "c";

	public static String initContainerVar(final ComponentParameter cParameter) {
		final StringBuilder sb = new StringBuilder();
		sb.append("var ").append(VAR_CONTAINER).append("=").append(actionFunc(cParameter))
				.append(".container");
		final String containerId = (String) cParameter.getBeanProperty("containerId");
		if (StringUtils.hasText(containerId)) {
			sb.append(" || $(\"").append(containerId).append("\")");
		}
		sb.append("; if (!").append(VAR_CONTAINER).append(") return;");
		return sb.toString();
	}

	public static String jsonAttri(final ComponentParameter cParameter, final String attri) {
		final Object val = cParameter.getBeanProperty(attri);
		final StringBuilder sb = new StringBuilder();
		if (val instanceof Number || val instanceof Boolean) {
			sb.append("\"").append(attri).append("\": ").append(val).append(",");
		} else {
			final String sVal = Convert.toString(val);
			if (StringUtils.hasText(sVal)) {
				sb.append("\"").append(attri).append("\": \"").append(sVal).append("\",");
			}
		}
		return sb.toString();
	}

	public static void appendParameters(final StringBuilder sb, final ComponentParameter cParameter,
			final String strVar) {
		// 优先级：requestData < selector < parameters
		final String includeRequestData = (String) cParameter.getBeanProperty("includeRequestData");
		// if (StringUtils.hasText(includeRequestData)) {
		sb.append(strVar).append(" = ").append(strVar).append(".addParameter(\"");
		sb.append(AbstractUrlForward.putRequestData(cParameter, includeRequestData)).append("\");");
		// }
		sb.append(strVar).append(" = ").append(strVar).append(".addSelectorParameter(");
		sb.append(actionFunc(cParameter)).append(".selector");
		final String selector = (String) cParameter.getBeanProperty("selector");
		if (StringUtils.hasText(selector)) {
			sb.append(" || \"").append(selector).append("\"");
		}
		sb.append(");");
		final String parameters = (String) cParameter.getBeanProperty("parameters");
		if (StringUtils.hasText(parameters)) {
			sb.append(strVar).append(" = ").append(strVar).append(".addParameter(\"");
			sb.append(parameters).append("\");");
		}
	}

	public static String genParameters(final ComponentParameter cParameter) {
		final StringBuilder sb = new StringBuilder();
		final Map<String, Object> params = ComponentUtils.toFormParameters(cParameter);
		if (params != null && params.size() > 0) {
			sb.append("<div class=\"parameters\" id=\"").append(AbstractComponentBean.FORM_PREFIX);
			sb.append(cParameter.hashId()).append("\">");
			for (final Map.Entry<String, Object> entry : params.entrySet()) {
				sb.append(new InputElement(entry.getKey(), EInputType.hidden).setText(String
						.valueOf(entry.getValue())));
			}
			sb.append("</div>");
		}
		return sb.toString();
	}
}
