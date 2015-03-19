package ml.davvs;

import java.util.ArrayList;

public class Tournament {
	private String name;
	private ArrayList<Season> seasons;
	private ArrayList<TeamStaticInfo> teams;

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
	public ArrayList<TeamStaticInfo> getTeams() {
		return teams;
	}
	public void setTeams(ArrayList<TeamStaticInfo> teams) {
		this.teams = teams;
	}
}
