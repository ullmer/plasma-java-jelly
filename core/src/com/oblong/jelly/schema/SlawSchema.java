package com.oblong.jelly.schema;

import com.oblong.jelly.schema.fields.JellyField;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * Candidates for names: descriptor, metadata, contents, fields, structure, !!schema!!
 * Created with IntelliJ IDEA.
 * User: karol
 * Date: 9/5/13
 * Time: 2:00 PM
 * To change this template use File | Settings | File Templates.
 */
public class SlawSchema {
	private final List<JellyField<?>> fields = new ArrayList<JellyField<?>>();

	public void add(JellyField<?> fieldToAdd) {
		for (JellyField<?> existingField : fields) {
			if ( fieldToAdd.getName().equals(existingField.getName() )) {
				throw new RuntimeException("Field with the same name was already added: " + existingField);
			}
		}
		fields.add(fieldToAdd);
	}

	public <T> boolean has(JellyField<T> field) {
		return fields.contains(field);
	}
}
