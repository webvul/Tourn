package ml.davvs.tourn.model.persisted;

import java.util.ArrayList;
import java.util.UUID;

import ml.davvs.tourn.model.QualifierGroupException;

public class Division {
	private UUID id;
	private ArrayList<Subdivision> subdivisions;
	private int level;
	private int teamCount;
	private String name;
	private QualifierGroup upperQualifierGroup;
	private QualifierGroup lowerQualifierGroup;
	private Season season;
	
	public Division(Season s) {
		setId(UUID.randomUUID());
		season = s;
	}
	public Season getSeason() {
		return season;
	}
	public void setSeason(Season season) {
		this.season = season;
	}
	public QualifierGroup getUpperQualifierGroup() {
		return upperQualifierGroup;
	}
	public void setUpperQualifierGroup(QualifierGroup upperQualifierGroup) {
		this.upperQualifierGroup = upperQualifierGroup;
	}
	public QualifierGroup getLowerQualifierGroup() throws QualifierGroupException {
		if (lowerQualifierGroup == null) {
			throw new QualifierGroupException("Qualifier group to " + getName() + " does not exist");
		}
		return lowerQualifierGroup;
	}
	public void setLowerQualifierGroup(QualifierGroup lowerQualifierGroup) {
		this.lowerQualifierGroup = lowerQualifierGroup;
	}
	public String toString() {
		return name;
	}
	public int getTeamCount() {
		return teamCount;
	}
	public void setTeamCount(int teamCount) {
		this.teamCount = teamCount;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public ArrayList<Subdivision> getSubdivisions() {
		return subdivisions;
	}
	public void setSubdivisions(ArrayList<Subdivision> subDivisions) {
		this.subdivisions = subDivisions;
	}
	public int getLevel() {
		return level;
	}
	public void setLevel(int level) {
		this.level = level;
	}
	public UUID getId() {
		return id;
	}
	public void setId(UUID id) {
		this.id = id;
	}
	public Subdivision getSubdivisionWithTheLeastPlayers() {
		int bestId = (int) Math.floor(Math.random() * subdivisions.size());
		Subdivision best = subdivisions.get(bestId);
		for (int s = 0; s < subdivisions.size(); s++){
			if (bestId == s){
				continue;
			}
			Subdivision compare = subdivisions.get(s);
			if (best.getTeamSeasonStats().size() > compare.getTeamSeasonStats().size()) {
				bestId = s;
				best = compare;
			}
		}
		return best;
	}
}
