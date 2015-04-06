package ml.davvs.tourn.model;

import java.util.ArrayList;

public class Tournament {
	private String name;
	private ArrayList<Season> seasons;

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
}
