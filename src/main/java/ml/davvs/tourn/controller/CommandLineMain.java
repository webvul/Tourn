package ml.davvs.tourn.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import ml.davvs.tourn.model.SeasonPhase;
import ml.davvs.tourn.model.SeasonPhaseGuardException;
import ml.davvs.tourn.model.SeasonPhaseRequiredException;
import ml.davvs.tourn.model.persisted.ConfFileException;
import ml.davvs.tourn.model.persisted.Division;
import ml.davvs.tourn.model.persisted.QualifierGroup;
import ml.davvs.tourn.model.persisted.Season;
import ml.davvs.tourn.model.persisted.Subdivision;
import ml.davvs.tourn.model.persisted.Team;
import ml.davvs.tourn.model.persisted.TeamSeasonStats;
import ml.davvs.tourn.model.persisted.Tournament;
import ml.davvs.tourn.view.ConsoleOut;

public class CommandLineMain {

	public static void main(String[] args) throws Exception {
/*
		ConsoleOut consoleOut = new ConsoleOut();
		DatabaseManager dbManager = new DatabaseManager();
		dbManager.connect("localhost");
	*/	
		Tournament t = new Tournament();
		t.setName("Testo tournamento League");
		t.setSeasons(new ArrayList<Season>());
		System.out.println("Starting command line main!");

		System.out.println("Parsing input");

		String fileSetupPath = "/Users/davvs/progg/tourn/tournamentsetup.txt";
		createSeasonFromCSV(t, fileSetupPath);

		Season season1 = t.getSeasons().get(0);
		season1.setCurrentPhase(SeasonPhase.QUALIFIERSPREP);

		String filePath = "/Users/davvs/progg/tourn/userfile.txt";
		readTeamsFromCSV(t, season1, filePath);
		
		// TODO Auto-generated method stub
		System.out.println("Initializing tournament!");
		//t = createTournament(createTestTeams());
		/*
		ArrayList<Season> seasons = new ArrayList<Season>();
		Season season1 = new Season();
		seasons.add(season1);
		t.setSeasons(seasons);
		*/
		
		// To autogenerate leagues
		// season1.sortTeams(teams);
		// season1.setTeams(teams);
		// season1.createDivisions(teams.size(), 2, 6);
		// season1.distributeTeams();
		season1.setCurrentPhase(SeasonPhase.QUALIFIERS);
		season1.setCurrentPhase(SeasonPhase.SEASONPREP);
		
//		//May add another player
//		Division division = season1.getDivisions().get(1);
//		Subdivision sub = division.getSubDivisions().get(0);
//		season1.addTeam("Late Loomboy", "latie@iamalwayslate.com", 3.5f, sub);
//		
//		season1.generateGames();
//		season1.setCurrentPhase(SeasonPhase.SEASON);
//		//May add another if games have not been not played yet
//		season1.addTeam("SuperLate Loomboy2", "sprlat@iamalwayslate.com", 2.5f, season1.getDivisions().get(2).getSubDivisions().get(0));

		season1.generateGames();
		
		System.out.println("Tournament created!");
		Season s = t.getSeasons().get(t.getSeasons().size() - 1);
		ConsoleOut consoleOut = new ConsoleOut();
		consoleOut.printSeasonDivisions(s);
		
		consoleOut.printAllGamesRounds(s);
//		Subdivision subdivision = s.getDivisions().get(0).getSubDivisions().get(0);
//		System.out.println("\n" + subdivision.getName());
//		consoleOut.printGamesForSubdivision(subdivision, "  ");
		
		/*
		dbManager.close();
		*/
	}

	private static void createSeasonFromCSV(Tournament t, String filePath) throws NumberFormatException, IOException{
		File file = new File(filePath);
		BufferedReader reader = null;

	    reader = new BufferedReader(new FileReader(file));
	    String text = null;
	    Season season = new Season(t);
	    ArrayList<Division> divisions = new ArrayList<Division>();
	    season.setDivisions(divisions);
	    int totalSubDivisions = 0;
	    t.getSeasons().add(season);
	    while ((text = reader.readLine()) != null) {
			String parts[] = text.split(";");
			Division division = new Division(season);
			divisions.add(division);
			ArrayList<Subdivision> subdivisions = new ArrayList<Subdivision>(parts.length);
			int level = Integer.parseInt(parts[0]);
			String divisionName = parts[1];
			division.setName(divisionName);
			division.setLevel(level);
			String subdivisionNames[] = new String[parts.length - 2];
			for (int i = 0; i < parts.length - 2; i++) {
				Subdivision subdivision = new Subdivision(division);
				subdivision.setName(parts[i+2]);
				subdivisions.add(subdivision);
				subdivisionNames[i] = parts[i+1];
			}
			totalSubDivisions += subdivisionNames.length;
			division.setSubdivisions(subdivisions);
	    }
	    for (int d = 0; d < divisions.size() - 1; d++){
	    	Division upperDivision = divisions.get(d);
	    	Division lowerDivision = divisions.get(d+1);
	    	QualifierGroup qualifierGroup = new QualifierGroup();
	    	qualifierGroup.setName("Qualfiers to " + upperDivision.getName());
	    	qualifierGroup.setUpperDivision(upperDivision);
	    	qualifierGroup.setLowerDivision(lowerDivision);
	    	upperDivision.setLowerQualifierGroup(qualifierGroup);
	    	lowerDivision.setUpperQualifierGroup(qualifierGroup);
	    }
	    season.setSubdivisionCount(totalSubDivisions);
	    reader.close();
		return;
	}

	private static void readTeamsFromCSV(Tournament tournament, Season season, String filePath) throws NumberFormatException, IOException, ConfFileException {
		File file = new File(filePath);
		BufferedReader reader = null;

		ArrayList<TeamSeasonStats> teams =  season.getTeams();
		
	    reader = new BufferedReader(new FileReader(file));
	    String text = null;

	    while ((text = reader.readLine()) != null) {
			String parts[] = text.split(";");
			
			String name, email;
			int targetDiv, targetSubdiv = 0;
			float guessedSkill;
			String target;
			name = parts[0];
			email = parts[1];
			target = parts[2];
			String[] targetParts = target.split("\\.");
			targetDiv = Integer.parseInt(targetParts[0]);
			if (targetParts.length > 1){
				targetSubdiv = Integer.parseInt(targetParts[1]);
			}
			Team team = tournament.lookupTeam(name, email, 0.0f);
			TeamSeasonStats ts = team.getCurrentSeason();
			teams.add(ts);
			season.addTeam(ts, targetDiv, targetSubdiv);

	    }
	    reader.close();
	}

	private static ArrayList<TeamSeasonStats> createTestTeams(Tournament tournament) {
		ArrayList<TeamSeasonStats> teams = new ArrayList<TeamSeasonStats>();
		int numberOfTeams = 47;
		for (int a = 1; a <= numberOfTeams; a++) {
			Team team = tournament.lookupTeam("team" + a, "team" + a + "@davvs.ml", (float)Math.round(Math.random()*100)/10); 
			TeamSeasonStats ts = team.getCurrentSeason();
			teams.add(ts);
		}
        return teams;
	}
	
}
