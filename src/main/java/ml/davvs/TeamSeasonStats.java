package ml.davvs;

import java.util.ArrayList;

// Every time a team is participating in a season, The player will have these stats in the season.
// Possible created from the last season stats or from placement matches
public class TeamSeasonStats {
	private TeamStaticInfo teamInfo;
	private Division division;
	private Cup cup;
	private QualifierGroup qualifierGroup;
	private ArrayList<Game> placementMatches;
	private Season season;
	private int elo;

	public TeamStaticInfo getTeamInfo() {
		return teamInfo;
	}
	public void setTeamInfo(TeamStaticInfo teamInfo) {
		this.teamInfo = teamInfo;
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
