package ml.davvs.tourn.model.persisted;

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
	public Team lookupTeam(String name, String email, float guessedSkill) {
		//TODO Lookup if this email exists in seasons before...
		return new Team(name, email, guessedSkill);
	}
}
