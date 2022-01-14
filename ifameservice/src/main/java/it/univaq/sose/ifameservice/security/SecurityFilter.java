package it.univaq.sose.ifameservice.security;

import java.io.IOException;
import java.security.Key;

import javax.annotation.Priority;
import javax.inject.Inject;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.jsonwebtoken.Jwts;
import it.univaq.sose.ifameservice.utils.KeyGenerator;

@Provider
@Priority(Priorities.AUTHENTICATION)
public class SecurityFilter implements ContainerRequestFilter {

	private static final Logger LOGGER = LoggerFactory.getLogger(SecurityFilter.class);
	
	private static final String AUTHORIZATION_HEADER_PREFIX = "Bearer";
	private static final String IFAME_URL_PREFIX = "ifame";
	
	@Inject
    private KeyGenerator keyGenerator;
	
	@Override
	public void filter(ContainerRequestContext requestContext) throws IOException {
		LOGGER.info("[SecurityFilter]::[filter]");

		if (requestContext.getUriInfo().getPath().contains(IFAME_URL_PREFIX)) {
			// Get the HTTP Authorization header from the request
			String authorizationHeader = requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);
			
			// Check if the HTTP Authorization header is present and formatted correctly
			if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
				LOGGER.info("[SecurityFilter]::Invalid authorizationHeader : " + authorizationHeader);
				throw new NotAuthorizedException("Authorization header must be provided");
			}
			
			// Extract the token from the HTTP Authorization header
			String token = authorizationHeader.substring(AUTHORIZATION_HEADER_PREFIX.length()).trim();
			
			try {
				
				// Validate the token
				Key key = keyGenerator.generateKey();
				Jwts.parser().setSigningKey(key).parseClaimsJws(token);
				LOGGER.info("[Security]::[validateToken]::Valid");
				
			} catch (Exception e) {
				LOGGER.error("[Security]::[validateToken]::invalid token: " + token);
				requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).build());
			}
		}
        
	}


}
