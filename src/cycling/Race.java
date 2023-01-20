package cycling;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Race implements Serializable {
	/**
	 * this class is used to create races, uses getters and setters and is serializable 
	 */
    public static int availableId = 1;

    private int id;
    private String name;
    private String description;

    private List<Stage> stageList;

    public Race(String name, String description) {
        this.id = availableId;
        availableId += 1;

        this.name = name;
        this.description = description;
        stageList = new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Stage> getStageList() {
        return stageList;
    }

    @Override
    public String toString() {
        /*Any formatted string containing the race ID, name, description, the
                number of stages, and the total length (i.e., the sum of all stages'
                length).*/

        double totalLength = 0;
        for (Stage stage : stageList) {
            totalLength += stage.getLength();
        }

        return "Race{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", number of stages=" + stageList.size() +
                ", total length=" + totalLength +
                '}';
    }

    public int[] getStageIds() {
        int[] ids = new int[stageList.size()];
        for (int i = 0; i < stageList.size(); i++) {
            ids[i] = stageList.get(i).getId();
        }
        return ids;
    }

    public Stage getStage(int stageId) {
        for (Stage stage : stageList) {
            if (stage.getId() == stageId) {
                return stage;
            }
        }
        return null;
    }

    public Stage getStage(String stageName) {
        for (Stage stage : stageList) {
            if (stage.getStageName().equals(stageName)) {
                return stage;
            }
        }
        return null;
    }

    public void add(Stage stage) {
        stageList.add(stage);
    }

    public void remove(Stage stage) {
        stageList.remove(stage);
    }
}
