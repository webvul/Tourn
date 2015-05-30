package ml.davvs.tourn.model.persisted;

import java.util.Date;
import java.util.UUID;

public class Game {
	// field are mutually exclusive as a game is either played in a Division, Cup, QualifierGroup or is a placementMatch
	public enum GameTypes {DIVISION, CUP, QUALIFIER, PLACEMENT};
	public enum PlayTypes {NOT_PLAYED, POSTPONED, WALKED_OVER, PLAYED};
	
	private UUID id;
	
	private TeamSeasonStats homeTeam;
	private TeamSeasonStats awayTeam;

	private PlayTypes playType;

	private GameSet[] gameSets;

	private boolean isHomeWOWinner;
	private boolean isAwayWOWinner;

	private GameTypes gameType;
	
	private Date postponedDate;
	private Subdivision subdivision;
	private Cup cup;
	private QualifierGroup qualifier;

	private Integer round;

	public Game() {
		setId(UUID.randomUUID());
		playType = PlayTypes.NOT_PLAYED;
	}
	
	public boolean isHomeTeam(TeamSeasonStats team){
		return team.getTeam().getStaticInfo().getId() == homeTeam.getTeam().getStaticInfo().getId();
	}
	public boolean isAwayTeam(TeamSeasonStats team){
		return team.getTeam().getStaticInfo().getId() == awayTeam.getTeam().getStaticInfo().getId();
	}
	public boolean isTeamParticipating(TeamSeasonStats team){
		return isHomeTeam(team) || isAwayTeam(team);
	}
	
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

	public GameTypes getGameType() {
		return gameType;
	}

	public void setGameType(GameTypes gameType) {
		this.gameType = gameType;
	}

	public Subdivision getDivision() {
		return subdivision;
	}

	public void setDivision(Subdivision subdivision) {
		this.subdivision = subdivision;
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

	public boolean isPlayed() {
		return isHomeWOWinner() || isHomeWOWinner() || gameSets[0].getHomeScore() != 0 || gameSets[0].getAwayScore() != 0;
	}
	
	public UUID getId() {
		return id;
	}
	
	public void setId(UUID id) {
		this.id = id;
	}
	
	public void registerScoreByString(String scoreSet1,
			String scoreSet2, String scoreSet3) {
		
	}
	

	public GameSet[] getGameSets() {
		return gameSets;
	}
	public void setGameSets(GameSet[] gameSets) {
		this.gameSets = gameSets;
	}
	public Subdivision getSubdivision() {
		return subdivision;
	}
	public void setSubdivision(Subdivision subdivision) {
		this.subdivision = subdivision;
	}
	public PlayTypes getPlayType() {
		return playType;
	}
	public void setPlayType(PlayTypes playType) {
		this.playType = playType;
	}
	public Date getPostponedDate() {
		return postponedDate;
	}
	public void setPostponedDate(Date postponedDate) {
		this.postponedDate = postponedDate;
	}
	public boolean isHomeWOWinner() {
		return isHomeWOWinner;
	}
	public void setHomeWOWinner(boolean isHomeWOWinner) {
		this.isHomeWOWinner = isHomeWOWinner;
	}
	public boolean isAwayWOWinner() {
		return isAwayWOWinner;
	}
	public void setAwayWOWinner(boolean isAwayWOWinner) {
		this.isAwayWOWinner = isAwayWOWinner;
	}
	public Integer getHomeSetScore(){
		Integer score = 0;
		for (GameSet gs : gameSets){
			score += gs.getHomeScore() > gs.getAwayScore() ? 1 : 0;
		}
		return score;
	}
	public Integer getAwaySetScore(){
		Integer score = 0;
		for (GameSet gs : gameSets){
			score += gs.getAwayScore() > gs.getHomeScore() ? 1 : 0;;
		}
		return score;
	}

	public boolean isHomeWinner() {
		return getHomeSetScore() > getAwaySetScore();
	}
}
