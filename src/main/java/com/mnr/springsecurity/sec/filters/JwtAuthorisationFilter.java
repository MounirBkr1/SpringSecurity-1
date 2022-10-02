package com.mnr.springsecurity.sec.filters;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

public class JwtAuthorisationFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String autorisationToken = request.getHeader("Authorisation");

        if (autorisationToken != null && autorisationToken.startsWith("Bearer ")) {
            try {
                //verifier si token est valide
                //enlever les 7 premiers caracteres
                String jwt = autorisationToken.substring(7);
                //use le meme secret pr verifier le token
                Algorithm algorithm = Algorithm.HMAC256("mySecret1234");
                JWTVerifier jwtVerifier = JWT.require(algorithm).build();
                DecodedJWT decodedJWT = jwtVerifier.verify(jwt);

                //recuperer username
                String username = decodedJWT.getSubject();
                //recuperer les roles
                String[] roles = decodedJWT.getClaim("roles").asArray(String.class);
                //conversion vers collection de  grantedAuthorities
                Collection<GrantedAuthority> authorities = new ArrayList<>();
                for (String r : roles) {
                    authorities.add(new SimpleGrantedAuthority(r));
                }

                //autentifier user
                UsernamePasswordAuthenticationToken authenticationToken =
                        new UsernamePasswordAuthenticationToken(username, null, authorities);

                //authentification
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);

                //tu passe au filter suivant
                filterChain.doFilter(request, response);


            } catch (Exception e) {
                response.setHeader("error-message", e.getMessage());
                response.sendError(HttpServletResponse.SC_FORBIDDEN); //error 403
            }

        } else {
            filterChain.doFilter(request, response); //passe au suivant
        }
    }
}
