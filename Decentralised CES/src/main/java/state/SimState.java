
package state;

import org.apache.log4j.Logger;

public class SimState {

    private State state;
    private final Logger logger = Logger.getLogger(this.getClass());

    public SimState()
    {
        this.state = State.ChildrenRequest;
    }

    public void incrementState()
    {
        switch (this.state)
        {
            case ChildrenRequest: this.state = State.ParentRequest;
                break;
            case ParentRequest: this.state = State.Appropriate;
                break;
            case Appropriate: this.state = State.ChildrenRequest;
                break;
        }
        logger.info("Incrementing State. State is now "+ this.getState());
    }

    public State getState()
    {
        return state;
    }
}
