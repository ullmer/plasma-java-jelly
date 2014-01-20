package com.oblong.jelly.schema.fields;


import com.oblong.jelly.ISlawMap;
import com.oblong.jelly.schema.SlawSchema;
import com.oblong.jelly.schema.UnmarshalledSlaw;
import com.oblong.jelly.slaw.java.SlawMap;

public class UnmarshalledSlawMap extends UnmarshalledSlaw {

  protected ISlawMap slawMap = null;


  public UnmarshalledSlawMap(ISlawMap slawMap) {
    this.slawMap = slawMap;
  }

  @Override
  public SlawSchema getSchema() {
    throw new UnsupportedOperationException("FIXME remove this");
  }
}
