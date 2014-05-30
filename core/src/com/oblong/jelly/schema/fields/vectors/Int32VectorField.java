package com.oblong.jelly.schema.fields.vectors;

import com.oblong.jelly.Slaw;
import com.oblong.jelly.schema.SlawSchema;

/**
 * @author Karol, 2014-05-30
 */
public class Int32VectorField extends AbstractVectorField<int[]> {

	public Int32VectorField(SlawSchema schema, boolean isOptional, String name, int numDims) {
		super(schema, isOptional, name, numDims);
	}

	@Override
	protected int[] fromSlawsToVectorArray(Slaw[] slaws) {
		int[] retArray = new int[slaws.length];
		int i = 0;
		for (Slaw curSlaw : slaws) {
			retArray[i] = curSlaw.emitInt();
			i ++;
		}
		return retArray;
	}

	@Override
	protected Slaw toSlaw_Custom(int[] value) {
		checkVectorLength(value.length);
		return Slaw.vector(value);
	}
}
