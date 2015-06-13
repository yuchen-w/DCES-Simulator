package services;

import actions.Demand;
import actions.parentDemand;
import actions.childDemand;

import java.util.UUID;

import org.apache.log4j.Logger;
import com.google.inject.Inject;
import uk.ac.imperial.presage2.core.environment.EnvironmentServiceProvider;
import uk.ac.imperial.presage2.core.environment.EnvironmentSharedStateAccess;
import uk.ac.imperial.presage2.core.environment.UnavailableServiceException;
import uk.ac.imperial.presage2.util.environment.EnvironmentMembersService;

import java.util.HashMap;

//@ServiceDependencies({ EnvironmentMembersService.class })
public class ParentEnvService extends PowerPoolEnvService {

    protected final EnvironmentMembersService membersService;
    private PowerPoolEnvService EnvService;

    private HashMap<UUID, Demand> GroupDemandStorage = new HashMap<UUID, Demand>();

    private final Logger logger = Logger.getLogger(this.getClass());


    @Inject
    public ParentEnvService(EnvironmentServiceProvider serviceProvider, EnvironmentSharedStateAccess sharedState) {
        super(sharedState, serviceProvider);
        this.membersService = getMembersService(serviceProvider);
    }

    private EnvironmentMembersService getMembersService(
            EnvironmentServiceProvider serviceProvider) {
        try {
            return serviceProvider
                    .getEnvironmentService(EnvironmentMembersService.class);
        } catch (UnavailableServiceException e) {
            logger.warn("Could not retrieve EnvironmentMembersService in ParentEnvService; functionality limited.");
            return null;
        }
    }

    private PowerPoolEnvService getParentService()
    {
        if (EnvService == null)
        {
            try
            {
                logger.info("Getting ParentService (GlobalService) of ParentEnvService");
                this.EnvService = serviceProvider.getEnvironmentService(PowerPoolEnvService.class);
            }
            catch (UnavailableServiceException e)
            {
                logger.warn("Could not get ParentService (GlobalService)", e);
            }
        }
        return EnvService;
    }

    @Override
    public void incrementRequestCounter(UUID ParentID)
    {
        getParentService();
        if (RequestCounter.containsKey(ParentID) == false)
        {
            RequestCounter.put(ParentID, 1);
        }
        else
        {
            RequestCounter.put(ParentID,RequestCounter.get(ParentID)+1);
            if (RequestCounter.get(ParentID) >= ChildrenNum)
            {
                RequestCounter.put(ParentID, 0);    //Reset to zero if exceeds bigger than No. of children
                //Next State can't be changed here
            }
        }
    }
}