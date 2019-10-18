package org.renjin.cran.curl;

import org.renjin.eval.EvalException;
import org.renjin.repackaged.guava.base.Charsets;
import org.renjin.sexp.*;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

/**
 * Drop in replacement for C code.
 */
public class curl {

  public static SEXP R_new_handle() {
    SEXP handle = new ExternalPtr<>(new CurlHandle());
    handle = handle.setAttribute(Symbols.CLASS, new StringArrayVector("curl_handle"));
    return handle;
  }

  public static SEXP R_curl_version() {
    // Pretend to be the latest version curl + dependencies
    ListVector.NamedBuilder list = new ListVector.NamedBuilder();
    list.add("version", "7.47.0");
    list.add("ssl_version", "OpenSSL/1.0.2g");
    list.add("libz_version", "1.2.8");
    list.add("libssh_version", StringVector.NA);
    list.add("libidn_version", "1.32");
    list.add("host", "x86_64-pc-linux-gnu");
    list.add("protocols", new StringArrayVector("http", "https"));
    list.add("ipv6", LogicalVector.TRUE);
    list.add("http2", LogicalVector.FALSE);

    return list.build();
  }

  public static SEXP R_handle_setopt(SEXP ptr, SEXP keysSexp, SEXP values) {
    CurlHandle handle = getHandle(ptr);
    AtomicVector keyVector = (AtomicVector) keysSexp;

    for (int i = 0; i < keyVector.length(); i++) {
      int key = keyVector.getElementAsInt(i);
      CurlOption option = CurlOption.fromCode(key);
      SEXP value = values.getElementAsSEXP(i);

      handle.setOption(option, value);
    }

    return LogicalVector.TRUE;
  }

  public static SEXP R_handle_setheaders(SEXP ptr, SEXP vec) {
    if (!(vec instanceof StringVector)) {
      throw new EvalException("header vector must be a string.");
    }
    CurlHandle handle = getHandle(ptr);
    handle.setHeaders((StringVector)vec);

    return LogicalVector.TRUE;
  }

  public static SEXP R_handle_reset(SEXP ptr) {
    //reset all fields

    return LogicalVector.TRUE;
  }

  public static SEXP R_curl_fetch_memory(SEXP url, SEXP ptr, SEXP nonblocking) throws IOException {
    CurlHandle handle = getHandle(ptr);
    handle.setOption(CurlOption.URL, url);

    return handle.fetchRaw();
  }

  public static SEXP R_get_handle_response(SEXP ptr) throws IOException {

    CurlHandle handle = getHandle(ptr);

    ListVector.NamedBuilder res = new ListVector.NamedBuilder();
    res.add("url", handle.getUrl());
    res.add("status_code", handle.getLastResponse().getStatusCode());
    res.add("headers", handle.getLastResponse().getHeaders());
    res.add("modified", IntVector.NA);
    res.add("times", handle.getLastResponse().getTimings());
    res.add("content", Null.INSTANCE);

    return res.build();
  }

  public static SEXP R_get_handle_cookies(SEXP ptr) {

    CurlHandle handle = getHandle(ptr);

    return handle.getCookies();
  }

  public static SEXP R_curl_escape(SEXP urlSexp, SEXP unescape_) throws UnsupportedEncodingException {
    if (!(urlSexp instanceof StringVector)) {
      throw new EvalException("`url` must be a character vector.");
    }

    StringVector urlVector = (StringVector) urlSexp;
    boolean unescape = unescape_.asLogical().toBooleanStrict();
    int n = urlSexp.length();

    StringArrayVector.Builder output = new StringVector.Builder(0, n);
    for (String url : urlVector) {
      if (unescape) {
        output.add(URLDecoder.decode(url, Charsets.UTF_8.name()));
      } else {
        output.add(URLEncoder.encode(url, Charsets.UTF_8.name()));
      }
    }
    return output.build();
  }

  private static CurlHandle getHandle(SEXP ptr) {
    if(!(ptr instanceof ExternalPtr)) {
      throw new EvalException("invalid handle");
    }
    Object object = ((ExternalPtr) ptr).getInstance();
    if(!(object instanceof CurlHandle)) {
      throw new EvalException("invalid handle");
    }
    return (CurlHandle) object;
  }

}
