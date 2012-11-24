package net.simpleframework.mvc.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import net.simpleframework.common.ImageUtils;
import net.simpleframework.common.IoUtils;
import net.simpleframework.common.ObjectEx;
import net.simpleframework.common.StringUtils;
import net.simpleframework.common.web.HttpUtils;
import net.simpleframework.mvc.MVCUtils;
import net.simpleframework.mvc.PageRequestResponse;

/**
 * 这是一个开源的软件，请在LGPLv3下合法使用、修改或重新发布。
 * 
 * @author 陈侃(cknet@126.com, 13910090885)
 *         http://code.google.com/p/simpleframework/
 *         http://www.simpleframework.net
 */
public class ImageCache extends ObjectEx {
	private static final String CACHE_PATH = "/$image_cache/";
	static {
		IoUtils.createDirectoryRecursively(new File(MVCUtils.getRealPath(CACHE_PATH)));
	}

	private static final String NO_IMAGE_PATH = MVCUtils.getPageResourcePath()
			+ "/images/no_image.jpg";

	private final String url;

	public ImageCache(final String url) {
		this.url = url;
	}

	public String getUrl() {
		return url;
	}

	public String getPath(final PageRequestResponse rRequest) {
		return getPath(rRequest, 128, 128);
	}

	public String getPath(final PageRequestResponse rRequest, final int width, final int height) {
		String url = getUrl();
		if (!StringUtils.hasText(url)) {
			url = NO_IMAGE_PATH;
		}
		final String filename = StringUtils.hash(url) + "_" + width + "_" + height + ".png";
		final File cFile = new File(MVCUtils.getRealPath(CACHE_PATH) + File.separator + filename);
		if (!cFile.exists() || cFile.length() == 0) {
			InputStream inputStream = null;
			try {
				if (HttpUtils.isAbsoluteUrl(url)) {
					inputStream = new URL(url).openStream();
				}
			} catch (final IOException e) {
			}
			if (inputStream == null) {
				File oFile = new File(MVCUtils.getRealPath(url));
				if (!oFile.exists()) {
					oFile = new File(MVCUtils.getRealPath(NO_IMAGE_PATH));
				}
				try {
					inputStream = new FileInputStream(oFile);
				} catch (final FileNotFoundException e) {
				}
			}
			try {
				ImageUtils.thumbnail(inputStream, width, height, new FileOutputStream(cFile), "png");
			} catch (final IOException e) {
				log.warn(e);
			}
		}
		return rRequest.wrapContextPath(CACHE_PATH + filename);
	}
}
