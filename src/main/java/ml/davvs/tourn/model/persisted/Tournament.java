package ml.davvs.tourn.model.persisted;

import java.util.ArrayList;
import java.util.UUID;

public class Tournament {
	private UUID id;
	private String name;
	private ArrayList<Season> seasons;

	public Tournament() {
		setId(UUID.randomUUID());
		seasons = new ArrayList<Season>();
	}
	public UUID getId() {
		return id;
	}
	private void setId(UUID id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public ArrayList<Season> getSeasons() {
		return seasons;
	}
	public Team lookupTeam(String name, String email, float guessedSkill) {
		//TODO Lookup if this email exists in seasons before...
		return new Team(name, email, guessedSkill);
	}
}
