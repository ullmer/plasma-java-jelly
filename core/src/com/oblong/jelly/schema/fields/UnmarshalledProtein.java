package com.oblong.jelly.schema.fields;

/*Generated by MPS */


import com.oblong.jelly.Protein;
import com.oblong.jelly.communication.OttoEvent;
import com.oblong.jelly.slaw.java.SlawString;

public abstract class UnmarshalledProtein extends UnmarshalledSlawMap implements OttoEvent {

  // Navjot: put descrips

  public UnmarshalledProtein() {
    // no need to do anything (subclasses will init their fields)
  }

  public UnmarshalledProtein(Protein protein) {
//    super((SlawMap) protein.ingests()); // note, allowing only map slaws, for simplification of ProteinLang
    super(null); //avoid NPE after I've moved to final fields initialized in ctors
    // ingests = protein.getIngests();
  }

  public abstract SlawString getMainDescrip();
}
