package it.univaq.sose.restaurantservice.business.impl;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import it.univaq.sose.provider.restaurantservice.FoodCategory;
import it.univaq.sose.provider.restaurantservice.Restaurant;
import it.univaq.sose.restaurantservice.business.RestaurantDAO;

@Component
public class RestaurantDAOImpl implements RestaurantDAO {

	private static Logger LOGGER = LoggerFactory.getLogger(RestaurantDAOImpl.class);

	// Table name
	private static final String RESTAURANTS = "restaurants";
		
	// Column names for table restaurants
	private static final String ID_COLUMN = "restaurant_id";
	private static final String NAME_COLUMN = "name";
	private static final String LATITUDE_COLUMN = "latitude";
	private static final String LONGITUDE_COLUMN = "longitude";
	private static final String CITY_COLUMN = "city";
	private static final String STATE_COLUMN = "state";
	private static final String STREET_COLUMN = "street";
	private static final String FOODCATEGORIES_COLUMN = "food_categories";
	
	@Autowired
	private DataSource dataSource;

	private Double baseLatitude = 0.27112; // latitude at 30km
	private Double baseLongitude = 0.35559; // longitude at 30km
	
	@Override
	public List<Restaurant> find(String city, Double latitude, Double longitude, List<FoodCategory> categories) {
		
		// Query for restaurant find
		String sqlQuery = "SELECT * FROM "+RESTAURANTS+" WHERE ";	
		
		if (city != null) sqlQuery += CITY_COLUMN + "='" +city+"'";
		else if (latitude != null && latitude != 0 && longitude != null && longitude != 0) {
			sqlQuery += LATITUDE_COLUMN+" >=" + (latitude - baseLatitude) +" AND "+LATITUDE_COLUMN+ "<=" + (latitude + baseLatitude)+ " AND "+LONGITUDE_COLUMN+" >=" + (longitude - baseLongitude) + " AND " + LONGITUDE_COLUMN + "<=" + (longitude + baseLongitude);
		} 
		
		
		if (!categories.isEmpty())  {
			sqlQuery += " AND (";
			for (FoodCategory category : categories) {
				sqlQuery += FOODCATEGORIES_COLUMN + " LIKE '%" + category.value() +"%' OR ";
			}	
			sqlQuery = sqlQuery.substring(0, sqlQuery.length()-3);
			sqlQuery += ")";
		}
		
		
		LOGGER.info("[RestaurantDAOImpl]::[find]::[all]::perform the query: " + sqlQuery);

		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

		List<Restaurant> restaurants = null;
		
		try {
			con = dataSource.getConnection();
			st = con.createStatement();
			rs = st.executeQuery(sqlQuery);
			
			// Read again the returned participation fields and add to the object to return
			if (rs.next()) {
				restaurants = new ArrayList<Restaurant>();
				
				rs.beforeFirst();
				
				// Populate restaurants 
				while(rs.next()) {
					Restaurant res = new Restaurant();
					
					res.setId(rs.getInt(ID_COLUMN));
					res.setCity(rs.getString(CITY_COLUMN));
					res.setLatitude(rs.getDouble(LATITUDE_COLUMN));
					res.setLongitude(rs.getDouble(LONGITUDE_COLUMN));
					res.setName(rs.getString(NAME_COLUMN));
					res.setState(rs.getString(STATE_COLUMN));
					res.setStreet(rs.getNString(STREET_COLUMN));
					
					List<FoodCategory> foodCategories = new ArrayList<FoodCategory>();
					for (String foodCategory : rs.getString(FOODCATEGORIES_COLUMN).split(",")) {
						foodCategories.add(FoodCategory.fromValue(foodCategory));
					}
					res.setFoodCategories(foodCategories);
					restaurants.add(res);
				}

			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (st != null) {
				try {
					st.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (con != null) {
				try {
					con.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}

		return restaurants;
	}
	
	@Override
	public Restaurant find(Integer id) {
		
		// Query for restaurant find
		String sqlQuery = "SELECT * FROM "+RESTAURANTS+" WHERE "+ID_COLUMN+ "="+id;	
		
		
		LOGGER.info("[RestaurantDAOImpl]::[find]::[one]::perform the query: " + sqlQuery);

		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

		Restaurant restaurant = null;
		
		try {
			con = dataSource.getConnection();
			st = con.createStatement();
			rs = st.executeQuery(sqlQuery);

			restaurant = new Restaurant();
				
			// Populate restaurants 
			while(rs.next()) {
				
				restaurant.setId(rs.getInt(ID_COLUMN));
				restaurant.setCity(rs.getString(CITY_COLUMN));
				restaurant.setLatitude(rs.getDouble(LATITUDE_COLUMN));
				restaurant.setLongitude(rs.getDouble(LONGITUDE_COLUMN));
				restaurant.setName(rs.getString(NAME_COLUMN));
				restaurant.setState(rs.getString(STATE_COLUMN));
				restaurant.setStreet(rs.getNString(STREET_COLUMN));
				
				List<FoodCategory> foodCategories = new ArrayList<FoodCategory>();
				for (String foodCategory : rs.getNString(FOODCATEGORIES_COLUMN).split(",")) {
					foodCategories.add(FoodCategory.fromValue(foodCategory));
				}
				restaurant.setFoodCategories(foodCategories);
				
				LOGGER.info("[RestaurantDAOImpl]::[find]::[one]::restaurant_id" + restaurant.getId());
			}
			
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (st != null) {
				try {
					st.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (con != null) {
				try {
					con.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}

		return restaurant;
	}
}
