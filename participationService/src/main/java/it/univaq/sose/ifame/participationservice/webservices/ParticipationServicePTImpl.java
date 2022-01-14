package it.univaq.sose.ifame.participationservice.webservices;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import it.univaq.sose.ifame.participationservice.business.ParticipationService;
import it.univaq.sose.participationservice.EventListParticipationRequest;
import it.univaq.sose.participationservice.EventListParticipationResponse;
import it.univaq.sose.participationservice.EventParticipationRequest;
import it.univaq.sose.participationservice.EventParticipationResponse;
import it.univaq.sose.participationservice.ParticipationListRequest;
import it.univaq.sose.participationservice.ParticipationListResponse;
import it.univaq.sose.participationservice.ParticipationRequest;
import it.univaq.sose.participationservice.ParticipationResponse;
import it.univaq.sose.participationservice.ParticipationServicePT;
import it.univaq.sose.participationservice.ParticipationUpdateRequest;

@Component(value="ParticipationServicePTImpl")
public class ParticipationServicePTImpl implements ParticipationServicePT {

	private static Logger LOGGER = LoggerFactory.getLogger(ParticipationServicePTImpl.class);

	@Autowired
	private ParticipationService service;
	
	@Override
	public EventParticipationResponse eventParticipation(EventParticipationRequest parameters) {
		LOGGER.info("[ParticipationServicePTImpl]::eventParticipation");
		EventParticipationResponse response = service.getEventParticipation(parameters);
		return response;
	}

	@Override
	public EventListParticipationResponse eventListParticipation(EventListParticipationRequest parameters) {
		LOGGER.info("[ParticipationServicePTImpl]::eventListParticipation");
		
		return service.getEventListParticipation(parameters);
	}
	
	@Override
	public ParticipationListResponse participationList(ParticipationListRequest parameters) {
		LOGGER.info("[ParticipationServicePTImpl]::participationList");
		return service.getParticipations(parameters);
	}
	
	@Override
	public ParticipationResponse joinEvent(ParticipationRequest parameters) {
    	LOGGER.info("[ParticipationServicePTImpl]::joinEvent");	
    	service.joinEvent(parameters);
		return  new ParticipationResponse();
	}

	@Override
	public ParticipationResponse removeEventParticipation(ParticipationRequest parameters) {
    	LOGGER.info("[ParticipationServicePTImpl]::removeEventParticipation");	
    	service.removeEventParticipation(parameters);
    	ParticipationResponse result = new ParticipationResponse();		
		return result;

	}

	@Override
	public void updateParticipations(ParticipationUpdateRequest parameters) {
		LOGGER.info("[ParticipationServicePTImpl]::updateParticipations");
		service.updateParticipations(parameters);
	}

}
