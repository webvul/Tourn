package ml.davvs.tourn.model;

import java.util.ArrayList;

public class Division {
	private ArrayList<Subdivision> subDivisions;
	private int level;
	private int playerCount;
	private String name;
	private QualifierGroup upperQualifierGroup;
	private QualifierGroup lowerQualifierGroup;

	public QualifierGroup getUpperQualifierGroup() {
		return upperQualifierGroup;
	}
	public void setUpperQualifierGroup(QualifierGroup upperQualifierGroup) {
		this.upperQualifierGroup = upperQualifierGroup;
	}
	public QualifierGroup getLowerQualifierGroup() {
		return lowerQualifierGroup;
	}
	public void setLowerQualifierGroup(QualifierGroup lowerQualifierGroup) {
		this.lowerQualifierGroup = lowerQualifierGroup;
	}
	public String toString() {
		return name;
	}
	public int getPlayerCount() {
		return playerCount;
	}
	public void setPlayerCount(int playerCount) {
		this.playerCount = playerCount;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public ArrayList<Subdivision> getSubDivisions() {
		return subDivisions;
	}
	public void setSubDivisions(ArrayList<Subdivision> subDivisions) {
		this.subDivisions = subDivisions;
	}
	public int getLevel() {
		return level;
	}
	public void setLevel(int level) {
		this.level = level;
	}
}
