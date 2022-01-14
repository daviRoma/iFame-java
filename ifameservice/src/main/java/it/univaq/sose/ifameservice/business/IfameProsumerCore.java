package it.univaq.sose.ifameservice.business;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import javax.xml.datatype.DatatypeConfigurationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;

import it.univaq.sose.eventservice.EventCreationRequest;
import it.univaq.sose.eventservice.EventListRequest;
import it.univaq.sose.eventservice.EventRemoveRequest;
import it.univaq.sose.eventservice.EventUpdateRequest;
import it.univaq.sose.eventservice.FoodCategory;
import it.univaq.sose.ifameservice.business.providers.AccountServiceProvider;
import it.univaq.sose.ifameservice.business.providers.EventServiceProvider;
import it.univaq.sose.ifameservice.business.providers.ParticipationServiceProvider;
import it.univaq.sose.ifameservice.business.providers.RestaurantServiceProvider;
import it.univaq.sose.ifameservice.helpers.RestResourceHelper;
import it.univaq.sose.ifameservice.model.Event;
import it.univaq.sose.ifameservice.model.Participation;
import it.univaq.sose.ifameservice.model.RestaurantDetails;
import it.univaq.sose.ifameservice.model.User;
import it.univaq.sose.ifameservice.restresources.IfameRestService;

@Component
@EnableAsync
public class IfameProsumerCore {

	private static final Logger LOGGER = LoggerFactory.getLogger(IfameRestService.class);

	@Autowired
	private EventServiceProvider eventServiceProvider;

	@Autowired
	private ParticipationServiceProvider participationServiceProvider;
	
	@Autowired
	private RestaurantServiceProvider restaurantServiceProvider;
	
	@Autowired
	private AccountServiceProvider accountServiceProvider;
	
	/**
	 * Get single event detail (event, participations, restaurant)
	 * @param eventId
	 * @param restaurantId
	 * @return Event
	 */
	public Event getEvent(Integer eventId, Integer restaurantId) {

		LOGGER.info("[IfameProsumerCore]::[getEvent] --- Starting new thread for Event Service --- EventId: " + eventId);
        CompletableFuture<Event> event = eventServiceProvider.getEvent(eventId);

		LOGGER.info("[IfameProsumerCore]::[getEvent] --- Starting new thread for Participation Service ---");
        CompletableFuture<Participation> participation = participationServiceProvider.getEventParticipations(eventId);

		LOGGER.info("[IfameProsumerCore]::[getEvent] --- Starting new thread for Restaurant Service --- RestaurantId: " + restaurantId);
        CompletableFuture<RestaurantDetails> restaurant = restaurantServiceProvider.getRestaurantById(restaurantId);
        
        LOGGER.info("[IfameProsumerCore]::[getEvent] --- Waiting for join threads...");
        CompletableFuture.allOf(event, participation, restaurant).join();

        LOGGER.info("[IfameProsumerCore]::[getEvent]::Threads joined: going ahead");

		Event evt = new Event();
		
		try {
			LOGGER.info("[IfameProsumerCore]::[getEvent] -- Starting new thread for Account service");
	        List<User> users = participation.get() != null ? accountServiceProvider.getAccounts(participation.get().getParticipationIds()) : new ArrayList<User>();

			evt = event.get();
			evt.setRestaurant(restaurant.get());
			evt.setParticipants(users);

		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}

		return evt;
	}
	
	/**
	 * Get all events by user participation
	 * @param username
	 * @return List<Event>
	 */
	public List<Event> getMyJoinedEvents(String username) {
		LOGGER.info("[IfameProsumerCore]::[getMyJoinedEvents]");

        List<Integer> eventIds = participationServiceProvider.getMyJoinedEventParticipations(username);
        
		List<Participation> participations = null;
		
		if (!eventIds.isEmpty()) {			
			participations = participationServiceProvider.getParticipations(eventIds);
		}
		
        List<Event> events = eventServiceProvider.getMyJoinedEvents(eventIds);

        RestResourceHelper.buildEventParticipations(events, participations);
        
        LOGGER.info("[IfameProsumerCore]::[getMyJoinedEvents]::Response {}" + events);

		return events;
	}
	
