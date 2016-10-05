package sig.products.api.web;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

public class ApplicationContextListener implements ServletContextListener {

	@Override
	public void contextInitialized(ServletContextEvent arg0) {
		System.out.println("Hello initialized");
		
	}

	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		System.out.println("Hello destroyed");
		
	}


}
