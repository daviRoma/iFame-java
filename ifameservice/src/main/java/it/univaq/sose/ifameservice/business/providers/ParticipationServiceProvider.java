package it.univaq.sose.ifameservice.business.providers;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import it.univaq.sose.ifameservice.helpers.RestResourceHelper;
import it.univaq.sose.ifameservice.model.Participation;
import it.univaq.sose.participationservice.EventListParticipationRequest;
import it.univaq.sose.participationservice.EventListParticipationResponse;
import it.univaq.sose.participationservice.EventParticipationRequest;
import it.univaq.sose.participationservice.EventParticipationResponse;
import it.univaq.sose.participationservice.ParticipationListRequest;
import it.univaq.sose.participationservice.ParticipationListResponse;
import it.univaq.sose.participationservice.ParticipationRequest;
import it.univaq.sose.participationservice.ParticipationService;
import it.univaq.sose.participationservice.ParticipationServicePT;
import it.univaq.sose.participationservice.ParticipationUpdateRequest;


@Service
public class ParticipationServiceProvider {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ParticipationServiceProvider.class);

	ParticipationService participationService = new ParticipationService();
	ParticipationServicePT participationServicePT = participationService.getParticipationServicePort();
	
	@Async
	public CompletableFuture<Participation> getEventParticipations(Integer eventId) {
		LOGGER.info("[ParticipationServiceProvider]::[getEventParticipations]::CALL ParticipantionService --- getEventParticipation");
		
		EventParticipationRequest participationRequest = new EventParticipationRequest();
		participationRequest.setEventId(eventId);
		
		EventParticipationResponse response = participationServicePT.eventParticipation(participationRequest);
		
		LOGGER.info("[ParticipationServiceProvider]::[getEventParticipations]::Returning ParticipationService response");
		
		Participation participation = RestResourceHelper.participationResponseMapping(response);

		return CompletableFuture.completedFuture(participation);
	}
	
	public List<Integer> getMyJoinedEventParticipations(String username) {
		LOGGER.info("[ParticipationServiceProvider]::[getMyJoinedEventParticipations]::CALL ParticipantionService --- getEventListParticipations");
		
		EventListParticipationRequest participationRequest = new EventListParticipationRequest();
		participationRequest.setUsername(username);
		
		EventListParticipationResponse response = participationServicePT.eventListParticipation(participationRequest);
		
		LOGGER.info("[ParticipationServiceProvider]::[getMyJoinedEventParticipations]::Returning ParticipationService response");
		
		return response.getEventIds();
	}
	
	public List<Participation> getParticipations(List<Integer> eventIds) {
		LOGGER.info("[ParticipationServiceProvider]::[getParticipations]::CALL ParticipantionService --- getParticipationList");
		
		ParticipationListRequest request = new ParticipationListRequest();
		request.setEventIds(eventIds);

		ParticipationListResponse response = participationServicePT.participationList(request);
		
		LOGGER.info("[ParticipationServiceProvider]::[getParticipations]::Returning ParticipationService response");
		
		return RestResourceHelper.participationDetailToParticipationMapping(response.getParticipations());
	}

	public void addParticipation(Integer eventId, String username) {
		LOGGER.info("[ParticipationServiceProvider]::[addParticipation]::CALL ParticipantionService --- joinEvent");
		
		ParticipationRequest participationRequest = new ParticipationRequest();
		participationRequest.setEventId(eventId);
		participationRequest.setParticipant(username);
		
		participationServicePT.joinEvent(participationRequest);
		
		LOGGER.info("[ParticipationServiceProvider]::[addParticipation]::DONE");
	}
	
	public void removeParticipation(Integer eventId, String username) {
		LOGGER.info("[ParticipationServiceProvider]::[removeParticipation]::CALL ParticipantionService --- removeEventParticipation");
		
		ParticipationRequest participationRequest = new ParticipationRequest();
		participationRequest.setEventId(eventId);
		participationRequest.setParticipant(username);
		
		participationServicePT.removeEventParticipation(participationRequest);
		
		LOGGER.info("[ParticipationServiceProvider]::[removeParticipation]::DONE");
	}
	
	public void updateParticipation(String newUsername, String oldUsername) {
		LOGGER.info("[ParticipationServiceProvider]::[updateParticipation]::CALL ParticipantionService --- updateParticipations");
		
		ParticipationUpdateRequest request = new ParticipationUpdateRequest();
		request.setNewUsername(newUsername);
		request.setOldUsername(oldUsername);
		
		participationServicePT.updateParticipations(request);
		
		LOGGER.info("[ParticipationServiceProvider]::[updateParticipation]::DONE");
	}
}
