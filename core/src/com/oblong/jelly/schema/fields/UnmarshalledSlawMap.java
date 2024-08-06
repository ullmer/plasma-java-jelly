package com.oblong.jelly.schema.fields;


import com.oblong.jelly.ISlawMap;
import com.oblong.jelly.schema.SlawSchema;
import com.oblong.jelly.schema.UnmarshalledSlaw;
import com.oblong.jelly.slaw.MapSlawToSlaw;
import com.oblong.jelly.slaw.java.SlawMap;

public abstract class UnmarshalledSlawMap extends UnmarshalledSlaw {

  // Navjot: put ingests


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


	@Override public final SlawMap toSlaw() {
		MapSlawToSlaw mapSlawToSlaw = new MapSlawToSlaw();
		addFieldsToMap(mapSlawToSlaw);
		return mapSlawToSlaw.toSlaw();
	}

	protected void addFieldsToMap(MapSlawToSlaw mapSlawToSlaw) {
		// DO NOTHING. Empty method to allow immediate subclass to call super() in generated code, for consistency
	}

}
