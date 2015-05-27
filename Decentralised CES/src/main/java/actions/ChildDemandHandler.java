package actions;

import CES_Agents.ProsumerAgent;
import CES_Agents.ProsumerChildAgent;
import uk.ac.imperial.presage2.core.environment.EnvironmentServiceProvider;
import uk.ac.imperial.presage2.core.environment.EnvironmentSharedStateAccess;

import java.util.UUID;

public class ChildDemandHandler extends DemandHandler{

    private ProsumerAgent parent;

    public ChildDemandHandler(EnvironmentServiceProvider serviceProvider, EnvironmentSharedStateAccess sharedState) {
        super(serviceProvider, sharedState);
    }

    private ProsumerAgent getParent(ProsumerChildAgent childAgent)
    {
        //Get the parent via UUID
        //for (UUID pid : this)

           // for (UUID pid : this.membersService.getParticipants()) {

            //}
        return parent;
    }

}
