package ml.davvs.tourn.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import ml.davvs.tourn.model.ConfFileException;
import ml.davvs.tourn.model.SeasonPhase;
import ml.davvs.tourn.model.SeasonPhaseGuardException;
import ml.davvs.tourn.model.SeasonPhaseRequiredException;
import ml.davvs.tourn.model.persisted.Division;
import ml.davvs.tourn.model.persisted.GameRound;
import ml.davvs.tourn.model.persisted.QualifierGroup;
import ml.davvs.tourn.model.persisted.Season;
import ml.davvs.tourn.model.persisted.SeasonException;
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
		System.out.println("Starting command line main!");

		System.out.println("Parsing input");

		String fileSetupPath = "/Users/davvs/progg/tourn/seasonsetup.txt";
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
		Season lastSeason = t.getSeasons().get(t.getSeasons().size() - 1);
		ConsoleOut consoleOut = new ConsoleOut();
		//consoleOut.printSeasonDivisionsSetup(lastSeason);
		
		// consoleOut.printAllGamesRounds(lastSeason);
		
		String scheduleCSV = "/Users/davvs/progg/tourn/seasonschedule.txt";
		registerScheduleFromCSV(lastSeason, scheduleCSV);
//		Subdivision subdivision = s.getDivisions().get(0).getSubDivisions().get(0);
//		System.out.println("\n" + subdivision.getName());
//		consoleOut.printGamesForSubdivision(subdivision, "  ");
		
		/*
		dbManager.close();
		*/
		String gameResultCSV = "/Users/davvs/progg/tourn/gameresults.txt";
		registerGameResultsFromCSV (t, lastSeason, gameResultCSV);

		consoleOut.printSeasonDivisionsTable(lastSeason);
	}

	private static void registerScheduleFromCSV(Season season, String filePath) throws IOException, CSVParseException, ParseException {
		File file = new File(filePath);
		BufferedReader reader = null;

		int expectedGameRoundCount = season.getGameRounds();
	    reader = new BufferedReader(new FileReader(file));
	    String text = null;
	    DateFormat dateFormat = new SimpleDateFormat("YYYY-mm-dd");

	    ArrayList<HashMap<String, Object>> rounds = new ArrayList<HashMap<String, Object>>();
	    try {
		    while ((text = reader.readLine()) != null) {
		    	if (text.startsWith("#")){
		    		continue;
		    	}
				String parts[] = text.split(";");
				if (parts.length != 2) {
					throw new CSVParseException("Line " + text + " contains a bad number of columns");
				}
				HashMap<String, Object> part = new HashMap<String, Object>();
				part.put("gameStart", dateFormat.parse(parts[0]));
				part.put("gameInterval", Integer.parseInt(parts[1]));
				rounds.add(part);
		    }
		    if (expectedGameRoundCount != rounds.size()){
		    	throw new CSVParseException("Invalid amount of game rounds");
		    }
		    for (int gid = 0; gid < expectedGameRoundCount; gid ++){
		    	HashMap<String, Object> gameRoundData = rounds.get(gid);
		    	Date gameStart = (Date)gameRoundData.get("gameStart");
		    	Integer gameInterval = (Integer)gameRoundData.get("gameInterval");
		    	for (Division d : season.getDivisions()){
		    		for (Subdivision sd : d.getSubdivisions()){
		    			if (sd.getGameRounds().size() <= gid){
		    				continue;
		    			}
		    			GameRound gr  = sd.getGameRounds().get(gid);
		    			gr.setGameStart(gameStart);
		    			gr.setGameIntervalLengthDays(gameInterval);
		    		}
		    	}
		    }
	    } finally {
	    	reader.close();
	    }

	    
	}

	private static void registerGameResultsFromCSV(Tournament tournament, Season season,
			String filePath) throws NumberFormatException, IOException, SeasonException, ParseException, CSVParseException {
		File file = new File(filePath);
		Reader reader = null;
		reader = new FileReader(file);

	    season.registerGameResultsFromCSV(new FileReader(file));
	    reader.close();
		return;
	}

	private static void createSeasonFromCSV(Tournament t, String filePath) throws NumberFormatException, IOException, SeasonException{
		File file = new File(filePath);
		Reader reader = null;
		reader = new FileReader(file);
	    Season season = new Season(t);
	    t.getSeasons().add(season);

	    season.createFromCSV(new FileReader(file));
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
	    	if (text.startsWith("#")){
	    		continue;
	    	}
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
