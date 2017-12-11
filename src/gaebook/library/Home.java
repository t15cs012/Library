package gaebook.library;

import gaebook.util.*;
import javax.jdo.*;
import com.google.appengine.api.users.*;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import javax.servlet.http.*;
import java.util.logging.Logger;
import java.lang.ExceptionInInitializerError;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.context.Context;

/* ブログ システムのホーム画面．ログインしていればそのユーザのブログを列挙
 * していなければすべてのブログを列挙
 */
@SuppressWarnings("serial")
public class Home extends HttpServlet {
	private static Logger log = Logger.getLogger(Home.class.getName());
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
	throws IOException {
		UserService userService = UserServiceFactory.getUserService();
		User user = userService.getCurrentUser();
		log.info("hello");

		PersistenceManager pm = null;
		try {
			pm = PMF.get().getPersistenceManager();
			Query query = pm.newQuery(Blog.class);

			String signInUrl = userService.createLoginURL(req.getRequestURI());
			String signOutUrl = userService.createLogoutURL(req.getRequestURI());

			Context context = new VelocityContext();

			String template =
			    req.getHeader("User-Agent").contains("iPhone") ?
			    "WEB-INF/home.iphone.vm" :
			    "WEB-INF/home.vm" ;

			resp.setContentType("text/html");
			resp.setCharacterEncoding("utf-8");
			Renderer.render(template, context, resp.getWriter());
		}
		catch (ExceptionInInitializerError e) {
			e.printStackTrace();
		}
		finally {
			if (pm != null && !pm.isClosed())
				pm.close();
		}

	}
}
