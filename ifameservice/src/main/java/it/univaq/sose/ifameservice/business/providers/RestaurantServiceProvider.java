package it.univaq.sose.ifameservice.business.providers;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import it.univaq.sose.ifameservice.helpers.RestResourceHelper;
import it.univaq.sose.ifameservice.model.RestaurantDetails;
import it.univaq.sose.restaurantservice.FoodCategory;
import it.univaq.sose.restaurantservice.RestaurantRequest;
import it.univaq.sose.restaurantservice.RestaurantResponse;
import it.univaq.sose.restaurantservice.RestaurantService;
import it.univaq.sose.restaurantservice.RestaurantServicePT;

@Service
public class RestaurantServiceProvider {

	private static final Logger LOGGER = LoggerFactory.getLogger(RestaurantServiceProvider.class);

	RestaurantService restaurantService = new RestaurantService();
	RestaurantServicePT restaurantServicePT = restaurantService.getRestaurantServicePort();
	
	@Async
	public CompletableFuture<RestaurantDetails> getRestaurantById(Integer restaurantId) {
		LOGGER.info("[RestaurantServiceProvider]::[getRestaurantById]::CALL RestaurantService --- getRestaurants");
		
		RestaurantRequest request = new RestaurantRequest();
		request.setId(restaurantId);
		
		RestaurantResponse response = restaurantServicePT.getRestaurants(request);
		
		LOGGER.info("[RestaurantServiceProvider]::[getRestaurantById]::Returning RestaurantService response");
		
		List<RestaurantDetails> restaurants = RestResourceHelper.restaurantResponseMapping(response);
		
		return CompletableFuture.completedFuture(restaurants.get(0));
	}
	
	public List<RestaurantDetails> getRestaurants(double latitude, double longitude, String categories) {
		LOGGER.info("[RestaurantServiceProvider]::[getRestaurants]::CALL RestaurantService --- getRestaurants");
		
		RestaurantRequest request = new RestaurantRequest();
		request.setLatitude(latitude);
		request.setLongitude(longitude);
		
		if (categories != null && !categories.isEmpty()) {
			List<FoodCategory> foodCategories = new ArrayList<FoodCategory>();
			
			for (String cat : categories.split(",")) {
				foodCategories.add(FoodCategory.fromValue(cat));
			}
			request.setFoodCategories(foodCategories);
		}
		
		RestaurantResponse response = restaurantServicePT.getRestaurants(request);
		
		LOGGER.info("[RestaurantServiceProvider]::[getRestaurants]::Returning RestaurantService response");
		
		List<RestaurantDetails> restaurants = RestResourceHelper.restaurantResponseMapping(response);
		
		return restaurants;
	}
}
