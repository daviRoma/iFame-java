package it.univaq.sose.restaurantservice.business.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.univaq.sose.provider.restaurantservice.Restaurant;
import it.univaq.sose.provider.restaurantservice.RestaurantRequest;
import it.univaq.sose.restaurantservice.business.RestaurantDAO;
import it.univaq.sose.restaurantservice.business.RestaurantService;

@Service
public class RestaurantServiceImpl implements RestaurantService {

	private static Logger LOGGER = LoggerFactory.getLogger(RestaurantServiceImpl.class);
	
	@Autowired
	private RestaurantDAO restaurantDao;
	
	@Override
	public List<Restaurant> getRestaurantList(RestaurantRequest request) {
		LOGGER.info("[RestaurantServiceImpl]::[getRestaurantList]::[all]::" + request.toString());
		return restaurantDao.find(request.getCity(), request.getLatitude(), request.getLongitude(), request.getFoodCategories());
	}

	@Override
	public Restaurant getRestaurant(RestaurantRequest request) {
		LOGGER.info("[RestaurantServiceImpl]::[getRestaurant]::[one]::" + request.toString());
		return restaurantDao.find(request.getId());
	}

}
