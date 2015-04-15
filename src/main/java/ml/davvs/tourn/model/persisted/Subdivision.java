package ml.davvs.tourn.model.persisted;

import java.util.ArrayList;

import ml.davvs.tourn.model.GameRegistrationException;

public class Subdivision {
	private String name;
	private ArrayList<TeamSeasonStats> teams;
	private ArrayList<GameRound> gameRounds;
	private int siblingNumber;
	private int siblings;
	private Division division;
	
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
	public ArrayList<TeamSeasonStats> getTeams() {
		return teams;
	}
	public void setTeams(ArrayList<TeamSeasonStats> teams) {
		this.teams = teams;
	}

	public void registerGameResult(Game game, int homeScore, int awayScore) throws GameRegistrationException {
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
			throw new GameRegistrationException("Game " + game + " was already played " + getName());
		}
		game.setAwayScore(awayScore);
		game.setHomeScore(homeScore);
	}

	public void generateGames() {
		if(gameRounds != null) {
			//Forget all game rounds
			gameRounds.clear();
		}
		assert(teams != null && teams.size() > 1);
		/*
		#############
		#   A B C
		# A   3 1
		# B 3   2 
		# C 1 2
		*/ 
		gameRounds = new ArrayList<GameRound>();
		int oddNum = teams.size() % 2;
		int numRounds = teams.size() - (1-oddNum);
		int yr = teams.size() - 2 + oddNum;
		int xr = 0;
		for (int r = 0; r < numRounds; r++) {
			GameRound gameRound = new GameRound();
			gameRound.setGames(new ArrayList<Game>());
			int testRounds = (teams.size()-(1-oddNum));
			for (int g = 0; g < testRounds; g++){
				int x = (xr + r + g) % testRounds;
				int y = (yr - g + testRounds) % testRounds;
				if (x == y) {
					if (oddNum == 0) {
						Game game = new Game();
						game.setHomeTeam(teams.get(x));
						game.setAwayTeam(teams.get(yr+1));
						game.setRound(r);
						gameRound.getGames().add(game);

					}
					continue;
				}
				if (x < y) {
					continue;
				}
				Game game = new Game();
				game.setHomeTeam(teams.get(x));
				game.setAwayTeam(teams.get(y));
				gameRound.getGames().add(game);
				
			}
			gameRounds.add(gameRound);
			
		}
		Season s = division.getSeason();
		if (s.getGameRounds() < gameRounds.size()){
			s.setGameRounds(gameRounds.size());
		}
	}
}
