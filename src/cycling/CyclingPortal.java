package cycling;

import java.io.*;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CyclingPortal implements CyclingPortalInterface {

    private List<Race> raceList = new ArrayList<>();
    private List<Team> teamList = new ArrayList<>();

    /**
     * Get the races currently created in the platform.
     *
     * @return An array of race IDs in the system or an empty array if none exists.
     */
    public int[] getRaceIds() {
        int[] raceIds = new int[raceList.size()]; //creates an array from the ArrayList
        for (int i = 0; i < raceIds.length; i++) {
            raceIds[i] = raceList.get(i).getId();
        }
        return raceIds; //returns the ID created for the race
    }

    public int createRace(String name, String description) throws IllegalNameException, InvalidNameException {
    	/**
    	 * this method creates a new race with the name and description for the race which the user has entered
    	 */
        validateName(name); //validates whether the name has already been used

        Race race1 = getRace(name);
        if (race1 != null) {
            throw new IllegalNameException(name + " exists already."); //if the name has already been used in the system or is an illegal name then exception is thrown
        }

        Race race = new Race(name, description);
        raceList.add(race);

        return race.getId();
    }

    public String viewRaceDetails(int raceId) throws IDNotRecognisedException {
    	/**
    	 * method allows the interface to display the race details for a race
    	 */
        Race race = getRaceIfValidElseThrow(raceId);
        return race.toString();
    }

    public void removeRaceById(int raceId) throws IDNotRecognisedException {
    	/**
    	 * this method removes a race by the race ID entered
    	 */
        Race race = getRaceIfValidElseThrow(raceId);
        raceList.remove(race);
    }

    public int getNumberOfStages(int raceId) throws IDNotRecognisedException {
    	/**
    	 * method gets the number of stages of a race
    	 */
        Race race = getRaceIfValidElseThrow(raceId);
        return race.getStageList().size();
    }

    public int addStageToRace(int raceId, String stageName, String description, double length, LocalDateTime startTime,
                              StageType type)
            throws IDNotRecognisedException, IllegalNameException, InvalidNameException, InvalidLengthException {
        /**
         * this method adds a stage to a race
         */
    	assert startTime != null;
        if (length < 5) {
            throw new InvalidLengthException("length can't be less than 5km.");
            //if the length is longer than 5km then an exception is thrown 
        }

        validateName(stageName);

        Race race = getRaceIfValidElseThrow(raceId);

        Stage stage1 = race.getStage(stageName);
        if (stage1 != null) {
            throw new IllegalNameException("name already exists in the platform.");
            //if the name of the stage is already in the system then an exception is thrown
        }

        Stage stage = new Stage(stageName, description, length, startTime, type);
        race.add(stage);

        return stage.getId();
    }

    public int[] getRaceStages(int raceId) throws IDNotRecognisedException {
    	/**
    	 * thie meothod returns the number of stages in a race
    	 */
        Race race = getRaceIfValidElseThrow(raceId);
        int[] ids = race.getStageIds();
        return ids;
    }

    public double getStageLength(int stageId) throws IDNotRecognisedException {
    	/**
    	 * gets the length of the stage
    	 */
        Stage stage = getStageFromAnyRace(stageId);
        return stage.getLength();
    }

    public void removeStageById(int stageId) throws IDNotRecognisedException {
    	/**
    	 * removes the stage which has been entered
    	 */
        Stage stage = getStageFromAnyRace(stageId);
        for (Race race : raceList) {
            if (race.getStageList().contains(stage)) {
                race.remove(stage);
                break;
            }
        }
    }

    public int addCategorizedClimbToStage(int stageId, Double location, SegmentType type, Double averageGradient,
                                          Double length) throws IDNotRecognisedException, InvalidLocationException, InvalidStageStateException,
            InvalidStageTypeException {
    	/**
    	 * this method adds a categorized climb to a stage (a segment) 
    	 */

        Stage stage = getStageFromAnyRace(stageId);

        if (stage.getLength() < location) {
            throw new InvalidLocationException("location is out of bounds of the stage length.");
            //throws exception if the location is invalid 
        }

        if (stage.getStageState().equals("waiting for results")) {
            throw new InvalidStageStateException("stage is \"waiting for results\".");
            //throws exception if the stage state is not complete
        }

        if (stage.getStageType() == StageType.TT) {
            throw new InvalidStageTypeException("Time-trial stages cannot contain any segment.");
            //throws exception if the stage type is a time-trial 
        }

        Segment segment = new Segment(location, type, averageGradient, length);
        stage.add(segment);

        return segment.getId();
    }

    public int addIntermediateSprintToStage(int stageId, double location) throws IDNotRecognisedException,
            InvalidLocationException, InvalidStageStateException, InvalidStageTypeException {
    	/**
    	 * adds an intermediate sprint to a stage (a segment is added to the stage)
    	 */
        Stage stage = getStageFromAnyRace(stageId);

        if (stage.getLength() < location) {
            throw new InvalidLocationException("location is out of bounds of the stage length.");
        }

        if (stage.getStageState().equals("waiting for results")) {
            throw new InvalidStageStateException("stage is \"waiting for results\".");
        }

        if (stage.getStageType() == StageType.TT) {
            throw new InvalidStageTypeException("Time-trial stages cannot contain any segment.");
        }

        Segment segment = new Segment(location, SegmentType.SPRINT);
        stage.add(segment);

        return segment.getId();
    }

    public void removeSegment(int segmentId) throws IDNotRecognisedException, InvalidStageStateException {
    	/**
    	 * this method removes a segment from a stage 
    	 */
        Stage stage = getStageForSegmentIdFromAnyRace(segmentId);
        if (stage.getStageState().equals("waiting for results")) {
            throw new InvalidStageStateException("stage is \"waiting for results\".");
        }

        Segment segmentToRemove = null;
        List<Segment> segmentList = stage.getSegmentList();
        for (Segment segment : segmentList) {
            if (segment.getId() == segmentId) {
                segmentToRemove = segment;
                break;
            }
        }
        if (segmentToRemove != null) {
            stage.remove(segmentToRemove);
        }
    }

    public void concludeStagePreparation(int stageId) throws IDNotRecognisedException, InvalidStageStateException {
    	/**
    	 * concludes the stage state so that it is closed and cannot be edited 
    	 */
        Stage stage = getStageFromAnyRace(stageId);
        if (stage.getStageState().equals("waiting for results")) {
            throw new InvalidStageStateException("stage is \"waiting for results\".");
        }
        stage.setStageState("waiting for results");
    }

    public int[] getStageSegments(int stageId) throws IDNotRecognisedException {
    	/**
    	 * returns all the segment IDs for a stage
    	 */
        Stage stage = getStageFromAnyRace(stageId);

        List<Segment> segmentList = stage.getSegmentList();
        if (segmentList.isEmpty())
            return new int[0];

        segmentList.sort((a, b) -> {
            if (a.getLocation() < b.getLocation()) return -1;
            if (a.getLocation() > b.getLocation()) return +1;
            return 0;
        });

        int[] ids = new int[segmentList.size()];
        for (int i = 0; i < ids.length; i++) {
            ids[i] = segmentList.get(i).getId();
        }

        return ids;
    }

    public int createTeam(String name, String description) throws IllegalNameException, InvalidNameException {
    	/**
    	 * creates a team by creating an ID 
    	 */
        for (Team team : teamList) {
            if (team.getName().equals(name)) {
                throw new IllegalNameException("name already exists in the platform.");
                //if the name is already in the system then an exception is thrown
            }
        }

        validateName(name);

        Team team = new Team(name, description);
        teamList.add(team);

        return team.getId();
    }

    public void removeTeam(int teamId) throws IDNotRecognisedException {
    	/**
    	 * removes a team
    	 */
        Team team = getTeamIfValidElseThrow(teamId);
        teamList.remove(team);
    }

    public int[] getTeams() {
    	/**
    	 * returns an integer array with all the team IDs which have been created
    	 */
        int[] ids = new int[teamList.size()];
        for (int i = 0; i < ids.length; i++) {
            ids[i] = teamList.get(i).getId();
        }
        return ids;
    }

    public int[] getTeamRiders(int teamId) throws IDNotRecognisedException {
    	/**
    	 * returns the IDs of all the team riders
    	 */
        Team team = getTeamIfValidElseThrow(teamId);
        List<Rider> riderList = team.getRiderList();
        int[] ids = new int[riderList.size()];
        for (int i = 0; i < ids.length; i++) {
            ids[i] = riderList.get(i).getId();
        }
        return ids;
    }

    public int createRider(int teamID, String name, int yearOfBirth) throws
            IDNotRecognisedException, IllegalArgumentException {
    	/**
    	 * this method is used to create a rider ID from the information entered by the user
    	 */
        Team team = getTeamIfValidElseThrow(teamID);

        if (name == null || yearOfBirth < 1900) {
            throw new IllegalArgumentException("name of the rider is null or the year of birth is less than 1900.");
            //if the parameters aren't met then an exception is thrown
        }

        Rider rider = new Rider(name, yearOfBirth);
        team.add(rider);

        return rider.getId();
    }

    public void removeRider(int riderId) throws IDNotRecognisedException {
        Rider rider = getRiderIfValidElseThrow(riderId);
        Team team = getTeamForRiderElseThrow(riderId);
        team.remove(rider);

        /* When a rider is removed from the platform,
         all of its results should be also removed.
         Race results must be updated.*/

        for (Race race : raceList) {
            for (Stage stage : race.getStageList()) {
                if (stage.hasResult(riderId)) {
                    RiderResult result = stage.getResult(riderId);
                    stage.removeResult(result);
                }
            }
        }
    }

    public void registerRiderResultsInStage(int stageId, int riderId, LocalTime... checkpoints)
            throws IDNotRecognisedException, DuplicatedResultException, InvalidCheckpointsException,
            InvalidStageStateException {
    	/**
    	 * registers a riders result in the stage
    	 */

        Stage stage = getStageFromAnyRace(stageId);
        Rider rider = getRiderIfValidElseThrow(riderId);

        if (!stage.getStageState().equals("waiting for results")) {
            throw new InvalidStageStateException("stage is not \"waiting for results\". Results can only be added to a stage while it is \"waiting for results\".");
        }

        if (stage.getSegmentList().size() + 2 != checkpoints.length) {
            throw new InvalidCheckpointsException("length of checkpoints is not equal to n+2, where n is the number of segments in the stage; +2 represents the start time and the finish time of the stage.");
        }

        if (stage.hasResult(riderId)) {
            throw new DuplicatedResultException("rider has already a result for the stage. Each rider can have only one result per stage.");
        }

        stage.addResult(stageId, riderId, checkpoints);
    }

    public LocalTime[] getRiderResultsInStage(int stageId, int riderId) throws IDNotRecognisedException {
    	/**
    	 * returns the segment times for a rider in a stage
    	 */
        Stage stage = getStageFromAnyRace(stageId);
        Rider rider = getRiderIfValidElseThrow(riderId);

        if (!stage.hasResult(riderId)) {
            return new LocalTime[0];
        }

        RiderResult result = stage.getResult(riderId);
        return result.getSegmentTimes();
    }

    public LocalTime getRiderAdjustedElapsedTimeInStage(int stageId, int riderId) throws
            IDNotRecognisedException {
    	/**
    	 * gets the adjusted elapsed time in stage for a rider from a time trial
    	 */

        Stage stage = getStageFromAnyRace(stageId);
        Rider rider = getRiderIfValidElseThrow(riderId);

        if (!stage.hasResult(riderId)) {
            return null;
        }

        RiderResult stageResult = stage.getResult(riderId);
        if (stage.getStageType() == StageType.TT) {
            return stageResult.getFinishTime();
        }

        LocalTime adjustedElapsedTime = stage.getAdjustedElapsedTime(riderId);

        return adjustedElapsedTime;
    }

    public void deleteRiderResultsInStage(int stageId, int riderId) throws IDNotRecognisedException {
    	/**
    	 * deletes a riders result in a stage 
    	 */
        Stage stage = getStageFromAnyRace(stageId);
        Rider rider = getRiderIfValidElseThrow(riderId);
        RiderResult result = stage.getResult(riderId);
        stage.removeResult(result);
    }

    public int[] getRidersRankInStage(int stageId) throws IDNotRecognisedException {
    	/**
    	 * returns an integer array with the rank of the riders results from a stage
    	 */
        Stage stage = getStageFromAnyRace(stageId);
        if (stage.hasNoResult()) {
            return new int[0];
        }

        // A list of riders ID sorted by their elapsed time.
        int[] ranks = stage.getRiderRanks();
        return ranks;
    }

    public LocalTime[] getRankedAdjustedElapsedTimesInStage(int stageId) throws IDNotRecognisedException {
        Stage stage = getStageFromAnyRace(stageId);
        if (stage.hasNoResult()) {
            return new LocalTime[0];
        }

        LocalTime[] adjustedElapsedTimes = stage.getAdjustedElapsedTimes();
        //returns an array of the LocalTime of all the adjustedElapsedTimes
        return adjustedElapsedTimes;
    }

    public int[] getRidersPointsInStage(int stageId) throws IDNotRecognisedException {
    	/**
    	 * returns an integer array with all the riders points 
    	 */

        Stage stage = getStageFromAnyRace(stageId);
        if (stage.hasNoResult()) {
            return new int[0];
        }

        return stage.getRidersPointsInStage();
    }

    public int[] getRidersMountainPointsInStage(int stageId) throws IDNotRecognisedException {

        Stage stage = getStageFromAnyRace(stageId);
        if (stage.hasNoResult()) {
            return new int[0];
        }

        return stage.getRidersMountainPointsInStage();
    }

    /**
     * Method empties this MiniCyclingPortalInterface of its contents and resets all
     * internal counters.
     */
    public void eraseCyclingPortal() {
        raceList.clear();
        teamList.clear();
        Race.availableId = 1;
        Rider.availableId = 1;
        Segment.availableId = 1;
        Stage.availableId = 1;
        Team.availableId = 1;
    }

    public void saveCyclingPortal(String filename) throws IOException {
    	/**
    	 * this method saves the contents of internal coutners and saves them into a serialised and deserialised 
    	 */
        //Saving of object in a file
        FileOutputStream file = new FileOutputStream(filename);
        ObjectOutputStream out = new ObjectOutputStream(file);

        // Method for serialization of object
        DataToSerializeDeserialize data = new DataToSerializeDeserialize();

        data.availableRaceId = Race.availableId;
        data.availableRiderId = Rider.availableId;
        data.availableSegmentId = Segment.availableId;
        data.availableStageId = Stage.availableId;
        data.availableTeamId = Team.availableId;
        data.raceList = raceList;
        data.teamList = teamList;

        out.writeObject(data);
        
        //closes the FileOutputStream and ObjectOutputStream
        out.close();
        file.close();
    }

    public void loadCyclingPortal(String filename) throws IOException, ClassNotFoundException {
        // Reading the object from a file
        FileInputStream file = new FileInputStream(filename);
        ObjectInputStream in = new ObjectInputStream(file);

        // Method for deserialization of object
        DataToSerializeDeserialize data = (DataToSerializeDeserialize) in.readObject();

        Race.availableId = data.availableRaceId;
        Rider.availableId = data.availableRiderId;
        Segment.availableId = data.availableSegmentId;
        Stage.availableId = data.availableStageId;
        Team.availableId = data.availableTeamId;

        raceList = data.raceList;
        teamList = data.teamList;

        //closes the FileInputStream and ObjectInputStream
        in.close();
        file.close();
    }

    public void removeRaceByName(String name) throws NameNotRecognisedException {
    	/**
    	 * removes a race by the name entered 
    	 */
        Race race = getRaceIfValidElseThrow(name);
        raceList.remove(race);
    }

    public LocalTime[] getGeneralClassificationTimesInRace(int raceId) throws IDNotRecognisedException {
        Race race = getRaceIfValidElseThrow(raceId);
        List<Stage> stageList = race.getStageList();
        for (Stage stage : stageList) {
            if (stage.hasNoResult()) {
                return new LocalTime[0];
            }
        }

        Map<Integer, Integer> totalAdjustedTimes = new HashMap<>();

        for (Stage stage : stageList) {

            LocalTime[] adjustedElapsedTimes = stage.getAdjustedElapsedTimes();
            int[] allIds = stage.getAllIds();

            for (int i = 0; i < allIds.length; i++) {
                int id = allIds[i];
                LocalTime time = adjustedElapsedTimes[i];
                totalAdjustedTimes.put(id, totalAdjustedTimes.getOrDefault(id, 0) + time.toSecondOfDay());
            }
        }

        LocalTime[] ans = new LocalTime[totalAdjustedTimes.keySet().size()];

        int[] ridersGeneralClassificationRank = getRidersGeneralClassificationRank(raceId);

        for (int i = 0, j = 0; j < ans.length && i < ridersGeneralClassificationRank.length; i++, j++) {
            int id = ridersGeneralClassificationRank[i];
            int elapsedSeconds = totalAdjustedTimes.get(id);
            int hour = elapsedSeconds / 3600;
            int minute = (elapsedSeconds - hour * 3600) / 60;
            int second = elapsedSeconds - hour * 3600 - minute * 60;
            ans[j] = LocalTime.of(hour, minute, second);
        }

        return ans;
    }

    public int[] getRidersPointsInRace(int raceId) throws IDNotRecognisedException {
    	/**
    	 * Get the overall points of riders in a race
    	 */
        Race race = getRaceIfValidElseThrow(raceId);
        List<Stage> stageList = race.getStageList();
        for (Stage stage : stageList) {
            if (stage.hasNoResult()) {
                return new int[0];
            }
        }

        Map<Integer, Integer> totalPoints = new HashMap<>();
        for (Stage stage : race.getStageList()) {
            int[] ridersPointsInStage = stage.getRidersPointsInStage();
            int[] allIds = stage.getAllIds();
            for (int i = 0; i < allIds.length; i++) {
                int id = allIds[i];
                int point = ridersPointsInStage[i];
                totalPoints.put(id, totalPoints.getOrDefault(id, 0) + point);
            }
        }

        // now sort by the total elapsed time.
        int[] ans = new int[totalPoints.keySet().size()];
        int[] ridersGeneralClassificationRank = getRidersGeneralClassificationRank(raceId);

        for (int i = 0, j = 0; j < ans.length && i < ridersGeneralClassificationRank.length; i++, j++) {
            int id = ridersGeneralClassificationRank[i];
            int totalPoint = totalPoints.get(id);
            ans[j] = totalPoint;
        }

        return ans;
    }

    public int[] getRidersMountainPointsInRace(int raceId) throws IDNotRecognisedException {
    	/**
    	 * Get the overall mountain points of riders in a race
    	 * A list of riders' mountain points (i.e., the sum of their mountainpoints in all stages of the race), sorted by the total elapsed time.
    	 * An empty list if there is no result for any stage in the race. 
    	 * These points should match the riders returned by getRidersGeneralClassificationRank(int).
    	 */
        Race race = getRaceIfValidElseThrow(raceId);
        List<Stage> stageList = race.getStageList();
        for (Stage stage : stageList) {
            if (stage.hasNoResult()) {
                return new int[0];
            }
        }

        Map<Integer, Integer> totalPoints = new HashMap<>();
        for (Stage stage : race.getStageList()) {
            int[] ridersPointsInStage = stage.getRidersMountainPointsInStage();
            int[] allIds = stage.getAllIds();
            for (int i = 0; i < allIds.length; i++) {
                int id = allIds[i];
                int point = ridersPointsInStage[i];
                totalPoints.put(id, totalPoints.getOrDefault(id, 0) + point);
            }
        }

        // now sort by the total elapsed time.
        int[] ans = new int[totalPoints.keySet().size()];
        int[] ridersGeneralClassificationRank = getRidersGeneralClassificationRank(raceId);

        for (int i = 0, j = 0; j < ans.length && i < ridersGeneralClassificationRank.length; i++, j++) {
            int id = ridersGeneralClassificationRank[i];
            int totalPoint = totalPoints.get(id);
            ans[j] = totalPoint;
        }

        return ans;
    }

    public int[] getRidersGeneralClassificationRank(int raceId) throws IDNotRecognisedException {
    	/**
    	 * A ranked list of riders' IDs sorted ascending by the sum of theiradjusted elapsed times in all stages of the race. 
    	 * That is, the first in this list is the winner (least time). 
    	 * An empty list if there is no result for any stage in the race
    	 */
        Race race = getRaceIfValidElseThrow(raceId);
        List<Stage> stageList = race.getStageList();
        for (Stage stage : stageList) {
            if (stage.hasNoResult()) {
                return new int[0];
            }
        }

        Map<Integer, Integer> map = new HashMap<>();
        for (Stage stage : race.getStageList()) {
            LocalTime[] adjustedElapsedTimes = stage.getAdjustedElapsedTimes();
            int[] allIds = stage.getAllIds();
            for (int i = 0; i < allIds.length; i++) {
                int id = allIds[i];
                int time = adjustedElapsedTimes[i].toSecondOfDay();
                map.put(id, map.getOrDefault(id, 0) + time);
            }
        }

        List<Integer> idList = new ArrayList<>(map.keySet());

        idList.sort((a, b) -> {
            Integer pointA = map.get(a);
            Integer pointB = map.get(b);
            return pointA.compareTo(pointB);
        });

        int[] ans = new int[idList.size()];
        for (int i = 0; i < ans.length; i++) {
            ans[i] = idList.get(i);
        }
        return ans;
    }

    public int[] getRidersPointClassificationRank(int raceId) throws IDNotRecognisedException {
    	/**
    	 * A ranked list of riders' IDs sorted descending by the sum of theirpoints in all stages of the race. 
    	 * That is, the first in this list is the winner (more points).  
    	 * An empty list if there is no result for any stage in the race
    	 */
        Race race = getRaceIfValidElseThrow(raceId);
        List<Stage> stageList = race.getStageList();
        for (Stage stage : stageList) {
            if (stage.hasNoResult()) {
                return new int[0];
            }
        }

        Map<Integer, Integer> map = new HashMap<>();
        for (Stage stage : race.getStageList()) {
            int[] ridersPointsInStage = stage.getRidersPointsInStage();
            int[] allIds = stage.getAllIds();
            for (int i = 0; i < allIds.length; i++) {
                int id = allIds[i];
                map.put(id, map.getOrDefault(id, 0) + ridersPointsInStage[i]);
            }
        }

        List<Integer> idList = new ArrayList<>(map.keySet());

        idList.sort((a, b) -> {
            Integer pointA = map.get(a);
            Integer pointB = map.get(b);
            return pointA.compareTo(pointB);
        });

        int[] ans = new int[idList.size()];
        for (int i = 0; i < ans.length; i++) {
            ans[i] = idList.get(i);
        }
        return ans;
    }

    public int[] getRidersMountainPointClassificationRank(int raceId) throws IDNotRecognisedException {
    	/**
    	 * A ranked list of riders' IDs sorted descending by the sum of their mountain points in all stages of the race. 
    	 * That is, the first in this list is the winner (more points). 
    	 * An empty list if there is no result for any stage in the race
    	 */
        Race race = getRaceIfValidElseThrow(raceId);
        List<Stage> stageList = race.getStageList();
        for (Stage stage : stageList) {
            if (stage.hasNoResult()) {
                return new int[0];
            }
        }

        Map<Integer, Integer> map = new HashMap<>();
        for (Stage stage : race.getStageList()) {
            int[] ridersPointsInStage = stage.getRidersMountainPointsInStage();
            int[] allIds = stage.getAllIds();
            for (int i = 0; i < allIds.length; i++) {
                int id = allIds[i];
                map.put(id, map.getOrDefault(id, 0) + ridersPointsInStage[i]);
            }
        }

        List<Integer> idList = new ArrayList<>(map.keySet());

        idList.sort((a, b) -> {
            Integer pointA = map.get(a);
            Integer pointB = map.get(b);
            return pointA.compareTo(pointB);
        });

        int[] ans = new int[idList.size()];
        for (int i = 0; i < ans.length; i++) {
            ans[i] = idList.get(i);
        }
        return ans;
    }

    //**********************************//
    //        Our Private methods       //
    //**********************************//

    private Race getRaceIfValidElseThrow(String name) throws NameNotRecognisedException {
        Race race = getRace(name);
        if (race == null) {
            throw new NameNotRecognisedException("name does not match to any race in the system.");
        }
        return race;
    }

    private Race getRace(int raceId) {
        for (Race race : raceList) {
            if (race.getId() == raceId) {
                return race;
            }
        }
        return null;
    }

    private Race getRace(String name) {
        for (Race race : raceList) {
            if (race.getName().equals(name)) {
                return race;
            }
        }
        return null;
    }

    private Race getRaceIfValidElseThrow(int raceId) throws IDNotRecognisedException {
        Race race = getRace(raceId);
        if (race == null) {
            throw new IDNotRecognisedException(raceId + " does not exists.");
        }
        return race;
    }

    private Stage getStageFromAnyRace(int stageId) throws IDNotRecognisedException {
        for (Race race : raceList) {
            Stage stage = race.getStage(stageId);
            if (stage != null) {
                return stage;
            }
        }
        throw new IDNotRecognisedException("ID does not match to any stage in the system.");
    }

    private Stage getStageForSegmentIdFromAnyRace(int segmentId) throws IDNotRecognisedException {
        for (Race race : raceList) {
            for (Stage stage : race.getStageList()) {
                for (Segment segment : stage.getSegmentList()) {
                    if (segment.getId() == segmentId) {
                        return stage;
                    }
                }
            }
        }
        throw new IDNotRecognisedException("ID does not match to any segment in the system.");
    }

    private Segment getSegmentFromAnyRace(int segmentId) throws IDNotRecognisedException {
        for (Race race : raceList) {
            for (Stage stage : race.getStageList()) {
                for (Segment segment : stage.getSegmentList()) {
                    if (segment.getId() == segmentId) {
                        return segment;
                    }
                }
            }
        }
        throw new IDNotRecognisedException("ID does not match to any segment in the system.");
    }

    private Team getTeamIfValidElseThrow(int teamId) throws IDNotRecognisedException {
        for (Team team : teamList) {
            if (team.getId() == teamId) {
                return team;
            }
        }
        throw new IDNotRecognisedException(teamId + " does not exists.");
    }

    private Rider getRiderIfValidElseThrow(int riderId) throws IDNotRecognisedException {
        for (Team team : teamList) {
            for (Rider rider : team.getRiderList()) {
                if (rider.getId() == riderId) {
                    return rider;
                }
            }
        }

        throw new IDNotRecognisedException("ID does not match to any rider in the system");
    }

    private Team getTeamForRiderElseThrow(int riderId) throws IDNotRecognisedException {
        for (Team team : teamList) {
            for (Rider rider : team.getRiderList()) {
                if (rider.getId() == riderId) {
                    return team;
                }
            }
        }

        throw new IDNotRecognisedException("ID does not match to any rider in the system");
    }

    private void validateName(String name) throws InvalidNameException {
        if (name == null || name.isEmpty() || name.length() > 30 || name.contains(" ") || name.contains("\t")) {
            throw new InvalidNameException(name + " is not valid.");
        }
    }
}
