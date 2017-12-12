package gaebook.library;

import gaebook.util.*;
import javax.jdo.*;
import java.io.IOException;
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

	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		log.info("hello");

		PersistenceManager pm = null;
		try {
			pm = PMF.get().getPersistenceManager();

			Context context = new VelocityContext();

			String template = req.getHeader("User-Agent").contains("iPhone") ? "WEB-INF/home.iphone.vm"
					: "WEB-INF/home.vm";

			resp.setContentType("text/html");
			resp.setCharacterEncoding("utf-8");
			Renderer.render(template, context, resp.getWriter());
		} catch (ExceptionInInitializerError e) {
			e.printStackTrace();
		} finally {
			if (pm != null && !pm.isClosed())
				pm.close();
		}

	}
}
