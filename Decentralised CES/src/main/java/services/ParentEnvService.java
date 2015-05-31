package services;

import actions.Demand;
import actions.childDemand;
import java.util.UUID;

import javafx.scene.Parent;
import org.apache.log4j.Logger;
import com.google.inject.Inject;
import uk.ac.imperial.presage2.core.environment.EnvironmentSharedStateAccess;
import uk.ac.imperial.presage2.core.environment.ServiceDependencies;

import java.util.HashMap;

@ServiceDependencies({ PowerPoolEnvService.class })
public class ParentEnvService extends PowerPoolEnvService {

    private HashMap<UUID, Demand> GroupDemandStorage = new HashMap<>();
    private final Logger logger = Logger.getLogger(this.getClass());


    @Inject
    public ParentEnvService(EnvironmentSharedStateAccess sharedState) {
        super(sharedState);
    }


    public void addtoPool(childDemand d) {
        logger.info("Attempting to add to Parent Pool. ");

        //Add to history
        //Add to Pool[ParentID] for agent
        if (GroupDemandStorage.containsKey(d.getParentID()) == false)
        {
            logger.info("For ParentID " +d.getParentID()+" HashMap Entry !Exists, creating new entry. (ParentID:"+d.getParentID()+")");
            GroupDemandStorage.put(d.getParentID(),d);
        }
        else
        {
            logger.info("For ParentID " +d.getParentID()+" HashMap Entry Exists, adding demand.");
            Demand temp;
            temp = GroupDemandStorage.get(d.getParentID());
            GroupDemandStorage.put(d.getParentID(), temp.addDemand(d));

        }
    }


}