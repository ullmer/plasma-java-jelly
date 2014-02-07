package com.oblong.jelly.schema;

import com.oblong.jelly.Slaw;

/**
 * Created with IntelliJ IDEA.
 * User: karol
 * Date: 9/6/13
 * Time: 10:17 PM
 *
 */
public interface HasToSlaw<T extends Slaw> {
	Slaw toSlaw();
}
