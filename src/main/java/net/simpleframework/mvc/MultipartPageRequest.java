package net.simpleframework.mvc;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import net.simpleframework.common.IoUtils;
import net.simpleframework.lib.com.oreilly.servlet.MultipartRequest;

/**
 * 这是一个开源的软件，请在LGPLv3下合法使用、修改或重新发布。
 * 
 * @author 陈侃(cknet@126.com, 13910090885)
 *         http://code.google.com/p/simpleframework/
 *         http://www.simpleframework.net
 */
public class MultipartPageRequest extends HttpServletRequestWrapper implements
		IMultipartPageRequest {
	private final MultipartRequest mRequest;

	public MultipartPageRequest(final HttpServletRequest request, final int maxUploadSize)
			throws IOException {
		super(request);
		final MVCConfig config = MVCContextFactory.config();
		mRequest = new MultipartRequest(request, config.getTmpdir().getAbsolutePath(), maxUploadSize,
				config.getCharset());
	}

	@Override
	public Enumeration<?> getParameterNames() {
		return mRequest.getParameterNames();
	}

	@Override
	public String[] getParameterValues(final String name) {
		return mRequest.getParameterValues(name);
	}

	@Override
	public String getParameter(final String name) {
		return mRequest.getParameter(name);
	}

	@Override
	public IMultipartFile getFile(final String name) {
		return new IMultipartFile() {

			@Override
			public String getOriginalFilename() {
				return mRequest.getOriginalFileName(name);
			}

			@Override
			public File getFile() {
				return mRequest.getFile(name);
			}

			@Override
			public long getSize() {
				final File file = mRequest.getFile(name);
				return file == null ? 0 : file.length();
			}

			@Override
			public InputStream getInputStream() throws IOException {
				final File file = mRequest.getFile(name);
				return file == null ? null : new FileInputStream(file);
			}

			@Override
			public byte[] getBytes() throws IOException {
				final ByteArrayOutputStream oStream = new ByteArrayOutputStream();
				IoUtils.copyStream(getInputStream(), oStream);
				return oStream.toByteArray();
			}

			@Override
			public void transferTo(final File file) throws IOException {
				if (file.exists() && !file.delete()) {
					throw new IOException("Destination file [" + file.getAbsolutePath()
							+ "] already exists and could not be deleted");
				}
				try {
					IoUtils.copyFile(getInputStream(), file);
				} catch (final IOException ex) {
					throw ex;
				} catch (final Exception ex) {
					throw new IOException("Could not transfer to file: " + ex.getMessage());
				}
			}
		};
	}
}
