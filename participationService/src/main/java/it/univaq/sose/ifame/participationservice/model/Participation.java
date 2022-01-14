package it.univaq.sose.ifame.participationservice.model;

import java.util.List;

public class Participation {

	private Integer eventId;
	private List<String> participants;
	
	public Integer getEventId() {
		return this.eventId;
	}
	
	public void setEventId(Integer eventId) {
		this.eventId = eventId;
	}
	
	public List<String> getParticipationIds() {
		return this.participants;
	}
	
	public void setParticipationIds(List<String> participants) {
		this.participants = participants;
	}
	
}
