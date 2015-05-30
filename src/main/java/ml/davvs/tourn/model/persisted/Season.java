package ml.davvs.tourn.model.persisted;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

import junit.framework.Assert;
import ml.davvs.tourn.controller.CSVParseException;
import ml.davvs.tourn.model.ConfFileException;
import ml.davvs.tourn.model.SeasonPhase;
import ml.davvs.tourn.model.SeasonPhaseGuard;
import ml.davvs.tourn.model.SeasonPhaseGuardException;
import ml.davvs.tourn.model.SeasonPhaseRequiredException;
import ml.davvs.tourn.model.persisted.Game.PlayTypes;

public class Season {

	private SeasonPhase currentPhase;
	private ArrayList<Division> divisions;
	private ArrayList<TeamSeasonStats> teams;
	private int playerCount;
	private int subdivisionCount;
	private Cup cup;
	private ArrayList<Game> placementMatches;
	private Tournament tournament;
	private SeasonPhaseGuard phaseGuard;
	private int gameRounds;
	private boolean distribtionDone;
	private UUID id;
	private int seasonSequence;
	
	public String getName() {
		return "Season " + seasonSequence;
	}
	public int getGameRounds() {
		return gameRounds;
	}
	public void setGameRounds(int gameRounds) {
		this.gameRounds = gameRounds;
	}
	public SeasonPhase getCurrentPhase() {
		return currentPhase;
	}
	public void setCurrentPhase(SeasonPhase currentPhase) throws SeasonPhaseGuardException {
		phaseGuard.assertSwitchPhaseIsOK(currentPhase);
		this.currentPhase = currentPhase;
	}
	public Tournament getTournament() {
		return tournament;
	}
	public int getSubdivisionCount() {
		return subdivisionCount;
	}
	public void setSubdivisionCount(int subdivisionCount) {
		this.subdivisionCount = subdivisionCount;
	}
	public int getPlayerCount() {
		return playerCount;
	}
	public void setPlayerCount(int playerCount) {
		this.playerCount = playerCount;
	}
	public ArrayList<Division> getDivisions() {
		return divisions;
	}
	public void setDivisions(ArrayList<Division> divisions) {
		this.divisions = divisions;
	}
	public ArrayList<TeamSeasonStats> getTeams() {
		return teams;
	}
	public void setTeams(ArrayList<TeamSeasonStats> teams) throws SeasonPhaseRequiredException {
		SeasonPhase.assertPhase(currentPhase, SeasonPhase.PREPARATION);
		this.teams = teams;
	}
	public Cup getCup() {
		return cup;
	}
	public void setCup(Cup cup) {
		this.cup = cup;
	}
	public ArrayList<Game> getPlacementMatches() {
		return placementMatches;
	}
	public void setPlacementMatches(ArrayList<Game> placementMatches) {
		this.placementMatches = placementMatches;
	}
	
	public Season(Tournament t) {
		setId(UUID.randomUUID());
		currentPhase = SeasonPhase.PREPARATION;
		divisions = new ArrayList<Division>();
		phaseGuard = new SeasonPhaseGuard(this);
		teams = new ArrayList<TeamSeasonStats>();
		gameRounds = 0;
		distribtionDone = false;
		tournament = t;
		seasonSequence = 1;
	}

	public void addTeam(String name, String email, float guessedSkill, Subdivision subdivision) throws SeasonPhaseRequiredException {
		if (!(currentPhase == SeasonPhase.SEASON || currentPhase == SeasonPhase.SEASONPREP)){
			throw new SeasonPhaseRequiredException(currentPhase, SeasonPhase.SEASONPREP);
		}
		//Add a team to a subdivision late
		Team t = new Team(name, email, guessedSkill); //TODO make a lookup if this team already exists
		t.getCurrentSeason().setSeason(this);
		t.getCurrentSeason().setSubDivision(subdivision);
		subdivision.getTeamSeasonStats().add(t.getCurrentSeason());
		teams.add(t.getCurrentSeason());
		t.getCurrentSeason().setSubDivision(subdivision);
		playerCount ++;
		distribtionDone = false; //TODO Assert that this is true when passing on to the next phase
	}
	
