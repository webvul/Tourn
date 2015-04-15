package ml.davvs.tourn.model;

import java.util.ArrayList;

import ml.davvs.tourn.model.persisted.Division;
import ml.davvs.tourn.model.persisted.QualifierGroup;
import ml.davvs.tourn.model.persisted.Season;
import ml.davvs.tourn.model.persisted.TeamSeasonStats;

public class SeasonPhaseGuard {
	private Season season;
	
	public SeasonPhaseGuard(Season season){
		this.season = season;
	}
	
	public void assertSwitchPhaseIsOK(SeasonPhase nextPhase) throws SeasonPhaseGuardException {
		int to = getPhaseOrder(nextPhase);
		int from = getPhaseOrder(season.getCurrentPhase());
		if (to < from){
			throw new SeasonPhaseGuardException("Trying to decrement phase (Not supported yet)!");
		}
		if (to - from == 0){
			throw new SeasonPhaseGuardException("Trying to reset current state!");
		}
		if (to - from != 1){
			throw new SeasonPhaseGuardException("Trying to increment more than 1 phase! from:" + season.getCurrentPhase() + " to " + nextPhase);
		}
		if (nextPhase == SeasonPhase.QUALIFIERS) {
			for (int d = 0; d < season.getDivisions().size() - 1; d++) {
				Division division = season.getDivisions().get(d);
				
				QualifierGroup qualifierGroup;
				try {
					qualifierGroup = division.getLowerQualifierGroup();
				} catch (QualifierGroupException e) {
					throw new SeasonPhaseGuardException("Failed to get lower qualfier group", e);
				}
				
				ArrayList<TeamSeasonStats> teams = qualifierGroup.getTeams();
				for (TeamSeasonStats t : teams) {
					if (t.getQualifierGroup() != qualifierGroup) {
						throw new SeasonPhaseGuardException("Team " + t.getTeam().getStaticInfo().getName() + " has inconsistent Qualifier Group");
					}
				}
				if (qualifierGroup.getCountTeamsLower() + qualifierGroup.getCountTeamsUpper() > 0) {
					if (qualifierGroup.getCountTeamsUpper() <= 0) {
						throw new SeasonPhaseGuardException("Qualifier group lack teams in upper division");
					}
					if (qualifierGroup.getCountTeamsLower() <= 0) {
						throw new SeasonPhaseGuardException("Qualifier group lack teams in lower division");
					}
				}
			}
		} else if (nextPhase == SeasonPhase.SEASONPREP) {
			for (int d = 0; d < season.getDivisions().size() - 1; d++) {
				Division division = season.getDivisions().get(d);
				QualifierGroup qualifierGroup;
				try {
					qualifierGroup = division.getLowerQualifierGroup();
				} catch (QualifierGroupException e) {
					throw new SeasonPhaseGuardException("Failed to get lower qualfier group", e);
				}

				if (qualifierGroup.getTeams().size() > 0){
					if (!qualifierGroup.isFinished()) {
						throw new SeasonPhaseGuardException("Qualifier group " + qualifierGroup.getName() + " is not completed");
					} else if (qualifierGroup.isStartedPlaying()) {
						// TODO Make sure we verify that the qualfier group is resolved/played
						throw new SeasonPhaseGuardException("Qualifier group " + qualifierGroup.getName() + " is not played");
					}
				}
			}
		}
	}
	
	private int getPhaseOrder(SeasonPhase phase){
		switch (phase){
			case PREPARATION: return 0;
			case QUALIFIERSPREP: return 1;
			case QUALIFIERS: return 2;
			case SEASONPREP: return 3;
			case SEASON: return 4;
			case PLAYOFFPREP: return 5;
			case PLAYOFF: return 6;
			case FINISHED: return 7;
			default:
				throw new RuntimeException("Invalid SeasonPhase " + phase);
		}
	}
}
