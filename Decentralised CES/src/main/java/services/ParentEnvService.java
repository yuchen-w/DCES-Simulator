package services;

import actions.childDemand;
import com.google.inject.Inject;
import uk.ac.imperial.presage2.core.environment.EnvironmentSharedStateAccess;
import uk.ac.imperial.presage2.core.environment.ServiceDependencies;

@ServiceDependencies({ PowerPoolEnvService.class })
public class ParentEnvService extends PowerPoolEnvService {
    @Inject
    public ParentEnvService(EnvironmentSharedStateAccess sharedState) {
        super(sharedState);
    }


    public void addtoPool(childDemand d) {
        this.totalDemand = this.totalDemand + d.getDemand();
        this.totalGeneration = this.totalGeneration + d.getGeneration();
    }

}