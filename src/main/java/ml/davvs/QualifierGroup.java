package ml.davvs;

import java.util.ArrayList;

//Between each division there will be a qualifier group.
//The best of the group will be qualified to the upper division. 
public class QualifierGroup {
	private String qualifierGroupName;
	private ArrayList<TeamSeasonStats> teams;

	public String getQualifierGroupName() {
		return qualifierGroupName;
	}
	public void setQualifierGroupName(String qualifierGroupName) {
		this.qualifierGroupName = qualifierGroupName;
	}
	public ArrayList<TeamSeasonStats> getTeams() {
		return teams;
	}
	public void setTeams(ArrayList<TeamSeasonStats> teams) {
		this.teams = teams;
	}
}
