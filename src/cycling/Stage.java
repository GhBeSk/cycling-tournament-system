package cycling;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Stage implements Serializable {
	/**
	 * creates a stage 
	 */
	
	//variables which will be used
    public static int availableId = 1;

    private int id;
    private String stageName;
    private String description;
    private double length;
    private LocalDateTime startTime;
    private StageType stageType;
    private String stageState;

    private List<Segment> segmentList;
    private List<RiderResult> stageResult;

    //creates a stage 
    public Stage(String stageName, String description, double length, LocalDateTime startTime, StageType stageType) {
        this.id = availableId;
        availableId += 1;

        this.stageName = stageName;
        this.description = description;
        this.length = length;
        this.startTime = startTime;
        this.stageType = stageType;
        this.stageState = "";

        this.segmentList = new ArrayList<>();
        this.stageResult = new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public double getLength() {
        return length;
    }

    public String getStageName() {
        return stageName;
    }

    public String getStageState() {
        return stageState;
    }

    public void setStageState(String stageState) {
        this.stageState = stageState;
    }

    public StageType getStageType() {
        return stageType;
    }

    public List<Segment> getSegmentList() {
        return segmentList;
    }

    public void add(Segment segment) {
        segmentList.add(segment);
    }

    public void remove(Segment segment) {
        segmentList.remove(segment);
    }

    //checks whether a rider has a result for the stage  
    boolean hasResult(int riderId) {
        for (int i = 0; i < stageResult.size(); i++) {
            if (stageResult.get(i).getRiderId() == riderId) {
                return true;
            }
        }
        return false;
    }

    //adds a result to the stage results 
    void addResult(int stageId, int riderId, LocalTime[] localTimes) {
        if (hasResult(riderId)) {
            return;
        }
        RiderResult riderResult = new RiderResult(stageId, riderId, localTimes);
        stageResult.add(riderResult);
    }

    //gets the result for a rider from a stage 
    RiderResult getResult(int riderId) {
        for (int i = 0; i < stageResult.size(); i++) {
            RiderResult riderResult = stageResult.get(i);
            if (riderResult.getRiderId() == riderId) {
                return riderResult;
            }
        }
        return null;
    }

    public void removeResult(RiderResult result) {
        stageResult.remove(result);
    }

    public boolean hasNoResult() {
        return stageResult.isEmpty();
    }

    //returns the riders ranking in order 
    public int[] getRiderRanks() {

        stageResult.sort((a, b) -> {
            if (a.getElapsedSeconds() < b.getElapsedSeconds()) return -1;
            if (a.getElapsedSeconds() > b.getElapsedSeconds()) return +1;
            return 0;
        });

        int[] ids = new int[stageResult.size()];
        for (int i = 0; i < ids.length; i++) {
            ids[i] = stageResult.get(i).getRiderId();
        }
        return ids;
    }

    //gets all the IDs for the stages created 
    public int[] getAllIds() {
        int[] ids = new int[stageResult.size()];
        for (int i = 0; i < ids.length; i++) {
            ids[i] = stageResult.get(i).getRiderId();
        }
        return ids;
    }

    //returns the LocalTimes array for all the adjusted elapsed times 
    public LocalTime[] getAdjustedElapsedTimes() {
        sortResultsByFinishTime();

        LocalTime[] localTimes = new LocalTime[stageResult.size()];
        localTimes[0] = stageResult.get(0).getFinishTime();
        for (int i = 1; i < localTimes.length; i++) {
            int i_1_second = stageResult.get(i - 1).getFinishTime().toSecondOfDay();
            int i_second = stageResult.get(i).getFinishTime().toSecondOfDay();
            if (i_1_second + 1 == i_second) {
                localTimes[i] = localTimes[i - 1];
            } else {
                localTimes[i] = stageResult.get(i).getFinishTime();
            }
        }

        return localTimes;
    }

    //sorts the results bt fnish time
    private void sortResultsByFinishTime() {
        stageResult.sort((a, b) -> {
            int secondA = a.getFinishTime().toSecondOfDay();
            int secondB = b.getFinishTime().toSecondOfDay();
            if (secondA < secondB) return -1;
            if (secondA > secondB) return +1;
            return 0;
        });
    }

    public LocalTime getAdjustedElapsedTime(int riderId) {
        // we may think that all start time is same
        // finish - start is same as comparing with finish
        // but let's try with finish time first as suggested
        sortResultsByFinishTime();

        int i = 0;
        for (; i < stageResult.size(); i++) {
            if (stageResult.get(i).getRiderId() == riderId) {
                break;
            }
        }

        if (i == stageResult.size()) {
            return null;
        }

        //        i
        //1 3 4 5 9
        //0 1 2 3 4
        LocalTime localTime = null;
        for (int j = i; j >= 1; j--) {
            int aj = stageResult.get(j).getFinishTime().toSecondOfDay();
            int aj_1 = stageResult.get(j - 1).getFinishTime().toSecondOfDay();
            if (aj_1 + 1 == aj) {
                continue;
            }
            localTime = stageResult.get(j).getFinishTime();
            break;
        }
        return localTime;
    }

    public List<RiderResult> getStageResult() {
        return stageResult;
    }

    //returns an integer array of the riders points in a stage 
    public int[] getRidersPointsInStage() {

        Map<Integer, Integer> riderPoints = new HashMap<>();
        // init points to 0
        for (RiderResult result : stageResult) {
            riderPoints.put(result.getRiderId(), 0);
        }

        // for first 15 riders if any
        var pointsForIntermediateSprint = new int[]{20, 17, 15, 13, 11, 10, 9, 8, 7, 6, 5, 4, 3, 2, 1};
        int totalRidersToGivePoints = Math.min(15, stageResult.size());

        for (int i = 0; i < segmentList.size(); i++) {
            Segment segment = segmentList.get(i);
            SegmentType segmentType = segment.getSegmentType();
            if (segmentType != SegmentType.SPRINT) {
                // ignore if not an intermediate sprint.
                continue;
            }

            // for segment i sort the stageResult first based on segmentTimes.get(i).toSecondsOfDay()
            sortBySegmentIndex(i);

            for (int j = 0; j < totalRidersToGivePoints; j++) {
                RiderResult riderResult = stageResult.get(j);
                int riderId = riderResult.getRiderId();
                riderPoints.put(riderId, riderPoints.get(riderId) + pointsForIntermediateSprint[j]);
            }
        }

        // now allocate points based on who finishes early
        stageResult.sort((a, b) -> {
            if (a.getElapsedSeconds() < b.getElapsedSeconds()) return -1;
            if (a.getElapsedSeconds() > b.getElapsedSeconds()) return +1;
            return 0;
        });

        Map<StageType, int[]> points = new HashMap<>();
        points.put(StageType.FLAT, new int[]{50, 30, 20, 18, 16, 14, 12, 10, 8, 7, 6, 5, 4, 3, 2});
        points.put(StageType.MEDIUM_MOUNTAIN, new int[]{30, 25, 22, 19, 17, 15, 13, 11, 9, 7, 6, 5, 4, 3, 2});
        points.put(StageType.HIGH_MOUNTAIN, new int[]{20, 17, 15, 13, 11, 10, 9, 8, 7, 6, 5, 4, 3, 2, 1});
        points.put(StageType.TT, new int[]{20, 17, 15, 13, 11, 10, 9, 8, 7, 6, 5, 4, 3, 2, 1});

        // for first 15 riders if any
        int[] pointsForThisStageType = points.get(stageType);

        for (int j = 0; j < totalRidersToGivePoints; j++) {
            RiderResult riderResult = stageResult.get(j);
            int riderId = riderResult.getRiderId();
            riderPoints.put(riderId, riderPoints.get(riderId) + pointsForThisStageType[j]);
        }

        int[] ans = new int[stageResult.size()];
        for (int i = 0; i < ans.length; i++) {
            ans[i] = riderPoints.get(stageResult.get(i).getRiderId());
        }

        return ans;
    }

   
    private void sortBySegmentIndex(int segmentIndexToUse) {
        stageResult.sort((a, b) -> {

            LocalTime[] segmentTimesOfA = a.getSegmentTimes();
            LocalTime[] segmentTimesOfB = b.getSegmentTimes();

            LocalTime localTimeOfA = segmentTimesOfA[segmentIndexToUse];
            LocalTime localTimeOfB = segmentTimesOfB[segmentIndexToUse];

            // 2 AM == 2 * 3600 sec
            int secondA = localTimeOfA.toSecondOfDay();
            int secondB = localTimeOfB.toSecondOfDay();

            if (secondA < secondB) return -1;
            if (secondA > secondB) return +1;

            return 0;
        });
    }

    public int[] getRidersMountainPointsInStage() {

        Map<SegmentType, int[]> points = new HashMap<>();
        points.put(SegmentType.HC, new int[]{20, 15, 12, 10, 8, 6, 4, 2});
        points.put(SegmentType.C1, new int[]{10, 8, 6, 4, 2, 1, 0, 0});
        points.put(SegmentType.C2, new int[]{5, 3, 2, 1, 0, 0, 0, 0});
        points.put(SegmentType.C3, new int[]{2, 1, 0, 0, 0, 0, 0, 0});
        points.put(SegmentType.C4, new int[]{1, 0, 0, 0, 0, 0, 0, 0});

        Map<Integer, Integer> riderPoints = new HashMap<>();
        // init points to 0
        for (RiderResult result : stageResult) {
            riderPoints.put(result.getRiderId(), 0);
        }

        // for first 8 riders if any
        int totalRidersToGivePoints = Math.min(8, stageResult.size());

        for (int i = 0; i < segmentList.size(); i++) {
            Segment segment = segmentList.get(i);
            SegmentType segmentType = segment.getSegmentType();
            if (segmentType == SegmentType.SPRINT) {
                // ignore if an intermediate sprint.
                continue;
            }

            int[] pointsToGive = points.get(segmentType);

            // for segment i sort the stageResult first based on segmentTimes.get(i).toSecondsOfDay()
            sortBySegmentIndex(i);

            for (int j = 0; j < totalRidersToGivePoints; j++) {
                RiderResult riderResult = stageResult.get(j);
                int riderId = riderResult.getRiderId();
                riderPoints.put(riderId, riderPoints.get(riderId) + pointsToGive[j]);
            }
        }

        // now allocate points based on who finishes early
        sortResultsByFinishTime();

        int[] ans = new int[stageResult.size()];
        for (int i = 0; i < ans.length; i++) {
            ans[i] = riderPoints.get(stageResult.get(i).getRiderId());
        }

        return ans;
    }
}
