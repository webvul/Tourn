package ml.davvs.tourn.model.persisted;

import java.util.ArrayList;
import java.util.UUID;

public class Team {
	private TeamStaticInfo staticInfo;
	private ArrayList<TeamSeasonStats> seasonStats;
	private UUID id;

	public Team(String name, String email, float guessedSkill){
		//TODO Check and see if this team already exists
		setId(UUID.randomUUID());
		this.staticInfo = new TeamStaticInfo(name, email);
		TeamSeasonStats currentSeason = new TeamSeasonStats();
		currentSeason.setTeam(this);
		currentSeason.setGuessedSkill(guessedSkill);
		this.seasonStats = new ArrayList<TeamSeasonStats>();
		this.seasonStats.add(currentSeason);
	}
	
	public TeamSeasonStats getCurrentSeason(){
		return seasonStats.get(seasonStats.size() - 1);
	}

	public int getLastElo() {
		TeamSeasonStats lastSeason = seasonStats.get(seasonStats.size()-1);
		return lastSeason.getElo();
	}

	public TeamStaticInfo getStaticInfo() {
		return staticInfo;
	}
	public void setStaticInfo(TeamStaticInfo staticInfo) {
		this.staticInfo = staticInfo;
	}
	public ArrayList<TeamSeasonStats> getSeasonStats() {
		return seasonStats;
	}
	public void setSeasonStats(ArrayList<TeamSeasonStats> seasonStats) {
		this.seasonStats = seasonStats;
	}

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}
	
}
