package com.oblong.jelly.schema.fields;


import com.oblong.jelly.ISlawMap;
import com.oblong.jelly.schema.SlawSchema;
import com.oblong.jelly.schema.UnmarshalledSlaw;
import com.oblong.jelly.slaw.java.SlawMap;

public class UnmarshalledSlawMap extends UnmarshalledSlaw {

//  protected ISlawMap slawMap = null;

  public UnmarshalledSlawMap() {
  }

  public UnmarshalledSlawMap(ISlawMap slawMap) {
    // nothing to do with the parem; it is here just for consistency with subclasses
//    this.slawMap = slawMap;
  }

  @Override
  public SlawSchema getSchema() {
    throw new UnsupportedOperationException("FIXME remove this");
  }
}
