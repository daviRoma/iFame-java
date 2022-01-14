package it.univaq.sose.ifameservice.business.providers;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import it.univaq.sose.eventservice.CreateEventResponse;
import it.univaq.sose.eventservice.EventCreationRequest;
import it.univaq.sose.eventservice.EventListRequest;
import it.univaq.sose.eventservice.EventListResponse;
import it.univaq.sose.eventservice.EventRemoveRequest;
import it.univaq.sose.eventservice.EventService;
import it.univaq.sose.eventservice.EventServicePT;
import it.univaq.sose.eventservice.EventUpdateRequest;
import it.univaq.sose.eventservice.FoodCategoriesResponse;
import it.univaq.sose.eventservice.FoodCategory;
import it.univaq.sose.eventservice.SingleEventRequest;
import it.univaq.sose.eventservice.SingleEventResponse;
import it.univaq.sose.ifameservice.helpers.RestResourceHelper;
import it.univaq.sose.ifameservice.model.Event;

@Service
public class EventServiceProvider {

	private static final Logger LOGGER = LoggerFactory.getLogger(EventServiceProvider.class);

	EventService eventService = new EventService();
	EventServicePT eventServicePT = eventService.getEventServicePort();

	/**
	 * Get event from EventService
	 * @param request
	 * @return List<Event>
	 */
	@Async
	public CompletableFuture<Event> getEvent(Integer eventId) {
		LOGGER.info("[EventServiceProvider]::[getEvent]::CALL EventService --- eventDetails");
		
		SingleEventRequest request = new SingleEventRequest();
		request.setEventId(eventId);
		
		SingleEventResponse response = eventServicePT.eventDetails(request);
		LOGGER.info("[EventServiceProvider]::[getEvent]::EventService response: " + response.toString());
		
		Event evt = RestResourceHelper.buildEvent(response.getEvent());
		return CompletableFuture.completedFuture(evt);
	}
	
	/**
	 * Get events from EventService
	 * @param request
	 * @return List<Event>
	 */
	public List<Event> getEvents(EventListRequest request) {
		LOGGER.info("[EventServiceProvider]::[getEvents]::CALL EventService --- getEvents");
		
		EventListResponse response = eventServicePT.eventList(request);
		LOGGER.info("[EventServiceProvider]::[getEvents]::EventService response");
		
		return RestResourceHelper.eventsResponseMapping(response.getEvents());
	}
	
	/**
	 * Get my joined events from EventService
	 * @param request
	 * @return List<Event>
	 */
	public List<Event> getMyJoinedEvents(List<Integer> eventIds) {
		LOGGER.info("[EventServiceProvider]::[getMyJoinedEvents]::CALL EventService --- getEvents");
		EventListRequest request = new EventListRequest();
		request.setEventIds(eventIds);
		
		EventListResponse response = eventServicePT.eventList(request);
		LOGGER.info("[EventServiceProvider]::[getMyJoinedEvents]::EventService response");
		
		return RestResourceHelper.eventsResponseMapping(response.getEvents());
	}
	
	/**
	 * Create events
	 * @param request
	 */
	public Integer createEvent(EventCreationRequest request) {
		LOGGER.info("[EventServiceProvider]::[createEvent]::CALL EventService --- eventCreation");
		
		CreateEventResponse response = eventServicePT.eventCreation(request);
		LOGGER.info("[EventServiceProvider]::[createEvent]::Response --- EventId --- " + response.getEventId());
		
		return response.getEventId();
	}
	
	/**
	 * Get Food Categories
	 */
	public List<FoodCategory> getFoodCategories() {
		LOGGER.info("[EventServiceProvider]::[getFoodCategories]::CALL EventService --- allFoodCategories");
		
		FoodCategoriesResponse response = eventServicePT.allFoodCategories();
		LOGGER.info("[EventServiceProvider]::[getFoodCategories]::Response --> " + response.getFoodCategories());
		
		return response.getFoodCategories();
	}
	
	/**
	 * Update events
	 * @param request
	 */
	public void updateEvent(EventUpdateRequest request) {
		LOGGER.info("[EventServiceProvider]::[updateEvent]::CALL EventService --- eventUpdate");
		
		eventServicePT.eventUpdate(request);
		LOGGER.info("[EventServiceProvider]::[updateEvent]::Response");
	}
	
	/**
	 * Update events
	 * @param request
	 */
	public void removeEvent(EventRemoveRequest request) {
		LOGGER.info("[EventServiceProvider]::[removeEvent]::CALL EventService --- eventRemove");
		
		eventServicePT.eventRemove(request);
		LOGGER.info("[EventServiceProvider]::[removeEvent]::Response");
	}
	
}
