package agents;

import actions.Demand;

import com.google.inject.Inject;
import com.google.inject.name.Named;

//import java.util.Set;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;

import services.ParentEnvService;
import services.PowerPoolEnvService;
import uk.ac.imperial.presage2.core.environment.*;
//import uk.ac.imperial.presage2.core.environment.ParticipantSharedState;
import uk.ac.imperial.presage2.core.simulator.Initialisor;
import uk.ac.imperial.presage2.core.simulator.Step;
//import uk.ac.imperial.presage2.util.location.Location;
//import uk.ac.imperial.presage2.util.location.Move;
//import uk.ac.imperial.presage2.util.location.ParticipantLocationService;
import uk.ac.imperial.presage2.util.participant.AbstractParticipant;
import uk.ac.imperial.presage2.util.participant.StateAccessor;

public class ParentAgent extends AbstractParticipant
{
    private final Demand GroupDemand;
    //public State<Integer> ChildrenNum;


    public ParentAgent(UUID id, String name, double consumption, double allocation, int ChildrenNum)
    {
        super(id, name);
        this.GroupDemand = new Demand(consumption, allocation, ChildrenNum);
        //this.ChildrenNum = new State<Integer>("ChildrenNum", ChildrenNum);

    }

    public ParentAgent(UUID id, String name, double consumption, double allocation, String behaviour)
    {
        super(id, name);
        this.GroupDemand = new Demand(consumption, allocation);
    }

    //For using with SharedState
//    public int getChildrenNum()
//    {
//        return this.ChildrenNum.get();
//    }

    @Initialisor
    public void init()
    {
        super.initialise();
    }


    @Step
    public void step(int t) throws ActionHandlingException {
        logger.info("My required Group Demand is: " 	+ this.GroupDemand.getDemand());
        logger.info("My Group Generation is: " 	+ this.GroupDemand.getGeneration());
        logger.info("My Group Allocation is: "+ this.GroupDemand.getAllocation());

        try
        {
			environment.act(GroupDemand, getID(), authkey);
		} catch (ActionHandlingException e) {
			logger.warn("Failed to add demand to the pool", e);
		}

    }


}


