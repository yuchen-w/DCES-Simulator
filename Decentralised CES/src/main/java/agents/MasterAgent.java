package agents;

import actions.MasterAction;
import org.eclipse.jdt.internal.compiler.ast.AllocationExpression;
import uk.ac.imperial.presage2.core.environment.ActionHandlingException;
import uk.ac.imperial.presage2.core.simulator.Step;
import uk.ac.imperial.presage2.util.participant.AbstractParticipant;

import java.util.UUID;

public class MasterAgent extends AbstractParticipant {
    MasterAction Action = new MasterAction();
    public MasterAgent(UUID id, String name)
    {
        super(id, name);
    }

    @Step
    public void step(int t) throws ActionHandlingException {
        try
        {
            environment.act(Action, getID(), authkey);
        } catch (ActionHandlingException e) {
            logger.warn("Master Agent failed to act.", e);
        }
        //Do allocate if State=allocate. Use Allocate action.
    }
}
