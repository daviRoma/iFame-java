package it.univaq.sose.restaurantservice.business;

import java.util.List;

import it.univaq.sose.provider.restaurantservice.FoodCategory;
import it.univaq.sose.provider.restaurantservice.Restaurant;

public interface RestaurantDAO {

	List<Restaurant> find(String city, Double latituda, Double longitude, List<FoodCategory> categories);
	Restaurant find(Integer id);
}
