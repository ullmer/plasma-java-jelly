package com.oblong.jelly.schema;

import com.oblong.jelly.schema.fields.AbstractField;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * Candidates for names: descriptor, metadata, contents, fields, structure, !!schema!!
 *
 *  TODO: consider making a relationship between MapField and SlawSchema
 *
 * Created with IntelliJ IDEA.
 * User: karol
 * Date: 9/5/13
 * Time: 2:00 PM
 */
public class SlawSchema {
	private final List<AbstractField<?>> fields = new ArrayList<AbstractField<?>>();

	public void add(AbstractField<?> fieldToAdd) {
		for (AbstractField<?> existingField : fields) {
			if ( fieldToAdd.getName().equals(existingField.getName() )) {
				throw new RuntimeException("Field with the same name was already added: " + existingField);
			}
		}
		fields.add(fieldToAdd);
	}

	public <T> boolean has(AbstractField<T> field) {
		return fields.contains(field);
	}
}
