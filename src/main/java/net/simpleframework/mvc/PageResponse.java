package net.simpleframework.mvc;

import java.io.ByteArrayOutputStream;
import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.zip.GZIPOutputStream;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import net.simpleframework.common.TextUtils;

/**
 * 这是一个开源的软件，请在LGPLv3下合法使用、修改或重新发布。
 * 
 * @author 陈侃(cknet@126.com, 13910090885)
 *         http://code.google.com/p/simpleframework/
 *         http://www.simpleframework.net
 */
public class PageResponse extends HttpServletResponseWrapper {
	private class BufferedServletOutputStream extends ServletOutputStream {
		private OutputStream os;

		private ByteArrayOutputStream bos; // for html string

		public BufferedServletOutputStream() throws IOException {
			super();
			bos = new ByteArrayOutputStream();
		}

		@Override
		public void close() throws IOException {
			if (os != null) {
				os.close();
				os = null;
			}
			if (bos != null) {
				bos.close();
				bos = null;
			}
		}

		@Override
		public void write(final int b) throws IOException {
			if (os != null) {
				os.write(b);
			}
			if (bos != null) {
				bos.write(b);
			}
		}

		public void createOutputStream() throws IOException {
			os = getResponse().getOutputStream();
			if (gzipContent) {
				os = new GZIPOutputStream(os);
			}
			if (bos != null) {
				bos.close();
				bos = null;
			}
		}
	}

	private final boolean gzipContent;

	public PageResponse(final HttpServletResponse response, final boolean gzipContent)
			throws IOException {
		super(response);
		this.gzipContent = gzipContent;
		if (this.gzipContent) {
			response.setHeader("Content-Encoding", "gzip");
		}
	}

	void initOutputStream() throws IOException {
		if (stream != null) {
			stream.createOutputStream();
		}
	}

	@Override
	public void flushBuffer() throws IOException {
		// 避免数据输出到客户端
	}

	private BufferedServletOutputStream stream;

	@Override
	public ServletOutputStream getOutputStream() throws IOException {
		if (stream == null) {
			stream = new BufferedServletOutputStream();
		}
		return stream;
	}

	private PrintWriter writer;

	private CharArrayWriter output;

	@Override
	public PrintWriter getWriter() {
		if (writer == null) {
			writer = new PrintWriter(output = new CharArrayWriter());
		}
		return writer;
	}

	@Override
	public String toString() {
		if (writer != null) {
			// tomcat, jetty ...
			return output.toString();
		} else if (stream != null && stream.bos != null) {
			// weblogic ...
			try {
				final byte[] bytes = stream.bos.toByteArray();
				return new String(bytes, TextUtils.isUTF8(bytes) ? "UTF-8" : MVCContextFactory.config()
						.getCharset());
			} catch (final UnsupportedEncodingException e) {
			}
		}
		return super.toString();
	}
}
