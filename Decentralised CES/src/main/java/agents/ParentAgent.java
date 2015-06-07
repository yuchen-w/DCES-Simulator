package agents;

import actions.parentDemand;

//import java.util.Set;
import java.util.*;

import actions.DemandProfile;
import uk.ac.imperial.presage2.core.environment.*;
//import uk.ac.imperial.presage2.core.environment.ParticipantSharedState;
import uk.ac.imperial.presage2.core.simulator.Initialisor;
import uk.ac.imperial.presage2.core.simulator.Step;
//import uk.ac.imperial.presage2.util.location.Location;
//import uk.ac.imperial.presage2.util.location.Move;
//import uk.ac.imperial.presage2.util.location.ParticipantLocationService;


public class ParentAgent extends MasterAgent
{
    private parentDemand GroupDemand;
    DemandProfile demandProfile = new DemandProfile();
    int hourCount = 0;

    //public State<Integer> ChildrenNum;


    public ParentAgent(UUID id, String name, double consumption, double allocation, int ChildrenNum)
    {
        super(id, name);
        this.GroupDemand = new parentDemand(consumption, allocation, id, ChildrenList);
        //this.ChildrenNum = new State<Integer>("ChildrenNum", ChildrenNum);

    }

    public ParentAgent(UUID id, String name)
    {
        super(id, name);
    }

    public void addProfileHourly(double D, double G)
    {
        this.demandProfile.addProfile(D, G);
    }

    @Initialisor
    public void init()
    {
        super.initialise();
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


