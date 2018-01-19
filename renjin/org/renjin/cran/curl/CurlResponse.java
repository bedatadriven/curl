package org.renjin.cran.curl;


import org.renjin.repackaged.guava.base.Charsets;
import org.renjin.sexp.*;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.List;
import java.util.Map;

public class CurlResponse {
  private HttpURLConnection connection;
  private RawVector headers;

  public CurlResponse(HttpURLConnection connection) {
    this.connection = connection;
  }

  public int getStatusCode() throws IOException {
    return connection.getResponseCode();
  }

  public RawVector getHeaders() {
    if(headers == null) {

      StringBuilder buffer = new StringBuilder(connection.getHeaderField(0));

      for (Map.Entry<String, List<String>> entry : connection.getHeaderFields().entrySet()) {
        if(entry.getKey() != null) {
          for (String value : entry.getValue()) {
            buffer.append(entry.getKey()).append(": ").append(value).append("\n");
          }
        }
      }

      headers = new RawVector(buffer.toString().getBytes(Charsets.UTF_8));
    }
    return headers;
  }

  public DoubleVector getTimings() {
    DoubleArrayVector.Builder times = new DoubleArrayVector.Builder();
    times.add(0);
    times.add(0);
    times.add(0);
    times.add(0);
    times.add(0);
    times.add(0);
    times.setAttribute(Symbols.NAMES,
        new StringArrayVector(
            "redirect",
            "namelookup",
            "connect",
            "pretransfer",
            "starttransfer",
            "total"));

    return times.build();
  }
}
