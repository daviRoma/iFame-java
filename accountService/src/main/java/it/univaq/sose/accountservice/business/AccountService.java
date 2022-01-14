package it.univaq.sose.accountservice.business;

import java.util.List;

import it.univaq.sose.accountservice.model.Account;

public interface AccountService {

	Account login(String username, String password);
	Account getAccount(String username);
	Account getAccount(Integer id);
	List<Account> getAccounts(List<String> usernames);
	void createAccount(Account account);
	void updateAccount(Account account);
	void updateAccountAndReferences(Account account, String header);
	void deleteAccount(Integer id);
	
}
