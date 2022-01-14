package it.univaq.sose.ifameservice.model;

import java.io.Serializable;
import java.util.List;

public class Participation implements Serializable {

	private Integer eventId;
	private List<String> participants;
	
	public Integer getEventId() {
		return eventId;
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
