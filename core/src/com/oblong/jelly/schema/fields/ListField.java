package com.oblong.jelly.schema.fields;

import com.oblong.jelly.Slaw;
import com.oblong.jelly.schema.HasToSlaw;
import com.oblong.jelly.schema.SlawSchema;
import com.oblong.jelly.schema.UnmarshalledSlaw;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: karol
 * Date: 7/10/13
 * Time: 2:04 PM
 * To change this template use File | Settings | File Templates.
 */
public class ListField extends AbstractField<List<Slaw>> {

	public ListField(String name, SlawSchema schema) {
		super(name, schema);
	}

	public ListField(String name) {
		super(name, null);
	}

	@Override
	protected List<Slaw> getCustom(Slaw containingSlaw) {
		return containingSlaw.emitList();
	}

	@Override
	public Slaw toSlaw(List<Slaw> value) {
		return Slaw.list(value);
	}

	public void putTo(UnmarshalledSlaw targetUnmarshalledSlaw, List<? extends HasToSlaw> listUSlaws) {
		List<Slaw> listSlaws = new ArrayList<Slaw>();
		for (HasToSlaw uSlaw : listUSlaws) {
			listSlaws.add(uSlaw.toSlaw());
		}
		targetUnmarshalledSlaw.put(this, listSlaws);
	}
}
