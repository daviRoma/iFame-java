package it.univaq.sose.restaurantservice.webservices;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import it.univaq.sose.provider.restaurantservice.Restaurant;
import it.univaq.sose.provider.restaurantservice.RestaurantRequest;
import it.univaq.sose.provider.restaurantservice.RestaurantResponse;
import it.univaq.sose.provider.restaurantservice.RestaurantServicePT;
import it.univaq.sose.restaurantservice.business.RestaurantService;

@Component(value="RestaurantServicePTImpl")
public class RestaurantServicePTImpl implements RestaurantServicePT {

	private static Logger logger = LoggerFactory.getLogger(RestaurantServicePTImpl.class);

	@Autowired
	private RestaurantService service;
	
	@Override
	public RestaurantResponse getRestaurants(RestaurantRequest request) {
    	logger.info("[RestaurantService]::getRestaurants");	
    	RestaurantResponse result = new RestaurantResponse();
    	
    	if (request.getId() != null) {
    		List<Restaurant> restaurants = new ArrayList<Restaurant>();
    		restaurants.add(service.getRestaurant(request));
    		result.setReturn(restaurants);
    	} else {
    		result.setReturn(service.getRestaurantList(request));    		
    	}
		
		return result;
	}

}
