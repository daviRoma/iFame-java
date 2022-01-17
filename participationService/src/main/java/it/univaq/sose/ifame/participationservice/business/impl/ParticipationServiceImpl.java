package it.univaq.sose.ifame.participationservice.business.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.univaq.sose.ifame.participationservice.business.ParticipationDAO;
import it.univaq.sose.ifame.participationservice.business.ParticipationService;
import it.univaq.sose.ifame.participationservice.model.Participation;
import it.univaq.sose.participationservice.EventListParticipationRequest;
import it.univaq.sose.participationservice.EventListParticipationResponse;
import it.univaq.sose.participationservice.EventParticipationRequest;
import it.univaq.sose.participationservice.EventParticipationResponse;
import it.univaq.sose.participationservice.ParticipationDetail;
import it.univaq.sose.participationservice.ParticipationListRequest;
import it.univaq.sose.participationservice.ParticipationListResponse;
import it.univaq.sose.participationservice.ParticipationRequest;
import it.univaq.sose.participationservice.ParticipationUpdateRequest;

@Service
public class ParticipationServiceImpl implements ParticipationService {
	
	private static Logger LOGGER = LoggerFactory.getLogger(ParticipationServiceImpl.class);
	
	@Autowired
	private ParticipationDAO participationDao;
	
	@Override
	public EventParticipationResponse getEventParticipation(EventParticipationRequest request) {
		LOGGER.info("[ParticipationServiceImpl]::[getEventParticipation] -- Event Id: " + request.getEventId());
		Participation participation = participationDao.find(request.getEventId());
		
		EventParticipationResponse response = new EventParticipationResponse();
		
		if (participation != null) {
			ParticipationDetail pa = new ParticipationDetail();
			pa.setEventId(participation.getEventId());
			pa.setParticipants(participation.getParticipationIds());			
			response.setParticipationDetail(pa);
		}		
		
		return response;
	}
	
	@Override
	public EventListParticipationResponse getEventListParticipation(EventListParticipationRequest request) {
		LOGGER.info("[ParticipationServiceImpl]::[getEventListParticipation] -- " + request.getUsername());
		EventListParticipationResponse response = new EventListParticipationResponse();
		response.setEventIds(participationDao.find(request.getUsername()));
		
		return response;
	}
	
	@Override
	public ParticipationListResponse getParticipations(ParticipationListRequest request) {
		LOGGER.info("[ParticipationServiceImpl]::[getParticipations] -- " + request.toString());
		
		List<Participation> participations = participationDao.find(request.getEventIds());
		List<ParticipationDetail> participationsDetail = new ArrayList<ParticipationDetail>();
		
		if (participations != null) {
			for (Participation pa : participations) {
				ParticipationDetail paDet = new ParticipationDetail();
				paDet.setEventId(pa.getEventId());
				paDet.setParticipants(pa.getParticipationIds());
				participationsDetail.add(paDet);
			}
		}
		
		ParticipationListResponse response = new ParticipationListResponse();
		response.setParticipations(participationsDetail);
		return response;
	}

	@Override
	public void joinEvent(ParticipationRequest request) {
		LOGGER.info("[ParticipationServiceImpl]::[joinEvent] -- " + request.toString());
		
		Participation participation = new Participation();
		participation.setEventId(request.getEventId());
		participation.setParticipationIds(new ArrayList<String>(Arrays.asList(request.getParticipant())));
		participationDao.insert(participation);
	}

	@Override
	public void removeEventParticipation(ParticipationRequest request) {
		LOGGER.info("[ParticipationServiceImpl]::[removeEventParticipation] -- " + request.toString());
		
		Participation participation = new Participation();
		participation.setEventId(request.getEventId());
		participation.setParticipationIds(new ArrayList<String>(Arrays.asList(request.getParticipant())));
		participationDao.delete(participation);
	}

	@Override
	public void updateParticipations(ParticipationUpdateRequest request) {
		LOGGER.info("[ParticipationServiceImpl]::[updateParticipations] -- " + request.getOldUsername() + "to -> " + request.getNewUsername());
		participationDao.update(request.getNewUsername(), request.getOldUsername());
	}

}
