package it.univaq.sose.eventservice.business.impl;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import it.univaq.sose.eventservice.EventDetails;
import it.univaq.sose.eventservice.FoodCategory;
import it.univaq.sose.eventservice.business.EventDAO;

@Component
public class EventDAOImpl implements EventDAO {

	private static Logger LOGGER = LoggerFactory.getLogger(EventDAOImpl.class);
	
	// Table names
	private static final String EVENTS = "events";
	private static final String FOOD_CATEGORIES = "food_categories";
	private static final String FOOD_CATEGORIES_REL = "food_categories_rel";
		
	// Column names for table events
	private static final String ID_COLUMN = "id_event";
	private static final String TITLE_COLUMN = "title";
	private static final String DESCRIPTION_COLUMN = "description";
	private static final String RESTAURANTID_COLUMN = "restaurant_id";
	private static final String RESTAURANTNAME_COLUMN = "restaurant_name";
	private static final String EVENTDATE_COLUMN = "event_date";
	private static final String EVENTTIME_COLUMN = "event_time";
	private static final String LATITUDE_COLUMN = "latitude";
	private static final String LONGITUDE_COLUMN = "longitude";
	private static final String CITY_COLUMN = "city";
	private static final String PARTICIPANTN_COLUMN = "participant_number";
	private static final String IMAGE_COLUMN = "image";
	private static final String OWNERID_COLUMN = "owner_id";
	
	// Column names for table food_categories
	private static final String IDFC_COLUMN = "id_food_categories";
	private static final String CATEGORY_NAME_COLUMN = "category_name";
	
	// Column names for table food_categories_rel
	private static final String IDFCREL_COLUMN = "id_food_categories_rel";
	private static final String FK_EVEVENTS_COLUMN = "id_event";
	private static final String FK_FOODCATEFGORIES_COLUMN = "id_food_categories";
	
	@Autowired
	private DataSource dataSource;

	private Double baseLatitude = 0.27112; // latitude at 30km
	private Double baseLongitude = 0.35559; // longitude at 30km

