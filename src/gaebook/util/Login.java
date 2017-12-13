package gaebook.util;

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;

@SuppressWarnings("serial")
public class Login extends HttpServlet {
	public void doGet(HttpServletRequest req, HttpServletResponse res)
	throws IOException, ServletException {

		res.setContentType("text/html; charset=utf-8");
		PrintWriter out = res.getWriter();

		out.println("<html>");
	    out.println("<head>");
	    out.println("<title>ログインページ</title>");
	    out.println("</head>");
	    out.println("<body>");

	    out.println("<h1>ログイン画面</h1>");

	    HttpSession session = req.getSession(true);

	    /* 認証失敗から呼び出されたのかどうか */
	    Object status = session.getAttribute("status");

	    if (status != null){
	      out.println("<p>認証に失敗しました</p>");
	      out.println("<p>再度ユーザー名とパスワードを入力して下さい</p>");

	      session.setAttribute("status", null);
	    }

	    out.println("<form method=\"POST\" action=\"/LoginCheck\" name=\"loginform\">");
	    out.println("<table>");
	    out.println("<tr>");
	    out.println("<td>ユーザーID</td>");
	    out.println("<td><input type=\"text\" name=\"user\" size=\"32\"></td>");
	    out.println("</tr>");
	    out.println("<tr>");
	    out.println("<td>パスワード</td>");
	    out.println("<td><input type=\"password\" name=\"pass\" size=\"32\"></td>");
	    out.println("</tr>");
	    out.println("<tr>");
	    out.println("<td><input type=\"submit\" value=\"login\"></td>");
	    out.println("<td><input type=\"reset\" value=\"reset\"></td>");
	    out.println("</tr>");
	    out.println("</table>");
	    out.println("</form>");

	    out.println("</body>");
	    out.println("</html>");
	}
}