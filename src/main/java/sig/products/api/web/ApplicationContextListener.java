package sig.products.api.web;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class ApplicationContextListener implements ServletContextListener {

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		System.out.println("Servlet initialized");
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		System.out.println("Servlet destroyed");
		
	}
}
