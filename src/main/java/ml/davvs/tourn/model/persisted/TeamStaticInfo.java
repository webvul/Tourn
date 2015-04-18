package ml.davvs.tourn.model.persisted;

import java.util.UUID;

public class TeamStaticInfo {
	private UUID id;
	private String name;
	private String email;

	TeamStaticInfo(String name, String email) {
		setId(UUID.randomUUID());
		this.name = name;
		this.email = email;
	}
	public UUID getId() {
		return id;
	}
	private void setId(UUID id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
}
