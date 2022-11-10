package io.lanu.travian.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class WebSecurity extends WebSecurityConfigurerAdapter {
    private Environment environment;
    private UsersService usersService;
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    public WebSecurity(Environment environment, UsersService usersService, BCryptPasswordEncoder bCryptPasswordEncoder)
    {
        this.environment = environment;
        this.usersService = usersService;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http.csrf().disable();
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        http.authorizeRequests().antMatchers("/api/auth/**", "/api/users/fake").permitAll();

        http.authorizeRequests().antMatchers(HttpMethod.GET, "/api/users") // just for a demo purpose
                .hasAnyAuthority("ROLE_USER", "ROLE_ADMIN");


        http.authorizeRequests().anyRequest().authenticated();  // should be uncommented for security
        //http.authorizeRequests().anyRequest().permitAll(); // should be deleted

        http.addFilter(getAuthenticationFilter());
        http.addFilterBefore(new CustomAuthorizationFilter(environment), UsernamePasswordAuthenticationFilter.class);
    }

    private CustomAuthenticationFilter getAuthenticationFilter() throws Exception
    {
        CustomAuthenticationFilter authenticationFilter = new CustomAuthenticationFilter(usersService, environment, authenticationManager());
        authenticationFilter.setAuthenticationManager(authenticationManager());
        authenticationFilter.setFilterProcessesUrl("/api/auth/login");
        return authenticationFilter;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(usersService).passwordEncoder(bCryptPasswordEncoder);
    }
}
