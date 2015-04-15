package ml.davvs.tourn.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import ml.davvs.tourn.model.SeasonPhase;
import ml.davvs.tourn.model.SeasonPhaseGuardException;
import ml.davvs.tourn.model.SeasonPhaseRequiredException;
import ml.davvs.tourn.model.persisted.Division;
import ml.davvs.tourn.model.persisted.Season;
import ml.davvs.tourn.model.persisted.Subdivision;
import ml.davvs.tourn.model.persisted.Team;
import ml.davvs.tourn.model.persisted.TeamSeasonStats;
import ml.davvs.tourn.model.persisted.Tournament;
import ml.davvs.tourn.view.ConsoleOut;

public class CommandLineMain {

	public static void main(String[] args) throws SeasonPhaseRequiredException, SeasonPhaseGuardException, NumberFormatException, IOException {
/*
		ConsoleOut consoleOut = new ConsoleOut();
		DatabaseManager dbManager = new DatabaseManager();
		dbManager.connect("localhost");
	*/	
		Tournament t = new Tournament();

		System.out.println("Starting command line main!");

		System.out.println("Parsing input");

		String filePath = "/Users/davvs/progg/tourn/userfile.txt";
		ArrayList<TeamSeasonStats> teams = readTeammsFromCSV(t, filePath);
		
		// TODO Auto-generated method stub
		System.out.println("Initializing tournament!");
		//t = createTournament(createTestTeams());
		initializeTournament(t, teams);
		
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

	private static ArrayList<TeamSeasonStats> readTeammsFromCSV(Tournament tournament, String filePath) throws NumberFormatException, IOException {
		File file = new File(filePath);
		BufferedReader reader = null;

		ArrayList<TeamSeasonStats> teams = new ArrayList<TeamSeasonStats>(); //createTestTeams(tournament);
		
	    reader = new BufferedReader(new FileReader(file));
	    String text = null;

	    while ((text = reader.readLine()) != null) {
			String parts[] = text.split(";");
			
			String name, email;
			float guessedSkill;
			name = parts[0];
			email = parts[1];
			guessedSkill =  Float.parseFloat(parts[2]);
			Team team = tournament.lookupTeam(name, email, guessedSkill);
			TeamSeasonStats ts = team.getCurrentSeason();
			teams.add(ts);

	    }
	    reader.close();
		
		return teams;
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
	
	public static void initializeTournament(Tournament tournament, ArrayList<TeamSeasonStats> teams) throws SeasonPhaseRequiredException, SeasonPhaseGuardException {
		int subDivisionSiblingMax = 2;
		int targetPlayersPerDivision = 6;
		tournament.setName("Testo tournamento League");
		ArrayList<Season> seasons = new ArrayList<Season>();

		Season season1 = new Season();
		seasons.add(season1);
		tournament.setSeasons(seasons);

		season1.setCup(null);
		season1.setTournament(tournament);

		season1.sortTeams(teams);
		season1.setTeams(teams);
		season1.setCurrentPhase(SeasonPhase.QUALIFIERSPREP);
		season1.createDivisions(teams.size(), subDivisionSiblingMax, targetPlayersPerDivision);
		season1.distributeTeams();
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
		
	}
}
