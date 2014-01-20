package com.oblong.jelly.communication;

import com.oblong.jelly.Hose;
import com.oblong.jelly.PoolServerAddress;
import com.oblong.jelly.communication.ObPoolConnector;

/**
 * Created with IntelliJ IDEA.
 * User: karol
 * Date: 8/30/13
 * Time: 1:59 PM
 *
 */
public abstract class HoseFactory {
	public abstract Hose createHose(ObPoolConnector obPoolConnector, PoolServerAddress obPoolsAddr, String obPool);
}
