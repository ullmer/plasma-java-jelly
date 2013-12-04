package com.oblong.jelly.communication;

import com.oblong.jelly.Protein;


/***
 * Used by threads to inform about important events
 * @author valeria
 *
 */
public interface ObPoolCommunicationEventHandler {
	
	void onProteinReceived(Protein p);

	/** sender thread connected */
	void onConnected();
	
	void onErrorConnecting(UnableToConnectEvent unableToConnectEvent);

	void onConnectionLost(String reason);

}
