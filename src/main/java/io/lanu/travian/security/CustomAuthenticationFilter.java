package io.lanu.travian.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

public class CustomAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final UsersService usersService;
    private final Environment environment;

    public CustomAuthenticationFilter(UsersService usersService,
                                      Environment environment,
                                      AuthenticationManager authenticationManager) {
        this.usersService = usersService;
        this.environment = environment;
        super.setAuthenticationManager(authenticationManager);
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest req,
                                                HttpServletResponse res) throws AuthenticationException {
        try {

            AuthRequest creds = new ObjectMapper()
                    .readValue(req.getInputStream(), AuthRequest.class);

            return getAuthenticationManager().authenticate(
                    new UsernamePasswordAuthenticationToken(
                            creds.getEmail(),
                            creds.getPassword(),
                            new ArrayList<>())
            );

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest req,
                                            HttpServletResponse res,
                                            FilterChain chain,
                                            Authentication auth) throws IOException, ServletException {
        String email = ((User) auth.getPrincipal()).getUsername();
        UserEntity userDetails = usersService.getUserByEmail(email);

        Date expDate = new Date(System.currentTimeMillis() +
                Long.parseLong(environment.getProperty("authorization.token.expiration_time")));

        String token = Jwts.builder()
                .setSubject(userDetails.getUserId())
                .setExpiration(expDate)
                .signWith(SignatureAlgorithm.HS512, environment.getProperty("authorization.token.secret") )
                .compact();

        new ObjectMapper()
                .writeValue(res.getOutputStream(),
                        AuthResponse.builder()
                                .token(token)
                                .expirationDate(expDate)
                                .email(email)
                                .username(userDetails.getUsername())
                                .userId(userDetails.getUserId())
                                .statisticsId(userDetails.getStatisticsId())
                                .build()
                );
    }
}
