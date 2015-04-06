package ml.davvs.tourn.test;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ml.davvs.tourn.model.Game;
import ml.davvs.tourn.model.GameRound;
import ml.davvs.tourn.model.QualifierGroup;
import ml.davvs.tourn.model.QualifierGroupException;
import ml.davvs.tourn.model.Season;
import ml.davvs.tourn.model.SeasonPhase;
import ml.davvs.tourn.model.SeasonPhaseGuardException;
import ml.davvs.tourn.model.SeasonPhaseRequiredException;
import ml.davvs.tourn.model.Subdivision;
import ml.davvs.tourn.model.Team;
import ml.davvs.tourn.model.TeamSeasonStats;
import ml.davvs.tourn.model.Tournament;
import ml.davvs.tourn.view.ConsoleOut;

public class SeasonTest {
	private Season s;
	private Tournament tournament;
	private ArrayList<TeamSeasonStats> teams;

	@Before
	public void setUp() throws Exception {
		s = new Season();
		tournament = new Tournament();
		tournament.setName("Test Tournament");
		s.setTournament(tournament);
		teams = new ArrayList<TeamSeasonStats>();
	}

	@After
	public void tearDown() throws Exception {
	}
	
	private void setUpTeams(final int teamCount) {
		for (int t = 0; t < teamCount; t++) {
			Team team = new Team("team" + t, "team" + t + "@example.com", 0.5f * t);
			TeamSeasonStats ts = new TeamSeasonStats();
			ts.setTeam(team);
			teams.add(ts);
		}
	}

	@Test
	public void testCreateDivisionsTenPlayers() throws SeasonPhaseRequiredException, SeasonPhaseGuardException {
		setUpTeams(10);

		s.sortTeams(teams);
		s.setTeams(teams);
		s.setCurrentPhase(SeasonPhase.QUALIFIERSPREP);
		s.createDivisions(teams.size(), 2, 6);
		
		assertEquals(1, s.getDivisions().size());
		assertEquals(1, s.getDivisions().get(0).getSubDivisions().size());
	}

