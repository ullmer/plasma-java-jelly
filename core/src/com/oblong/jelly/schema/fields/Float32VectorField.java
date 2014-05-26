package com.oblong.jelly.schema.fields;

import com.oblong.jelly.Slaw;
import com.oblong.jelly.schema.SlawSchema;
import com.oblong.util.ExceptionHandler;

import java.util.Arrays;

/**
 * User: karol
 * Date: 2/3/14
 * Time: 5:31 PM
 */
public class Float32VectorField extends AbstractField<float[]> {

	private final int numDims;

	public Float32VectorField(SlawSchema schema, boolean isOptional, String name, int numDims) {
		super(schema, isOptional, name);
		this.numDims = numDims;
	}

	@Override
	protected float[] fromSlaw_Custom(Slaw slaw) {
		Slaw[] slaws = slaw.emitArray();
		if ( slaws.length != numDims ) {
			ExceptionHandler.handleException("slaws.length != numDims: " + slaws.length +  " vs " + numDims +
					" For this field " + Float32VectorField.this);
		}
		float[] retArray = new float[slaws.length];
		int i = 0;
		for (Slaw curSlaw : slaws) {
			retArray[i] = curSlaw.emitFloat();
			i ++;
		}
		return retArray;
	}

	@Override
	protected Slaw toSlaw_Custom(float[] value) {
		if ( value.length != numDims ) {
			throw new IllegalArgumentException("Wrong number of dimensions: " + numDims + " vs actual: " + Arrays.toString(value));
		}
		return Slaw.vector(value);
	}

	public int getNumDims() {
		return numDims;
	}

}
