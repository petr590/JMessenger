package x590.jmessenger.config;

import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

public class WebMvcInitializer extends AbstractAnnotationConfigDispatcherServletInitializer {

	@Override
	protected Class<?>[] getRootConfigClasses() {
		return null;
	}

	@Override
	protected Class<?>[] getServletConfigClasses() {
		return new Class[] { WebMvcConfig.class };
	}

	@Override
	protected String[] getServletMappings() {
		return new String[] { "/" };
	}

//	@Override
//	protected void customizeRegistration(ServletRegistration.Dynamic registration) {
//		System.out.println("customizeRegistration");
//		registration.setMultipartConfig(new MultipartConfigElement("", 0x200000, 4193304, 0x200000));
//	}
}
