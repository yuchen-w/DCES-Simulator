package actions;

import java.util.UUID;

public class childDemand extends Demand {

    UUID parent;
    //UUID actor;

    public childDemand(double demand, double generation, UUID parent) {
        super(demand, generation);
        this.parent = parent;
        //this.actor = actor;

    }

//    public UUID getActor() {
//        return actor;
//    }

    public UUID getParentID()
    {
        return parent;
    }
}

