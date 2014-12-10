package org.magnum.mobilecloud.video.core;

/**
 * Created by cong on 11/2/14.
 */
public class GiftStatus {
    public enum GiftState {
		READY, PROCESSING
	}

	private GiftState state;

	public GiftStatus(GiftState state) {
		super();
		this.state = state;
	}

	public GiftState getState() {
		return state;
	}

	public void setState(GiftState state) {
		this.state = state;
	}

}