	@Test
	public void testCreateDivisions30Players() throws SeasonPhaseRequiredException, SeasonPhaseGuardException {
		setUpTeams(30);

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
	public void testCreateDivisions24Players() throws SeasonPhaseRequiredException, SeasonPhaseGuardException {
		setUpTeams(24);

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
	public void testCreateDivisions27Players8Size() throws SeasonPhaseRequiredException, SeasonPhaseGuardException {
		setUpTeams(27);

		s.sortTeams(teams);
		s.setTeams(teams);
		s.setCurrentPhase(SeasonPhase.QUALIFIERSPREP);
		s.createDivisions(teams.size(), 2, 8);

		assertEquals(3, s.getDivisions().size());
		assertEquals(1, s.getDivisions().get(0).getSubDivisions().size());
		assertEquals(1, s.getDivisions().get(1).getSubDivisions().size());
		assertEquals(1, s.getDivisions().get(2).getSubDivisions().size());
	}

	@Test
	public void testDistributeTeamsTenTeams() throws SeasonPhaseRequiredException, SeasonPhaseGuardException {
		setUpTeams(10);

		s.sortTeams(teams);
		s.setTeams(teams);
		s.setCurrentPhase(SeasonPhase.QUALIFIERSPREP);
		s.createDivisions(teams.size(), 2, 6);
		s.distributeTeams();
		assertEquals(1, s.getDivisions().size());
		assertEquals(1, s.getDivisions().get(0).getSubDivisions().size());

		s.setCurrentPhase(SeasonPhase.QUALIFIERS);

		s.setCurrentPhase(SeasonPhase.SEASONPREP);
		assertEquals(10, s.getDivisions().get(0).getSubDivisions().get(0).getTeams().size());
	}

	@Test
	public void testDistributeTeamsTenTeamsSubdivisionSizeMin3() throws SeasonPhaseRequiredException, SeasonPhaseGuardException {
		setUpTeams(10);

		s.sortTeams(teams);
		s.setTeams(teams);
		s.setCurrentPhase(SeasonPhase.QUALIFIERSPREP);
		s.createDivisions(teams.size(), 2, 3);
		s.distributeTeams();
		assertEquals(3, s.getDivisions().size());
		assertEquals(1, s.getDivisions().get(0).getSubDivisions().size());
		assertEquals(1, s.getDivisions().get(1).getSubDivisions().size());
		assertEquals(1, s.getDivisions().get(2).getSubDivisions().size());

		s.setCurrentPhase(SeasonPhase.QUALIFIERS);
		s.setCurrentPhase(SeasonPhase.SEASONPREP);
		assertEquals(4, s.getDivisions().get(0).getSubDivisions().get(0).getTeams().size());
		assertEquals(3, s.getDivisions().get(1).getSubDivisions().get(0).getTeams().size());
		assertEquals(3, s.getDivisions().get(2).getSubDivisions().get(0).getTeams().size());

	}

	@Test
	public void testCreateDivisions24PlayersDistribute() throws SeasonPhaseRequiredException, SeasonPhaseGuardException {
		testCreateDivisions24Players();
		s.distributeTeams();
		
		assertEquals(6, s.getDivisions().get(0).getSubDivisions().get(0).getTeams().size());
		assertEquals(6, s.getDivisions().get(1).getSubDivisions().get(0).getTeams().size());
		assertEquals(6, s.getDivisions().get(1).getSubDivisions().get(1).getTeams().size());
		assertEquals(6, s.getDivisions().get(2).getSubDivisions().get(0).getTeams().size());
	}

	private void assertAllExpectedGamesAreScheduled() throws SeasonPhaseRequiredException {
		for (TeamSeasonStats t : s.getTeams()) {
			Subdivision sd = t.getSubDivision();
			ArrayList<TeamSeasonStats> subdivisionMembersRemaining = (ArrayList<TeamSeasonStats>) sd.getTeams().clone();
			for (GameRound gr : sd.getGameRounds()) {
				for (Game game : gr.getGames()){
					TeamSeasonStats opponent = null;
					if (game.getHomeTeam() == t) {
						opponent = game.getAwayTeam();
					} else if (game.getAwayTeam() == t) {
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
			assertEquals(t, subdivisionMembersRemaining.get(0));
		}
	}

	@Test
	public void testCreateDivisions24PlayersGenerateGames() throws SeasonPhaseRequiredException, SeasonPhaseGuardException {
		testCreateDivisions24PlayersDistribute();

		s.setCurrentPhase(SeasonPhase.QUALIFIERS);
		s.setCurrentPhase(SeasonPhase.SEASONPREP);
		s.generateGames();
		
		assertAllExpectedGamesAreScheduled();
	}

	@Test
	public void testCreateDivisions27PlayersGenerateGames() throws SeasonPhaseRequiredException, SeasonPhaseGuardException {
		setUpTeams(30);

		s.sortTeams(teams);
		s.setTeams(teams);
		s.setCurrentPhase(SeasonPhase.QUALIFIERSPREP);
		s.createDivisions(teams.size(), 2, 6);
		s.distributeTeams();
		s.setCurrentPhase(SeasonPhase.QUALIFIERS);
		s.setCurrentPhase(SeasonPhase.SEASONPREP);
		s.generateGames();
		
		assertAllExpectedGamesAreScheduled();
	}
	
	@Test
	public void testQualifierGroupCoversOnlyOneDivision() throws SeasonPhaseRequiredException, SeasonPhaseGuardException, QualifierGroupException {
		setUpTeams(8);
		s.sortTeams(teams);
		s.setTeams(teams);
		s.setCurrentPhase(SeasonPhase.QUALIFIERSPREP);
		s.createDivisions(teams.size(), 2, 4);
		s.distributeTeams();
		QualifierGroup qualifierGroup = s.getDivisions().get(0).getLowerQualifierGroup();
		TeamSeasonStats t1 = s.getTeams().get(0);
		TeamSeasonStats t2 = s.getTeams().get(1);
		t1.setQualifierGroup(qualifierGroup);
		t2.setQualifierGroup(qualifierGroup);
		qualifierGroup.getTeams().add(t1);
		qualifierGroup.getTeams().add(t2);
		try {
			s.setCurrentPhase(SeasonPhase.QUALIFIERS);
		} catch (SeasonPhaseGuardException e) {
			if (e.getMessage().equalsIgnoreCase("Qualifier group lack teams in lower division")){
				return;
			}
			fail("Wrong exception called");
		}
		fail("Exception not thrown");
	}

	@Test
	public void testQualifierGroupCoversBothDivision() throws SeasonPhaseRequiredException, SeasonPhaseGuardException, QualifierGroupException {
		setUpTeams(8);
		s.sortTeams(teams);
		s.setTeams(teams);
		s.setCurrentPhase(SeasonPhase.QUALIFIERSPREP);
		s.createDivisions(teams.size(), 2, 4);
		s.distributeTeams();
		QualifierGroup qualifierGroup = s.getDivisions().get(0).getLowerQualifierGroup();
		TeamSeasonStats t1 = s.getTeams().get(3);
		TeamSeasonStats t2 = s.getTeams().get(4);
		t1.setQualifierGroup(qualifierGroup);
		t2.setQualifierGroup(qualifierGroup);
		qualifierGroup.getTeams().add(t1);
		qualifierGroup.getTeams().add(t2);
		s.setCurrentPhase(SeasonPhase.QUALIFIERS);
	}

	@Test
	public void testQualifierGroupCoversOnlyOneDivision2() throws SeasonPhaseRequiredException, SeasonPhaseGuardException, QualifierGroupException {
		setUpTeams(8);
		s.sortTeams(teams);
		s.setTeams(teams);
		s.setCurrentPhase(SeasonPhase.QUALIFIERSPREP);
		s.createDivisions(teams.size(), 2, 4);
		s.distributeTeams();
		QualifierGroup qualifierGroup = s.getDivisions().get(0).getLowerQualifierGroup();
		TeamSeasonStats t1 = s.getTeams().get(4);
		TeamSeasonStats t2 = s.getTeams().get(5);
		t1.setQualifierGroup(qualifierGroup);
		t2.setQualifierGroup(qualifierGroup);
		qualifierGroup.getTeams().add(t1);
		qualifierGroup.getTeams().add(t2);
		try {
			s.setCurrentPhase(SeasonPhase.QUALIFIERS);
		} catch (SeasonPhaseGuardException e) {
			if (e.getMessage().equalsIgnoreCase("Qualifier group lack teams in upper division")){
				return;
			}
			fail("Wrong exception called " + e.getMessage());
		}
		fail("Exception not thrown");
	}
	
	@Test (expected=QualifierGroupException.class)
	public void testQualifiersInvalidQualfierGroup() throws SeasonPhaseRequiredException, SeasonPhaseGuardException, QualifierGroupException {
		setUpTeams(16);
		s.sortTeams(teams);
		s.setTeams(teams);
		s.setCurrentPhase(SeasonPhase.QUALIFIERSPREP);
		s.createDivisions(teams.size(), 2, 4);
		s.distributeTeams();
		QualifierGroup qualifierGroup = s.getDivisions().get(0).getLowerQualifierGroup();
		s.getTeams().get(3).setQualifierGroup(qualifierGroup);
		s.getTeams().get(4).setQualifierGroup(qualifierGroup);
		
		s.getTeams().get(9).setQualifierGroup(qualifierGroup);
		s.getTeams().get(10).setQualifierGroup(qualifierGroup);

		s.getTeams().get(11).setQualifierGroup(qualifierGroup);
		s.getTeams().get(12).setQualifierGroup(qualifierGroup);

		s.setCurrentPhase(SeasonPhase.QUALIFIERS);
		
		ConsoleOut consoleOut = new ConsoleOut();
		consoleOut.printAllTeams(s);
	}
	
	@Test
	public void testQualifiersPrint() throws SeasonPhaseRequiredException, SeasonPhaseGuardException, QualifierGroupException {
		setUpTeams(16);
		s.sortTeams(teams);
		s.setTeams(teams);
		s.setCurrentPhase(SeasonPhase.QUALIFIERSPREP);
		s.createDivisions(teams.size(), 2, 4);
		s.distributeTeams();
		QualifierGroup qualifierGroup = s.getDivisions().get(0).getLowerQualifierGroup();
		s.getTeams().get(3).setQualifierGroup(qualifierGroup);
		s.getTeams().get(4).setQualifierGroup(qualifierGroup);
		
		QualifierGroup qualifierGroupBotton = s.getDivisions().get(1).getLowerQualifierGroup();

		s.getTeams().get(9).setQualifierGroup(qualifierGroupBotton);
		s.getTeams().get(10).setQualifierGroup(qualifierGroupBotton);

		s.getTeams().get(11).setQualifierGroup(qualifierGroupBotton);
		s.getTeams().get(12).setQualifierGroup(qualifierGroupBotton);

		s.setCurrentPhase(SeasonPhase.QUALIFIERS);
		
		ConsoleOut consoleOut = new ConsoleOut();
		consoleOut.printAllTeams(s);
	}

	@Test(expected=QualifierGroupException.class)
	public void testQualifiersQualifierGroupMissing() throws SeasonPhaseRequiredException, SeasonPhaseGuardException, QualifierGroupException {
		setUpTeams(16);
		s.sortTeams(teams);
		s.setTeams(teams);
		s.setCurrentPhase(SeasonPhase.QUALIFIERSPREP);
		s.createDivisions(teams.size(), 2, 4);
		s.distributeTeams();
		s.getDivisions().get(2).getLowerQualifierGroup();
	}
}
