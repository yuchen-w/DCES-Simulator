package agents;

import actions.parentDemand;

import java.util.*;
import actions.DemandProfile;
import uk.ac.imperial.presage2.core.environment.*;
import uk.ac.imperial.presage2.core.simulator.Initialisor;
import uk.ac.imperial.presage2.core.simulator.Step;
import uk.ac.imperial.presage2.util.participant.AbstractParticipant;


public class ParentAgent extends AbstractParticipant
{
    private parentDemand GroupDemand;
    protected ArrayList<UUID> ChildrenList = new ArrayList<UUID>();
    DemandProfile demandProfile = new DemandProfile();
    int hourCount = 0;

    public ParentAgent(UUID id, String name, double consumption, double allocation, int ChildrenNum)
    {
        super(id, name);
        this.GroupDemand = new parentDemand(consumption, allocation, id, ChildrenList);
    }

    public ParentAgent(UUID id, String name)
    {
        super(id, name);
    }



    @Initialisor
    public void init()
    {
        super.initialise();
    }

    public void addChild(UUID id)
    {
        ChildrenList.add(id);
    }

    @Step
    public void step(int t) throws ActionHandlingException {
//        logger.info("My required Group parentDemand is: " 	+ this.GroupDemand.getDemandRequest());
//        logger.info("My Group Generation is: " 	+ this.GroupDemand.getGenerationRequest());
//        logger.info("My Group Allocation is: "+ this.GroupDemand.getAllocation());
        GroupDemand.setT(t);
        try
        {
			environment.act(GroupDemand, getID(), authkey);
		} catch (ActionHandlingException e)
        {
			logger.warn("Failed to add Group Demand to the pool", e);
		}

    }


}


