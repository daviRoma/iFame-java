package it.univaq.sose.accountservice.restresources;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import it.univaq.sose.accountservice.business.AccountService;
import it.univaq.sose.accountservice.model.Account;
import it.univaq.sose.accountservice.model.Credentials;

@Path("/account")
public class AccountRestResource {

	private static final Logger LOGGER = LoggerFactory.getLogger(AccountRestResource.class);

	/**
	* Use uriInfo to get current context path and to build HATEOAS links
	* */
	@Context
	private UriInfo uriInfo;
	
	@Autowired
	private AccountService accountService;

	@GET
	@Path("/{username}")
	@Produces(MediaType.APPLICATION_JSON)
	public Account getAccount(@PathParam("username") String username) {
		LOGGER.info("[AccountRestResource]::[getAccount]");
		return accountService.getAccount(username);
	}
	
	@POST
	@Path("/login")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Account login(Credentials credentials) {
		LOGGER.info("[AccountRestResource]::[login]");
		return accountService.login(credentials.getUsername(), credentials.getPassword());
	}
	
	@POST
	@Path("/list")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public List<Account> getAccounts(List<String> usernames) {
		LOGGER.info("[AccountRestResource]::[getAccounts]");
		return accountService.getAccounts(usernames);
	}
	
	@POST
	@Path("/register")
	@Consumes(MediaType.APPLICATION_JSON)
	public void createAccount(Account account) {
		LOGGER.info("[AccountRestResource]::[createAccount]");
		accountService.createAccount(account);
	}
	
	@PUT
	@Path("/update/{id}")
	@Consumes(MediaType.APPLICATION_JSON)
	public void updateAccount(@Context HttpHeaders headers, @PathParam("id") Integer id, Account account) {
		LOGGER.info("[AccountRestResource]::[updateAccount]::id: " + id);

		account.setId(id);
		
		if (account.getUsername() != null) {
			List<String> authHeaders = headers.getRequestHeader(HttpHeaders.AUTHORIZATION);
			accountService.updateAccountAndReferences(account, authHeaders.get(0));
		} else {
			accountService.updateAccount(account);			
		}
	}
	
	@DELETE
	@Path("/delete/{id}")
	public void deleteAccount(@PathParam("id") Integer id) {
		LOGGER.info("[AccountRestResource]::[deleteAccount]::id: " + id);
		accountService.deleteAccount(id);
	}
	
}
