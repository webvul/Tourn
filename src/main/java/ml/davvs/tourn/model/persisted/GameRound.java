package ml.davvs.tourn.model.persisted;

import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

public class GameRound {
	private ArrayList<Game> games;
	private UUID id;
	private Date gameStart;
	private int gameIntervalLengthDays;
	
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
	public int getGameIntervalLengthDays() {
		return gameIntervalLengthDays;
	}
	public void setGameIntervalLengthDays(int gameIntervalLengthDays) {
		this.gameIntervalLengthDays = gameIntervalLengthDays;
	}
	public Date getGameStart() {
		return gameStart;
	}
	public void setGameStart(Date gameStart) {
		this.gameStart = gameStart;
	}
	
	
}
