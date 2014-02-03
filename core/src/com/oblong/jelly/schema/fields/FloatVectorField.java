package com.oblong.jelly.schema.fields;

import com.oblong.jelly.Slaw;
import com.oblong.jelly.schema.SlawSchema;
import com.oblong.util.ExceptionHandler;

/**
 * Maybe call it "FloatVectorField"...
 *
 * User: karol
 * Date: 2/3/14
 * Time: 5:31 PM
 */
public class FloatVectorField extends AbstractField<float[]> {

	private final int numDims;

	public FloatVectorField(SlawSchema schema, boolean isOptional, String name, int numDims) {
		super(schema, isOptional, name);
		this.numDims = numDims;
	}

	@Override
	protected float[] getCustom(Slaw slaw) {
		Slaw[] slaws = slaw.emitArray();
		if ( slaws.length != numDims ) {
			ExceptionHandler.handleException("slaws.length != numDims: " + slaws.length +  " vs " + numDims +
					" For this field " + FloatVectorField.this);
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
	public Slaw toSlaw(float[] value) {
		return Slaw.array(value);
	}

}
