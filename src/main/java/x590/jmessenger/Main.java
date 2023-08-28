package x590.jmessenger;

import org.apache.catalina.LifecycleException;
import org.apache.catalina.startup.Tomcat;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;
import x590.jmessenger.config.JMessengerConfig;

public class Main {

	public static void main(String[] args) throws LifecycleException {
		var applicationContext = new AnnotationConfigWebApplicationContext();
		applicationContext.register(JMessengerConfig.class);

        var tomcat = new Tomcat();
		tomcat.setPort(8080);
		tomcat.getConnector();

		var tomcatContext = tomcat.addContext("", null);
		var servlet = Tomcat.addServlet(tomcatContext, "jmessenger", new DispatcherServlet(applicationContext));
		servlet.addMapping("/*");

		tomcat.start();
	}
}
