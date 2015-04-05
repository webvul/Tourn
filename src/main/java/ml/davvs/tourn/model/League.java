package ml.davvs.tourn.model;

import java.util.ArrayList;

public class League {
	private String name;
	private ArrayList<Season> seasons;
	private ArrayList<Cup> cups;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public ArrayList<Season> getSeasons() {
		return seasons;
	}
	public void setSeasons(ArrayList<Season> seasons) {
		this.seasons = seasons;
	}
	public ArrayList<Cup> getCups() {
		return cups;
	}
	public void setCups(ArrayList<Cup> cups) {
		this.cups = cups;
	}
	
	
}
