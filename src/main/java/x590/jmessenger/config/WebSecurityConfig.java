package x590.jmessenger.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.session.HttpSessionEventPublisher;
import x590.jmessenger.service.UserService;

import static x590.jmessenger.entity.RoleNames.*;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

	private final ApplicationContext context;

	@Autowired
	public WebSecurityConfig(ApplicationContext context) {
		this.context = context;
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}


	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http.csrf(CsrfConfigurer::disable)
				.authenticationProvider(authenticationProvider())

				.authorizeHttpRequests(auth -> auth
						.requestMatchers("/register").permitAll()
						.requestMatchers("/users").hasAuthority(ADMIN)
						.requestMatchers("/user/**", "/users/**", "/chats/**").authenticated()
						.anyRequest().permitAll())

				.formLogin(formLogin -> formLogin
						.loginPage("/login")
						.loginProcessingUrl("/authentication")
						.defaultSuccessUrl("/")
						.permitAll())

				.logout(logout -> logout
						.logoutUrl("/logout")
						.logoutSuccessUrl("/")
						.permitAll())

				.exceptionHandling(configurer -> configurer.accessDeniedPage("/error/403"))

				.sessionManagement(management -> management
						.maximumSessions(1)
						.sessionRegistry(sessionRegistry())
				);

		return http.build();
	}

	@Bean
	public AuthenticationProvider authenticationProvider() {
		var authProvider = new DaoAuthenticationProvider(passwordEncoder());
		authProvider.setUserDetailsService(context.getBean(UserService.class));
		return authProvider;
	}

	@Bean
	public SessionRegistry sessionRegistry() {
		return new SessionRegistryImpl();
	}

	@Bean
	public ServletListenerRegistrationBean<HttpSessionEventPublisher> httpSessionEventPublisher() {
		return new ServletListenerRegistrationBean<>(new HttpSessionEventPublisher());
	}
}
