package it.univaq.sose.ifame.participationservice.business.impl;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import it.univaq.sose.ifame.participationservice.business.ParticipationDAO;
import it.univaq.sose.ifame.participationservice.model.Participation;

@Component
public class ParticipationDAOImpl implements ParticipationDAO {
	
	private static Logger LOGGER = LoggerFactory.getLogger(ParticipationDAOImpl.class);

	// Table name
	private static final String PARTICIPATIONS = "participations";
		
	// Column names for table participations
	private static final String ID_COLUMN = "participation_id";
	private static final String USERNAME_COLUMN = "username";
	private static final String EVENTID_COLUMN = "event_id";

	@Autowired
	private DataSource dataSource;
	
	@Override
	public Participation find(Integer eventId) {
		// Query for participation find
		String sqlQuery = "SELECT * FROM "+PARTICIPATIONS+" WHERE "+EVENTID_COLUMN+" ="+eventId;	

		LOGGER.info("[ParticipationDAOImpl]::[find]::[one]::perform the query: " + sqlQuery);

		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

		Participation participation = null;
		
		try {
			con = dataSource.getConnection();
			st = con.createStatement();
			rs = st.executeQuery(sqlQuery);
			
			// Read again the returned participation fields and add to the object to return
			if (rs != null && rs.next()) {

				participation = new Participation();
				
				participation.setEventId(rs.getInt(EVENTID_COLUMN));
				
				List<String> usernames = new ArrayList<String>();
				
				rs.beforeFirst();
				
				// Populate categories 
				while(rs.next()) {
					usernames.add(rs.getString(USERNAME_COLUMN));
				}
				
				participation.setParticipationIds(usernames);
				
				LOGGER.info("[ParticipationDAOImpl]::[find]::[one]::Participation --- eventId: " + participation.getEventId());
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

		return participation;
	}

	@Override
	public List<Participation> find(List<Integer> eventIds) {
		String events = "";
		
		for (Integer id : eventIds) events += id + ",";
		
		events = events.length() > 0 ? events.substring(0, events.length()-1) : "";
		
		// Query for participation find
		String sqlQuery = "SELECT * FROM "+PARTICIPATIONS+" WHERE "+EVENTID_COLUMN+" IN ("+events+")";	

		LOGGER.info("[ParticipationDAOImpl]::[find]::[All participations]::perform the query: " + sqlQuery);

		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

		List<Participation> participations = null;
		
		try {
			con = dataSource.getConnection();
			st = con.createStatement();
			rs = st.executeQuery(sqlQuery);
			
			// Read again the returned participation fields and add to the object to return
			if (rs.next()) {
				rs.beforeFirst();
				
				participations = new ArrayList<Participation>();
				
				Map<Integer, List<String>> participationMap = new HashMap<Integer, List<String>>();
				
				// Populate categories 
				while(rs.next()) {
					if(participationMap.containsKey(rs.getInt(EVENTID_COLUMN))) {
						participationMap.get(rs.getInt(EVENTID_COLUMN)).add(rs.getString(USERNAME_COLUMN));
					} else {
						participationMap.put(rs.getInt(EVENTID_COLUMN), new ArrayList<String>(Arrays.asList(rs.getString(USERNAME_COLUMN))));
					}
				}
				
				for (Integer eventId : participationMap.keySet()) {
					Participation participation = new Participation();
					participation.setEventId(eventId);
					List<String> usernames = new ArrayList<String>();
					
					for (String username : participationMap.get(eventId)) {
						usernames.add(username);
					}
					participation.setParticipationIds(usernames);
					participations.add(participation);
				}
			
				
				LOGGER.info("[ParticipationDAOImpl]::[find]::[All participations]::Participations: " + participations.size());
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

		return participations;
	}
	
	@Override
	public List<Integer> find(String username) {
		// Query for participation find
		String sqlQuery = "SELECT * FROM "+PARTICIPATIONS+" WHERE "+USERNAME_COLUMN+" = '"+username+"'";	

		LOGGER.info("[ParticipationDAOImpl]::[find]::[allByUsername]::perform the query: " + sqlQuery);

		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

		List<Integer> eventIds = null;
		
		try {
			con = dataSource.getConnection();
			st = con.createStatement();
			rs = st.executeQuery(sqlQuery);
			
			// Read again the returned participation fields and add to the object to return
			if (rs.next()) {
				rs.beforeFirst();
				
				eventIds = new ArrayList<Integer>();
				
				// Populate categories 
				while(rs.next()) {
					eventIds.add(rs.getInt(EVENTID_COLUMN));
				}
				
				
				LOGGER.info("[ParticipationDAOImpl]::[find]::[allByUsername]::Participation --- eventIds: " + eventIds);
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

		return eventIds;

	}
	
	@Override
	public void insert(Participation participation) {
		
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

		try {
			
			con = dataSource.getConnection();
	        st = con.createStatement();
	        
	        if (!participation.getParticipationIds().isEmpty()) {
	        	
	        	for(String username : participation.getParticipationIds()) {
			        String query = "INSERT INTO `"+PARTICIPATIONS+"`(`"+EVENTID_COLUMN+"`, `"+USERNAME_COLUMN+"`) VALUES ("+participation.getEventId()+",'"+username+"')";

			        LOGGER.info("[ParticipationDAOImpl]::[insert]::perform the query: " + query);

			        st.executeUpdate(query, Statement.RETURN_GENERATED_KEYS);
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
	public void update(String newUsername, String oldUsername) {
		LOGGER.info("[ParticipationDAOImpl]::[update]");
				
		//  Query for participations update
		String query = "UPDATE "+PARTICIPATIONS+" SET " + USERNAME_COLUMN + "='" +newUsername+"' WHERE " + USERNAME_COLUMN + "='" + oldUsername + "'";

		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

		try {
	        LOGGER.info("[ParticipationDAOImpl]::[update]::perform the query: " + query);

			con = dataSource.getConnection();
	        st = con.createStatement();
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
	
	@Override
	public void delete(Participation participation) {
		
		String usernames = "";
		for (String username : participation.getParticipationIds()) {
			usernames += "'"+username+"',";
		}
		
		usernames = usernames.substring(0, usernames.length()-1);
		
		
		//  Query for participation delete
		String query = "DELETE FROM "+PARTICIPATIONS+" WHERE "+EVENTID_COLUMN+" ="+participation.getEventId()+" AND "+USERNAME_COLUMN+" IN (" +usernames+")" ;	

		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

		try {
			
			con = dataSource.getConnection();
	        st = con.createStatement();

	        LOGGER.info("[ParticipationDAOImpl]::[delete]::perform the query: " + query);

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
