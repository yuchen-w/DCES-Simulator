
package state;

public class SimState {

    private State state;

    public SimState()
    {
        this.state = State.ChildrenRequest;
    }

    public void IncrementState()
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
    }

    public State getState()
    {
        return state;
    }
}
