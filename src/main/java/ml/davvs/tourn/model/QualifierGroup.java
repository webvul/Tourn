package ml.davvs.tourn.model;

import java.util.ArrayList;

//Between each division there will be a qualifier group.
//The best of the group will be qualified to the upper division. 
public class QualifierGroup {
	private String name;
	private ArrayList<TeamSeasonStats> teams;
	private Division upperDivision;
	private Division lowerDivision;

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Division getUpperDivision() {
		return upperDivision;
	}
	public void setUpperDivision(Division upperDivision) {
		this.upperDivision = upperDivision;
	}
	public Division getLowerDivision() {
		return lowerDivision;
	}
	public void setLowerDivision(Division lowerDivision) {
		this.lowerDivision = lowerDivision;
	}
	public final ArrayList<TeamSeasonStats> getTeams() {
		return teams;
	}
	public void addTeam(TeamSeasonStats ts) {
		teams.add(ts);
	}
	
	public QualifierGroup() {
		teams = new ArrayList<TeamSeasonStats>();
	}
	
	public int getCountTeamsUpper(){
		int count = 0;
		for (TeamSeasonStats t : teams) {
			int tLevel = t.getSubDivision().getDivision().getLevel();
			if (tLevel == upperDivision.getLevel()) {
				count ++;
			}
		}
		return count;
	}

	public int getCountTeamsLower(){
		int count = 0;
		for (TeamSeasonStats t : teams) {
			int tLevel = t.getSubDivision().getDivision().getLevel();
			if (tLevel == lowerDivision.getLevel()) {
				count ++;
			}
		}
		return count;
	}
}
