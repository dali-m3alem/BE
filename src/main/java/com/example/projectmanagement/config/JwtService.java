package com.example.projectmanagement.config;

import com.example.projectmanagement.Domaine.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.*;
import java.util.function.Function;

@Service
public class JwtService {

  private static final String SECRET_KEY = "404D635166546A576E5A7234753777217A25432A462D4A614E645267556B5870";

  public String extractUsername(String token) {

    return extractClaim(token, Claims::getSubject);
  }

  public Integer extractId(String token) {
    return extClaim(token, "id");
  }
  public List<String> extractRoles(String token) {
    List<String> roles = new ArrayList<>();

    try {
      Claims claims = Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token).getBody();
      List<Map<String, String>> rolesMap = (List<Map<String, String>>) claims.get("roles");

      for (Map<String, String> role : rolesMap) {
        String authority = role.get("authority");
        roles.add(authority);
      }
    } catch (Exception e) {
      // Gérer les exceptions appropriées ici
    }

    return roles;
  }


  public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
    final Claims claims = extractAllClaims(token);
    return claimsResolver.apply(claims);
  }

  public <T> T extClaim(String token, String claimName) {
    final Claims claims = extractAllClaims(token);
    return (T) claims.get(claimName);
  }
  public String generateToken(UserDetails userDetails) {
    Map<String, Object> claims = new HashMap<>();
    User user = (User) userDetails;
    claims.put("email", ((User) userDetails).getEmail());
    claims.put("id", ((User) userDetails).getId());
    claims.put("roles", userDetails.getAuthorities()); // ou user.getAuthorities() si cela renvoie tous les rôles
    return generateToken(claims, userDetails);
  }

  public String generateToken(
      Map<String, Object> extraClaims,
      UserDetails userDetails
  ) {
    return Jwts
        .builder()
        .setClaims(extraClaims)
        .setSubject(userDetails.getUsername())
        .setIssuedAt(new Date(System.currentTimeMillis()))
        .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 24*100))
        .signWith(getSignInKey(), SignatureAlgorithm.HS256)
        .compact();
  }

  public boolean isTokenValid(String token, UserDetails userDetails) {
    final String username = extractUsername(token);
    return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
  }

  private boolean isTokenExpired(String token) {
    return extractExpiration(token).before(new Date());
  }

  private Date extractExpiration(String token) {
    return extractClaim(token, Claims::getExpiration);
  }

  private Claims extractAllClaims(String token) {
    return Jwts
        .parserBuilder()
        .setSigningKey(getSignInKey())
        .build()
        .parseClaimsJws(token)
        .getBody();
  }

  private Key getSignInKey() {
    byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
    return Keys.hmacShaKeyFor(keyBytes);
  }

}
