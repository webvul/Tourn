package ml.davvs.tourn.view;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;

import org.junit.runner.manipulation.Sortable;

import ml.davvs.tourn.controller.InvalidStateException;
import ml.davvs.tourn.model.TableTeamComparator;
import ml.davvs.tourn.model.TableTeamData;
import ml.davvs.tourn.model.persisted.Division;
import ml.davvs.tourn.model.persisted.Game;
import ml.davvs.tourn.model.persisted.Game.PlayTypes;
import ml.davvs.tourn.model.persisted.GameRound;
import ml.davvs.tourn.model.persisted.GameSet;
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
			System.out.println("Round " + (gr + 1));
			for (int d = 0; d < season.getDivisions().size(); d++) {
				Division division = season.getDivisions().get(d);
				for (int s = 0; s < division.getSubdivisions().size(); s ++) {
					Subdivision subdivision = division.getSubdivisions().get(s);
					if (gr < subdivision.getGameRounds().size()){
						GameRound gameRound = subdivision.getGameRounds().get(gr);
						for (Game game : gameRound.getGames()) {
							String home = game.getHomeTeam().getTeam().getStaticInfo().getName();
							String away = game.getAwayTeam().getTeam().getStaticInfo().getName();
							System.out.println(subdivision.getName() +  "\t" + home + "\t" + away);							
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
			System.out.println("### Round " + gri + " ###");
			gri ++;
			for (Game g : gr.getGames()) {
				System.out.println(g.getHomeTeam().getTeam().getStaticInfo().getName() + " - " + g.getAwayTeam().getTeam().getStaticInfo().getName());
			}
		}
		// TeamSeasonStats team = teams.get(p);
			//System.out.println(prefix + team.getTeam().getStaticInfo().getName() + " elo:" + team.getElo() + " guess:" + team.getGuessedSkill());
	}

	
	public void printTableHeaderRow(){
		System.out.println(
				"Rank\t" +
				"Team name\t" +
				"Games\t" +
				"Wins\t" + 
				"Losses\t" + 
				"Set+-\t" +
				"Sets\t" +
				"Score+-\t" +
				"Score\t" +  
				"WOWins\t" + 
				"WOLosses\t");
	}
	public void printTableRow(TableTeamData tableTeamData){
		System.out.println(
				tableTeamData.getRank() + "\t" +
				tableTeamData.getTeamSeasonStats().getTeam().getStaticInfo().getName() + "    \t" +
				tableTeamData.getGamesPlayed() + "\t" +
				tableTeamData.getTotalWins() + "\t" +
				tableTeamData.getTotalLosses() + "\t" +
				tableTeamData.getSetDiff() + "\t" +
				tableTeamData.getSetWins() + "-" +
				tableTeamData.getSetLosses() + "\t" +
				tableTeamData.getBallDiff() + "\t" +
				tableTeamData.getBallWins() + "-" +
				tableTeamData.getBallLosses() + "\t" +
				tableTeamData.getWOWins() + "\t" +
				tableTeamData.getWOLosses() + "\t");
	}
	public void printSeasonDivisionsTable(final Season season) throws InvalidStateException{
		System.out.println("*** Division setup ***");

		ArrayList<Division> divs = season.getDivisions();
		for (int d = 0; d < divs.size(); d++){
			Division division = divs.get(d);
			//System.out.println("*** " + division.getName() + " ***");
			for (int s = 0; s < division.getSubdivisions().size(); s++){
				Subdivision subdivision = division.getSubdivisions().get(s);
				ArrayList<TableTeamData> subdivisionTable = new ArrayList<TableTeamData>();
				//System.out.println("" + subdivision.getName());
				//printTableHeaderRow();
				for (int t = 0; t < subdivision.getTeamSeasonStats().size(); t++){
					TableTeamData tableTeamData = getTableTeamData(season, subdivision.getTeamSeasonStats().get(t));
					//printTableRow(tableTeamData);
					subdivisionTable.add(tableTeamData);
					
				}
				sortTable(subdivisionTable);
				printTable(subdivision, subdivisionTable);
			}
		}
	}
	private void sortTable(ArrayList<TableTeamData> subdivisionTable) {
		TableTeamComparator comp = new TableTeamComparator();
		Collections.sort(subdivisionTable, comp);
		int rank = 1;
		TableTeamData previous = null;
		for (TableTeamData tableTeam : subdivisionTable) {
			if (previous == null) {
				tableTeam.setRank(rank);
				previous = tableTeam;
				continue;
			}
			if (comp.compare(tableTeam, previous) != 0) {
				rank ++;
			}
			tableTeam.setRank(rank);
			previous = tableTeam; 
			
		}
	}

	private void printTable(Subdivision subdivision, ArrayList<TableTeamData> subdivisionTable) throws InvalidStateException {
		System.out.println("" + subdivision.getName());
		printTableHeaderRow();
		//for (int t = 0; t < subdivision.getTeamSeasonStats().size(); t++){
		for (TableTeamData tableTeamData : subdivisionTable) {
			//TableTeamData tableTeamData = getTableTeamData(subdivision.getDivision().getSeason(), subdivision.getTeamSeasonStats().get(t));
			Integer totalWins = tableTeamData.getTotalWins();
			printTableRow(tableTeamData);
		}		
	}

	private TableTeamData getTableTeamData(Season season, TeamSeasonStats ts) throws InvalidStateException {
		TableTeamData tableTeamData = new TableTeamData();
		tableTeamData.setTeamSeasonStats(ts);
		for (Game game : season.getAllGamesForTeam(ts)){
			if (!game.isTeamParticipating(ts)){
				throw new InvalidStateException("Got all games for team, but team not participating in all games! team:" + ts.getId() + " game:" + game.getId());
			}
			boolean isHomeTeam = game.isHomeTeam(ts);

			if (!(game.getPlayType() == PlayTypes.WALKED_OVER || game.getPlayType() == PlayTypes.PLAYED)){
				continue;
			}
			Integer mySetScore = 0;
			Integer opponentSetScore = 0;
			Integer myBallScore = 0;
			Integer opponentBallScore = 0;

			if (game.getPlayType() == PlayTypes.WALKED_OVER){
				boolean WOWin;
				if ((isHomeTeam && game.isHomeWOWinner()) || (!isHomeTeam && game.isAwayWOWinner())){
					//Win, opponent loss on WO
					WOWin = true;
				} else if ((!isHomeTeam && game.isHomeWOWinner()) || (isHomeTeam && game.isAwayWOWinner())){
					//Opponent win on WO
					WOWin = false;
				} else {
					//Double loss on WO, both players lose on WO
					WOWin = false;
				}
				
				//WOScore is 11-3,11-3
				final int WO_WIN_SCORE = 11;
				final int WO_LOSE_SCORE = 3;
				if (WOWin){
					myBallScore += WO_WIN_SCORE * 2;
					opponentBallScore += WO_LOSE_SCORE * 2;
					mySetScore += 2;
					opponentSetScore += 0;
					tableTeamData.setWOWins(tableTeamData.getWOWins() + 1);
					tableTeamData.setTotalWins(tableTeamData.getTotalWins() + 1);
				} else {
					myBallScore += WO_LOSE_SCORE * 2;
					opponentBallScore += WO_WIN_SCORE * 2;
					mySetScore += 0;
					opponentSetScore += 2;
					tableTeamData.setWOLosses(tableTeamData.getWOLosses() + 1);
					tableTeamData.setTotalLosses(tableTeamData.getTotalLosses() + 1);
				}
				
			} else if (game.getPlayType() == PlayTypes.PLAYED) {
				if (isHomeTeam){
					mySetScore = game.getHomeSetScore();
					opponentSetScore = game.getAwaySetScore();
				} else {
					mySetScore = game.getAwaySetScore();
					opponentSetScore = game.getHomeSetScore();
				}
				for (GameSet set : game.getGameSets()){
					if (isHomeTeam){
						myBallScore += set.getHomeScore();
						opponentBallScore += set.getAwayScore();
					} else {
						myBallScore += set.getAwayScore();
						opponentBallScore += set.getHomeScore();
					}
				}

				if (isHomeTeam) {
					if (game.isHomeWinner()){
						tableTeamData.setTotalWins(tableTeamData.getTotalWins() + 1);
					} else {
						tableTeamData.setTotalLosses(tableTeamData.getTotalLosses() + 1);
					}
				} else {
					if (!game.isHomeWinner()){
						tableTeamData.setTotalWins(tableTeamData.getTotalWins() + 1);
					} else {
						tableTeamData.setTotalLosses(tableTeamData.getTotalLosses() + 1);
					}
				}
			}
			tableTeamData.setGamesPlayed(tableTeamData.getGamesPlayed() + 1);
			tableTeamData.setBallWins(tableTeamData.getBallWins() + myBallScore);
			tableTeamData.setBallLosses(tableTeamData.getBallLosses() + opponentBallScore);
			tableTeamData.setSetWins(tableTeamData.getSetWins() + mySetScore);
			tableTeamData.setSetLosses(tableTeamData.getSetLosses() + opponentSetScore);
			
		}

		return tableTeamData;
	}

	public void printSeasonDivisionsSetup(final Season season){
		System.out.println("*** Division tables ***");
		ArrayList<Division> divs = season.getDivisions();
		for (int d = 0; d < divs.size(); d++){
			Division division = divs.get(d);
			//System.out.println("*** " + division.getName() + " ***");
			for (int s = 0; s < division.getSubdivisions().size(); s++){
				Subdivision subdivision = division.getSubdivisions().get(s);
				System.out.println("" + subdivision.getName());
				for (int t = 0; t < subdivision.getTeamSeasonStats().size(); t++){
					TeamSeasonStats ts = subdivision.getTeamSeasonStats().get(t);
					System.out.println(ts.getTeam().getStaticInfo().getName() +
							 "\t"  + ts.getTeam().getStaticInfo().getEmail());
					;
				}
			}
		}
	}
}
