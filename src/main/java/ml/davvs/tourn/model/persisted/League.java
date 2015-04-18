package ml.davvs.tourn.model.persisted;

import java.util.ArrayList;
import java.util.UUID;

public class League {
	private String name;
	private ArrayList<Season> seasons;
	private ArrayList<Cup> cups;
	private UUID id;

	public League(){
		setId(UUID.randomUUID());
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
	public void setSeasons(ArrayList<Season> seasons) {
		this.seasons = seasons;
	}
	public ArrayList<Cup> getCups() {
		return cups;
	}
	public void setCups(ArrayList<Cup> cups) {
		this.cups = cups;
	}
	public UUID getId() {
		return id;
	}
	public void setId(UUID id) {
		this.id = id;
	}
	
	
}
