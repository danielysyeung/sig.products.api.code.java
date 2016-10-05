package sig.products.api.web;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

public class ApplicationContextListener implements ServletContextListener {

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		System.out.println("Hello initialized");
		// sce.getServletContext().setAttribute(arg0, arg1);
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		System.out.println("Hello destroyed");
		
	}


}
