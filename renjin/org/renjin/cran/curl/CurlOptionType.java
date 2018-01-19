package org.renjin.cran.curl;

import org.renjin.sexp.IntVector;
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
  },
  INTEGER {
    @Override
    public Object parse(SEXP value) {
      if(!(value instanceof IntVector) || value.length() != 1) {
        throw new IllegalArgumentException("expected an integer");
      }
      return value.asInt();
    }
  },
  UNKNOWN {
    @Override
    public Object parse(SEXP value) {
      return value;
    }
  };

  public abstract Object parse(SEXP value);
}
