package ml.davvs;

public class Game {
	// field are mutually exclusive as a game is either played in a Division, Cup, QualifierGroup or is a placementMatch
	public enum GameTypes {DIVISION, CUP, QUALIFIER, PLACEMENT};
	
	private TeamSeasonStats homeTeam;
	private TeamSeasonStats awayTeam;
	private Integer homeScore;
	private Integer awayScore;

	private GameTypes gameType;
	private Division division;
	private Cup cup;
	private QualifierGroup qualifier;

	private Integer round;

	public TeamSeasonStats getHomeTeam() {
		return homeTeam;
	}

	public void setHomeTeam(TeamSeasonStats homeTeam) {
		this.homeTeam = homeTeam;
	}

	public TeamSeasonStats getAwayTeam() {
		return awayTeam;
	}

	public void setAwayTeam(TeamSeasonStats awayTeam) {
		this.awayTeam = awayTeam;
	}

	public Integer getHomeScore() {
		return homeScore;
	}

	public void setHomeScore(Integer homeScore) {
		this.homeScore = homeScore;
	}

	public Integer getAwayScore() {
		return awayScore;
	}

	public void setAwayScore(Integer awayScore) {
		this.awayScore = awayScore;
	}

	public GameTypes getGameType() {
		return gameType;
	}

	public void setGameType(GameTypes gameType) {
		this.gameType = gameType;
	}

	public Division getDivision() {
		return division;
	}

	public void setDivision(Division division) {
		this.division = division;
	}

	public Cup getCup() {
		return cup;
	}

	public void setCup(Cup cup) {
		this.cup = cup;
	}

	public QualifierGroup getQualifier() {
		return qualifier;
	}

	public void setQualifier(QualifierGroup qualifier) {
		this.qualifier = qualifier;
	}

	public Integer getRound() {
		return round;
	}

	public void setRound(Integer round) {
		this.round = round;
	}
	
	
}