	/**
	 * Get all events and participants
	 * @param latitude
	 * @param longitude
	 * @param startDate
	 * @param categories
	 * @return List<Event>
	 */
	public List<Event> getAllEvents(double latitude, double longitude, Date startDate, List<FoodCategory> categories) {
		LOGGER.info("[IfameProsumerCore]::[getAllEvents]");
		
		EventListRequest request = new EventListRequest();
		request.setLatitude(latitude);
		request.setLongitude(longitude);
		request.setEventDate(startDate);
		request.setFoodCategories(categories);

		List<Event> events = eventServiceProvider.getEvents(request);
		
		List<Participation> participations = null;
		
		if (!events.isEmpty()) {
			List<Integer> eventIds = new ArrayList<Integer>();
			for (Event evt : events) {
				eventIds.add(evt.getId());
			}
			
			participations = participationServiceProvider.getParticipations(eventIds);
		}

		RestResourceHelper.buildEventParticipations(events, participations);
		
		return events;
	}
	
	/**
	 * Get all events by Owner
	 * @param ownerId
	 * @return List<Event>
	 */
	public List<Event> getAllEventsByOwner(Integer ownerId) {
		LOGGER.info("[IfameProsumerCore]::[getAllEventsByOwner]");
		
		EventListRequest request = new EventListRequest();
		request.setOwnerId(ownerId);

		return eventServiceProvider.getEvents(request);
	}
	
	/**
	 * Get restaurant list
	 * @param latitude
	 * @param longitude
	 * @param categories
	 * @return List<RestaurantDetails>
	 */
	public List<RestaurantDetails> getRestaurants(double latitude, double longitude, String categories) {
		LOGGER.info("[IfameProsumerCore]::[getRestaurants]::latitude: " + latitude + " -- longitude: " + longitude + " -- categories: " + categories);
		return restaurantServiceProvider.getRestaurants(latitude, longitude, categories);
	}
	
	/**
	 * Get all food categories
	 * @return List<FoodCategory>
	 */
	public List<FoodCategory> getFoodCategories() {
		LOGGER.info("[IfameProsumerCore]::[getFoodCategories]");
		return eventServiceProvider.getFoodCategories();
	}
	
	/**
	 * Create event
	 * @param event
	 * @return Integer (event id)
	 * @throws DatatypeConfigurationException 
	 */
	public Integer createEvent(Event event) throws DatatypeConfigurationException {
		LOGGER.info("[IfameProsumerCore]::[createEvent]");
		
		EventCreationRequest request = new EventCreationRequest();
		
		request.setEvent(RestResourceHelper.eventRequestMapping(event));
		
		// Create event from Event Service
		Integer eventId = eventServiceProvider.createEvent(request);
		
		// Add event creator as participant
		if (!event.getParticipants().isEmpty()) participationServiceProvider.addParticipation(eventId, event.getParticipants().get(0).getUsername());
		
		return eventId;
	}
	
	/**
	 * Update user references on participations
	 * @param user
	 */
	public void updateUserReferences(String newUsername, String oldUsername)  {
		LOGGER.info("[IfameProsumerCore]::[updateUserReferences]");
		
		// update participations
		participationServiceProvider.updateParticipation(newUsername, oldUsername);
		
	}
	
	/**
	 * Update event
	 * @param event
	 * @throws DatatypeConfigurationException
	 */
	public void updateEvent(Event event) throws DatatypeConfigurationException {
		LOGGER.info("[IfameProsumerCore]::[updateEvent]");
		
		EventUpdateRequest request = new EventUpdateRequest();
		request.setEvent(RestResourceHelper.eventRequestMapping(event));
		eventServiceProvider.updateEvent(request);
	}
	
	/**
	 * Delete event
	 * @param eventId
	 */
	public void deleteEvent(Integer eventId) {
		LOGGER.info("[IfameProsumerCore]::[deleteEvent]");
		
		EventRemoveRequest request = new EventRemoveRequest();
		request.setEventId(eventId);
		eventServiceProvider.removeEvent(request);
	}
	
	/**
	 * Add participation
	 * @param eventId
	 * @param username
	 */
	public void addParticipation(Integer eventId, String username) {
		LOGGER.info("[IfameProsumerCore]::[addParticipation]");
		participationServiceProvider.addParticipation(eventId, username);
	}
	
	/**
	 * Remove participation
	 * @param eventId
	 * @param username
	 */
	public void removeParticipation(Integer eventId, String username) {
		LOGGER.info("[IfameProsumerCore]::[removeParticipation]");
		participationServiceProvider.removeParticipation(eventId, username);
	}
}
