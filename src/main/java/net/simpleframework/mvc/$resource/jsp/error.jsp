<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="net.simpleframework.common.Convert"%>
<%@ page import="net.simpleframework.common.html.HtmlUtils"%>
<%@ page import="net.simpleframework.mvc.IFilterListener"%>
<%@ page import="net.simpleframework.mvc.LocalSessionCache"%>
<%@ page import="net.simpleframework.common.ThrowableUtils"%>
<%@ page import="net.simpleframework.common.html.HtmlEncoder"%>
<%
	final Throwable th = (Throwable) LocalSessionCache.get(session,
			IFilterListener.SESSION_THROWABLE);
	if (th == null) {
%>
<script type="text/javascript">
  window.onload = function() {
    $Actions.loc('/');
  };
</script>
<%
	return;
	}
	LocalSessionCache
			.remove(session, IFilterListener.SESSION_THROWABLE);
%>
<div align="center">
  <div class="simple_toolbar1" style="width: 640px; margin-top: 100px; text-align: left;">
    <div style="height: 24px;">
      <div style="float: right;">
        <input type="button" value="#(error.1)" onclick="history.back();" /> <input type="button"
          value="#(error.2)" onclick="$Actions.loc('/');" />
      </div>
      <div style="float: left" class="f3">#(error.0)</div>
    </div>
    <div class="simple_toolbar wrap_text" style="margin: 6px 0px; color: #8D3212;"><%=HtmlUtils.convertHtmlLines(HtmlEncoder
					.text(ThrowableUtils.getThrowableMessage(th)))%></div>
    <div class="simple_toolbar1" style="padding: 4px;">
      <textarea style="height: 300px; border: 0; width: 100%; background-image: none;" readonly><%=HtmlEncoder.text(Convert.toString(th))%></textarea>
    </div>
  </div>
</div>
