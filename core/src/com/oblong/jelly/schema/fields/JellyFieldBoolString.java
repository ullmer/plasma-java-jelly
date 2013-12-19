package com.oblong.jelly.schema.fields;

import com.oblong.jelly.Slaw;
import com.oblong.jelly.SlawIlk;

/**
 * Created with IntelliJ IDEA.
 * User: valeria
 * Date: 12/19/13
 * Time: 1:56 PM
 */
public class JellyFieldBoolString extends JellyField<Object> {

	public JellyFieldBoolString(String name) {
		super(name);
	}

	@Override
	protected Object getCustom(Slaw slaw) {
		if(slaw.ilk() == SlawIlk.BOOL){
			return slaw.emitBoolean();
		} else {
			return slaw.emitString();
		}
	}

	@Override
	public Slaw toSlaw(Object value) {
		if(value instanceof Boolean){
			return Slaw.bool((Boolean) value);
		} else {
			return Slaw.string((String) value);
		}
	}

}
