package cycling;

import java.io.Serializable;
import java.util.List;

public class DataToSerializeDeserialize implements Serializable {
    public int availableRaceId;
    public int availableRiderId;
    public int availableSegmentId;
    public int availableStageId;
    public int availableTeamId;

    public List<Race> raceList;
    public List<Team> teamList;
}
