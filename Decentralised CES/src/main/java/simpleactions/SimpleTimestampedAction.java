package simpleactions;

import uk.ac.imperial.presage2.core.Action;

public abstract class SimpleTimestampedAction implements Action {

	int t;

	SimpleTimestampedAction() {
		super();
	}

	SimpleTimestampedAction(int t) {
		super();
		this.t = t;
	}

	public int getT() {
		return t;
	}

	public void setT(int t) {
		this.t = t;
	}

}