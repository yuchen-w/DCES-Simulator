package services;


import actions.childDemand;
import uk.ac.imperial.presage2.core.environment.EnvironmentServiceProvider;
import uk.ac.imperial.presage2.core.environment.EnvironmentSharedStateAccess;
import uk.ac.imperial.presage2.core.environment.ServiceDependencies;

@ServiceDependencies({ ParentEnvService.class })
public class ChildEnvService extends PowerPoolEnvService
{
    public ChildEnvService(EnvironmentSharedStateAccess sharedState, EnvironmentServiceProvider serviceProvider)
    {
        super(sharedState, serviceProvider);
    }
}
