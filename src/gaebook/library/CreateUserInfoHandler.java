package gaebook.library;

import java.io.*;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.jdo.Transaction;
import javax.servlet.http.*;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.context.Context;

import gaebook.util.ErrorPage;
import gaebook.util.PMF;
import gaebook.util.Renderer;
import gaebook.util.TransactionManager;
import gaebook.util.ErrorPage.ErrorPageException;
import gaebook.util.TransactionManager.Body;

// 新しいブログを作成
@SuppressWarnings("serial")
public class CreateUserInfoHandler extends HttpServlet {
	static Logger log = Logger.getLogger(CreateBookInfoHandler.class.getName());

	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		log.info("hello");

	}

	public void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
		res.setContentType("text/html; charset=utf-8");
		final String id = (String) req.getSession().getAttribute("user");
		final String pass = (String) req.getSession().getAttribute("pass");
		final String name = (String) req.getSession().getAttribute("name");
		final String eMail = (String) req.getSession().getAttribute("email");
		final String num = (String) req.getSession().getAttribute("num");

		if (!UserInfo.createUserIfNotExist(id, pass, name, eMail, num))
			ErrorPage.create(res, "指定されたブログ名は利用できません．" + "別の名前を試してみてください", "/createBlog.html");
		
		// もとのページにリダイレクト
		res.sendRedirect("/home");
	}

}
