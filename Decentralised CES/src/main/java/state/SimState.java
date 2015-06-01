
package state;

import org.apache.log4j.Logger;

public class SimState {

    private State state;
    private final Logger logger = Logger.getLogger(this.getClass());

    public SimState()
    {
        this.state = State.Request;
    }

    public void incrementState()
    {
        switch (this.state)
        {
            case Request: this.state = State.Appropriate;
                break;
            case Appropriate: this.state = State.Request;
                break;
        }
        logger.info("Incrementing State. State is now "+ this.getState());
    }

    public State getState()
    {
        return state;
    }
}
