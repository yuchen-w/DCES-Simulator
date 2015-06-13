package agents;

import actions.MasterAction;
import uk.ac.imperial.presage2.core.environment.ActionHandlingException;
import uk.ac.imperial.presage2.core.simulator.Step;
import uk.ac.imperial.presage2.util.participant.AbstractParticipant;

import java.util.ArrayList;
import java.util.UUID;

public class MasterAgent extends ParentAgent {
    MasterAction Action;


    public MasterAgent(UUID id, String name)
    {
        super(id, name);
        Action = new MasterAction(id, ChildrenList);
    }



    @Step
    public void step(int t) throws ActionHandlingException {
        Action.setT(t);
        try
        {
            environment.act(Action, getID(), authkey);
        } catch (ActionHandlingException e) {
            logger.warn("Master Agent failed to act.", e);
        }
        //Do allocate if State=allocate. Use Allocate action.
    }
}
