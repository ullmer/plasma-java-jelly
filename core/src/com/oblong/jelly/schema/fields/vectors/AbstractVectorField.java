package com.oblong.jelly.schema.fields.vectors;

import com.oblong.jelly.Slaw;
import com.oblong.jelly.schema.SlawSchema;
import com.oblong.jelly.schema.fields.AbstractField;
import com.oblong.util.ExceptionHandler;

/**
 * @author Karol, 2014-05-30
 */
public abstract class AbstractVectorField<T> extends AbstractField<T> {

	protected final int numDims;

	public AbstractVectorField(SlawSchema schema, boolean isOptional, String name, int numDims) {
		super(schema, isOptional, name);
		this.numDims = numDims;

	}


	@Override final protected T fromSlaw_Custom(Slaw slaw) {
		Slaw[] slaws = slaw.emitArray();
		int length = slaws.length;
		checkVectorLength(length);
		return fromSlawsToVectorArray(slaws);
	}

	public int getNumDims() {
		return numDims;
	}

	protected void checkVectorLength(int length) {
		if ( length != getNumDims()) {
			ExceptionHandler.handleException("length != numDims: " + length + " vs " + getNumDims() +
					" For this field " + AbstractVectorField.this);
		}
	}

	protected abstract T fromSlawsToVectorArray(Slaw[] slaws);

}
