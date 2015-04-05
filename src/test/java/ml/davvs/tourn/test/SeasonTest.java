package ml.davvs.tourn.test;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ml.davvs.tourn.model.Game;
import ml.davvs.tourn.model.GameRound;
import ml.davvs.tourn.model.Season;
import ml.davvs.tourn.model.SeasonPhase;
import ml.davvs.tourn.model.SeasonPhaseException;
import ml.davvs.tourn.model.Subdivision;
import ml.davvs.tourn.model.Team;
import ml.davvs.tourn.model.TeamSeasonStats;
import ml.davvs.tourn.model.TeamStaticInfo;
import ml.davvs.tourn.model.Tournament;

public class SeasonTest {
	private Season s;
	private Tournament tournament;
	private ArrayList<Team> teams;

	@Before
	public void setUp() throws Exception {
		s = new Season();
		tournament = new Tournament();
		tournament.setName("Test Tournament");
		s.setTournament(tournament);
		teams = new ArrayList<Team>();
	}

	@After
	public void tearDown() throws Exception {
	}
	
	private void setUpTeams(final int teamCount) {
		for (int t = 0; t < teamCount; t++) {
			Team team = new Team("team" + t, "team" + t + "@example.com", 0.5f * t);
			teams.add(team);
		}
	}

	@Test
	public void testCreateDivisionsTenPlayers() throws SeasonPhaseException {
		setUpTeams(10);

		s.sortTeams(teams);
		s.setTeams(teams);
		s.setCurrentPhase(SeasonPhase.QUALIFIERSPREP);
		s.createDivisions(teams.size(), 2, 6);
		
		assertEquals(2, s.getDivisions().size());
		assertEquals(1, s.getDivisions().get(0).getSubDivisions().size());
		assertEquals(1, s.getDivisions().get(1).getSubDivisions().size());
	}

	@Test
	public void testCreateDivisions30Players() throws SeasonPhaseException {
		setUpTeams(30);

		s.setCurrentPhase(SeasonPhase.PREPARATION);
		s.sortTeams(teams);
		s.setTeams(teams);
		s.setCurrentPhase(SeasonPhase.QUALIFIERSPREP);
		s.createDivisions(teams.size(), 2, 6);

		assertEquals(4, s.getDivisions().size());
		assertEquals(5, s.getSubdivisionCount());
		assertEquals(1, s.getDivisions().get(0).getSubDivisions().size());
		assertEquals(2, s.getDivisions().get(1).getSubDivisions().size());
		assertEquals(1, s.getDivisions().get(2).getSubDivisions().size());
		assertEquals(1, s.getDivisions().get(3).getSubDivisions().size());
	}
	
	@Test
	public void testCreateDivisions24Players() throws SeasonPhaseException {
		setUpTeams(24);

		s.setCurrentPhase(SeasonPhase.PREPARATION);
		s.sortTeams(teams);
		s.setTeams(teams);
		s.setCurrentPhase(SeasonPhase.QUALIFIERSPREP);
		s.createDivisions(teams.size(), 2, 6);

		assertEquals(3, s.getDivisions().size());
		assertEquals(1, s.getDivisions().get(0).getSubDivisions().size());
		assertEquals(2, s.getDivisions().get(1).getSubDivisions().size());
		assertEquals(1, s.getDivisions().get(2).getSubDivisions().size());
	}

	@Test
	public void testDistributeTeamsTenTeams() throws SeasonPhaseException {
		setUpTeams(10);

		s.setCurrentPhase(SeasonPhase.PREPARATION);
		s.sortTeams(teams);
		s.setTeams(teams);
		s.setCurrentPhase(SeasonPhase.QUALIFIERSPREP);
		s.createDivisions(teams.size(), 2, 6);
		assertEquals(2, s.getDivisions().size());
		assertEquals(1, s.getDivisions().get(0).getSubDivisions().size());
		assertEquals(1, s.getDivisions().get(1).getSubDivisions().size());

		s.setCurrentPhase(SeasonPhase.SEASONPREP);
		s.distributeTeams(teams);
		assertEquals(5, s.getDivisions().get(0).getSubDivisions().get(0).getTeams().size());
		assertEquals(5, s.getDivisions().get(1).getSubDivisions().get(0).getTeams().size());
	}

	@Test
	public void testCreateDivisions24PlayersDistribute() throws SeasonPhaseException {
		testCreateDivisions24Players();
		s.setCurrentPhase(SeasonPhase.SEASONPREP);

		s.distributeTeams(teams);
		
		assertEquals(6, s.getDivisions().get(0).getSubDivisions().get(0).getTeams().size());
		assertEquals(6, s.getDivisions().get(1).getSubDivisions().get(0).getTeams().size());
		assertEquals(6, s.getDivisions().get(1).getSubDivisions().get(1).getTeams().size());
		assertEquals(6, s.getDivisions().get(2).getSubDivisions().get(0).getTeams().size());
	}

	private void assertAllExpectedGamesAreScheduled() throws SeasonPhaseException {
		for (Team t : s.getTeams()) {
			Subdivision sd = t.getCurrentSeason().getSubDivision();
			ArrayList<TeamSeasonStats> subdivisionMembersRemaining = (ArrayList<TeamSeasonStats>) sd.getTeams().clone();
			for (GameRound gr : sd.getGameRounds()) {
				for (Game game : gr.getGames()){
					TeamSeasonStats opponent = null;
					if (game.getHomeTeam() == t.getCurrentSeason()) {
						opponent = game.getAwayTeam();
					} else if (game.getAwayTeam() == t.getCurrentSeason()) {
						opponent = game.getHomeTeam();
					}
					if (opponent != null) {
						boolean foundOpponent = false;
						for (int i = 0; i < subdivisionMembersRemaining.size(); i ++) {
							TeamSeasonStats tss = subdivisionMembersRemaining.get(i);
							if (tss == opponent) {
								subdivisionMembersRemaining.remove(i);
								foundOpponent = true;
								break;
							}
						}
						assertTrue(foundOpponent);
					}
				}
			}
	
			assertEquals(1,subdivisionMembersRemaining.size());
			assertEquals(t.getCurrentSeason(), subdivisionMembersRemaining.get(0));
		}
	}

	@Test
	public void testCreateDivisions24PlayersGenerateGames() throws SeasonPhaseException {
		testCreateDivisions24PlayersDistribute();

		s.setCurrentPhase(SeasonPhase.SEASONPREP);
		s.generateGames();
		
		assertAllExpectedGamesAreScheduled();
	}

	@Test
	public void testCreateDivisions27PlayersGenerateGames() throws SeasonPhaseException {
		setUpTeams(30);

		s.sortTeams(teams);
		s.setTeams(teams);
		s.setCurrentPhase(SeasonPhase.QUALIFIERSPREP);
		s.createDivisions(teams.size(), 2, 6);
		s.setCurrentPhase(SeasonPhase.SEASONPREP);
		s.distributeTeams(teams);
		s.generateGames();
		
		assertAllExpectedGamesAreScheduled();
	}
}
