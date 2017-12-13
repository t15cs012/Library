package gaebook.library;

import java.io.*;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.*;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.context.Context;

import gaebook.util.Renderer;

// 新しい利用者を登録
@SuppressWarnings("serial")
public class CreateUserInfo extends HttpServlet {
	static Logger log = Logger.getLogger(CreateBookInfoHandler.class.getName());

	public void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
		res.setContentType("text/html; charset=utf-8");

		Context context = new VelocityContext();

		/* iPhone とそれ以外でテンプレートを切り替え */
		String template = req.getHeader("User-Agent").contains("iPhone") ? "WEB-INF/createUserInfo.iphone.vm"
				: "WEB-INF/createUserInfo.vm";

		res.setContentType("text/html");
		res.setCharacterEncoding("utf-8");
		Renderer.render(template, context, res.getWriter());
	}

}
