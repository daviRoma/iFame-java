package it.univaq.sose.restaurantservice.business;

import java.util.List;

import it.univaq.sose.provider.restaurantservice.Restaurant;
import it.univaq.sose.provider.restaurantservice.RestaurantRequest;

public interface RestaurantService {
	      
	List<Restaurant> getRestaurantList(RestaurantRequest request);
	Restaurant getRestaurant(RestaurantRequest request);
    
}
