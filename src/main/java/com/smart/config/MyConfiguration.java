package com.smart.config;

import com.smart.dao.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;

@Configuration
@EnableWebSecurity
public class MyConfiguration  {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserDetailsServiceImpl userDetailsService;

     @Bean
    public UserDetailsService getUserDetailService(){
        this.userDetailsService=new UserDetailsServiceImpl();
        /* UserDetails userDetails= org.springframework.security.core.userdetails.User.withUsername("vishal@gmail.com").password(passwordEncoder().encode("vishal")).roles("USER").build();
         UserDetails userDetails1=org.springframework.security.core.userdetails.User.withUsername("vishal1@gmail.com").password(passwordEncoder().encode("vishal1")).roles("ADMIN").build();
         return new InMemoryUserDetailsManager(userDetails,userDetails1);*/
      return userDetailsService;
     }
     @Bean
     public BCryptPasswordEncoder passwordEncoder(){
         return new BCryptPasswordEncoder();
     }
     @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider(){
         DaoAuthenticationProvider daoAuthenticationProvider=new DaoAuthenticationProvider();
         daoAuthenticationProvider.setUserDetailsService(this.getUserDetailService());
         daoAuthenticationProvider.setPasswordEncoder(this.passwordEncoder());
         return daoAuthenticationProvider;
     }
     // Configure Method

    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(this.daoAuthenticationProvider());
    }
    @Bean
     public SecurityFilterChain configure(HttpSecurity http) throws Exception{
         http.csrf().disable().authorizeHttpRequests().requestMatchers("/admin/**").hasRole("ADMIN")
                         .requestMatchers("/user/**").hasRole("USER").requestMatchers("/**")
                         .permitAll().anyRequest().authenticated().and().formLogin().loginPage("/signin")
                         .loginProcessingUrl("/dologin")
                          .defaultSuccessUrl("/user/index")
                           ;
        /* http.authorizeHttpRequests().requestMatchers("/admin/**").hasRole("ADMIN")
                 .requestMatchers("/user/**").hasRole("USER")
                 .requestMatchers("/**").permitAll().and().formLogin().and().csrf().disable();*/
         return http.build();
     }
  /*  @Override
    protected void configure1(HttpSecurity http) throws Exception {
        http.authorizeRequests().antMatchers("/admin/**").hasRole("ADMIN")
                .antMatchers("/user/**").hasRole("USER")
                .antMatchers("/**").permitAll().and().formLogin().and().csrf().disable();
    }*/
}


