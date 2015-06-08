package actions.handlers;

import actions.Demand;
import actions.childDemand;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import services.ParentEnvService;
import uk.ac.imperial.presage2.core.Action;
import uk.ac.imperial.presage2.core.environment.ActionHandlingException;
import uk.ac.imperial.presage2.core.environment.EnvironmentServiceProvider;
import uk.ac.imperial.presage2.core.environment.EnvironmentSharedStateAccess;
import uk.ac.imperial.presage2.core.environment.UnavailableServiceException;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.UUID;

import org.apache.log4j.Logger;
public class ChildDemandHandler extends DemandHandler{

    final private Logger logger = Logger.getLogger(ChildDemandHandler.class);

    private ParentEnvService ParentService;

    @Inject
    @Named("params.hours")
    protected int hours;	//Variable here needs to be the same as it is called in the SimpleSim.java file

    @Inject
    public ChildDemandHandler(EnvironmentServiceProvider serviceProvider, EnvironmentSharedStateAccess sharedState)
            throws UnavailableServiceException
    {
        super(serviceProvider, sharedState);
    }

    @Override
    public boolean canHandle(Action cDemand) {
        return cDemand instanceof childDemand;
    }

    @Override
    public Object handle(Action demand_action, UUID actor) throws ActionHandlingException {
        if (demand_action instanceof childDemand)
        {
            childDemand d = (childDemand)demand_action;
            getParentService();
            int CurrentState = d.getT()%d.getStateNum();

            if (CurrentState == 0)
            {
                //logger.info("T= "+ d.getT() +". Children Request round");
                //logger.info("ProsumerAgent: " + actor +" requesting: " + d.getDemandRequest() + " and is providing " + d.getGenerationRequest());		//Debug
                //logger.info ("Agent: " + d.getAgentID() + " Total Canon Weight: " + d.getTotalCanonWeight());
                this.ParentService.addToAgentPool(d);

            }

            if (CurrentState == 4)
            {
                getParentService();
                //logger.info("CurrentState = " + CurrentState + " T = "+ d.getT() +". Children Receive round");
                //logger.info("Agent: " + actor + " attempting to retrieve allocation");

                //todo
                Demand allocated = ParentService.getAllocation(actor);

                //logger.info("Agent demand was: " + d.getDemandRequest() + " " + d.getGenerationRequest());

                //todo
                d.allocate(allocated.getAllocationD(), allocated.getAllocationG());
                d.setCanonEqualityRank(allocated.getCanonEqualityRank());
                d.setCanonSupplyAndDemandRank(allocated.getCanonSupplyAndDemandRank());
                d.setCanonSocialUtilityRank(allocated.getCanonSocialUtilityRank());
                d.setCanonProductivityRank(allocated.getCanonProductivityRank());
                d.setCanonNeedsRank(allocated.getCanonNeedsRank());

                double satisfaction = allocated.getAllocationD()/d.getDemandRequest();
                //logger.info("ParentEnvService.Feedback");
                //ParentEnvService.Feedback(actor, satisfaction);
                ParentService.Feedback(actor, satisfaction);

                if (d.getT() == hours*d.getStateNum()-1)
                {
                    logger.info("Agent: " + actor + " Satisfaction " + ParentService.getSatisfaction(actor));

                    try{
                        PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("satisfaction.txt", true)));
                        out.println("Agent: " + actor + " Satisfaction " + ParentService.getSatisfaction(actor));
                        out.close();
                    }catch (IOException e) {
                        logger.info("Failed to write to file" + "satisfaction.txt");
                    }
                }
            }
        }
        return null;
    }

    protected ParentEnvService getParentService()
    {
        if (ParentService == null)
        {
            try
            {
//                logger.info("Getting ParentService");
                this.ParentService = serviceProvider.getEnvironmentService(ParentEnvService.class);
            }
            catch (UnavailableServiceException e)
            {
                logger.warn("Could not get ParentService", e);
            }
        }
        return ParentService;
    }
}
