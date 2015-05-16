package myagents;

import java.util.UUID;

import com.google.inject.Inject;
import com.google.inject.name.Named;

import uk.ac.imperial.presage2.core.environment.ActionHandlingException;
import uk.ac.imperial.presage2.core.simulator.Initialisor;
import uk.ac.imperial.presage2.core.simulator.Step;
import uk.ac.imperial.presage2.core.util.random.Random;
import uk.ac.imperial.presage2.util.location.Location;
import uk.ac.imperial.presage2.util.location.Move;
import uk.ac.imperial.presage2.util.participant.AbstractParticipant;

public class SimpleProsumerAgent extends AbstractParticipant {

	public State<Location> myLocation;

	@Inject
	@Named("params.size")
	public int size;

	SimpleProsumerAgent(UUID id, String name, Location myLocation) {
		super(id, name);
		this.myLocation = new State<Location>("util.location", myLocation);
	}

	@Initialisor
	public void init() {

	}

	@Step
	public void step(int t) throws ActionHandlingException {
		Location loc = myLocation.get();
		logger.info("My location is: " + myLocation.get());

		Move m = loc
				.getMoveTo(
						new Location(Random.randomInt(size), Random
								.randomInt(size)), 2);
		act(m);
	}

}