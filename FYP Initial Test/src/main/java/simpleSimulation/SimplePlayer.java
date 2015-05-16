package simpleSimulation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.math.stat.descriptive.DescriptiveStatistics;
import org.apache.commons.math.stat.descriptive.SummaryStatistics;




import uk.ac.imperial.presage2.core.db.StorageService;
import uk.ac.imperial.presage2.core.db.persistent.TransientAgentState;
import uk.ac.imperial.presage2.core.environment.ActionHandlingException;
import uk.ac.imperial.presage2.core.environment.ParticipantSharedState;
import uk.ac.imperial.presage2.core.environment.UnavailableServiceException;
import uk.ac.imperial.presage2.core.simulator.Initialisor;
import uk.ac.imperial.presage2.core.simulator.Step;
import uk.ac.imperial.presage2.util.participant.AbstractParticipant;


public class SimplePlayer extends AbstractParticipant
{

	double storageCapacity = 0;
	double powerConsumption = 0;
	double powerGeneration = 0;

	public SimplePlayer(UUID id, String name) 
	{
		super(id, name);
		// TODO Auto-generated constructor stub
	}

	public double getStorageCapacity()
	{
		return storageCapacity;
	}

	public double getPowerConsumption()
	{
		return powerConsumption;
	}

	public double getPowerGeneration()
	{
		return powerGeneration;
	}

	@Initialisor
	public void init() 
	{
		
	}

	protected void demand(double d)
	{
		try {
			environment.act(new Demand(d), getID(), authkey);
			this.d = d;
		} catch (ActionHandlingException e) {
			logger.warn("Failed to demand", e);
		}
	}

	protected void provision(double p) {
		try {
			environment.act(new Provision(p), getID(), authkey);
			this.p = p;
		} catch (ActionHandlingException e) {
			logger.warn("Failed to provision", e);
		}
	}

	protected void appropriate(double r) {
		try {
			environment.act(new Appropriate(r), getID(), authkey);
		} catch (ActionHandlingException e) {
			logger.warn("Failed to appropriate", e);
		}
	}

	@Step
	public void step(int t) throws ActionHandlingException
	{
		//super.execute();
		if (game.getRound()==RoundType.DEMAND)
		{
			powerConsumption = game.getPowerConsumption(getID());
		}
		else if (game.getRound() == RoundType.APPROPRIATE)
		{
			appropriate(game.getAllocated(getID()));
		}
	}

}
