package x590.jmessenger.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.filter.HiddenHttpMethodFilter;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewResolverRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.spring6.templateresolver.SpringResourceTemplateResolver;
import org.thymeleaf.spring6.view.ThymeleafViewResolver;

import java.util.List;

import static x590.jmessenger.config.Resources.*;

@Configuration
@EnableWebMvc
@ComponentScan("x590.jmessenger")
@PropertySource("classpath:application.properties")
public class WebMvcConfig implements WebMvcConfigurer {
	private final ApplicationContext context;

	@Autowired
	public WebMvcConfig(ApplicationContext context) {
		this.context = context;
	}

	@Bean
	public SpringResourceTemplateResolver templateResolver() {
		var templateResolver = new SpringResourceTemplateResolver();
		templateResolver.setApplicationContext(context);
		templateResolver.setCharacterEncoding("UTF-8");
		templateResolver.setPrefix("/WEB-INF/views/");
		templateResolver.setSuffix(".html");
		templateResolver.setCacheable(false);
		return templateResolver;
	}

	@Bean
	public SpringTemplateEngine templateEngine() {
		var templateEngine = new SpringTemplateEngine();
		templateEngine.addTemplateResolver(templateResolver());
		templateEngine.setEnableSpringELCompiler(true);
		return templateEngine;
	}

	@Override
	public void configureViewResolvers(ViewResolverRegistry registry) {
		var resolver = new ThymeleafViewResolver();
		resolver.setTemplateEngine(templateEngine());
		registry.viewResolver(resolver);
	}

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler(STYLE_PATTERN).addResourceLocations("/WEB-INF/views/" + STYLE + "/");
		registry.addResourceHandler(SCRIPT_PATTERN).addResourceLocations("/WEB-INF/views/" + SCRIPT + "/");
		registry.addResourceHandler(IMAGE_PATTERN).addResourceLocations("/WEB-INF/views/" + IMAGE + "/");
	}

	@Bean
	public FilterRegistrationBean<HiddenHttpMethodFilter> hiddenHttpMethodFilter() {
		var filterBean = new FilterRegistrationBean<>(new HiddenHttpMethodFilter());
		filterBean.setUrlPatterns(List.of("/*"));
		return filterBean;
	}
}
