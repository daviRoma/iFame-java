package it.univaq.sose.eventservice.business;

import java.util.Date;
import java.util.List;

import it.univaq.sose.eventservice.EventDetails;
import it.univaq.sose.eventservice.FoodCategory;

public interface EventDAO {

	EventDetails find(Integer id);
	List<EventDetails> find(Integer ownerId, Double latitute, Double longitude, Date startDate, List<FoodCategory> categories);
	List<EventDetails> find(List<Integer> eventIds);
	List<FoodCategory> findCategories();
	Integer insert(EventDetails event);
	void update(EventDetails event);
	void delete(Integer id);
}
