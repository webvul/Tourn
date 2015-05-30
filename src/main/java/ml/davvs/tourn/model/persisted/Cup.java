package ml.davvs.tourn.model.persisted;

import java.util.ArrayList;
import java.util.List;
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
	private static int[] generateCalcBrackets(int numTeams) {
		int powOf2 = 1;
		while (powOf2 < numTeams) {
			powOf2 *= 2;
		}
		int calcTeams = powOf2;

		int[] groups = new int[calcTeams];
		for (int g = 0; g < groups.length; g++){
			groups[g] = 0;
		}
		/*     0  1  2  3  4  5  6  7
		 * 3:  1           2
		 * 5:  1     4     2     3
		 * 9:  1  8  4  5  2  7  3  6
		 */
		int step;
		int firstId;
		int sum;
		groups[0] = 1;
		step = calcTeams;
		firstId = step / 2;
		sum = 3;

		do {
			for (int id = firstId; id < calcTeams; id += step) {
				int opponent = sum - groups[id - firstId];
				groups[id] = opponent <= numTeams ? opponent : 0;
			}
			step /= 2;
			firstId /= 2;
			sum = (sum - 1) * 2 + 1; 
		} while (firstId > 0);

		return groups;
	}

	public void createCupFromTeams(List<TeamSeasonStats> teams){
		int[] calcBrackets = generateCalcBrackets(teams.size());
		
	}

	public UUID getId() {
		return id;
	}
	public void setId(UUID id) {
		this.id = id;
	}
}
