package ml.davvs;

import java.util.ArrayList;

public class Season {
	private ArrayList<Division> divisions;
	private ArrayList<TeamSeasonStats> teams;
	private ArrayList<QualifierGroup> qualifierGroups;
	private Cup cup;
	private ArrayList<Game> placementMatches;
	public ArrayList<Division> getDivisions() {
		return divisions;
	}
	public void setDivisions(ArrayList<Division> divisions) {
		this.divisions = divisions;
	}
	public ArrayList<TeamSeasonStats> getTeams() {
		return teams;
	}
	public void setTeams(ArrayList<TeamSeasonStats> teams) {
		this.teams = teams;
	}
	public ArrayList<QualifierGroup> getQualifierGroups() {
		return qualifierGroups;
	}
	public void setQualifierGroups(ArrayList<QualifierGroup> qualifierGroups) {
		this.qualifierGroups = qualifierGroups;
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
}
