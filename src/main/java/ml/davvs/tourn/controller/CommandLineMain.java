package ml.davvs.tourn.controller;

import java.util.ArrayList;

import ml.davvs.tourn.model.DatabaseManager;
import ml.davvs.tourn.model.Division;
import ml.davvs.tourn.model.Season;
import ml.davvs.tourn.model.SeasonPhaseException;
import ml.davvs.tourn.model.Subdivision;
import ml.davvs.tourn.model.Team;
import ml.davvs.tourn.model.TeamSeasonStats;
import ml.davvs.tourn.model.Tournament;
import ml.davvs.tourn.view.ConsoleOut;

import com.datastax.driver.core.schemabuilder.CreateType;

public class CommandLineMain {

	public static void main(String[] args) throws SeasonPhaseException {

		ConsoleOut consoleOut = new ConsoleOut();
		DatabaseManager dbManager = new DatabaseManager();
		dbManager.connect("localhost");
		
		// TODO Auto-generated method stub
		System.out.println("Starting command line main!");
		Tournament t;
		System.out.println("Creating tournament!");
		t = createTournament();
		System.out.println("Tournament created!");
		Season s = t.getSeasons().get(t.getSeasons().size() - 1);
		consoleOut.printSeasonDivisions(s);
		Subdivision subdivision = s.getDivisions().get(0).getSubDivisions().get(0);
		System.out.println("\n" + subdivision.getName());
		consoleOut.printGamesForSubdivision(subdivision, "  ");
		dbManager.close();
	}

	private static ArrayList<Team> createTestTeams() {
		ArrayList<Team> teams = new ArrayList<Team>();
		int numberOfTeams = 2;
		for (int a = 1; a <= numberOfTeams; a++) {
			teams.add(new Team("team" + a, "team" + a + "@davvs.ml", (float)Math.round(Math.random()*100)/10));
		}
        return teams;
	}
	
	public static Tournament createTournament() throws SeasonPhaseException {
		int subDivisionSiblingMax = 3;
		int targetPlayersPerDivision = 6;
		Tournament tnm = new Tournament();
		tnm.setName("Testo tournamento League");
		ArrayList<Season> seasons = new ArrayList<Season>();
		ArrayList<Team> teams = createTestTeams();
		Season season1 = new Season();
		season1.setCup(null);
		season1.setTournament(tnm);

		season1.sortTeams(teams);
		season1.setTeams(teams);
		season1.createDivisions(teams.size(), subDivisionSiblingMax, targetPlayersPerDivision);
		season1.distributeTeams(teams);
		season1.generateGames();

		
		seasons.add(season1);
		tnm.setSeasons(seasons);
		tnm.setTeams(teams);
		return tnm;
	}
}
