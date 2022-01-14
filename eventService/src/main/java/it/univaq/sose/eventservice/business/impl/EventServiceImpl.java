package it.univaq.sose.eventservice.business.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.univaq.sose.eventservice.CreateEventResponse;
import it.univaq.sose.eventservice.EventCreationRequest;
import it.univaq.sose.eventservice.EventDetails;
import it.univaq.sose.eventservice.EventListRequest;
import it.univaq.sose.eventservice.EventRemoveRequest;
import it.univaq.sose.eventservice.EventUpdateRequest;
import it.univaq.sose.eventservice.FoodCategoriesResponse;
import it.univaq.sose.eventservice.FoodCategory;
import it.univaq.sose.eventservice.SingleEventRequest;
import it.univaq.sose.eventservice.business.EventDAO;
import it.univaq.sose.eventservice.business.EventService;

@Service
public class EventServiceImpl implements EventService {

	private static Logger LOGGER = LoggerFactory.getLogger(EventServiceImpl.class);

	@Autowired
	private EventDAO eventDao;
	
	@Override
	public List<EventDetails> getEventList(EventListRequest request) {
		LOGGER.info("[EventServiceImpl]::[getEventList]" + request);
		
		if (!request.getEventIds().isEmpty()) return eventDao.find(request.getEventIds());
		
		return eventDao.find(request.getOwnerId(), request.getLatitude(), request.getLongitude(), request.getEventDate(), request.getFoodCategories());
	}

	@Override
	public Integer createEvent(EventCreationRequest request) {
		LOGGER.info("[EventServiceImpl]::[createEvent]");
		return eventDao.insert(request.getEvent());
	}

	@Override
	public void updateEvent(EventUpdateRequest request) {
		LOGGER.info("[EventServiceImpl]::[updateEvent]");
		eventDao.update(request.getEvent());
	}

	@Override
	public void removeEvent(EventRemoveRequest request) {
		LOGGER.info("[EventServiceImpl]::[removeEvent]");
		eventDao.delete(request.getEventId());
	}

	@Override
	public EventDetails getEvent(SingleEventRequest request) {
		LOGGER.info("[EventServiceImpl]::[getEvent]");
		return eventDao.find(request.getEventId());
	}

	@Override
	public List<FoodCategory> getFoodCategories() {
		LOGGER.info("[EventServiceImpl]::[getFoodCategories]");
		return eventDao.findCategories();
	}

}
