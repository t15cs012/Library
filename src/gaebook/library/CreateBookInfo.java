package gaebook.library;

import java.io.*;
import java.util.logging.Logger;

import javax.servlet.http.*;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.context.Context;

import gaebook.util.Renderer;

// 新しい図書情報登録ページへリダイレクト
@SuppressWarnings("serial")
public class CreateBookInfo extends HttpServlet {
	static Logger logger = Logger.getLogger(CreateBookInfoHandler.class.getName());

	public void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
		logger.info("Render CreateBookInfo page.");
		
		res.setContentType("text/html; charset=utf-8");

		Context context = new VelocityContext();

		/* iPhone とそれ以外でテンプレートを切り替え */
		String template = req.getHeader("User-Agent").contains("iPhone") ? "WEB-INF/createBookInfo.iphone.vm"
				: "WEB-INF/createBookInfo.vm";

		res.setContentType("text/html");
		res.setCharacterEncoding("utf-8");
		Renderer.render(template, context, res.getWriter());
	}

}
