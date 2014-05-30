package com.oblong.jelly.schema.fields.vectors;

import com.oblong.jelly.Slaw;
import com.oblong.jelly.schema.SlawSchema;

/**
 * User: karol
 * Date: 2/3/14
 * Time: 5:31 PM
 */
public class Float64VectorField extends AbstractVectorField<double[]> {

	public Float64VectorField(SlawSchema schema, boolean isOptional, String name, int numDims) {
		super(schema, isOptional, name, numDims);
	}

	@Override
	protected double[] fromSlawsToVectorArray(Slaw[] slaws) {
		double[] retArray = new double[slaws.length];
		int i = 0;
		for (Slaw curSlaw : slaws) {
			retArray[i] = curSlaw.emitDouble();
			i ++;
		}
		return retArray;
	}

	@Override
	protected Slaw toSlaw_Custom(double[] value) {
		checkVectorLength(value.length);
		return Slaw.vector(value);
	}

}
