package com.oblong.jelly.schema.fields;


import com.oblong.jelly.schema.SlawSchema;
import com.oblong.jelly.schema.UnmarshalledSlaw;
import com.oblong.jelly.slaw.java.SlawMap;

public class UnmarshalledSlawMap extends UnmarshalledSlaw {

  protected SlawMap slawMap = null;


  public UnmarshalledSlawMap(SlawMap slawMap) {
    this.slawMap = slawMap;
  }

	@Override
	public SlawSchema getSchema() {
		throw new UnsupportedOperationException("FIXME remove this");
	}
}
