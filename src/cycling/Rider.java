package cycling;

import java.io.Serializable;

public class Rider implements Serializable {
	/**
	 * this class is used for the creation of a rider ID and is serializable 
	 */
    public static int availableId = 1;

    private int id;
    private String name;
    private int yearOfBirth;

    public Rider(String name, int yearOfBirth) {
        this.id = availableId;
        availableId += 1;

        this.name = name;
        this.yearOfBirth = yearOfBirth;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
