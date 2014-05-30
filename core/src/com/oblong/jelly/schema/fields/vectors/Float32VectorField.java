package com.oblong.jelly.schema.fields.vectors;

import com.oblong.jelly.Slaw;
import com.oblong.jelly.schema.SlawSchema;

/**
 * User: karol
 * Date: 2/3/14
 * Time: 5:31 PM
 */
public class Float32VectorField extends AbstractVectorField<float[]> {

	public Float32VectorField(SlawSchema schema, boolean isOptional, String name, int numDims) {
		super(schema, isOptional, name, numDims);
	}

	@Override
	protected float[] fromSlawsToVectorArray(Slaw[] slaws) {
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
		checkVectorLength(value.length);
		return Slaw.vector(value);
	}

}
