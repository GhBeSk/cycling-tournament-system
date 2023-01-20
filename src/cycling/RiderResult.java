package cycling;

import java.io.Serializable;
import java.time.Duration;
import java.time.LocalTime;

public class RiderResult implements Serializable {
	/**
	 * this class deals with the riders results and calculates the elapsed time for segments
	 */
	
	//variables which will be used
    private int stageId;
    private int riderId;
    private LocalTime[] segmentTimes;
    private LocalTime startTime, finishTime;
    private int elapsedSeconds;

    public RiderResult(int stageId, int riderId, LocalTime[] localTimes) {
        this.stageId = stageId;
        this.riderId = riderId;

        
        this.segmentTimes = new LocalTime[localTimes.length - 1];
        for (int i = 1; i < localTimes.length - 1; i++) {
            segmentTimes[i - 1] = localTimes[i];
        }

        this.startTime = localTimes[0];
        this.finishTime = localTimes[localTimes.length - 1];
        this.elapsedSeconds = (int) Math.abs(Duration.between(startTime, finishTime).toSeconds());

        int hour = elapsedSeconds / 3600;
        int minute = (elapsedSeconds - hour * 3600) / 60;
        int second = elapsedSeconds - hour * 3600 - minute * 60;
        segmentTimes[segmentTimes.length - 1] = LocalTime.of(hour, minute, second);
    }

    public int getStageId() {
        return stageId;
    }

    public int getRiderId() {
        return riderId;
    }

    public LocalTime[] getSegmentTimes() {
        return segmentTimes;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public LocalTime getFinishTime() {
        return finishTime;
    }

    public int getElapsedSeconds() {
        return elapsedSeconds;
    }
}
