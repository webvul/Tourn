package ml.davvs.tourn.model.persisted;

import java.util.ArrayList;
import java.util.UUID;

/**
 * 
 * @author davvs
 * 
 * A-B
 * C-D  W1-W2
 *             W12-W34
 * E-F  W3-W4
 * G-H
 * 
 */

public class Cup {
	private UUID id;
	private ArrayList<TeamSeasonStats> teams;  //teams.length == 2^n
	private ArrayList<Game> games; //games.length == teams.length - 1
	private Season season;

	public ArrayList<TeamSeasonStats> getTeams() {
		return teams;
	}
	public void setTeams(ArrayList<TeamSeasonStats> teams) {
		this.teams = teams;
	}
	public ArrayList<Game> getGames() {
		return games;
	}
	public void setGames(ArrayList<Game> games) {
		this.games = games;
	}
	public Season getSeason() {
		return season;
	}
	public void setSeason(Season season) {
		this.season = season;
	}
	
	public Cup(){
		setId(UUID.randomUUID());
	}
	public UUID getId() {
		return id;
	}
	public void setId(UUID id) {
		this.id = id;
	}
}
