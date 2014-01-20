package com.oblong.jelly;

import com.oblong.jelly.schema.fields.AbstractField;

import java.util.Map;

/**
* Created with IntelliJ IDEA.
* User: karol
* Date: 7/10/13
* Time: 3:59 PM
*/
public interface ISlawMap {

	<Type> Type get(AbstractField<Type> field);

	Map<Slaw,Slaw> emitContainedMap();

}
