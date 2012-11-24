/**
 * @author 陈侃(cknet@126.com, 13910090885)
 *         http://code.google.com/p/simpleframework/
 *         http://www.simpleframework.net
 */
var Browser = {
	support : {
		canvas : !!document.createElement("canvas").getContext
	}
};

var $Actions = {
	loc : function(url, open) {
		if (typeof url != "string" || url.length == 0)
			return;
		if (url.charAt(0) == "/"
				&& url.toUpperCase().lastIndexOf(CONTEXT_PATH.toUpperCase(), 0) != 0) {
			url = CONTEXT_PATH + url;
		}
		if (open)
			window.open(url);
		else
			window.location = url;
	}
};

var $call = function(o) {
	try {
		if (typeof o === "function") {
			return o.apply(o, Array.prototype.slice.call(arguments, 1));
		} else if (typeof o === "string") {
			return eval(o);
		}
	} catch (e) {
		alert(e.name + ": " + e.message);
	}
};

/** document */
document.getEvent = function(e) {
	var ev = e || window.event;
	if (!ev) {
		var c = document.getEvent.caller;
		var arr = [];
		while (c && c.arguments) {
			ev = c.arguments[0];
			if (ev
					&& ev.constructor
					&& (window.Event == ev.constructor || window.MouseEvent == ev.constructor)) {
				break;
			} else {
				ev = undefined;
			}
			if (c == c.caller || arr.include(c.caller))
				break;
			else
				arr.push(c = c.caller);
		}
	}
	return ev && Event.extend(ev).target ? ev : null;
};

document.setCookie = function(key, value, hour) {
	var expires = "";
	if (hour) {
		var date = new Date();
		date.setTime(date.getTime() + (hour * 60 * 60 * 1000));
		expires = "; expires=" + date.toGMTString();
	}
	document.cookie = key + "=" + encodeURIComponent(value) + expires
			+ "; path=/";
};

document.getCookie = function(key) {
	var cookies = document.cookie.match(key + '=(.*?)(;|$)');
	if (cookies) {
		return decodeURI(cookies[1]);
	} else {
		return null;
	}
};

var StylesheetFragment = new RegExp("<link[^>]+stylesheet[^>]*>", "img");

String.prototype.stripStylesheets = function() {
	return this.replace(StylesheetFragment, '');
};

String.prototype.convertHtmlLines = function() {
	return this.replace(/\r/g, '<br>').replace(/\n/g, '<br>');
};

var HEX_ARRAY = new Array("0", "1", "2", "3", "4", "5", "6", "7", "8", "9",
		"a", "b", "c", "d", "e", "f");

String.prototype.hexToString = function() {
	var r = "";
	for ( var i = 0; i < this.length; i += 2) {
		r += String.fromCharCode(parseInt(this.substr(i, 2), 16));
	}
	return r;
};

String.prototype.stringToHex = function() {
	var r = "";
	for ( var i = 0; i < this.length; i++) {
		r += HEX_ARRAY[this.charCodeAt(i) >> 4]
				+ HEX_ARRAY[this.charCodeAt(i) & 0xf];
	}
	return r;
};

String.prototype.addParameter = function(parameters) {
	if (!parameters || parameters == '') {
		return this.toString();
	}
	var p = this.indexOf('?');
	var request;
	var query;
	if (p > -1) {
		request = this.substring(0, p);
		query = this.substring(p + 1);
	} else {
		var isQueryString = this.indexOf('=') > 0;
		if (isQueryString) {
			request = '';
			query = this.toString();
		} else {
			request = this.toString();
			query = '';
		}
	}
	var o = new Object();
	var doAttri = function(q) {
		var qArr = q.split("&");
		for ( var i = 0; i < qArr.length; i++) {
			var v = qArr[i].split("=");
			o[v[0]] = v.length > 1 ? v[1] : "";
		}
	}
	doAttri(query);
	doAttri(parameters);
	var ret = "";
	var i = 0;
	for (property in o) {
		if (i++ > 0) {
			ret += "&";
		}
		ret += (property + "=" + o[property]);
	}
	if (request.length > 0)
		request += '?';
	return request + ret;
};

String.prototype.makeElement = function(parameters) {
	var wrapper = document.createElement('div');
	wrapper.innerHTML = this.toString();
	return wrapper.firstElementChild || wrapper.firstChild;
};

Number.prototype.toFileString = function() {
	var size = this;
	if (size < 0) {
		return "";
	} else {
		var str;
		if (size > 1024 * 1024) {
			str = (Math.round((size / (1024 * 1024)) * 100) / 100) + "MB";
		} else if (size > 1024) {
			str = (Math.round((size / 1024) * 100) / 100) + "KB";
		} else {
			str = size + "B";
		}
		return str;
	}
};

Array.prototype.empty = function() {
	return !this.length;
};

Array.prototype.removeAt = function(index) {
	var object = this[index];
	this.splice(index, 1);
	return object;
};

Array.prototype.remove = function(object) {
	var index;
	while ((index = this.indexOf(object)) != -1)
		this.removeAt(index);
	return object;
};

Array.prototype.insert = function(index) {
	var args = this.slice.call(arguments);
	args.shift();
	this.splice.apply(this, [ index, 0 ].concat(args));
	return this;
};

var $UI = {
	setBrowserTitle : function(str, append) {
		document.title = append ? document.title + " - " + str : str;
	},

	addStyleSheet : function(css) {
		var style = document.createElement("style");
		style.setAttribute("type", "text/css");
		style.setAttribute("media", "screen");

		$(document.getElementsByTagName('head')[0]).appendChild(style);
		if (style.styleSheet)
			style.styleSheet.cssText = css;
		else
			style.appendText(css);
		return style;
	}
};
