package x590.jmessenger.config;

import org.springframework.lang.NonNull;
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

public class JMessengerInitializer extends AbstractAnnotationConfigDispatcherServletInitializer {

    @Override
    protected Class<?>[] getRootConfigClasses() {
        return null;
    }

    @Override
    protected Class<?>[] getServletConfigClasses() {
        return new Class[] { JMessengerConfig.class };
    }

    @Override
    protected @NonNull String[] getServletMappings() {
        return new String[] { "/" };
    }
}
