package it.univaq.sose.eventservice.business;

import java.util.List;

import it.univaq.sose.eventservice.EventCreationRequest;
import it.univaq.sose.eventservice.EventDetails;
import it.univaq.sose.eventservice.EventListRequest;
import it.univaq.sose.eventservice.EventRemoveRequest;
import it.univaq.sose.eventservice.EventUpdateRequest;
import it.univaq.sose.eventservice.FoodCategoriesResponse;
import it.univaq.sose.eventservice.FoodCategory;
import it.univaq.sose.eventservice.SingleEventRequest;

public interface EventService {

	public List<EventDetails> getEventList(EventListRequest request);
	public Integer createEvent(EventCreationRequest request);
	public void updateEvent(EventUpdateRequest request);
	public void removeEvent(EventRemoveRequest request);
	public EventDetails getEvent(SingleEventRequest request);
	public List<FoodCategory> getFoodCategories();
	
}
