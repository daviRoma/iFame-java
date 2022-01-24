package it.univaq.sose.accountservice.business.impl;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.univaq.sose.accountservice.business.AccountDAO;
import it.univaq.sose.accountservice.business.AccountService;
import it.univaq.sose.accountservice.model.Account;

@Service
public class AccountServiceImpl implements AccountService {
	
	private static final String IFAMESERVICE_URL = "http://localhost:8080/ifameService/ifame";

	private static Logger LOGGER = LoggerFactory.getLogger(AccountServiceImpl.class);
	
	@Autowired
	private AccountDAO accountDao;

	@Override
	public Account login(String username, String password) {
		LOGGER.info("[AccountServiceImpl]::[login]");
		
		Account acc = accountDao.find(username);
		
		if (acc != null) {
			if (acc.getPassword().equals(password)) {
				acc.setPassword(null);
				return acc;
			}
		}
		return null;
	}
	
	@Override
	public Account getAccount(String username) {
		return accountDao.find(username);
	}
	
	@Override
	public Account getAccount(Integer id) {
		return accountDao.find(id);
	}

	@Override
	public List<Account> getAccounts(List<String> usernames) {
		return accountDao.find(usernames);
	}

	@Override
	public void createAccount(Account account) {
		LOGGER.info("[AccountServiceImpl]::[createAccount]::" + account.getUsername());
		accountDao.insert(account);
	}

	@Override
	public void updateAccount(Account account) {
		accountDao.update(account);
	}
	
	@Override
	public void updateAccountAndReferences(Account account, String header) {
		Account acc = this.getAccount(account.getId());
		
		this.updateAccount(account);
		
		try {
			LOGGER.info("[AccountServiceImpl]::[updateAccountAndReferences]::Calling IFame service");

			URL forecastUrl = new URL(IFAMESERVICE_URL + "/participation/update/username/" + account.getUsername() + "/" + acc.getUsername());
			HttpURLConnection c = (HttpURLConnection) forecastUrl.openConnection();
			c.setDoOutput(true); // Triggers PUT
			c.setRequestMethod("PUT");
			c.setRequestProperty("Content-Type", "application/json");
			c.setRequestProperty("Accept", "application/json");
			c.setRequestProperty("Authorization", header);
			c.setUseCaches(false);
			c.setAllowUserInteraction(false);

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


				LOGGER.info("[AccountServiceImpl]::[updateAccountAndReferences]::DONE");

			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	@Override
	public void deleteAccount(Integer id) {
		accountDao.delete(id);
	}
	
}
