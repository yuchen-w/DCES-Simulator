package myagents;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import java.util.Set;
import java.util.UUID;
import uk.ac.imperial.presage2.core.environment.ActionHandlingException;
import uk.ac.imperial.presage2.core.environment.ParticipantSharedState;
import uk.ac.imperial.presage2.core.simulator.Initialisor;
import uk.ac.imperial.presage2.core.simulator.Step;
import uk.ac.imperial.presage2.util.location.Location;
import uk.ac.imperial.presage2.util.location.Move;
import uk.ac.imperial.presage2.util.participant.AbstractParticipant;

public class SituatedAgent extends AbstractParticipant {

    public State<Location> myLocation;
    public boolean alive;

    @Inject
    @Named("params.size")
    public int size;

    SituatedAgent(UUID id, String name, Location myLocation, boolean alive) {
        super(id, name);
        this.myLocation = new State<Location>("util.location", myLocation);
        this.alive = alive;
    }

    @Initialisor
    public void init() {
    }

    public void die() {
        alive = false;
    }
    
    public boolean getAlive(){
        return alive;
    }

    public Location getLocation() {
        try {
            return myLocation.get();
        } catch (Exception e) {
            return null;
        }
    }

    public boolean neighbour(SituatedAgent agent) {
        int myX, myY, aX, aY;
        Location myLoc, aLoc;

        try {
            myLoc = myLocation.get();
            aLoc = agent.myLocation.get();
        } catch (Exception e) {
            return false;
        }
        myX = (int) myLoc.getX();
        myY = (int) myLoc.getY();

        aX = (int) aLoc.getX();
        aY = (int) aLoc.getY();

        return Math.abs(myX - aX) <= 1 && Math.abs(myY - aY) <= 1;
    }

    @Step
    public void step(int t) throws ActionHandlingException {
        Location loc = myLocation.get();
        int x, y, dx = 1, dy = 1;

        logger.info("My location is: " + myLocation.get());

        if (Math.random() < 0.5) {
            dx *= -1;
        }

        x = (int) loc.getX() + dx;

        if (Math.random() < 0.5) {
            dy *= -1;
        }
        y = (int) loc.getY() + dy;

        Move m = loc
                .getMoveTo(
                        new Location(x, y));
        act(m);
    }

    @Override
    protected Set<ParticipantSharedState> getSharedState() {
        Set<ParticipantSharedState> ss = super.getSharedState();
        ss.add(new ParticipantSharedState("alive", alive, getID()));
        return ss;
    }

}
