package ml.davvs.tourn.model;

public enum SeasonPhase {
	PREPARATION, //Teams register. New teams need to run placement matches here
	QUALIFIERSPREP, //Registration closed. Committee will decide setup of qualifiers.
	QUALIFIERS, //Qualfiers getting played
	SEASONPREP, //Season setup is prepared, games are generated
	SEASON, //Games in the season is played
	PLAYOFFPREP, //The playoff is prepared. Players registering for the playoff may join
	PLAYOFF, //The playoff is being played
	FINISHED //Season is finished
;

	public static void assertPhase(SeasonPhase currentPhase, SeasonPhase requiredPhase) throws SeasonPhaseException {
		if (currentPhase != requiredPhase){
			throw new SeasonPhaseException(currentPhase, requiredPhase);
		}
	}
}
