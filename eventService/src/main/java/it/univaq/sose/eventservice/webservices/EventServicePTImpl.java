package it.univaq.sose.eventservice.webservices;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import it.univaq.sose.eventservice.CreateEventResponse;
import it.univaq.sose.eventservice.EventCreationRequest;
import it.univaq.sose.eventservice.EventListRequest;
import it.univaq.sose.eventservice.EventListResponse;
import it.univaq.sose.eventservice.EventRemoveRequest;
import it.univaq.sose.eventservice.EventServicePT;
import it.univaq.sose.eventservice.EventUpdateRequest;
import it.univaq.sose.eventservice.FoodCategoriesResponse;
import it.univaq.sose.eventservice.SingleEventRequest;
import it.univaq.sose.eventservice.SingleEventResponse;
import it.univaq.sose.eventservice.business.EventService;

@Component(value="EventServicePTImpl")
public class EventServicePTImpl implements EventServicePT {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(EventServicePTImpl.class);

	@Autowired
	private EventService service;
	
	@Override
	public CreateEventResponse eventCreation(EventCreationRequest parameters) {
    	LOGGER.info("[EventServicePTImpl]::eventCreation");	
    	CreateEventResponse response = new CreateEventResponse();
    	response.setEventId(service.createEvent(parameters));
    	return response;
	}

	@Override
	public void eventUpdate(EventUpdateRequest parameters) {
		LOGGER.info("[EventServicePTImpl]::eventUpdate");	
		service.updateEvent(parameters);
	}

	@Override
	public void eventRemove(EventRemoveRequest parameters) {
		LOGGER.info("[EventServicePTImpl]::eventRemove");
		service.removeEvent(parameters);
	} 

	@Override
	public SingleEventResponse eventDetails(SingleEventRequest parameters) {
		LOGGER.info("[EventServicePTImpl]::eventDetails");
		SingleEventResponse result = new SingleEventResponse();
		result.setEvent(service.getEvent(parameters));
		return result;
	}
	
	@Override
	public EventListResponse eventList(EventListRequest parameters) {
		LOGGER.info("[EventServicePTImpl]::eventList");	
		EventListResponse result = new EventListResponse();
		result.setEvents(service.getEventList(parameters));
		return result;
	}

	@Override
	public FoodCategoriesResponse allFoodCategories() {
		LOGGER.info("[EventServicePTImpl]::allFoodCategories");	
		FoodCategoriesResponse response = new FoodCategoriesResponse();
		response.setFoodCategories(service.getFoodCategories());
		return response;
	}

}
