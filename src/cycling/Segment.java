package cycling;

import java.io.Serializable;

public class Segment implements Serializable {
	/**
	 * creates the segment (ID) and is serializable 
	 */
	
	//variables which will be used
    public static int availableId = 1;

    private int id;
    private double location;
    private SegmentType segmentType;
    private double averageGradient;
    private double length;

    //creates the segment 
    public Segment(double location, SegmentType segmentType, double averageGradient, double length) {
        this(location, segmentType);
        this.averageGradient = averageGradient;
        this.length = length;
    }

    public Segment(double location, SegmentType segmentType) {
        this.id = availableId;
        availableId += 1;
        this.location = location;
        this.segmentType = segmentType;
    }

    public int getId() {
        return id;
    }

    public double getLocation() {
        return location;
    }

    public SegmentType getSegmentType() {
        return segmentType;
    }
}
