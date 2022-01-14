package it.univaq.sose.accountservice.business.impl;

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
import org.springframework.stereotype.Repository;

import it.univaq.sose.accountservice.business.AccountDAO;
import it.univaq.sose.accountservice.model.Account;
import it.univaq.sose.accountservice.model.FoodCategory;

@Repository
public class AccountDAOImpl implements AccountDAO {

	private static Logger LOGGER = LoggerFactory.getLogger(AccountDAOImpl.class);
	
	// Table names
	private static final String ACCOUNTS = "accounts";
		
	// Column names for table accounts
	private static final String IDACCOUNT_COLUMN = "id_account";
	private static final String FIRSTNAME_COLUMN = "firstname";
	private static final String LASTNAME_COLUMN = "lastname";
	private static final String USERNAME_COLUMN = "username";
	private static final String PASSWORD_COLUMN = "password";
	private static final String EMAIL_COLUMN = "email";
	private static final String PICTURE_COLUMN = "picture";
	private static final String PREFERENCES_COLUMN = "preferences";
	
	@Autowired
	private DataSource dataSource;

	@Override
	public Account find(String username) {
		// Query for account find
		String sqlQuery = "SELECT * FROM "+ACCOUNTS+" WHERE "+USERNAME_COLUMN+" ='"+username+"'";	

		LOGGER.info("[AccountDAOImpl]::[find]::[one]::perform the query: " + sqlQuery);

		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

		Account account = null;
		List<String> foodCategories;
		
		try {
			con = dataSource.getConnection();
			st = con.createStatement();
			rs = st.executeQuery(sqlQuery);
			
			// Read again the returned account fields and add to the object to return
			if (rs != null && rs.next()) { 
				account = new Account();
				foodCategories = new ArrayList<String>();

				account.setId(rs.getInt(IDACCOUNT_COLUMN));
				account.setFirstname(rs.getString(FIRSTNAME_COLUMN));
				account.setLastname(rs.getString(LASTNAME_COLUMN));
				account.setUsername(rs.getString(USERNAME_COLUMN));
				account.setPassword(rs.getNString(PASSWORD_COLUMN));
				account.setEmail(rs.getString(EMAIL_COLUMN));;
				account.setPicture(rs.getNString(PICTURE_COLUMN));
				
				if (rs.getString(PREFERENCES_COLUMN) != null) {
					for (String pref : rs.getString(PREFERENCES_COLUMN).split(",")) {
						foodCategories.add(pref);
					}
				}
				account.setPreferences(foodCategories);					
				
				LOGGER.info("[AccountDAOImpl]::[find]::[one]::account_id: " + account.getId());
			}
		} catch (SQLException e) {
			LOGGER.error("[AccountDAOImpl]::[find]::[one]::[SQLException]");
			e.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					LOGGER.error("[AccountDAOImpl]::[find]::[one]::[SQLException]::rt.close()");
					e.printStackTrace();
				}
			}
			if (st != null) {
				try {
					st.close();
				} catch (SQLException e) {
					LOGGER.error("[AccountDAOImpl]::[find]::[one]::[SQLException]::st.close()");
					e.printStackTrace();
				}
			}
			if (con != null) {
				try {
					con.close();
				} catch (SQLException e) {
					LOGGER.error("[AccountDAOImpl]::[find]::[one]::[SQLException]::con.close()");
					e.printStackTrace();
				}
			}
		}
		
		return account;
	}
	
	@Override
	public Account find(Integer id) {
		// Query for account find
		String sqlQuery = "SELECT * FROM "+ACCOUNTS+" WHERE "+IDACCOUNT_COLUMN+" ="+id;	

		LOGGER.info("[AccountDAOImpl]::[find]::[one]::perform the query: " + sqlQuery);

		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

		Account account = null;
		List<String> foodCategories;
		
		try {
			con = dataSource.getConnection();
			st = con.createStatement();
			rs = st.executeQuery(sqlQuery);
			
			// Read again the returned account fields and add to the object to return
			if (rs != null && rs.next()) { 
				account = new Account();
				foodCategories = new ArrayList<String>();

				account.setId(rs.getInt(IDACCOUNT_COLUMN));
				account.setFirstname(rs.getString(FIRSTNAME_COLUMN));
				account.setLastname(rs.getString(LASTNAME_COLUMN));
				account.setUsername(rs.getString(USERNAME_COLUMN));
				account.setPassword(rs.getNString(PASSWORD_COLUMN));
				account.setEmail(rs.getString(EMAIL_COLUMN));;
				account.setPicture(rs.getNString(PICTURE_COLUMN));
				
				if (rs.getString(PREFERENCES_COLUMN) != null) {
					for (String pref : rs.getString(PREFERENCES_COLUMN).split(",")) {
						foodCategories.add(pref);
					}
				}
				account.setPreferences(foodCategories);					
				
				LOGGER.info("[AccountDAOImpl]::[find]::[one]::account_id: " + account.getId());
			}
		} catch (SQLException e) {
			LOGGER.error("[AccountDAOImpl]::[find]::[one]::[SQLException]");
			e.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					LOGGER.error("[AccountDAOImpl]::[find]::[one]::[SQLException]::rt.close()");
					e.printStackTrace();
				}
			}
			if (st != null) {
				try {
					st.close();
				} catch (SQLException e) {
					LOGGER.error("[AccountDAOImpl]::[find]::[one]::[SQLException]::st.close()");
					e.printStackTrace();
				}
			}
			if (con != null) {
				try {
					con.close();
				} catch (SQLException e) {
					LOGGER.error("[AccountDAOImpl]::[find]::[one]::[SQLException]::con.close()");
					e.printStackTrace();
				}
			}
		}
		
		return account;
	}

	@Override
	public List<Account> find(List<String> usernames) {
		String whereCondition = "";
		
		for (String user : usernames) whereCondition += "'" + user + "',";
		whereCondition = whereCondition.substring(0, whereCondition.length()-1);
		
		// Query for account find
		String sqlQuery = "SELECT * FROM "+ACCOUNTS+" WHERE "+USERNAME_COLUMN+" IN ("+whereCondition+")";	

		LOGGER.info("[AccountDAOImpl]::[find]::[ids]::perform the query: " + sqlQuery);

		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

		List<Account> accounts = null;
		
		try {
			con = dataSource.getConnection();
			st = con.createStatement();
			rs = st.executeQuery(sqlQuery);
			
			accounts = new ArrayList<Account>();
				
			// Read again the returned account fields and add to the object to return
			while (rs.next()) {
				Account account = new Account();
				List<String> foodCategories = new ArrayList<String>();
				
				account.setId(rs.getInt(IDACCOUNT_COLUMN));
				account.setFirstname(rs.getString(FIRSTNAME_COLUMN));
				account.setLastname(rs.getString(LASTNAME_COLUMN));
				account.setUsername(rs.getString(USERNAME_COLUMN));
				account.setPicture(rs.getNString(PICTURE_COLUMN));
				
				for (String pref : rs.getString(PREFERENCES_COLUMN).split(",")) {
					foodCategories.add(pref);
				}
				account.setPreferences(foodCategories);
				
				accounts.add(account);
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

		return accounts;
	}

	@Override
	public void insert(Account account) {
		
		String preferences = account.getPreferences() != null ? String.join(",", account.getPreferences()) : "";

		//  Query for account insert
		String query = "INSERT INTO `"+ACCOUNTS+"`(`"+FIRSTNAME_COLUMN+"`, `"+LASTNAME_COLUMN+"`, `"+USERNAME_COLUMN+"`, `"+PASSWORD_COLUMN+"`, `"+EMAIL_COLUMN+"`, `"+PICTURE_COLUMN+"`, `"+PREFERENCES_COLUMN+"`)"
				+ " VALUES ('" + account.getFirstname().replace("'", "\\'") + "','" + account.getLastname().replace("'", "\\'") + "','" + account.getUsername().replace("'", "\\'") + "','"+account.getPassword() + "','" + account.getEmail() + "','" + account.getPicture() + "','" + preferences + "')";		

		LOGGER.info("[AccountDAOImpl]::[insert]::perform the query: " + query);
		 
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

		try {
			
			con = dataSource.getConnection();
			
	        st = con.createStatement();

	        st.executeUpdate(query, Statement.RETURN_GENERATED_KEYS);
	        		   
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
	public void update(Account account) {
		LOGGER.info("[AccountDAOImpl]::[update]");
		String preferences = account.getPreferences() != null ? String.join(",", account.getPreferences()) : "";
		
		//  Query for account update
		String query = "UPDATE "+ACCOUNTS+" SET ";

		if (account.getFirstname() != null) query += FIRSTNAME_COLUMN + "='"+ account.getFirstname().replace("'", "\\'") + "',";
		if (account.getLastname() != null) query += LASTNAME_COLUMN + "='"+ account.getLastname().replace("'", "\\'") + "',";
		if (account.getUsername() != null) query += USERNAME_COLUMN+"='"+ account.getUsername().replace("'", "\\'") + "',";
		if (account.getEmail() != null) query += EMAIL_COLUMN+"='"+ account.getEmail() +"',";
		if (account.getPassword() != null) query += PASSWORD_COLUMN+"='"+account.getPassword() +"',";
		if (account.getPicture() != null) query += PICTURE_COLUMN + "='"+ account.getPicture() + "',";
		if (account.getPreferences() != null) query += PREFERENCES_COLUMN+"='"+ preferences + "'";
		
		query += " WHERE "+IDACCOUNT_COLUMN+"="+account.getId();
		
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

		try {
			
			con = dataSource.getConnection();
			
	        st = con.createStatement();

	        LOGGER.info("[AccountDAOImpl]::[update]::perform the query: " + query);

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
	public void delete(Integer id) {

		//  Query for account delete
		String query = "DELETE FROM "+ACCOUNTS+" WHERE "+IDACCOUNT_COLUMN+" ="+id;	

		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

		try {
			
			con = dataSource.getConnection();
			
	        st = con.createStatement();

	        LOGGER.info("[AccountDAOImpl]::[delete]::perform the query: " + query);

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
