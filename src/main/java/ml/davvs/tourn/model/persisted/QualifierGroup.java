package ml.davvs.tourn.model.persisted;

import java.util.ArrayList;

import ml.davvs.tourn.model.QualifierGroupException;

//Between each division there will be a qualifier group.
//The best of the group will be qualified to the upper division. 
public class QualifierGroup {
	private String name;
	private ArrayList<TeamSeasonStats> teams;
	private Division upperDivision;
	private Division lowerDivision;
	private boolean finished;
	private boolean startedPlaying;

	public boolean isStartedPlaying() {
		return startedPlaying;
	}
	public void setStartedPlaying(boolean startedPlaying) {
		this.startedPlaying = startedPlaying;
	}
	public boolean isFinished() {
		return finished;
	}
	public void setFinished(boolean finished) {
		this.finished = finished;
	}
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
	public void addTeam(TeamSeasonStats ts) throws QualifierGroupException {
		if (startedPlaying) {
			throw new QualifierGroupException("Unable to add team while qualifier group is playing");
		}
		teams.add(ts);
		finished = false;
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
