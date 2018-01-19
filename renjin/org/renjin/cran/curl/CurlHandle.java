package org.renjin.cran.curl;

import org.renjin.repackaged.guava.io.ByteStreams;
import org.renjin.sexp.RawVector;
import org.renjin.sexp.SEXP;
import org.renjin.sexp.StringArrayVector;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Holds the options and state associated with the curl handle.
 */
public class CurlHandle {

  private Map<CurlOption, Object> options = new HashMap<>();
  private CurlResponse lastResponse;

  public void setOption(CurlOption option, SEXP value) {
    options.put(option, option.getType().parse(value));
  }

  public String getStringOption(CurlOption option) {
    assert option.getType() == CurlOptionType.STRING;
    return (String) options.get(option);
  }

  public String getUrl() {
    return getStringOption(CurlOption.URL);
  }


  public SEXP fetchRaw() throws IOException {
    URL url = new URL(getStringOption(CurlOption.URL));
    HttpURLConnection connection = (HttpURLConnection) url.openConnection();

    byte[] data;
    try(InputStream inputStream = url.openStream()) {
      data = ByteStreams.toByteArray(inputStream);
    }

    lastResponse = new CurlResponse(connection);

    return new RawVector(data);
  }

  public CurlResponse getLastResponse() {
    return lastResponse;
  }

  public SEXP getCookies() {
    return StringArrayVector.EMPTY;
  }
}
