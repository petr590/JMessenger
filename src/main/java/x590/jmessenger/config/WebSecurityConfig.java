package x590.jmessenger.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import x590.jmessenger.service.UserService;

import static x590.jmessenger.entity.RoleNames.*;
import static x590.jmessenger.config.Resources.*;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

	private final ApplicationContext context;

	@Autowired
	public WebSecurityConfig(ApplicationContext context) {
		this.context = context;
	}

	@Bean
	public UserDetailsService users() {
		UserDetails user = User.builder()
				.username("user")
				.password("{noop}user")
				.roles(USER)
				.build();

		UserDetails admin = User.builder()
				.username("admin")
				.password("{noop}admin")
				.roles(USER, ADMIN)
				.build();

		return new InMemoryUserDetailsManager(user, admin);
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}


	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http.csrf(AbstractHttpConfigurer::disable)
//				.exceptionHandling(exception -> exception.authenticationEntryPoint(unauthorizedHandler))
//				.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.authenticationProvider(authenticationProvider())

				.authorizeHttpRequests(auth -> auth
						.requestMatchers("/register").anonymous()
						.requestMatchers("/user", "/user/**", "/chats", "/chats/**").authenticated()
						.requestMatchers("/users", "/users/**").hasAuthority(ADMIN)
						.requestMatchers(STYLE_PATTERN, SCRIPT_PATTERN, IMAGE_PATTERN).permitAll()
						.anyRequest().permitAll())

				.formLogin(configurer -> configurer
						.loginPage("/login")
						.loginProcessingUrl("/authentication")
						.defaultSuccessUrl("/")
						.permitAll())

				.logout(configurer -> configurer
						.logoutUrl("/logout")
						.logoutSuccessUrl("/")
						.permitAll());

		return http.build();
	}

	@Bean
	public AuthenticationProvider authenticationProvider() {
		var authProvider = new DaoAuthenticationProvider();

		authProvider.setUserDetailsService(context.getBean(UserService.class));
		authProvider.setPasswordEncoder(passwordEncoder());

		return authProvider;
	}
}
