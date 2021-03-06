package agents;

import actions.childDemand;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import uk.ac.imperial.presage2.core.environment.ActionHandlingException;
import uk.ac.imperial.presage2.core.simulator.Step;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.UUID;

public class ProsumerAgent extends ParentAgent {

    @Inject
    @Named("params.children")
    public int children;

    protected String parent;
    protected UUID parent_id;
    childDemand AgentDemand;

    public String request = "request.csv";
    public String allocation = "allocation.csv";

    ArrayList<Integer> Productivity = new ArrayList<>();
    ArrayList<Integer> SocialUtility = new ArrayList<>();

    int CanonEqualityWeight = 1;
    int CanonNeedsWeight = 1;
    int CanonProductivityWeight = 1;
    int CanonSocialUtilityWeight = 1;
    int CanonSupplyAndDemandWeight = 1;


    //Used by SimpleDNOSim
    @Deprecated
    public ProsumerAgent(UUID id, String name, double consumption, double allocation, String parent, UUID parent_id)
    {
        super(id, name, consumption, allocation, 0);
        this.parent = parent;
        this.parent_id = parent_id;
        this.AgentDemand = new childDemand(consumption, allocation, id, parent_id);
        logger.info("Initiated " + name + " with d: " +consumption+ " and g=" +allocation );
    }

    //Used by newer simulator
    public ProsumerAgent(UUID id, String name, String parent, UUID parent_id)
    {
        super(id, name);
        this.parent = parent;
        this.parent_id = parent_id;
//        try{
//            PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("AgentBordaVotes.csv", true)));
//            out.println("Hour" +", "+ "AgentName" + ", " + "Equality" + " ,"
//                            +"Productivity"+ " ," + "Utility" + " ,"
//                            + "Needs" + " ," + "Supply and Demand"
//            );
//            out.close();
//        }catch (IOException e) {
//            logger.info("Failed to write to file" + allocation);
//        }
    }

    public void addProductivity(int i)
    {
        this.Productivity.add(i);
    }

    public void addSocialUtility(int i)
    {
        this.SocialUtility.add(i);
    }

    void setBordaWeights(childDemand AgentDemand)
    {
        this.CanonEqualityWeight        = children - AgentDemand.getCanonEqualityRank() + 1;
        this.CanonProductivityWeight    = children - AgentDemand.getCanonProductivityRank() + 1;
        this.CanonSocialUtilityWeight   = children - AgentDemand.getCanonSocialUtilityRank() + 1;
        this.CanonNeedsWeight           = children - AgentDemand.getCanonNeedsRank() + 1;
        this.CanonSupplyAndDemandWeight = children - AgentDemand.getCanonSupplyAndDemandRank() + 1;

        try{
            PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("AgentCanonVotes.csv", true)));
            out.println(hourCount +", "+ this.getID() + ", " + this.CanonEqualityWeight + " ,"
                            +this.CanonProductivityWeight + " ," + this.CanonSocialUtilityWeight + " ,"
                            + CanonNeedsWeight + " ," + CanonSupplyAndDemandWeight
            );
            out.close();
        }catch (IOException e) {
            logger.info("Failed to write to file" + allocation);
        }
    }

    public void addProfileHourly(double D, double G)
    {
        this.demandProfile.addProfile(D, G);
    }

    @Step
    public void step(int t) throws ActionHandlingException {
        AgentDemand = new childDemand(demandProfile.getDemandRequest(hourCount), demandProfile.getGenerationRequest(hourCount), this.getID(), this.parent_id);
        AgentDemand.setT(t);
        AgentDemand.setProductivity(Productivity.get(hourCount));
        AgentDemand.setSocial_utility(SocialUtility.get(hourCount));

        AgentDemand.setCanonNeedsWeight(CanonNeedsWeight);
        AgentDemand.setCanonEqualityWeight(CanonEqualityWeight);
        AgentDemand.setCanonProductivityWeight(CanonProductivityWeight);
        AgentDemand.setCanonSocialUtilityWeight(CanonSocialUtilityWeight);
        AgentDemand.setCanonSupplyAndDemandWeight(CanonSupplyAndDemandWeight);
        AgentDemand.setHour(hourCount);

        //logger.info("Agent: " + getID() + " Total Canon Weight: " + AgentDemand.getTotalCanonWeight() + " Composed of: ");    //debug

        try
        {
            environment.act(AgentDemand, getID(), authkey);
        } catch (ActionHandlingException e) {
            logger.warn("Failed to add demand to the pool", e);
        }
        if(t% AgentDemand.getStateNum() == 4){
            logger.info("Agent: " + this.getID() + " d: " + AgentDemand.getDemandRequest() + " allocation: d =" + AgentDemand.getAllocationD() + " g = " + AgentDemand.getGenerationRequest() + " allocation g = " + AgentDemand.getAllocationG());

            try{
                PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(request, true)));
                out.println(hourCount +", " + this.getName() + ", " +AgentDemand.getDemandRequest()+ ", "+ AgentDemand.getGenerationRequest() + "," + Productivity.get(hourCount) + "," + SocialUtility.get(hourCount));
                out.close();
            }catch (IOException e) {
                logger.info("Failed to write to file" + request);
            }

            try{
                PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(allocation, true)));
                out.println(hourCount +", "+ this.getName() + ", " +AgentDemand.getAllocationD()+ ", "+ AgentDemand.getAllocationG());
                out.close();
            }catch (IOException e) {
                logger.info("Failed to write to file" + allocation);
            }

            try{
                PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("AgentRanking.csv", true)));
                out.println(hourCount +", "+ this.getName() + " ," + this.getID() + " ," + AgentDemand.getCanonEqualityRank() + " ,"
                        + AgentDemand.getCanonNeedsRank() + " ," + AgentDemand.getCanonProductivityRank() + " ,"
                        + AgentDemand.getCanonSocialUtilityRank() + " ," + AgentDemand.getCanonSupplyAndDemandRank()
                        );
                out.close();
            }catch (IOException e) {
                logger.info("Failed to write to file" + allocation);
            }

            setBordaWeights(AgentDemand);

            hourCount++;
            logger.info("It is now hour: " + hourCount);
        }
    }
}


