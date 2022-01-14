package it.univaq.sose.accountservice.business;

import java.util.List;

import it.univaq.sose.accountservice.model.Account;

public interface AccountDAO {
	
	Account find(String username);
	Account find(Integer id);
	List<Account> find(List<String> usernames);
	void insert(Account account);
	void update(Account account);
	void delete(Integer id);
}
