package com.oblong.jelly;

import com.oblong.jelly.schema.fields.AbstractField;

/**
* Created with IntelliJ IDEA.
* User: karol
* Date: 7/10/13
* Time: 3:59 PM
* To change this template use File | Settings | File Templates.
*/
public interface ISlawMap {

	<Type> Type get(AbstractField<Type> field);

}
