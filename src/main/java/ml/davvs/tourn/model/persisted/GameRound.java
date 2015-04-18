package ml.davvs.tourn.model.persisted;

import java.util.ArrayList;
import java.util.UUID;

public class GameRound {
	private ArrayList<Game> games;
	private UUID id;

	public GameRound() {
		setId(UUID.randomUUID());
	}
	public ArrayList<Game> getGames() {
		return games;
	}

	public void setGames(ArrayList<Game> games) {
		this.games = games;
	}
	public UUID getId() {
		return id;
	}
	public void setId(UUID id) {
		this.id = id;
	}
	
	
}
