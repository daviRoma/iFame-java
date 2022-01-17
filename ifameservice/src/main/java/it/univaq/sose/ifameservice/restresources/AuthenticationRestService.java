package it.univaq.sose.ifameservice.restresources;

import java.security.Key;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import javax.inject.Inject;
import javax.resource.spi.SecurityException;
import javax.transaction.Transactional;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import it.univaq.sose.ifameservice.business.providers.AccountServiceProvider;
import it.univaq.sose.ifameservice.model.AuthResponse;
import it.univaq.sose.ifameservice.model.Credentials;
import it.univaq.sose.ifameservice.security.Secured;
import it.univaq.sose.ifameservice.utils.KeyGenerator;

@Path("/authentication")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Transactional
public class AuthenticationRestService {

	private static final Logger LOGGER = LoggerFactory.getLogger(AuthenticationRestService.class);
	
	@Context
	private UriInfo uriInfo;
	
	@Inject
    private KeyGenerator keyGenerator;
	
	@Autowired
	private AccountServiceProvider accountServiceProvider;

	@GET
	@Secured
	@Path("/validate/token")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response validateToken() {

		LOGGER.info("[AuthenticationRestService]::[validateToken]");
		
		return Response.ok().build();
    }
	
	@POST
	@Path("/login")
    @Consumes(MediaType.APPLICATION_JSON)
    public AuthResponse authenticateUser(Credentials credentials) {

		LOGGER.info("[AuthenticationRestService]::[authenticateUser]::" + credentials.toString());
		
        try {

        	String username = credentials.getUsername();
        	
            // Authenticate the user using the credentials provided
            authenticate(credentials);

            // Issue a token for the user
            String token = issueToken(username);

            // Return the token on the response
            Response.ok().build();
            return new AuthResponse(token, "Authorized");

        } catch (Exception e) {
        	Response.status(Response.Status.FORBIDDEN).build();
            return new AuthResponse(null, "Invalid credentials");
        }      
    }

	/**
	 * Authenticate the user
	 * @param username
	 * @param password
	 * @throws Exception
	 */
    private void authenticate(Credentials credentials) throws Exception {
        // Authenticate against a database, LDAP, file or whatever
        // Throw an Exception if the credentials are invalid
    	if (!accountServiceProvider.validate(credentials)) throw new SecurityException("Invalid credentials");
    }

    /**
     * Return jwt token to the cliente after authentication
     * @param username
     * @return String
     */
    private String issueToken(String username) {
    	Key key = keyGenerator.generateKey();
        String jwtToken = Jwts.builder()
                .setSubject(username)
                .setIssuer(uriInfo.getAbsolutePath().toString())
                .setIssuedAt(new Date())
                .setExpiration(toDate(LocalDateTime.now().plusMinutes(15L)))
                .signWith(SignatureAlgorithm.HS512, key)
                .compact();
        return jwtToken;
    }
    
    private Date toDate(LocalDateTime localDateTime) {
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }
}
