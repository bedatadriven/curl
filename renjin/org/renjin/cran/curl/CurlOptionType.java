package org.renjin.cran.curl;

import org.renjin.sexp.LogicalVector;
import org.renjin.sexp.SEXP;
import org.renjin.sexp.StringVector;

public enum  CurlOptionType {
  STRING {
    @Override
    public Object parse(SEXP value) {
      if(!(value instanceof StringVector) || value.length() != 1) {
        throw new IllegalArgumentException("expected a string");
      }
      return value.asString();
    }
  },
  BOOLEAN {
    @Override
    public Object parse(SEXP value) {
      if(!(value instanceof LogicalVector) || value.length() != 1) {
        throw new IllegalArgumentException("expected a logical");

      }
      return value.asLogical().toBooleanStrict();
    }
  };

  public abstract Object parse(SEXP value);
}
