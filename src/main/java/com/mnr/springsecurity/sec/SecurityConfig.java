package com.mnr.springsecurity.sec;

import com.mnr.springsecurity.sec.entities.AppUser;
import com.mnr.springsecurity.sec.filters.JwtAuthenticationFilter;
import com.mnr.springsecurity.sec.filters.JwtAuthorisationFilter;
import com.mnr.springsecurity.sec.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.ArrayList;
import java.util.Collection;


@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    private AccountService accountServie;


    //une fois username & password saisi, appeler cette methode
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {

        //UserDetailsService interface qui implemente cette classe
        auth.userDetailsService(new UserDetailsService() {
            @Override
            public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
                AppUser appUser = accountServie.loadUserByUsername(username);

                //convert to a colllection of authorities
                Collection<GrantedAuthority> authorities = new ArrayList<>();
                appUser.getAppRoles().forEach(r -> {
                    authorities.add(new SimpleGrantedAuthority(r.getRoleName()));
                });


                return new User(appUser.getPassword(), appUser.getPassword(), authorities);
            }
        });


    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        //disable csrf=statfull security= utilisation des sessions
        http.csrf().disable();

        //authentication stateless= use token
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        //disactiver les frames => seulement pour h2 database
        http.headers().frameOptions().disable();

        //permettre tous ce qui h2-console
        http.authorizeRequests().antMatchers("/h2-console/**").permitAll();

        //autoriser tout
        //http.authorizeRequests().anyRequest().permitAll();

        //authenticated
        //http.formLogin(); //afficher le formulaire d authentification en cas de statfull
        http.authorizeRequests().anyRequest().authenticated();

        http.addFilter(new JwtAuthenticationFilter(authenticationManagerBean()));

        http.addFilterBefore(new JwtAuthorisationFilter(), UsernamePasswordAuthenticationFilter.class);


    }

    @Bean  //objet authenticationManager, on peut l'injecter là ou on veux
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }
}
