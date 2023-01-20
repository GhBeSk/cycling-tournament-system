import cycling.*;

import java.time.LocalDateTime;
import java.time.Month;

/**
 * A short program to illustrate an app testing some minimal functionality of a
 * concrete implementation of the CyclingPortalInterface interface -- note you
 * will want to increase these checks, and run it on your CyclingPortal class
 * (not the BadCyclingPortal class).
 *
 * 
 * @author Diogo Pacheco
 * @version 1.0
 */
public class CyclingPortalInterfaceTestApp {

	/**
	 * Test method.
	 * 
	 * @param args not used
	 */
	public static void main(String[] args) {
		System.out.println("The system compiled and started the execution...");

		//MiniCyclingPortalInterface portal = new BadMiniCyclingPortal();
		CyclingPortalInterface portal = new BadCyclingPortal();

		assert (portal.getRaceIds().length == 0)
				: "Innitial SocialMediaPlatform not empty as required or not returning an empty array.";
		
		
		/**
		 * throw exception for createRace method
		 */
		try {
			portal.createRace("race1", "the first race to be written");
			portal.createRace("race2", "second race");
			portal.createRace("race3", "third race");
		} catch (IllegalNameException | InvalidNameException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		/**
		 * throw exception for viewRaceDetails method
		 */
		try {
			portal.viewRaceDetails(2000);
			//race.removeRaceById(2001);
		} catch (IDNotRecognisedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		/**
		 * throw exception for addStageToRace method
		 */
		try {
			portal.addStageToRace(2000, "stage1", "first stage", 20, LocalDateTime.of(2022, Month.JANUARY, 2, 13, 30, 00), StageType.FLAT);
			portal.addStageToRace(2001, "stage number 1", "yeah", 150, LocalDateTime.of(2022, Month.JANUARY, 3, 10, 00, 30), StageType.FLAT);
			portal.addStageToRace(2000, "stage 2", "second stage for race 2000", 30, LocalDateTime.of(2022, Month.JANUARY, 4, 14, 15, 30), StageType.MEDIUM_MOUNTAIN);
			portal.addStageToRace(2000, "stage 3", "third stage for race 2000", 200, LocalDateTime.of(2022, Month.JANUARY, 5, 14, 15, 30), StageType.TT);
			portal.addStageToRace(2000, "stage 4", "third stage for race 2000", 200, LocalDateTime.of(2022, Month.JANUARY, 5, 14, 15, 30), StageType.TT);
			portal.addStageToRace(2000, "stage 5", "third stage for race 2000", 200, LocalDateTime.of(2022, Month.JANUARY, 5, 14, 15, 30), StageType.TT);

			portal.addStageToRace(2002, "stage 1", "race3 stage 1", 20, LocalDateTime.of(2022, Month.JANUARY, 6, 13, 40, 55), StageType.FLAT);
			portal.addStageToRace(2000, "stage 6", "this is the fourth stage", 100, LocalDateTime.of(2022, Month.JANUARY, 6, 17, 00, 30), StageType.HIGH_MOUNTAIN);
		} catch (IDNotRecognisedException | IllegalNameException | InvalidNameException | InvalidLengthException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		/**
		 * throw exception for getNumberOfStages method
		 */
		try {
			portal.getNumberOfStages(2001);
		} catch (IDNotRecognisedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		/**
		 * throw exception for getRaceStages method
		 */
		try {
			portal.getRaceStages(2000);
		} catch (IDNotRecognisedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		/**
		 * throw exception for getStageLength method
		 */
		try {
			portal.getStageLength(1001);
		} catch (IDNotRecognisedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		/**
		 * throw exception for addCategorisedClimbToStage method
		 */
		try {
			portal.addCategorizedClimbToStage(1001, 20.0, SegmentType.C4, 4.0, 3.5);
			portal.addCategorizedClimbToStage(1002, 10.0, SegmentType.C3, 8.5, 5.0);
		} catch (IDNotRecognisedException | InvalidLocationException | InvalidStageStateException
				| InvalidStageTypeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		/**
		 * throw exception for addIntermediateSprintToStage method
		 */
		try {
			portal.addIntermediateSprintToStage(1001, 100);
		} catch (IDNotRecognisedException | InvalidLocationException | InvalidStageStateException
				| InvalidStageTypeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		/**
		 * throw exception for remvoeSegment method
		 */
		try {
			portal.removeSegment(3000);
		} catch (IDNotRecognisedException | InvalidStageStateException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		
		
		/**
		 * throw exception for getStageSegments method
		 */
		try {
			portal.getStageSegments(1001);
		} catch (IDNotRecognisedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		/**
		 * throw exception for removeStageById method
		 */
		try {
			portal.removeStageById(1002);
		} catch (IDNotRecognisedException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		
		
		/**
		 * throw exception for removeRaceById method
		 */
		try {
			portal.removeRaceById(2000);
		} catch (IDNotRecognisedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		/** 
		 * throw exception for removeRaceByName method
		 */
		try {
			portal.removeRaceByName("race3");
			//portal.removeRaceByName("race1");
		} catch (NameNotRecognisedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
