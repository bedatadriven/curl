package org.renjin.cran.curl;

import org.renjin.eval.EvalException;
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

    try {
      options.put(option, option.getType().parse(value));
    } catch (IllegalArgumentException e) {
      throw new EvalException("Failed to set value for option " + option.name() + ": " + e.getMessage() +
          " found: " + value.getTypeName());
    }
  }

  public boolean getBooleanOption(CurlOption option, boolean defaultValue) {
    Boolean value = (Boolean) options.get(option);
    if(value == null) {
      return defaultValue;
    } else {
      return value == Boolean.TRUE;
    }
  }

  public boolean getBooleanOption(CurlOption option) {
    return getBooleanOption(option, false);
  }

  public boolean isNoBody() {
    return getBooleanOption(CurlOption.NOBODY, false);
  }

  public String getStringOption(CurlOption option) {
    assert option.getType() == CurlOptionType.STRING;
    return (String) options.get(option);
  }

  public String getMethod() {
    if(options.containsKey(CurlOption.CUSTOMREQUEST)) {
      return getStringOption(CurlOption.CUSTOMREQUEST);

    } else if(getBooleanOption(CurlOption.POST) || getBooleanOption(CurlOption.HTTPPOST)) {
      return "POST";

    } else {
      return "GET";
    }
  }

  public String getUrl() {
    return getStringOption(CurlOption.URL);
  }


  public SEXP fetchRaw() throws IOException {
    URL url = new URL(getStringOption(CurlOption.URL));
    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    connection.setRequestMethod(getMethod());

    byte[] data;
    if(isNoBody()) {
      data = new byte[0];
    } else {
      if(connection.getResponseCode() >= 400) {
        try (InputStream inputStream = connection.getErrorStream()) {
          data = ByteStreams.toByteArray(inputStream);
        }
      } else {
        try (InputStream inputStream = connection.getInputStream()) {
          data = ByteStreams.toByteArray(inputStream);
        }
      }
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