	@Override
	public EventDetails find(Integer id) {
		// Query for event find
		String sqlQuery = "SELECT e.*, fc.*, fcr.* FROM "+EVENTS+" as e JOIN "+FOOD_CATEGORIES_REL+" as fcr JOIN "+FOOD_CATEGORIES+" as fc ON fc."+IDFC_COLUMN+"=fcr."+FK_FOODCATEFGORIES_COLUMN+" AND e."+ID_COLUMN+"=fcr."+FK_EVEVENTS_COLUMN+" WHERE e."+ID_COLUMN+" ="+id;	

		LOGGER.info("[EventDAOImpl]::[find]::[one]::perform the query: " + sqlQuery);

		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

		EventDetails event = null;
		List<FoodCategory> foodCategories = new ArrayList<>(); // Keep track of returned categories
		
		try {
			con = dataSource.getConnection();
			st = con.createStatement();
			rs = st.executeQuery(sqlQuery);
			
			// Read again the returned event fields and add to the object to return
			if (rs != null && rs.next()) {
				
				rs.beforeFirst();
				
				event = new EventDetails();
				foodCategories = new ArrayList<FoodCategory>();
				
				// Populate categories 
				while(rs.next()) {
					foodCategories.add(FoodCategory.fromValue(rs.getString(CATEGORY_NAME_COLUMN)));
				}
				
				// After reading food categories
				rs.beforeFirst();
				rs.next();
				
				event.setId(rs.getInt(ID_COLUMN));
				event.setCity(rs.getString(CITY_COLUMN));
				event.setEventDate(rs.getDate(EVENTDATE_COLUMN));
				
				if (rs.getTimestamp(EVENTTIME_COLUMN) != null) {
					Date date = new Date(rs.getTimestamp(EVENTTIME_COLUMN).getTime());
											
					GregorianCalendar gregory = new GregorianCalendar();
					gregory.setTimeInMillis(rs.getTimestamp(EVENTTIME_COLUMN).getTime()*1000);
					gregory.setTime(date);
					
					XMLGregorianCalendar calendar = DatatypeFactory.newInstance().newXMLGregorianCalendar(gregory);

					event.setEventTime(calendar);
				}
				
				event.setLatitude(rs.getDouble(LATITUDE_COLUMN));
				event.setLongitude(rs.getDouble(LONGITUDE_COLUMN));
				event.setTitle(rs.getString(TITLE_COLUMN));
				event.setDescription(rs.getNString(DESCRIPTION_COLUMN));
				event.setOwnerId(rs.getInt(OWNERID_COLUMN));
				event.setParticipantNumber(rs.getInt(PARTICIPANTN_COLUMN));
				event.setRestaurantId(rs.getInt(RESTAURANTID_COLUMN));
				event.setRestaurantName(rs.getString(RESTAURANTNAME_COLUMN));
				event.setImage(rs.getString(IMAGE_COLUMN));
				event.setFoodCategories(foodCategories);

			}
			
		} catch (SQLException | DatatypeConfigurationException e) {
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

		return event;
	}

	@Override
	public List<EventDetails> find(Integer ownerId, Double latitude, Double longitude, Date startDate, List<FoodCategory> categories) {
		LOGGER.info("[EventDAOImpl]::[find]::List of events...");

		// Query for event find
		String sqlQuery = "SELECT e.*, fc.*, fcr.* FROM "+EVENTS+" as e JOIN "+FOOD_CATEGORIES_REL+" as fcr JOIN "+FOOD_CATEGORIES+" as fc ON fc."+IDFC_COLUMN+"=fcr."+FK_FOODCATEFGORIES_COLUMN+" AND e."+ID_COLUMN+"=fcr."+FK_EVEVENTS_COLUMN+" WHERE ";	

		// Set event date for the query (must be >= today)
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		String formatted;
				
		if (startDate != null) formatted = df.format(startDate);
		else formatted = df.format(new Date());
		
		sqlQuery += "Date(e."+EVENTDATE_COLUMN+")>= '" + formatted + "'";

		// Check for ownerId
		if (ownerId != null && ownerId != 0) sqlQuery += " AND e."+OWNERID_COLUMN+" ='"+ownerId+"'";
		else if (latitude != null && latitude != 0 && longitude != null && longitude != 0) {
			sqlQuery += " AND e."+LATITUDE_COLUMN+" >=" + (latitude - baseLatitude) +" AND e."+LATITUDE_COLUMN+ "<=" + (latitude + baseLatitude)+ " AND e."+LONGITUDE_COLUMN+" >=" + (longitude - baseLongitude) + " AND e." + LONGITUDE_COLUMN + "<=" + (longitude + baseLongitude);
		} 
		
		
		if (!categories.isEmpty())  {
			sqlQuery += " AND fc." +CATEGORY_NAME_COLUMN+" IN (";
			for (FoodCategory category : categories) {
				sqlQuery += " '" + category.value() +"',";
			}	
			sqlQuery = sqlQuery.substring(0, sqlQuery.length()-1);
			sqlQuery += ")";
		}
		
		LOGGER.info("[EventDAOImpl]::[find]::perform the query: " + sqlQuery);

		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

		List<EventDetails> events = null;
		
		Map<Integer,List<FoodCategory>> foodCategoryMap = new HashMap<>();
		
		try {
			con = dataSource.getConnection();
			st = con.createStatement();
			rs = st.executeQuery(sqlQuery);
						
			// Read again the returned account fields and add to the object to return
			if (rs.next()) {
				LOGGER.info("[EventDAOImpl]::[find]::Read results...");
				
				events = new ArrayList<EventDetails>();
				
				// Read again result list 
				rs.beforeFirst();
				
				// Populate food category map
				while(rs.next()) {
					if (foodCategoryMap.containsKey(rs.getInt(ID_COLUMN))) {
						foodCategoryMap.get(rs.getInt(ID_COLUMN)).add(FoodCategory.fromValue(rs.getString(CATEGORY_NAME_COLUMN)));
					} else {
						foodCategoryMap.put(rs.getInt(ID_COLUMN), new ArrayList<FoodCategory>(Arrays.asList(FoodCategory.fromValue(rs.getString(CATEGORY_NAME_COLUMN)))));						
					}
				}
				
				// Read again result list 
				rs.beforeFirst();
				
				// Populate activity list
				while (rs.next()) {
					EventDetails event = new EventDetails();

					event.setId(rs.getInt(ID_COLUMN));
					event.setCity(rs.getString(CITY_COLUMN));
					event.setEventDate(rs.getDate(EVENTDATE_COLUMN));

					if (rs.getTimestamp(EVENTTIME_COLUMN) != null) {
						Date date = new Date(rs.getTimestamp(EVENTTIME_COLUMN).getTime());
												
						GregorianCalendar gregory = new GregorianCalendar();
						gregory.setTimeInMillis(rs.getTimestamp(EVENTTIME_COLUMN).getTime()*1000);
						gregory.setTime(date);
						
						XMLGregorianCalendar calendar = DatatypeFactory.newInstance().newXMLGregorianCalendar(gregory);

						event.setEventTime(calendar);
					}
					
					event.setLatitude(rs.getDouble(LATITUDE_COLUMN));
					event.setLongitude(rs.getDouble(LONGITUDE_COLUMN));
					event.setTitle(rs.getString(TITLE_COLUMN));
					event.setDescription(rs.getNString(DESCRIPTION_COLUMN));
					event.setOwnerId(rs.getInt(OWNERID_COLUMN));
					event.setParticipantNumber(rs.getInt(PARTICIPANTN_COLUMN));
					event.setRestaurantId(rs.getInt(RESTAURANTID_COLUMN));
					event.setRestaurantName(rs.getString(RESTAURANTNAME_COLUMN));
					event.setImage(rs.getString(IMAGE_COLUMN));

					
					if (foodCategoryMap.containsKey(rs.getInt(ID_COLUMN))) {
						event.setFoodCategories(foodCategoryMap.get(rs.getInt(ID_COLUMN)));
					}
					events.add(event);
				}
				

			}
		} catch (SQLException se) {
			LOGGER.error("[EventDAOImpl]::[find]::Error Message: " + se.getMessage());
			se.printStackTrace();
		} catch (DatatypeConfigurationException de) {
			LOGGER.error("[EventDAOImpl]::[find]::Error Message: " + de.getMessage());
			de.printStackTrace();
		} catch (Exception ex) {
			LOGGER.error("[EventDAOImpl]::[find]::Error Message: " + ex.getMessage());
			ex.printStackTrace();
		}
		finally {
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

		return events;
	}
	
	@Override
	public List<EventDetails> find(List<Integer> eventIds) {
		
		// Query for event find
		String sqlQuery = "SELECT e.*, fc.*, fcr.* FROM "+EVENTS+" as e JOIN "+FOOD_CATEGORIES_REL+" as fcr JOIN "+FOOD_CATEGORIES+" as fc ON fc."+IDFC_COLUMN+"=fcr."+FK_FOODCATEFGORIES_COLUMN+" AND e."+ID_COLUMN+"=fcr."+FK_EVEVENTS_COLUMN+" WHERE e." + ID_COLUMN + " IN (";	

		
		if (!eventIds.isEmpty())  {
			
			for (Integer eventId : eventIds) {
				sqlQuery += eventId +",";
			}	
			sqlQuery = sqlQuery.substring(0, sqlQuery.length()-1);
			sqlQuery += ")";
		}
		
		LOGGER.info("[EventDAOImpl]::[find]::[byEventIds]::perform the query: " + sqlQuery);

		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

		List<EventDetails> events = null;
		
		Map<Integer,List<FoodCategory>> foodCategoryMap = new HashMap<>();
		
		try {
			con = dataSource.getConnection();
			st = con.createStatement();
			rs = st.executeQuery(sqlQuery);
						
			// Read again the returned account fields and add to the object to return
			if (rs.next()) {
				LOGGER.info("[EventDAOImpl]::[find]::[ids]::Read results...");
				
				events = new ArrayList<EventDetails>();
				
				// Read again result list 
				rs.beforeFirst();
				
				// Populate food category map
				while(rs.next()) {
					if (foodCategoryMap.containsKey(rs.getInt(ID_COLUMN))) {
						foodCategoryMap.get(rs.getInt(ID_COLUMN)).add(FoodCategory.fromValue(rs.getString(CATEGORY_NAME_COLUMN)));
					} else {
						foodCategoryMap.put(rs.getInt(ID_COLUMN), new ArrayList<FoodCategory>(Arrays.asList(FoodCategory.fromValue(rs.getString(CATEGORY_NAME_COLUMN)))));						
					}
				}
				
				// Read again result list 
				rs.beforeFirst();
				
				// Populate activity list
				while (rs.next()) {
					EventDetails event = new EventDetails();

					event.setId(rs.getInt(ID_COLUMN));
					event.setCity(rs.getString(CITY_COLUMN));
					event.setEventDate(rs.getDate(EVENTDATE_COLUMN));
					
					if (rs.getTimestamp(EVENTTIME_COLUMN) != null) {
						Date date = new Date(rs.getTimestamp(EVENTTIME_COLUMN).getTime());
												
						GregorianCalendar gregory = new GregorianCalendar();
						gregory.setTimeInMillis(rs.getTimestamp(EVENTTIME_COLUMN).getTime()*1000);
						gregory.setTime(date);
						
						XMLGregorianCalendar calendar = DatatypeFactory.newInstance().newXMLGregorianCalendar(gregory);

						event.setEventTime(calendar);
					}
					
					event.setLatitude(rs.getDouble(LATITUDE_COLUMN));
					event.setLongitude(rs.getDouble(LONGITUDE_COLUMN));
					event.setTitle(rs.getString(TITLE_COLUMN));
					event.setDescription(rs.getNString(DESCRIPTION_COLUMN));
					event.setOwnerId(rs.getInt(OWNERID_COLUMN));
					event.setParticipantNumber(rs.getInt(PARTICIPANTN_COLUMN));
					event.setRestaurantId(rs.getInt(RESTAURANTID_COLUMN));
					event.setRestaurantName(rs.getString(RESTAURANTNAME_COLUMN));
					event.setImage(rs.getString(IMAGE_COLUMN));
					
					if (foodCategoryMap.containsKey(rs.getInt(ID_COLUMN))) {
						event.setFoodCategories(foodCategoryMap.get(rs.getInt(ID_COLUMN)));
					}
					events.add(event);
				}
				

			}
		} catch (SQLException | DatatypeConfigurationException e) {
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

		return events;
	}

	@Override
	public List<FoodCategory> findCategories() {
		// Query for event find
		String sqlQuery = "SELECT * FROM "+FOOD_CATEGORIES;	

		LOGGER.info("[EventDAOImpl]::[findCategories]::perform the query: " + sqlQuery);

		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

		List<FoodCategory> foodCategories = new ArrayList<>(); // Keep track of returned categories
				
		try {
			con = dataSource.getConnection();
			st = con.createStatement();
			rs = st.executeQuery(sqlQuery);

			// Populate categories 
			while(rs.next()) {
				foodCategories.add(FoodCategory.fromValue(rs.getString(CATEGORY_NAME_COLUMN)));
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

		return foodCategories;
	}
	
	@Override
	public Integer insert(EventDetails event) {
		
		//  Query for account insert
		String query = "INSERT INTO `"+EVENTS+"`(`"+TITLE_COLUMN+"`, `"+DESCRIPTION_COLUMN+"`, `"+RESTAURANTID_COLUMN+"`, `"+RESTAURANTNAME_COLUMN+"`, `"+EVENTDATE_COLUMN+"`, `"+EVENTTIME_COLUMN+"`, `"+LATITUDE_COLUMN+"`, `"+LONGITUDE_COLUMN+"`, `"+CITY_COLUMN+"`, `"+PARTICIPANTN_COLUMN+"`, `"+IMAGE_COLUMN+"`, `"+OWNERID_COLUMN+"` )";

		String queryValues = " VALUES (";
		queryValues += "'" + (event.getTitle() != null ? event.getTitle().replace("'", "\\'") : "") + "',";
		queryValues += "'" + (event.getDescription() != null ? event.getDescription().replace("'", "\\'") : "") + "',";
		queryValues += event.getRestaurantId() + ",";
		queryValues += "'" + (event.getRestaurantName() != null ? event.getRestaurantName().replace("'", "\\'") : "") + "',";
		
		// Set event date for the query
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		queryValues += "'" + (event.getEventDate() != null ? df.format(event.getEventDate()) : "") + "',";
			
		// Set event time for the query
		SimpleDateFormat tf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Calendar now = Calendar.getInstance();
		now.setTime(event.getEventDate());
		now.set(Calendar.HOUR_OF_DAY, event.getEventTime().getHour());
		now.set(Calendar.MINUTE, event.getEventTime().getMinute());
		      
		queryValues += "'" + tf.format(now.getTime()) + "',";
		
		queryValues += event.getLatitude() + ",";
		queryValues += event.getLongitude() + ",";
		queryValues += "'" + (event.getCity() != null ? event.getCity().replace("'", "\\'") : "") + "',";
		queryValues += event.getParticipantNumber() + ",";
		queryValues += "'" + event.getImage() + "',";
		queryValues += event.getOwnerId() + ")";
		
		query += queryValues;
		
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

		try {
			
			con = dataSource.getConnection();
			
	        st = con.createStatement();

	        LOGGER.info("[EventDaoImpl]::[insert]::perform the query: " + query);

	        st.executeUpdate(query, Statement.RETURN_GENERATED_KEYS);
	        		   
		    int generatedKey = -1;
	        rs = st.getGeneratedKeys();
	        
	        if (rs.next()){
	            generatedKey=rs.getInt(1);
            }
	        
	        if (!event.getFoodCategories().isEmpty()) {
	        	
	        	String foodCategories = "";

	        	for (FoodCategory category : event.getFoodCategories()) foodCategories += "'" + category.value() + "',";
	        	foodCategories = foodCategories.substring(0, foodCategories.length()-1);
	        			
	    		String sqlCategoryQuery = "SELECT * FROM "+FOOD_CATEGORIES+" WHERE "+CATEGORY_NAME_COLUMN+" IN ("+foodCategories+")";	

	    		LOGGER.info("[EventDaoImpl]::[insert]::perform the query on food_categories: " + sqlCategoryQuery);
	    		
				st = con.createStatement();
				rs = st.executeQuery(sqlCategoryQuery);
				
				List<Integer> foodCategoryIds = new ArrayList<Integer>();

				// Populate categories 
				while(rs.next()) {
					foodCategoryIds.add(rs.getInt(IDFC_COLUMN));
				}
				
	        	for(Integer foodCategoryId : foodCategoryIds) {
			        String categoriesQuery = "INSERT INTO `"+FOOD_CATEGORIES_REL+"`(`"+FK_EVEVENTS_COLUMN+"`, `"+FK_FOODCATEFGORIES_COLUMN+"`) VALUES ("+generatedKey+","+foodCategoryId+")";

			        LOGGER.info("[EventDaoImpl]::[insert]::[FOOD_CATEGORIES_REL]::perform the query: " + categoriesQuery);

			        st.executeUpdate(categoriesQuery);
		        }	        	
	        }
	        
	        return generatedKey;
	        
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
		
		return null;
		
	}

	@Override
	public void update(EventDetails event) {
		
		
		//  Query for account update
		String query = "UPDATE `"+EVENTS+" SET "+TITLE_COLUMN+"='"+ event.getTitle() + "',"+DESCRIPTION_COLUMN+"='"+ event.getDescription() + "',"+RESTAURANTID_COLUMN+"='"+ event.getRestaurantId() + "',"+RESTAURANTNAME_COLUMN+"='"+ event.getRestaurantName() + "',"+EVENTDATE_COLUMN+"='"+ event.getEventDate() +"',"+LATITUDE_COLUMN+"='"+event.getLatitude()+"', `"+LONGITUDE_COLUMN+"='"+event.getLongitude()+ "',"+CITY_COLUMN+"='"+ event.getCity() + "', " +PARTICIPANTN_COLUMN+ "='"+event.getParticipantNumber()+"', "+OWNERID_COLUMN+"='"+event.getOwnerId()+"') WHERE "+ID_COLUMN+" ="+event.getId();	

		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

		try {
			
			con = dataSource.getConnection();
			
	        st = con.createStatement();

	        LOGGER.info("[EventDAOImpl]::[update]::perform the query: " + query);

	        st.executeUpdate(query);

	        if (!event.getFoodCategories().isEmpty()) {
	        	String foodCategories = "";

	        	for (FoodCategory category : event.getFoodCategories()) foodCategories += "'" + category.value() + "',";
	        	foodCategories = foodCategories.substring(0, foodCategories.length()-1);

	    		String sqlCategoryQuery = "SELECT * FROM "+FOOD_CATEGORIES+" WHERE "+CATEGORY_NAME_COLUMN+" IN ("+foodCategories+")";	

				st = con.createStatement();
				rs = st.executeQuery(sqlCategoryQuery);
				
				List<Integer> foodCategoryIds = new ArrayList<Integer>();

				// Populate categories 
				while(rs.next()) {
					foodCategoryIds.add(rs.getInt(IDFC_COLUMN));
				}
				
				
	        	for(Integer foodCategoryId : foodCategoryIds) {
			        String categoriesQuery = "INSERT INTO `"+FOOD_CATEGORIES_REL+"`(`"+FK_EVEVENTS_COLUMN+"`, `"+FK_FOODCATEFGORIES_COLUMN+"`) VALUES ("+event.getId()+","+foodCategoryId+")";

			        LOGGER.info("[EventDaoImpl]::[update]::[FOOD_CATEGORIES_REL]::perform the query: " + categoriesQuery);

			        st.executeUpdate(categoriesQuery);
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
		
	}

	@Override
	public void delete(Integer id) {

		//  Query for account delete
		String query = "DELETE FROM "+EVENTS+" WHERE "+ID_COLUMN+" ="+id;	

		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

		try {
			
			con = dataSource.getConnection();
			
	        st = con.createStatement();

	        LOGGER.info("[EventDAOImpl]::[delete]::perform the query: " + query);

	        st.executeUpdate(query);

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
		
	}

}
