package it.univaq.sose.ifame.participationservice.business;

import it.univaq.sose.participationservice.EventListParticipationRequest;
import it.univaq.sose.participationservice.EventListParticipationResponse;
import it.univaq.sose.participationservice.EventParticipationRequest;
import it.univaq.sose.participationservice.EventParticipationResponse;
import it.univaq.sose.participationservice.ParticipationListRequest;
import it.univaq.sose.participationservice.ParticipationListResponse;
import it.univaq.sose.participationservice.ParticipationRequest;
import it.univaq.sose.participationservice.ParticipationUpdateRequest;

public interface ParticipationService {

	EventParticipationResponse getEventParticipation(EventParticipationRequest request);
	EventListParticipationResponse getEventListParticipation(EventListParticipationRequest request);
	ParticipationListResponse getParticipations(ParticipationListRequest request);
	void joinEvent(ParticipationRequest request);
	void removeEventParticipation(ParticipationRequest request);
	void updateParticipations(ParticipationUpdateRequest request);
	
}
