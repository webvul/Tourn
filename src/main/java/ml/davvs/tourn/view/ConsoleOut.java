package ml.davvs.tourn.view;

import java.util.ArrayList;

import ml.davvs.tourn.model.Division;
import ml.davvs.tourn.model.Season;
import ml.davvs.tourn.model.Subdivision;
import ml.davvs.tourn.model.TeamSeasonStats;

public class ConsoleOut {
	public void printGamesForSubdivision(final Subdivision subdivision, final String prefix) {
		ArrayList<TeamSeasonStats> teams = subdivision.getTeams();
		for (int p = 0; p < teams.size(); p++) {
			TeamSeasonStats team = teams.get(p);
			System.out.println(prefix + team.getTeam().getStaticInfo().getName() + " elo:" + team.getElo() + " guess:" + team.getGuessedSkill());
		}
	}

	public void printSeasonDivisions(final Season season){
		ArrayList<Division> divs = season.getDivisions();
		for (int d = 0; d < divs.size(); d++){
			Division division = divs.get(d);
			System.out.println("*** " + division.getName() + " ***");
			for (int s = 0; s < division.getSubDivisions().size(); s++){
				Subdivision subdivision = division.getSubDivisions().get(s);
				System.out.println("  " + subdivision.getName());
				for (int t = 0; t < subdivision.getTeams().size(); t++){
					TeamSeasonStats ts = subdivision.getTeams().get(t);
					System.out.println("    " + "[" + ts.getElo()  + "]" + ts.getTeam().getStaticInfo().getName());
					;
				}
			}
		}
	}
}
