package com.clouway.chita;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author Mihail Lesikov (mlesikov@gmail.com)
 */
public class TextTransport implements ChitaTransport {


  @Override
  public <T> T in(InputStream in, Class<T> type) throws IOException {
    return type.cast(IOUtils.toString(in));
  }

  @Override
  public <T> void out(OutputStream out, Class<T> type, T data) {
    try {
      IOUtils.write(data.toString(), out);
    } catch (IOException var5) {
      throw new RuntimeException(var5);
    }
  }

  @Override
  public String contentType() {
    return "text/plain";
  }
}
