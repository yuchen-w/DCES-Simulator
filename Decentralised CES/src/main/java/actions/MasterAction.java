package actions;

import java.util.ArrayList;
import java.util.UUID;

public class MasterAction extends TimestampedAction {
    UUID AgentID;
    ArrayList<UUID> ChildrenList;

    public MasterAction(UUID AgentID, ArrayList<UUID> ChildrenList)
    {
        this.AgentID = AgentID;
        this.ChildrenList = ChildrenList;
    }

    public ArrayList<UUID> getChildrenList()
    {
        return ChildrenList;
    }

    public UUID getAgentID()
    {
        return AgentID;
    }
}