	public void sortTeams (ArrayList<TeamSeasonStats> teams) throws SeasonPhaseRequiredException {
		SeasonPhase.assertPhase(currentPhase, SeasonPhase.PREPARATION);

		Collections.sort(teams, new Comparator<TeamSeasonStats>() {
	        public int compare(TeamSeasonStats t1, TeamSeasonStats t2)
	        {
	        	int eloDiff = t1.getElo() - t2.getElo();
	        	if (eloDiff != 0) {
	        		return eloDiff;
	        	}
	        	float t1s = t1.getGuessedSkill();
	        	float t2s = t2.getGuessedSkill();
	    		return t1s > t2s ? -1 : (t1s < t2s ? 1 : 0);
	        }
	    });
	}

	public void generateGames() throws SeasonPhaseRequiredException{
		if (currentPhase != SeasonPhase.SEASONPREP && currentPhase != SeasonPhase.SEASON) {
			throw new SeasonPhaseRequiredException("invalid current phase. Should be " + SeasonPhase.SEASON + " or " + SeasonPhase.SEASONPREP);
		}

		for (Division division : divisions) {
			for (Subdivision subdivision : division.getSubdivisions()){
				subdivision.generateGames();
			}
		}
	}

	public void distributeTeams() throws SeasonPhaseRequiredException{
		SeasonPhase.assertPhase(currentPhase, SeasonPhase.QUALIFIERSPREP);

		int restPlayersTotal = teams.size() % subdivisionCount;
		int restPlayersToDistribute = restPlayersTotal;
		int nextTeamId = 0;
		for (int d = 0; d < divisions.size(); d++){
			Division division = divisions.get(d);
			float playersPerSubdivision = (teams.size() / subdivisionCount);
			int playersInDivision = (int)((float)division.getSubdivisions().size() * playersPerSubdivision);
			if (restPlayersToDistribute > division.getSubdivisions().size()) {
				playersInDivision += division.getSubdivisions().size();
				restPlayersToDistribute -= division.getSubdivisions().size();
			} else if (restPlayersToDistribute > 0) {
				playersInDivision += restPlayersToDistribute;
				restPlayersToDistribute -= restPlayersToDistribute;
			}

			int s = 0;
			
			while (playersInDivision > 0){
				int subdivisionId = s % division.getSubdivisions().size();
				Subdivision subdivision = division.getSubdivisions().get(subdivisionId);
				TeamSeasonStats teamSeasonStats = teams.get(nextTeamId);
				Team team = teamSeasonStats.getTeam();
				assert(teamSeasonStats.getSubDivision() == null);
				teamSeasonStats.setCup(null);
				teamSeasonStats.setSubDivision(subdivision);
				//teamSeasonStats.setElo(team.getLastElo());
				teamSeasonStats.setSeason(this);
				teamSeasonStats.setSubDivision(subdivision);
				teamSeasonStats.setTeam(team);
				team.getSeasonStats().add(teamSeasonStats); //TODO Fix this
				subdivision.getTeamSeasonStats().add(teamSeasonStats);
				nextTeamId ++;
				playersInDivision --;
				s++;
			}
		}
		assert(nextTeamId == teams.size() && restPlayersToDistribute == 0);
		distribtionDone = true;
	}

