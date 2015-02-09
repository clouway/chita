package com.clouway.chita;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author Tsony Tsonev (tsony.tsonev@clouway.com)
 */
public interface ChitaTransport {
  <T> T in(InputStream in, Class<T> type) throws IOException;

  <T> void out(OutputStream out, Class<T> type, T data) throws IOException;

  String contentType();
}
