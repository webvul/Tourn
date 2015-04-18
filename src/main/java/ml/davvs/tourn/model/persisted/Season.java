package ml.davvs.tourn.model.persisted;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.UUID;

import ml.davvs.tourn.model.SeasonPhase;
import ml.davvs.tourn.model.SeasonPhaseGuard;
import ml.davvs.tourn.model.SeasonPhaseGuardException;
import ml.davvs.tourn.model.SeasonPhaseRequiredException;

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
		id = UUID.randomUUID();
		currentPhase = SeasonPhase.PREPARATION;
		phaseGuard = new SeasonPhaseGuard(this);
		teams = new ArrayList<TeamSeasonStats>();
		gameRounds = 0;
		distribtionDone = false;
		tournament = t;
	}

	public void addTeam(String name, String email, float guessedSkill, Subdivision subdivision) throws SeasonPhaseRequiredException {
		if (!(currentPhase == SeasonPhase.SEASON || currentPhase == SeasonPhase.SEASONPREP)){
			throw new SeasonPhaseRequiredException(currentPhase, SeasonPhase.SEASONPREP);
		}
		//Add a team to a subdivision late
		Team t = new Team(name, email, guessedSkill); //TODO make a lookup if this team already exists
		t.getCurrentSeason().setSeason(this);
		t.getCurrentSeason().setSubDivision(subdivision);
		subdivision.getTeams().add(t.getCurrentSeason());
		teams.add(t.getCurrentSeason());
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
			for (Subdivision subdivision : division.getSubDivisions()){
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
			int playersInDivision = (int)((float)division.getSubDivisions().size() * playersPerSubdivision);
			if (restPlayersToDistribute > division.getSubDivisions().size()) {
				playersInDivision += division.getSubDivisions().size();
				restPlayersToDistribute -= division.getSubDivisions().size();
			} else if (restPlayersToDistribute > 0) {
				playersInDivision += restPlayersToDistribute;
				restPlayersToDistribute -= restPlayersToDistribute;
			}

			int s = 0;
			
			while (playersInDivision > 0){
				int subdivisionId = s % division.getSubDivisions().size();
				Subdivision subdivision = division.getSubDivisions().get(subdivisionId);
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
				subdivision.getTeams().add(teamSeasonStats);
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
			division.setSubDivisions(new ArrayList<Subdivision>());
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
				subdivision.setTeams(new ArrayList<TeamSeasonStats>());
				subdivision.setGameRounds(new ArrayList<GameRound>());
				subdivision.setSiblingNumber(s);

				if (s > 25){
					throw new RuntimeException("Unable to make up subdivision sibling letter");
				}
				subdivision.setName(division.getName() + (currentSubdivisions > 1 ? (char)(65 + s) : ""));
				division.getSubDivisions().add(subdivision);
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
			if (targetSubdiv > division.getSubDivisions().size()){
				throw new ConfFileException("Invalid subdivision " + targetSubdiv + " Amount of subdivisions are:" + division.getSubDivisions().size());
			}
			Subdivision subdivision =  division.getSubDivisions().get(targetSubdiv - 1);
			teams.add(teamSeasonStats);
			subdivision.getTeams().add(teamSeasonStats);
			return;
		}
		throw new ConfFileException("Unable to find division with level " + targetDiv);
	}
		
}
