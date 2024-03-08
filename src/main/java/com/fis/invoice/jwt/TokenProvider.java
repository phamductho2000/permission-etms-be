package com.fis.invoice.jwt;

import java.security.Key;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import com.fis.invoice.domain.User;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Component
public class TokenProvider {
	private static final byte[] SECRET = "TCT#HDDT#2022#Report$0986301253$Wc@20221218%AGR".getBytes();
	private static final long EXPIRED = 86400000;
	private static final Key KEY = Keys.hmacShaKeyFor(SECRET);

	public boolean validateToken(String token) {
		try {
			Jwts.parserBuilder().setSigningKey(SECRET).build().parseClaimsJws(token);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	@SuppressWarnings("unchecked")
	public Authentication getAuthentication(String token) {
		Claims claims = Jwts.parserBuilder().setSigningKey(SECRET).build().parseClaimsJws(token).getBody();
		List<String> roles = (List<String>) claims.get("roles");
		List<GrantedAuthority> authorities  = new ArrayList<GrantedAuthority>();
		for (String role : roles) {
		    authorities.add(new SimpleGrantedAuthority(role));
		}
		User user = new User();
		user.setUsr(claims.getSubject());
		user.setName(claims.get("name").toString());
		user.setCqt(claims.get("cqt").toString());
		user.setAdm(claims.get("adm").toString());
		user.setRoles(roles);
		return new UsernamePasswordAuthenticationToken(user, null, authorities);
	}

	public String createToken(Authentication authentication) {
		User user = (User) authentication.getPrincipal();
		String usr = user.getUsr();
		Map<String, Object> claims = new HashMap<String, Object>();
		claims.put("name", user.getName());
		claims.put("cqt", user.getCqt());
		claims.put("adm", user.getAdm());
		claims.put("roles", user.getRoles());
		long time = System.currentTimeMillis();
		return Jwts.builder().setClaims(claims).setSubject(usr).setIssuedAt(new Date(time))
				.setExpiration(new Date(time + EXPIRED)).signWith(KEY).compact();
	}

}