	/**
	 * 
	 * @param teams
	 * @param subdivisionSiblingsMax
	 * @param targetPlayersPerDivision. if there's multiple subdivisions, the divisions will contain
	 * 		targetPlayersPerDivision or targetPlayersPerDivision-1 amount of players. If there are very few divisions, it may contain more 
	 * @throws SeasonPhaseRequiredException
	 * @throws SeasonPhaseGuardException 
	 */
	public void createDivisions(int teams, int subdivisionSiblingsMax, int targetPlayersPerDivision) throws SeasonPhaseRequiredException, SeasonPhaseGuardException {
		SeasonPhase.assertPhase(currentPhase, SeasonPhase.QUALIFIERSPREP);
		if (divisions != null){
			throw new SeasonPhaseGuardException("Divisions are already set");
		}

		subdivisionCount = (int) Math.ceil((float)teams / targetPlayersPerDivision);
	
		if (subdivisionCount <= 0) {
			subdivisionCount = 1;
		}

		int midSubdivisions = subdivisionCount == 1 ? 0 : subdivisionCount - 2;
		int midDivisions;
        int subdivisionRestRemaining = 0;
        int midSubdivisionsBase;
		if (midSubdivisions == 0){
        	midDivisions = 0;
        	subdivisionRestRemaining = 0;
        	midSubdivisionsBase = 0;
		} else {
			
			midDivisions = 1 + (midSubdivisions-1) / subdivisionSiblingsMax;
			int subdivisionsPerDivision = midSubdivisions / midDivisions;
			subdivisionRestRemaining = midSubdivisions - (subdivisionsPerDivision * midDivisions); // The number of middivisions to have an extra sibling
    		midSubdivisionsBase = subdivisionsPerDivision;
		}
        int divisionCount = subdivisionCount == 1 ? 1 : midDivisions + 2;
        divisions = new ArrayList<Division>();
        QualifierGroup upperQualifierGroup = null;
		for (int d = 0; d < divisionCount; d++){
			Division division = new Division(this);
			QualifierGroup qualifierGroup = new QualifierGroup();
			if (d + 1 < divisionCount) {
				qualifierGroup.setUpperDivision(division);
				division.setLowerQualifierGroup(qualifierGroup);
			}
			if (upperQualifierGroup != null){
				upperQualifierGroup.setLowerDivision(division);
				division.setUpperQualifierGroup(upperQualifierGroup);
			}
			int currentDivisionLevel = divisionCount - d;
			division.setLevel(currentDivisionLevel);
			if (d==0){
				division.setName(tournament != null ? tournament.getName() : "");
			} else {
				division.setName("Division " + d);
			}
			qualifierGroup.setName("Promotion to " + division.getName() + " Qualifier group");
			division.setSubdivisions(new ArrayList<Subdivision>());
			int currentSubdivisions = 0;
			if (d == 0 || d == divisionCount - 1) {
				currentSubdivisions = 1;
			} else {
				currentSubdivisions = midSubdivisionsBase;
				if (subdivisionRestRemaining > 0){
					currentSubdivisions ++;
					subdivisionRestRemaining --;
				}
			}
			for (int s = 0; s < currentSubdivisions; s++){
				Subdivision subdivision = new Subdivision(division);
				subdivision.setSiblings(currentSubdivisions);
				subdivision.setTeamSeasonStats(new ArrayList<TeamSeasonStats>());
				subdivision.setGameRounds(new ArrayList<GameRound>());
				subdivision.setSiblingNumber(s);

				if (s > 25){
					throw new RuntimeException("Unable to make up subdivision sibling letter");
				}
				subdivision.setName(division.getName() + (currentSubdivisions > 1 ? (char)(65 + s) : ""));
				division.getSubdivisions().add(subdivision);
			}

			divisions.add(division);
			upperQualifierGroup = qualifierGroup;
		}
    }
	public void addTeam(TeamSeasonStats teamSeasonStats, int targetDiv, int targetSubdiv) throws ConfFileException {
		for (int d = 0; d < divisions.size(); d++ ){
			Division division = divisions.get(d);
			if (division.getLevel() != targetDiv) {
				continue;
			}
			if (targetSubdiv > division.getSubdivisions().size()){
				throw new ConfFileException("Invalid subdivision " + targetSubdiv + " Amount of subdivisions are:" + division.getSubdivisions().size());
			}
			Subdivision subdivision;
			if (targetSubdiv == 0){
				subdivision = division.getSubdivisionWithTheLeastPlayers();
			} else {
				subdivision =  division.getSubdivisions().get(targetSubdiv - 1);
			}
			teams.add(teamSeasonStats);
			subdivision.getTeamSeasonStats().add(teamSeasonStats);
			teamSeasonStats.setSubDivision(subdivision);
			return;
		}
		throw new ConfFileException("Unable to find division with level " + targetDiv);
	}
	public UUID getId() {
		return id;
	}
	public void setId(UUID id) {
		this.id = id;
	}
	public int getSeasonSequence() {
		return seasonSequence;
	}
	public void setSeasonSequence(int seasonSequence) {
		this.seasonSequence = seasonSequence;
	}
	
	
	public void createFromCSV(Reader unbufreader) throws NumberFormatException, IOException, SeasonException {
		if (divisions != null && divisions.size() > 0){
			System.out.println("WARNING! Overwriting divisions!");
		}
		BufferedReader reader = new BufferedReader(unbufreader); 
	    String text = null;
	    ArrayList<Division> divisions = new ArrayList<Division>();
	    setDivisions(divisions);
	    int totalSubDivisions = 0;
	    while ((text = reader.readLine()) != null) {
	    	if (text.startsWith("#")){
	    		continue;
	    	}
			String parts[] = text.split(";");
			Division division = new Division(this);
			divisions.add(division);
			ArrayList<Subdivision> subdivisions = new ArrayList<Subdivision>(parts.length);
			int level = Integer.parseInt(parts[0]);
			int timesToPlayEachOpponent = Integer.parseInt(parts[1]);
			String divisionName = parts[2];
			division.setName(divisionName);
			division.setLevel(level);
			String subdivisionNames[] = new String[parts.length - 2];
			for (int i = 1; i < parts.length - 2; i++) {
				Subdivision subdivision = new Subdivision(division);
				subdivision.setName(parts[i+2]);
				subdivisions.add(subdivision);
				subdivision.setTimesToPlayEachOpponent(timesToPlayEachOpponent);
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
	    this.setSubdivisionCount(totalSubDivisions);
	    reader.close();
	}
	public void addTeamsFromCSV(Reader unbufreader) throws IOException, SeasonException {
		BufferedReader reader = new BufferedReader(unbufreader); 
	    String text = null;
	    while ((text = reader.readLine()) != null) {
	    	if (text.startsWith("#")){
	    		continue;
	    	}
			String parts[] = text.split(";");
			if (parts.length != 3) {
				throw new SeasonException("All CSV Parts should be 3 fields. Bad line:" + text);
			}
			String name = parts[0];
			String email = parts[1];
			String divisionSkill = parts[2];
			//TODO Lookup team from email maybe
			throw new SeasonException("Division 2.4 does not exist! Maybe, this is a placeholder");
	    }
	}
	
	public void registerGameResultsFromCSV(Reader fileReader) throws IOException, SeasonException, ParseException, CSVParseException {

		BufferedReader reader = new BufferedReader(fileReader); 
	    String text = null;
	    DateFormat dateFormat = new SimpleDateFormat("YYYY-MM-dd");
	    ArrayList<HashMap<String,Object>> resultCSV = new ArrayList<HashMap<String,Object>>();
	    Integer currRoundId = null;
	    while ((text = reader.readLine()) != null) {
	    	try {
		    	if (text.startsWith("#") || text.equals("")){
		    		continue;
		    	}
				String parts[] = text.split("\t");
	
				if (parts[0].equalsIgnoreCase("Round")){
					currRoundId = Integer.parseInt(parts[1]) - 1;
					continue;
				}
				String subdivisionName = parts[0];
				String homePlayerName = parts[1];
				String awayPlayerName = parts[2];
	
				PlayTypes playedType;
				if (parts.length <= 3 || (parts[3].equals("") && parts[4].equals("") && parts[5].equals(""))) {
					playedType = PlayTypes.NOT_PLAYED;
					continue;
	    		} else if (parts[3].equalsIgnoreCase("W/O") || parts[4].equalsIgnoreCase("W/O") || parts[5].equalsIgnoreCase("W/O")) {
					playedType = PlayTypes.WALKED_OVER;
				} else if (!parts[3].equals("") && !parts[4].equalsIgnoreCase("")) {
					playedType = PlayTypes.PLAYED;
				}else {
					throw new CSVParseException("Unable to determine result of game!" + text);
				}
				Subdivision subdivision = lookupSubdivisionByName(subdivisionName);
				if (subdivision == null) {
					throw new CSVParseException("Unable to find subdivision specified in CSV. " + subdivisionName);
				}
				Game game = lookupGameByTeams(subdivision, currRoundId, homePlayerName, awayPlayerName);
			    if (game == null) {
			    	throw new CSVParseException("Unable to find game in round " + currRoundId + " for players " + homePlayerName + "," + awayPlayerName + ": " + text);
			    }
				HashMap<String,Object> csvLine = new HashMap<String,Object>();
				csvLine.put("roundId", currRoundId);
				csvLine.put("subdivision", subdivision);
				csvLine.put("game", game);
				
				if (playedType.equals(PlayTypes.WALKED_OVER)) {
					String winner = parts[6];
					csvLine.put("woWinner", winner);
					if (winner != null && !winner.equalsIgnoreCase(homePlayerName) && !winner.equalsIgnoreCase(awayPlayerName)) {
						throw new CSVParseException("Winner must be one of the teams, home or away. CSVLine: " + csvLine);
					}
					csvLine.put("playedType", PlayTypes.WALKED_OVER);
				} else {
					GameSet[] gameSets;
					gameSets = parsePlayedResult(parts[3], parts[4], parts[5]);
					csvLine.put("gameSets", gameSets);
					csvLine.put("playedType", PlayTypes.PLAYED);
				}
	
				resultCSV.add(csvLine);
	    	} catch (Exception e) {
	    		
	    		throw new CSVParseException("Unable to parse round " + (currRoundId+1) + " line " + text, e);
	    	}
	    }
	    
	    for (HashMap<String,Object> csvLine : resultCSV) {
	    	Game game = (Game)csvLine.get("game");
	    	Subdivision subdivision = (Subdivision)csvLine.get("subdivision");
	    	Integer roundId = (Integer)csvLine.get("roundId");
	    	PlayTypes playedType = (PlayTypes)csvLine.get("playedType");
			if (playedType == PlayTypes.PLAYED) {
				GameSet[] gameSets = (GameSet[]) csvLine.get("gameSets");
				game.setGameSets(gameSets);
			} else if (playedType == PlayTypes.POSTPONED) {
				Date postponedDate = (Date) csvLine.get("postponedDate");
				game.setPostponedDate(postponedDate);
			} else if (playedType == PlayTypes.WALKED_OVER) {
				String woWinner = (String) csvLine.get("woWinner");
				boolean homeWOWinner = woWinner != null && woWinner.equalsIgnoreCase(game.getHomeTeam().getTeam().getStaticInfo().getName());
				boolean awayWOWinner = woWinner != null && woWinner.equalsIgnoreCase(game.getAwayTeam().getTeam().getStaticInfo().getName());
				game.setHomeWOWinner(homeWOWinner);				
				game.setAwayWOWinner(awayWOWinner);
			} else if (playedType  == PlayTypes.NOT_PLAYED) {

			} else {
				throw new CSVParseException("PlayType has no valid value " + playedType + " on csv line:" + text);
			}
			game.setPlayType(playedType);
			
	    }
	}
	private GameSet[] parsePlayedResult(String set1, String set2, String set3) throws CSVParseException {
		String[] setStrings = {set1, set2, set3};
		GameSet[] gameSets = new GameSet[set3.equals("") ? 2 : 3];
		for (int i = 0; i < setStrings.length; i++){
			String setString = setStrings[i];
			if (setString.equals("")) {
				continue;
			}
			String[] scoreParts = setString.split("-");
			GameSet set = new GameSet();
			set.setHomeScore(Integer.parseInt(scoreParts[0].trim()));
			set.setAwayScore(Integer.parseInt(scoreParts[1].trim()));
			gameSets[i] = set;
		}
		return gameSets;
	}
	private Game lookupGameByTeams(Subdivision subdivision, Integer roundID, String homePlayerName, String awayPlayerName) {
		GameRound gr = subdivision.getGameRounds().get(roundID);
		for (Game game : gr.getGames()){
			if (homePlayerName.equals(game.getHomeTeam().getTeam().getStaticInfo().getName()) &&
				awayPlayerName.equals(game.getAwayTeam().getTeam().getStaticInfo().getName())) {
				return game;
			}
		}
		return null;
	}
	private Subdivision lookupSubdivisionByName(String subdivisionName) {
		for (Division d : divisions){
			for (Subdivision sd : d.getSubdivisions()){
				if (sd.getName().equalsIgnoreCase(subdivisionName)) {
					return sd;
				}
			}
		}
		return null;
	}
	public ArrayList<Game> getAllGamesForTeam(TeamSeasonStats ts) {
		ArrayList<Game> games = new ArrayList<Game>();
		for (GameRound gr : ts.getSubDivision().getGameRounds()){
			for (Game game : gr.getGames()) {
				if (game.isTeamParticipating(ts)){
					games.add(game);
					break;
				}
			}
		}
		return games;
	}
		
}
