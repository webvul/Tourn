package ml.davvs.tourn.model;

public class SeasonPhaseRequiredException extends Exception {
	private static final long serialVersionUID = 1L;

	private SeasonPhase current;
	private SeasonPhase required;
	public SeasonPhaseRequiredException(String message){
		super(message);
	}

	public SeasonPhaseRequiredException(SeasonPhase current, SeasonPhase required){
		this.current = current;
		this.required = required;
	}

	public String getMessage() {
		if (required != null) {
			return "Phase " + required.toString() + " required, but current phase is " + current.toString();
		} else {
			return super.getMessage();
		}
	}
}
