package ml.davvs.tourn.view;

import java.util.ArrayList;

import ml.davvs.tourn.model.persisted.Division;
import ml.davvs.tourn.model.persisted.Game;
import ml.davvs.tourn.model.persisted.GameRound;
import ml.davvs.tourn.model.persisted.Season;
import ml.davvs.tourn.model.persisted.Subdivision;
import ml.davvs.tourn.model.persisted.TeamSeasonStats;

public class ConsoleOut {
	
	public void printAllTeams(Season s) {
		for (TeamSeasonStats ts : s.getTeams()){
			String qs = "";
			if (ts.getQualifierGroup() != null){
				qs = " Qualifier:" + ts.getQualifierGroup().getName();
			}
			System.out.println(ts.getTeam().getStaticInfo().getName() + " " +
					"Division: " + ts.getSubDivision().getName() +
					qs);
		}
	}

	public void printAllGamesRounds(final Season season) {
		System.out.println("*** All Games ***");
		for (int gr = 0; gr < season.getGameRounds(); gr++) {
			System.out.println(" ### Round " + (gr + 1) + " ###");
			for (int d = 0; d < season.getDivisions().size(); d++) {
				Division division = season.getDivisions().get(d);
				for (int s = 0; s < division.getSubdivisions().size(); s ++) {
					Subdivision subdivision = division.getSubdivisions().get(s);
					if (gr < subdivision.getGameRounds().size()){
						GameRound gameRound = subdivision.getGameRounds().get(gr);
						for (Game game : gameRound.getGames()) {
							String home = game.getHomeTeam().getTeam().getStaticInfo().getName();
							String away = game.getAwayTeam().getTeam().getStaticInfo().getName();
							System.out.println("[" + subdivision.getName() +  "]:\t" + home + "\t" + away);							
						}
					}
				}
			}
		}
		// TeamSeasonStats team = teams.get(p);
			//System.out.println(prefix + team.getTeam().getStaticInfo().getName() + " elo:" + team.getElo() + " guess:" + team.getGuessedSkill());
	}

	public void printGamesForSubdivision(final Subdivision subdivision, final String prefix) {
		ArrayList<GameRound> grs = subdivision.getGameRounds();
		int gri = 1;
		
		for (GameRound gr : grs ){
			System.out.println(" ### Round " + gri + " ###");
			gri ++;
			for (Game g : gr.getGames()) {
				System.out.println(g.getHomeTeam().getTeam().getStaticInfo().getName() + " - " + g.getAwayTeam().getTeam().getStaticInfo().getName());
			}
		}
		// TeamSeasonStats team = teams.get(p);
			//System.out.println(prefix + team.getTeam().getStaticInfo().getName() + " elo:" + team.getElo() + " guess:" + team.getGuessedSkill());
	}

	public void printSeasonDivisions(final Season season){
		System.out.println("*** Divisions ***");
		ArrayList<Division> divs = season.getDivisions();
		for (int d = 0; d < divs.size(); d++){
			Division division = divs.get(d);
			System.out.println("*** " + division.getName() + " ***");
			for (int s = 0; s < division.getSubdivisions().size(); s++){
				Subdivision subdivision = division.getSubdivisions().get(s);
				System.out.println("  " + subdivision.getName());
				for (int t = 0; t < subdivision.getTeams().size(); t++){
					TeamSeasonStats ts = subdivision.getTeams().get(t);
					System.out.println("    " + " " + ts.getTeam().getStaticInfo().getName() +
							 " Guestimated skill:"+ ts.getGuessedSkill());
					;
				}
			}
		}
	}
}
