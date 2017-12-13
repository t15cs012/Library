package gaebook.util;

import java.io.*;
import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.servlet.*;
import javax.servlet.http.*;

import gaebook.library.UserInfo;

@SuppressWarnings("serial")
public class LoginCheck extends HttpServlet {

	public void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {

		res.setContentType("text/html; charset=utf-8");

		String user = req.getParameter("user"); // ユーザID
		String pass = req.getParameter("pass"); // パスワード

		HttpSession session = req.getSession(true);
		PersistenceManager pm = null;

		try {
			if (user.isEmpty() || pass.isEmpty()) {
				/* 認証に失敗したら、ログイン画面に戻す */
				session.setAttribute("status", "Not Auth");
				res.sendRedirect("/Login");
			}
			else {
				pm = PMF.get().getPersistenceManager();
				Query query = pm.newQuery(UserInfo.class);
				query.setFilter("key == user && password == pass"); // ユーザIDとパスワードが一致するか
				query.declareParameters("String user, String pass");
				@SuppressWarnings("unchecked")
				List<UserInfo> list = (List<UserInfo>) query.execute(user, pass);

				if (list.isEmpty()) {
					/* 認証に失敗したら、ログイン画面に戻す */
					session.setAttribute("status", "Not Auth");
					res.sendRedirect("/Login");
				}
				else {
					/* 認証済みにセット */
					session.setAttribute("login", "OK");

					/* 本来のアクセス先へ飛ばす */
					String target = (String) session.getAttribute("target");
					res.sendRedirect(target);
				}
			}
		} catch (ExceptionInInitializerError e) {
			e.printStackTrace();
		} finally {
			if (pm != null && !pm.isClosed())
				pm.close();
		}

	}

}