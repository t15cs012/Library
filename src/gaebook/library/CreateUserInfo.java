package gaebook.library;

import gaebook.util.*;
import java.io.IOException;
import javax.servlet.http.*;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.context.Context;

@SuppressWarnings("serial")
public class CreateUserInfo extends HttpServlet {

	public void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
		Context context = new VelocityContext();

		String template = req.getHeader("User-Agent").contains("iPhone") ? "WEB-INF/createUserInfo.iphone.vm"
				: "WEB-INF/createUserInfo.vm";

		res.setContentType("text/html");
		res.setCharacterEncoding("utf-8");
		Renderer.render(template, context, res.getWriter());

	}
}
