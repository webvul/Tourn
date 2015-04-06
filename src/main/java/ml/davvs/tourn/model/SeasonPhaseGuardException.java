package ml.davvs.tourn.model;

public class SeasonPhaseGuardException extends Exception {
	private static final long serialVersionUID = 1L;

	public SeasonPhaseGuardException(String message){
		super(message);
	}

	public SeasonPhaseGuardException(String message, Throwable previous) {
		super(message, previous);
	}
}
