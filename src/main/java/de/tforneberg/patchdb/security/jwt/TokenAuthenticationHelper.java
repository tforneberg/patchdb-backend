package de.tforneberg.patchdb.security.jwt;

import java.util.Collections;
import java.util.Date;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.util.WebUtils;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

public class TokenAuthenticationHelper {
	private static final long EXPIRATIONTIME = 1000 * 60 * 60 * 24; // 24 hours
	private static final String SECRET = "hee9pmw4tpr8hwevmw4983480hgey38h4tb9ureatSe4o24ouct28j4ob9uhwr98ans39cr48g";
	private static final String COOKIE_BEARER = "COOKIE-BEARER";
//	private static final String CSRF_COOKIE = ""
	
	static void addAuthentication(HttpServletRequest req, HttpServletResponse res, String username) {
		//Create JWT and put it into a cookie. Add cookie to response.
		String JWT = Jwts.builder()
				.setSubject(username)
				.setExpiration(new Date(System.currentTimeMillis() + EXPIRATIONTIME))
				.signWith(SignatureAlgorithm.HS512, SECRET)
				.compact();
		Cookie cookie = new Cookie(COOKIE_BEARER, JWT);
		cookie.setHttpOnly(true);
		cookie.setPath("/"); //TODO correct?
		res.addCookie(cookie);
	}
	
	static void removeAuthentication(HttpServletRequest req, HttpServletResponse res) {
		//overwrites the JWT cookie with an invalidated cookie
		Cookie cookie = new Cookie(COOKIE_BEARER, "");
		cookie.setMaxAge(0);
		res.addCookie(cookie);
	}
	
	static Authentication getAuthentication(HttpServletRequest request) {
		//get the JWT token from the request cookie
		String token = null;
		Cookie cookie = WebUtils.getCookie(request, COOKIE_BEARER);
		if (cookie != null) {
			token = cookie.getValue();
		}
		
		//check if token is valid and if so, get the user name from the payload
		if (token != null) {
			String userName = Jwts.parser()
					.setSigningKey(SECRET)
					.parseClaimsJws(token)
					.getBody()
					.getSubject();
			
			if (userName != null) {
				//grant the user with the given userName authentication
				return new UsernamePasswordAuthenticationToken(userName, null, Collections.emptyList());
			}
		}
		return null;
	}
}
