package ml.davvs.tourn.model;

public class SeasonPhaseException extends Exception {
	private static final long serialVersionUID = 1L;

	private SeasonPhase current;
	private SeasonPhase required;
	public SeasonPhaseException(SeasonPhase current, SeasonPhase required){
		this.current = current;
		this.required = required;
	}

	public String getMessage() {
		return "Phase " + required.toString() + " required, but current phase is " + current.toString();
	}
	
}
