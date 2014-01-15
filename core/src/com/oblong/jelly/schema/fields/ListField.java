package com.oblong.jelly.schema.fields;

import com.oblong.jelly.Slaw;
import com.oblong.jelly.schema.HasToSlaw;
import com.oblong.jelly.schema.SlawSchema;
import com.oblong.jelly.schema.UnmarshalledSlaw;
import com.oblong.jelly.slaw.java.SlawList;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * The creation of lists of appropriate item objects (e.g. List<SlideSlaw>) is offloaded to ProteinLang.
 * This is done to prevent bloated number of inner classes (and, hypothetically,
 * risk of bloated app binary and slow startup speed).
 *
 * User: karol
 * Date: 7/10/13
 * Time: 2:04 PM
 */
public class ListField<T extends HasToSlaw> extends AbstractField<List<T>> {

	public ListField(String name, SlawSchema schema) {
		super(name, schema);
	}

	public ListField(String name) {
		super(name, null);
	}

	@Override
	protected List<T> getCustom(Slaw containingSlaw) {
		throw new UnsupportedOperationException("This is handled in ProteinLang getters generation, instead of here," +
				"to avoid using reflection or having a need for bloated number of inner classes.");
//		return containingSlaw.emitList();
	}

	@Override
	public Slaw toSlaw(List<T> value) {
		List<Slaw> retList = new ArrayList<Slaw>();
		for (T unmarshalledSlaw : value) {
			retList.add(unmarshalledSlaw.toSlaw());
		}
		return Slaw.list(retList);
	}

	public void putTo(UnmarshalledSlaw targetUnmarshalledSlaw, List<T> listUSlaws) {
		targetUnmarshalledSlaw.put(this, listUSlaws);
	}

	@Override
	public SlawList getRawSlawFrom(Map<Slaw, Slaw> map) {
		return (SlawList) super.getRawSlawFrom(map);
	}
}
