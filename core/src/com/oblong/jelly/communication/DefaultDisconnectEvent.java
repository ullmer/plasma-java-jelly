package com.oblong.jelly.communication;

/**
 * User: karol
 * Date: 11/26/13
 * Time: 5:23 PM
 */
public class DefaultDisconnectEvent extends AbstractDisconnectEvent {

	public final DisconnectReason disconnectReason;
	public final String disconnectReasonString;

	/**
	 * User: karol
	 * Date: 12/3/13
	 * Time: 1:32 PM
	 */
	public static enum DisconnectReason implements ErrorCause {
		SERVER_RESTART,
		HEARTBEAT_TIMEOUT,
		CONNECTION_LOST,
		UNRECOGNIZED_MEZZ_STATE,
//		PERMISSION_DENIED,
		UNKNOWN_DISCONNECT_REASON,
		USER_REFUSED_TO_ENTER_PASSPHRASE,

		/** Is this really disconnect or just kicked out of session? */
		PASSPHRASE_BECAME_ENABLED,

		USER_REQUESTED_DISCONNECTING
	}


	public DefaultDisconnectEvent(DisconnectReason disconnectReason, String disconnectReasonString) {
		this.disconnectReason = disconnectReason;
		this.disconnectReasonString = disconnectReasonString;
	}

	public DefaultDisconnectEvent(DisconnectReason disconnectReason) {
		this(disconnectReason, null);
	}

	@Override
	public String toString() {
		return "DisconnectEvent{" +
				"disconnectReason='" + disconnectReason + '\'' +
				"} " + super.toString();
	}


}
