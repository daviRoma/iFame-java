package it.univaq.sose.ifameservice.helpers;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import it.univaq.sose.eventservice.EventDetails;
import it.univaq.sose.ifameservice.model.Event;
import it.univaq.sose.ifameservice.model.Participation;
import it.univaq.sose.ifameservice.model.RestaurantDetails;
import it.univaq.sose.ifameservice.model.User;
import it.univaq.sose.participationservice.EventParticipationResponse;
import it.univaq.sose.participationservice.ParticipationDetail;
import it.univaq.sose.restaurantservice.Restaurant;
import it.univaq.sose.restaurantservice.RestaurantResponse;


public class RestResourceHelper {

	public static List<Event> eventsResponseMapping(List<EventDetails> response) {
		
		List<Event> events = new ArrayList<Event>();
		for (EventDetails event : response) {
			events.add(buildEvent(event));
		}
		return events;
	}
	
	public static Event buildEvent(EventDetails eventDetail) {
		Event evt = new Event();
		evt.setCity(eventDetail.getCity());
		evt.setEventDate(eventDetail.getEventDate());

		ZonedDateTime utcZoned = eventDetail.getEventTime().toGregorianCalendar().toZonedDateTime();
		evt.setEventTime(utcZoned.toLocalDateTime());
		
		evt.setFoodCategories(eventDetail.getFoodCategories());
		evt.setId(Integer.valueOf(eventDetail.getId()));
		evt.setLatitude(eventDetail.getLatitude());
		evt.setLongitude(eventDetail.getLongitude());
		evt.setCity(eventDetail.getCity());
		evt.setTitle(eventDetail.getTitle());
		evt.setDescription(eventDetail.getDescription());
		evt.setOwnerId(eventDetail.getOwnerId());
		evt.setParticipantNumber(eventDetail.getParticipantNumber());
		evt.setImage(eventDetail.getImage());
		
		RestaurantDetails restaurant = new RestaurantDetails();
		restaurant.setId(eventDetail.getRestaurantId());
		restaurant.setName(eventDetail.getRestaurantName());
		
		evt.setRestaurant(restaurant);
		
		return evt;
	}
	
	public static Participation participationResponseMapping(EventParticipationResponse response) {
		Participation participation = null;
		if (response.getParticipationDetail() != null) {
			participation = new Participation();
			participation.setEventId(response.getParticipationDetail().getEventId());
			participation.setParticipationIds(response.getParticipationDetail().getParticipants());
		}
		return participation;

	}
	
	public static List<RestaurantDetails> restaurantResponseMapping(RestaurantResponse response) {
		List<RestaurantDetails> restaurants = new ArrayList<RestaurantDetails>();
		for (Restaurant rest : response.getReturn()) {
			RestaurantDetails restaurant = new RestaurantDetails();
			restaurant.setCity(rest.getCity());
			restaurant.setFoodCategories(rest.getFoodCategories());
			restaurant.setId(rest.getId());
			restaurant.setLatitude(rest.getLatitude());
			restaurant.setLongitude(rest.getLongitude());
			restaurant.setName(rest.getName());
			restaurant.setState(rest.getState());
			restaurant.setStreet(rest.getStreet());
			
			restaurants.add(restaurant);
		}
		return restaurants;
	}
	
	public static EventDetails eventRequestMapping(Event event) throws DatatypeConfigurationException {
		EventDetails eventDetails = new EventDetails();
		eventDetails.setCity(event.getCity());
		eventDetails.setEventDate(event.getEventDate());
									
		// Formatted time
		GregorianCalendar gregory = new GregorianCalendar();
		gregory.setTimeInMillis(event.getEventDate().getTime()*1000);
		gregory.setTime(event.getEventDate());
		XMLGregorianCalendar calendar = DatatypeFactory.newInstance().newXMLGregorianCalendar(gregory);

		eventDetails.setEventTime(calendar);

		eventDetails.setLatitude(event.getLatitude());
		eventDetails.setLongitude(event.getLongitude());
		eventDetails.setTitle(event.getTitle());
		eventDetails.setDescription(event.getDescription());
		eventDetails.setOwnerId(event.getOwnerId());
		eventDetails.setParticipantNumber(event.getParticipantNumber());
		eventDetails.setRestaurantId(event.getRestaurant().getId());
		eventDetails.setRestaurantName(event.getRestaurant().getName());
		eventDetails.setFoodCategories(event.getFoodCategories());
		eventDetails.setImage(event.getImage());
		
		return eventDetails;
	}
	
	public static void buildEventParticipations(List<Event> events, List<Participation> participations) {
		for (Event evt : events) {
			List<User> users = new ArrayList<User>();
			for (Participation pa : participations) {
				if (evt.getId() == pa.getEventId()) {
					for (String username : pa.getParticipationIds()) {
						User u = new User();
						u.setUsername(username);
						users.add(u);
					}
				}
			}
			evt.setParticipants(users);
		}
	}
	
	public static List<Participation> participationDetailToParticipationMapping(List<ParticipationDetail> participationList) {
		List<Participation> participations = new ArrayList<Participation>();
		
		for (ParticipationDetail pa : participationList) {
			Participation participation = new Participation();
			participation.setEventId(pa.getEventId());
			participation.setParticipationIds(pa.getParticipants());
			participations.add(participation);
		}
		
		return participations;
	}
	
}
