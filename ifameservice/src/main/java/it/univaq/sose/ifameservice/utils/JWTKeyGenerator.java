package it.univaq.sose.ifameservice.utils;

import java.security.Key;

import javax.crypto.spec.SecretKeySpec;

import org.springframework.stereotype.Service;

public class JWTKeyGenerator implements KeyGenerator {

	@Override
	public Key generateKey() {
		String keyString = "jwtkey";
        Key key = new SecretKeySpec(keyString.getBytes(), 0, keyString.getBytes().length, "DES");
        return key;
	}

}
