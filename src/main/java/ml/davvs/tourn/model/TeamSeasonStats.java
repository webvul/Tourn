package ml.davvs.tourn.model;

import java.util.ArrayList;

// Every time a team is participating in a season, The player will have these stats in the season.
// Possible created from the last season stats or from placement matches
public class TeamSeasonStats {
	private Team team;
	private Subdivision subdivision;
	private Cup cup;
	private float guessedSkill;
	private QualifierGroup qualifierGroup;
	private ArrayList<Game> placementMatches;
	private Season season;
	private int elo;

	public float getGuessedSkill() {
		return guessedSkill;
	}
	public void setGuessedSkill(float guessedSkill) {
		this.guessedSkill = guessedSkill;
	}
	public Team getTeam() {
		return team;
	}
	public void setTeam(Team team) {
		this.team = team;
	}
	public Subdivision getSubDivision() {
		return subdivision;
	}
	public void setSubDivision(Subdivision subdivision) {
		this.subdivision = subdivision;
	}
	public Cup getCup() {
		return cup;
	}
	public void setCup(Cup cup) {
		this.cup = cup;
	}
	public QualifierGroup getQualifierGroup() {
		return qualifierGroup;
	}
	public void setQualifierGroup(QualifierGroup qualifierGroup) {
		this.qualifierGroup = qualifierGroup;
	}
	public ArrayList<Game> getPlacementMatches() {
		return placementMatches;
	}
	public void setPlacementMatches(ArrayList<Game> placementMatches) {
		this.placementMatches = placementMatches;
	}
	public Season getSeason() {
		return season;
	}
	public void setSeason(Season season) {
		this.season = season;
	}
	public int getElo() {
		return elo;
	}
	public void setElo(int elo) {
		this.elo = elo;
	}
}
