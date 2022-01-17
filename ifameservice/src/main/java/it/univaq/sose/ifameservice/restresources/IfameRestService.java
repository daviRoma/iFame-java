package it.univaq.sose.ifameservice.restresources;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.datatype.DatatypeConfigurationException;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.gson.Gson;

import it.univaq.sose.eventservice.FoodCategory;
import it.univaq.sose.ifameservice.business.IfameProsumerCore;
import it.univaq.sose.ifameservice.model.Event;
import it.univaq.sose.ifameservice.model.Participation;
import it.univaq.sose.ifameservice.model.RestaurantDetails;
import it.univaq.sose.ifameservice.security.Secured;

import org.slf4j.Logger;

@Path("/ifame")
public class IfameRestService {

	private static final Logger LOGGER = LoggerFactory.getLogger(IfameRestService.class);

	@Autowired
	private IfameProsumerCore prosumerCore;
	
	@GET
	@Secured
	@Path("/event/{eventId}/{restaurantId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Event getEvent(@PathParam("eventId") Integer eventId, @PathParam("restaurantId") Integer restaurantId) {
		LOGGER.info("[IfameRestService]::[getEvent]::/ifame/event/" +eventId+ "/"+restaurantId);
		return prosumerCore.getEvent(eventId, restaurantId);
	}
	
	@GET
	@Secured
	@Path("/events/all")
	@Produces(MediaType.APPLICATION_JSON)
	public List<Event> getEvents(@QueryParam("latitude") double latitude, @QueryParam("longitude") double longitude, @QueryParam("startDate") Date startDate, @QueryParam("foodCategories") String foodCategories) {
		LOGGER.info("[IfameRestService]::[getEvents]::/ifame/events/all");
		
		List<FoodCategory> categories = new ArrayList<FoodCategory>();

		if (foodCategories != null && foodCategories != "") {
			
			for (String cat : foodCategories.split(",")) {
				categories.add(FoodCategory.fromValue(cat));
			}
			
		}
		
		return prosumerCore.getAllEvents(latitude, longitude, startDate, categories);		
	}
	
	@GET
	@Secured
	@Path("/events/joined/{username}")
	@Produces(MediaType.APPLICATION_JSON)
	public List<Event> getJoinedEvents(@PathParam("username") String username) {
		LOGGER.info("[IfameRestService]::[getEvents]::/ifame/events/joined/" + username);

		return prosumerCore.getMyJoinedEvents(username);
	}
	
	
	@GET
	@Secured
	@Path("/events/owner/{ownerId}")
	@Produces(MediaType.APPLICATION_JSON)
	public List<Event> getEventsByOwner(@PathParam("ownerId") final Integer ownerId) {
		LOGGER.info("[IfameRestService]::[getEventsByOwner]::/ifame/events/owner/"+ownerId);

		return prosumerCore.getAllEventsByOwner(ownerId);
	}
	
	@GET
	@Secured
	@Path("/restaurants/all")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public List<RestaurantDetails> getRestaurants(@QueryParam("latitude") double latitude, @QueryParam("longitude") double longitude, @QueryParam("categories") String categories) {
		LOGGER.info("[IfameRestService]::[getRestaurants]::/ifame/restaurants/all");
		
		return prosumerCore.getRestaurants(latitude, longitude, categories);
	}
	
	@GET
	@Secured
	@Path("/foodcategories/all")
	@Produces(MediaType.APPLICATION_JSON)
	public List<FoodCategory> getFoodCategories() {
		LOGGER.info("[IfameRestService]::[getFoodCategories]::/ifame/foodcategories/all");
		return prosumerCore.getFoodCategories();
	}
	
	@POST
	@Secured
	@Path("/event/create")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Integer createEvent(Event event) {
		LOGGER.info("[IfameRestService]::[createEvent]::/ifame/event/create");
		try {
			return prosumerCore.createEvent(event);
		} catch (DatatypeConfigurationException e) {
			LOGGER.error("[IfameRestService]::[createEvent]::Error message: " + e.getMessage());
			e.printStackTrace();
		}
		return null;
	}
	
	@POST
	@Secured
	@Path("/participation/event/join")
	@Consumes(MediaType.APPLICATION_JSON)
	public void joinEvent(Participation participation) {
		LOGGER.info("[IfameRestService]::[joinEvent]::/ifame/participation/event/join");
		prosumerCore.addParticipation(participation.getEventId(), participation.getParticipationIds().get(0));
	}
	
	@PUT
	@Secured
	@Path("/event/update/{eventId}")
	@Consumes(MediaType.APPLICATION_JSON)
	public void updateEvent(@PathParam("eventId") Integer eventId, Event event) {
		LOGGER.info("[IfameRestService]::[updateEvent]::/ifame/event/update/"+eventId);
		try {
			prosumerCore.updateEvent(event);
		} catch (DatatypeConfigurationException e) {
			LOGGER.error("[IfameRestService]::[updateEvent]::Error message: " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	@PUT
	@Secured
	@Path("/participation/update/username/{newusername}/{oldusername}")
	public void updateUserReferences(@PathParam("newusername") String newusername, @PathParam("oldusername") String oldusername) {
		LOGGER.info("[IfameRestService]::[updateUserReferences]::/ifame/participation/update/username/"+newusername+"/"+oldusername);
		prosumerCore.updateUserReferences(newusername, oldusername);
	}
	
	@DELETE
	@Secured
	@Path("/event/delete/{eventId}")
	public void deleteEvent(@PathParam("eventId") Integer eventId) {
		LOGGER.info("[IfameRestService]::[deleteEvent]::/ifame/event/delete/" + eventId);
		prosumerCore.deleteEvent(eventId);
	}
	
	@DELETE
	@Secured
	@Path("/participation/event/remove/{eventId}/{username}")
	public void removeParticipation(@PathParam("eventId") Integer eventId, @PathParam("username") String username) {
		LOGGER.info("[IfameRestService]::[removeParticipation]::/ifame/participation/event/remove/" + eventId + "/"+username);
		prosumerCore.removeParticipation(eventId, username);
	}
	
}
