package actions;

import uk.ac.imperial.presage2.core.Action;

public abstract class TimestampedAction implements Action {

	int t;
	int StateNum = 4;

	TimestampedAction() {
		super();
	}

	TimestampedAction(int t) {
		super();
		this.t = t;
	}

	public int getT() {
		return t;
	}

	public void setT(int t) {
		this.t = t;
	}

	public int getStateNum()
	{
		return this.StateNum;
	}
}