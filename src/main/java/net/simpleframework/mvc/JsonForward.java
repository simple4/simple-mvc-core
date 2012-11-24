package net.simpleframework.mvc;

import java.util.Map;

import net.simpleframework.common.coll.KVMap;

/**
 * 这是一个开源的软件，请在LGPLv3下合法使用、修改或重新发布。
 * 
 * @author 陈侃(cknet@126.com, 13910090885)
 *         http://code.google.com/p/simpleframework/
 *         http://www.simpleframework.net
 */
public class JsonForward extends TextForward {

	private final KVMap kv = new KVMap();

	public JsonForward() {
	}

	public JsonForward(final String key, final Object value) {
		put(key, value);
	}

	public JsonForward put(final String key, final Object value) {
		kv.add(key, value);
		return this;
	}

	public JsonForward put(final Map<String, Object> data) {
		kv.putAll(data);
		return this;
	}

	public JsonForward remove(final String key) {
		kv.remove(key);
		return this;
	}

	@Override
	public String getResponseText(final PageRequestResponse rRequest) {
		return kv.toJSON();
	}
}
