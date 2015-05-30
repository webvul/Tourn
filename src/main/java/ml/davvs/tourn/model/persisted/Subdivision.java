package ml.davvs.tourn.model.persisted;

import java.util.ArrayList;
import java.util.UUID;

import ml.davvs.tourn.model.GameRegistrationException;
import ml.davvs.tourn.model.persisted.Game.PlayTypes;

public class Subdivision {
	private UUID id;
	private String name;
	private ArrayList<TeamSeasonStats> teamSeasonStats;
	private ArrayList<GameRound> gameRounds;
	private int siblingNumber;
	private int siblings;
	private Division division;
	private int timesToPlayEachOpponent;
	
	public Subdivision(Division division) {
		setId(UUID.randomUUID());
		this.division = division;
		teamSeasonStats = new ArrayList<TeamSeasonStats>();
		gameRounds = new ArrayList<GameRound>();
	}
	public ArrayList<GameRound> getGameRounds() {
		return gameRounds;
	}
	public void setGameRounds(ArrayList<GameRound> gameRounds) {
		this.gameRounds = gameRounds;
	}

	public String toString() {
		return name;
	}

	public Division getDivision() {
		return division;
	}
	public void setDivision(Division division) {
		this.division = division;
	}
	public int getSiblings() {
		return siblings;
	}
	public void setSiblings(int siblings) {
		this.siblings = siblings;
	}
	public int getSiblingNumber() {
		return siblingNumber;
	}
	public void setSiblingNumber(int siblingNumber) {
		this.siblingNumber = siblingNumber;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public ArrayList<TeamSeasonStats> getTeamSeasonStats() {
		return teamSeasonStats;
	}
	public void setTeamSeasonStats(ArrayList<TeamSeasonStats> teams) {
		this.teamSeasonStats = teams;
	}

	public void registerGameResult(Game game, GameSet[] gameSets) throws GameRegistrationException {
		boolean match = false;
		for (GameRound gr : getGameRounds()) {
			if (game.getRound().equals(gr)){
				match = true;
			}
		}
		if (!match) {
			throw new GameRegistrationException("Game " + game + " does not belong in subdivision " + getName());
		}
		if (game.isPlayed()) {
			throw new GameRegistrationException("Game " + game + " was already played in subdivision " + getName());
		}
		game.setGameSets(gameSets);
	}

	public void generateGames() {
		if(gameRounds != null) {
			//Forget all game rounds
			gameRounds.clear();
		}
		assert(teamSeasonStats != null && teamSeasonStats.size() > 1);
		/*
		#############
		#   A B C
		# A   3 1
		# B 3   2 
		# C 1 2
		*/ 
		gameRounds = new ArrayList<GameRound>();
		int oddNum = teamSeasonStats.size() % 2;
		int numRounds = teamSeasonStats.size() - (1-oddNum);
		int yr = teamSeasonStats.size() - 2 + oddNum;
		int xr = 0;
		boolean swapHomeAway = false;
		for (int meetingNum = 0; meetingNum < timesToPlayEachOpponent; meetingNum ++){
			swapHomeAway = meetingNum % 2 == 1;
			for (int r = 0; r < numRounds; r++) {
				int roundId = r + numRounds * meetingNum;
				GameRound gameRound = new GameRound();
				gameRound.setGames(new ArrayList<Game>());
				int testRounds = (teamSeasonStats.size()-(1-oddNum));
				for (int g = 0; g < testRounds; g++){
					int x = (xr + r + g) % testRounds;
					int y = (yr - g + testRounds) % testRounds;
					if (x == y) {
						if (oddNum == 0) {
							Game game = new Game();
							if (swapHomeAway) {
								game.setHomeTeam(teamSeasonStats.get(yr+1));
								game.setAwayTeam(teamSeasonStats.get(x));
							} else {
								game.setHomeTeam(teamSeasonStats.get(x));
								game.setAwayTeam(teamSeasonStats.get(yr+1));
							}
							game.setRound(roundId);
							gameRound.getGames().add(game);
	
						}
						continue;
					}
					if (x < y) {
						continue;
					}
					Game game = new Game();
					game.setHomeTeam(teamSeasonStats.get(x));
					game.setAwayTeam(teamSeasonStats.get(y));
					gameRound.getGames().add(game);
					
				}
				gameRounds.add(gameRound);
				
			}
		}
		Season s = division.getSeason();
		if (s.getGameRounds() < gameRounds.size()){
			s.setGameRounds(gameRounds.size());
		}
	}
	public UUID getId() {
		return id;
	}
	public void setId(UUID id) {
		this.id = id;
	}
	public int getTimesToPlayEachOpponent() {
		return timesToPlayEachOpponent;
	}
	public void setTimesToPlayEachOpponent(int timesToPlayEachOpponent) {
		this.timesToPlayEachOpponent = timesToPlayEachOpponent;
	}
}
