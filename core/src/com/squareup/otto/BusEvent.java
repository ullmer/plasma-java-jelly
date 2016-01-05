package com.squareup.otto;

/**
 * Although this is an interface com.squareup.otto only allows subscription to concrete class types.
 *
 * This is a cleaned up version of the OttoEvent hierarchy that Karol introduced.  It should probably be replaced with Object. That's how
 * it is in the un-bodged Otto code.  For now all classes passed on the bus must implement this, and this is defined in the otto package.
 */
public interface BusEvent {
}
