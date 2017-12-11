import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;

public class Login1 extends HttpServlet {
	public void doGet(HttpServletRequest request, HttpServletResponse response)
	throws IOException, ServletException {

		response.setContentType("text/html; charset=Shift_JIS");
		PrintWriter out = response.getWriter();

		out.println("<html>");
		out.println("<head>");
		out.println("<title>ログインページ</title>");
		out.println("</head>");
		out.println("<body>");

		out.println("<h1>ログイン画面</h1>");

		out.println("<p>本来はここでログインするためのフォームが表示されます</p>");

		HttpSession session = request.getSession(false);

		if (session == null) {
			/* セッションが開始されずにここへ来た場合は無条件でエラー表示 */
			out.println("<p>不正なアクセスです</p>");
		}
		else {
			/* 今回は無条件で認証を許可する */
			out.println("<p>認証が行われました</p>");

			/* 認証済みにセット */
			session.setAttribute("login", "OK");

			/* 本来のアクセス先へのリンクを設定 */
			String target = (String)session.getAttribute("target");
			out.println("<a href=¥"" + target + "¥">ページを表示する</a>");
		}

		out.println("</body>");
		out.println("</html>");
	}
}