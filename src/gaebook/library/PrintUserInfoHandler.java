package gaebook.library;

import java.io.*;

import javax.servlet.http.*;


@SuppressWarnings("serial")
public class PrintUserInfoHandler extends HttpServlet {
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

		response.setContentType("text/html; charset=Shift_JIS");
		PrintWriter out = response.getWriter();

		out.println("<html>");
		out.println("<head>");
		out.println("<title>ユーザー認証テスト</title>");
		out.println("</head>");
		out.println("<body>");

		out.println("<p>テストページ1</p>");

		out.println("</body>");
		out.println("</html>");
	}

}
