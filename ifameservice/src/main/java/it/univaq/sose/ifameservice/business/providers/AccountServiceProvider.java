package it.univaq.sose.ifameservice.business.providers;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import it.univaq.sose.ifameservice.model.Credentials;
import it.univaq.sose.ifameservice.model.User;

@Service
public class AccountServiceProvider {

	private static final Logger LOGGER = LoggerFactory.getLogger(AccountServiceProvider.class);

	private static final String ACCOUNTSERVICE_URL = "http://localhost:8080/accountService/account";
	
	/**
	 * Validate user
	 * @param username
	 * @param password
	 * @return Boolean
	 */
	public Boolean validate(Credentials credentials) {
		try {
			LOGGER.info("[AccountServiceProvider]::[validate]::Calling Account service");

			URL forecastUrl = new URL(ACCOUNTSERVICE_URL + "/login");
			HttpURLConnection c = (HttpURLConnection) forecastUrl.openConnection();
			c.setDoOutput(true); // Triggers POST
			c.setRequestMethod("POST");
			c.setRequestProperty("Content-Type", "application/json");
			c.setRequestProperty("Accept", "application/json");
			c.setUseCaches(false);
			c.setAllowUserInteraction(false);
			
			Gson gson = new Gson();
			String jsonInputString = gson.toJson(credentials);
			byte[] postData = jsonInputString.getBytes( StandardCharsets.UTF_8 );
			LOGGER.info("[AccountServiceProvider]::[validate]::request body: " + jsonInputString);
			
	        OutputStream stream = c.getOutputStream();
	        stream.write(postData);

			int status = c.getResponseCode();

			switch (status) {
			case 200:
			case 201:
				StringBuilder sb = new StringBuilder();
				
				try(BufferedReader br = new BufferedReader( new InputStreamReader(c.getInputStream(), "utf-8"))) {
				    
				    String responseLine = null;
				    while ((responseLine = br.readLine()) != null) {
				        sb.append(responseLine.trim());
				    }
				}
					
				if (c != null) {
					c.disconnect();
				}

				JSONObject response = new JSONObject(sb.toString());

				LOGGER.info("[AccountServiceProvider]::[validate]::response: " + response.toString());

				return response != null;

			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	/**
	 * Get single user from Account Service
	 * @param username
	 * @return User
	 */
	public User getAccount(String username) {
		try {
			LOGGER.info("[AccountServiceProvider]::[getAccount]::Calling Account service");

			URL forecastUrl = new URL(ACCOUNTSERVICE_URL + "/" + username);
			HttpURLConnection c = (HttpURLConnection) forecastUrl.openConnection();
			c.setRequestMethod("GET");
			c.setRequestProperty("Content-length", "0");
			c.setRequestProperty("Accept", "application/json");
			c.setUseCaches(false);
			c.setAllowUserInteraction(false);
			c.connect();
			int status = c.getResponseCode();

			switch (status) {
			case 200:
			case 201:
				BufferedReader br = new BufferedReader(new InputStreamReader(c.getInputStream(), "UTF-8"));
				StringBuilder sb = new StringBuilder();
				String line;
				while ((line = br.readLine()) != null) {
					sb.append(line + "\n");
				}
				br.close();

				if (c != null) {
					c.disconnect();
				}
				
				JSONObject response = new JSONObject(sb.toString());
				LOGGER.info("[AccountServiceProvider]::[getAccount]::response: " + response.toString());

				Gson gsonResponse = new GsonBuilder().create();
				User user = gsonResponse.fromJson(response.toString(), User.class);

				LOGGER.info("[AccountServiceProvider]::[getAccount]::Returning Account service response");
				return user;

			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Get list of accounts by list of usernames
	 * @param participationIds
	 * @return List<User>
	 */
	public List<User> getAccounts(List<String> participationIds){
		LOGGER.info("[AccountServiceProvider]::[getAccounts] --- Calling AccountService...");
		try {

			URL accountServiceUrl = new URL(ACCOUNTSERVICE_URL + "/list");
			HttpURLConnection c = (HttpURLConnection) accountServiceUrl.openConnection();
			c.setRequestMethod("POST");
			c.setRequestProperty("Content-Type", "application/json");
			c.setRequestProperty("Accept", "application/json");
			c.setUseCaches(false);
			c.setAllowUserInteraction(false);
			c.setDoOutput(true);
			
			Gson gson = new Gson();
			String jsonInputString = gson.toJson(participationIds);
			
			LOGGER.info("[AccountServiceProvider]::[getAccounts]:jsonInputString --- " + jsonInputString);
			
			byte[] postData = jsonInputString.getBytes( StandardCharsets.UTF_8 );
			c.setRequestProperty("Content-Length", Integer.toString( postData.length ));
			
			try(OutputStream os = c.getOutputStream()) {
			    byte[] input = jsonInputString.getBytes("utf-8");
			    os.write(input, 0, input.length);			
			}
			
			c.connect();
			int status = c.getResponseCode();
			
			switch (status) {
				case 200:
				case 201:
					
					StringBuilder sb = new StringBuilder();
					
					try(BufferedReader br = new BufferedReader( new InputStreamReader(c.getInputStream(), "utf-8"))) {
					    
					    String responseLine = null;
					    while ((responseLine = br.readLine()) != null) {
					        sb.append(responseLine.trim());
					    }
					}
						
					if (c != null) {
						c.disconnect();
					}
		
					JSONArray usersResponse = new JSONArray(sb.toString());
					LOGGER.info("[AccountServiceProvider]::[getAccounts] --- AccountService response: " + usersResponse.toString());
					
					List<User> users = new ArrayList<User>();
					
	
					Gson gsonResponse = new GsonBuilder().create();
					
					for (int i=0; i < usersResponse.length(); i++) {
						User u = gsonResponse.fromJson(usersResponse.getJSONObject(i).toString(), User.class);
						users.add(u);
					}
	
					LOGGER.info("[AccountServiceProvider]::[getAccounts]:Returning weather service response.");
					return users;
	
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;

	}
	
}
