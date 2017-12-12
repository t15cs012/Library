package gaebook.library;

import gaebook.util.ErrorPage;
import java.io.IOException;
import java.util.logging.Logger;
import javax.servlet.http.*;
import com.google.appengine.api.users.*;


// 新しいブログを作成
public class CreateLendingBookInfoHandler extends HttpServlet {
	static Logger logger = Logger.getLogger(CreateLendingBookInfoHandler.class
	                                        .getName());

	public void doPost(HttpServletRequest req, HttpServletResponse res)
	throws IOException {
		logger.info("doPost");

		res.setContentType("text/html; charset=Shift_JIS");
		String target = req.getRequestURI();
		HttpSession session = req.getSession(false);

		/* まだ認証されていない */
		if (session == null) {
			session = req.getSession(true);

			res.sendRedirect("/Login");
		}
		else {
			Object loginCheck = session.getAttribute("login");
			/* まだ認証されていない */
			if (loginCheck == null)
				res.sendRedirect("/Login");
		}

        // 処理
	}

}
