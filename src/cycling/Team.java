package cycling;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class Team implements Serializable {
	/**
	 * this class is used for the creation of teams
	 */
	
	//variables to be used in this class 
    public static int availableId = 1;

    private int id;
    private String name;
    private String description;

    private List<Rider> riderList;

    //creates a team ID 
    public Team(String name, String description) {
        this.id = availableId;
        availableId += 1;

        this.name = name;
        this.description = description;
        riderList = new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<Rider> getRiderList() {
        return riderList;
    }

    public void add(Rider rider) {
        riderList.add(rider);
    }

    //removes a rider from the team 
    public void remove(Rider rider) {
        riderList.remove(rider);
    }
}
