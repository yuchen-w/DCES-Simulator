package simpleSimulation;

import org.apache.log4j.Logger;
import uk.ac.imperial.presage2.core.environment.EnvironmentService;
import uk.ac.imperial.presage2.core.environment.EnvironmentSharedStateAccess;
import uk.ac.imperial.presage2.core.event.EventListener;
import uk.ac.imperial.presage2.core.simulator.EndOfTimeCycle;

public class GameService extends EnvironmentService 
{
	protected GameService(EnvironmentSharedStateAccess sharedState) {
		super(sharedState);
		// TODO Auto-generated constructor stub
	}

	private final Logger logger = Logger.getLogger(this.getClass());
	RoundType round = RoundType.INIT;
	int roundNumber = 0;

	@EventListener
	public void onIncrementTime(EndOfTimeCycle e)
	{
		if (round == RoundType.DEMAND)
		{
			round = RoundType.APPROPRIATE;
		}
		else
		{
			round = RoundType.DEMAND;
		}
		roundNumber++;
		logger.info("Round: " + round);
	}
}
