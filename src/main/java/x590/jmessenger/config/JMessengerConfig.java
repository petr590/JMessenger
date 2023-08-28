package x590.jmessenger.config;

import com.mysql.cj.jdbc.MysqlDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ViewResolverRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.spring6.templateresolver.SpringResourceTemplateResolver;
import org.thymeleaf.spring6.view.ThymeleafViewResolver;

import javax.sql.DataSource;

@Configuration
@EnableWebMvc
@EnableAutoConfiguration
@ComponentScan("x590.jmessenger")
@EntityScan("x590.jmessenger.entities")
@EnableJpaRepositories(basePackages = "x590.jmessenger.repository")
public class JMessengerConfig implements WebMvcConfigurer {

	private final ApplicationContext context;

	@Autowired
	public JMessengerConfig(ApplicationContext context) {
		this.context = context;
	}


	@Bean
	public DataSource dataSource() {
		var dataSource = new MysqlDataSource();
		dataSource.setUrl("jdbc:mysql://localhost/jmessenger");
		dataSource.setUser("root");
		dataSource.setPassword("root");
		return dataSource;
	}

	@Bean
	public SpringResourceTemplateResolver templateResolver() {
		var templateResolver = new SpringResourceTemplateResolver();
		templateResolver.setApplicationContext(context);
		templateResolver.setPrefix("classpath:/views/");
		templateResolver.setCacheable(false);
		templateResolver.getRawTemplateModePatternSpec().addPattern("*.rjs");
		return templateResolver;
	}

//	@Bean
//	public SpringResourceTemplateResolver rawTemplateResolver() {
//		var templateResolver = templateResolver();
//		templateResolver.setTemplateMode(TemplateMode.RAW);
//		return templateResolver;
//	}

	@Bean
	public SpringTemplateEngine templateEngine() {
		var templateEngine = new SpringTemplateEngine();
		templateEngine.addTemplateResolver(templateResolver());
//		templateEngine.addTemplateResolver(rawTemplateResolver());
		templateEngine.setEnableSpringELCompiler(true);
		return templateEngine;
	}

	@Override
	public void configureViewResolvers(ViewResolverRegistry registry) {
		var resolver = new ThymeleafViewResolver();
		resolver.setTemplateEngine(templateEngine());
		registry.viewResolver(resolver);
	}
}
