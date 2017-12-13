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
public class CreateUserInfoHandler extends HttpServlet {
	static Logger log = Logger.getLogger(CreateBookInfoHandler.class.getName());

	public void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
		res.setContentType("text/html; charset=utf-8");

		final String userID = req.getParameter("user");
		final String pass = req.getParameter("pass");
		final String passConf = req.getParameter("passConf");
		final String name = req.getParameter("name");
		final String email = req.getParameter("email");
		final String phoneNum = req.getParameter("phoneNum");

		Context context = new VelocityContext();
		
		context.put("userID", userID);
		context.put("name", name);
		context.put("email", email);
		context.put("phoneNum", phoneNum);

		if (userID.isEmpty() || pass.isEmpty() || passConf.isEmpty() || name.isEmpty() || email.isEmpty()
				|| phoneNum.isEmpty()) {
			if (userID.isEmpty())
				context.put("userIDError", "半角英数値のみの4文字以上で入力してください.");
			if (pass.isEmpty())
				context.put("passError", "半角英数値のみの4文字以上で入力してください.");
			if (passConf.isEmpty())
				context.put("passConfError", "半角英数値のみの4文字以上で入力してください.");
			if (name.isEmpty())
				context.put("nameError", "半角英数値, 漢字, 平仮名, カタカナで入力してください.");
			if (email.isEmpty())
				context.put("emailError", "正しく入力してください.");
			if (phoneNum.isEmpty())
				context.put("phoneNumError", "空白やハイフンを入れずに正しく入力してください.");

		}
		else if (!checkFormatUserID(userID)) {
			context.put("userIDError", "半角英数値のみの4文字以上で入力してください.");
		}
		else if (userID.length() < 4) {
			context.put("userIDError", "4文字以上で入力してください.");
		}
		else if (!checkFormatPass(pass)) {
			context.put("passError", "半角英数値のみの4文字以上で入力してください.");
		}
		else if (pass.length() < 4) {
			context.put("passError", "4文字以上で入力してください.");
		}
		else if (!pass.equals(passConf)) {
			context.put("passError", "2つのパスワードが一致しません.");
		}
		else if (!checkFormatName(name)) {
			context.put("nameError", "半角英数値, 漢字, 平仮名, カタカナで入力してください.");
		}
		else if (!checkFormatEmail(email)) {
			context.put("emailError", "正しく入力してください.");
		}
		else if (!checkFormatPhoneNum(phoneNum)) {
			context.put("phoneNumError", "空白や‐を入れずに正しく入力してください.");
		}
		else if (!UserInfo.createUserInfoIfNotExist(userID, pass, name, email, phoneNum)) {
			context.put("userIDError", "指定されたユーザIDは利用できません．別の名前を試してみてください.");
		}
		else
			res.sendRedirect("/home");

		/* iPhone とそれ以外でテンプレートを切り替え */
		String template = req.getHeader("User-Agent").contains("iPhone") ? "WEB-INF/createUserInfo.iphone.vm"
				: "WEB-INF/createUserInfo.vm";

		res.setContentType("text/html");
		res.setCharacterEncoding("utf-8");
		Renderer.render(template, context, res.getWriter());
	}

	public boolean checkFormatUserID(String str) {
		Pattern p = Pattern.compile("^[0-9a-zA-Z]*$");
		Matcher m = p.matcher(str);
		return m.find();
	}

	public boolean checkFormatPass(String str) {
		return checkFormatUserID(str);
	}

	public boolean checkFormatName(String str) {
		Pattern p = Pattern.compile("^.*(?![0-9]).*$");
		Matcher m = p.matcher(str);
		return m.find();
	}

	public boolean checkFormatEmail(String str) {
		Pattern p = Pattern.compile("^\\w+([-+._]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$");
		Matcher m = p.matcher(str);
		return m.find();
	}

	public boolean checkFormatPhoneNum(String str) {
		Pattern p = Pattern.compile("^0\\d{8,9}$");
		Matcher m = p.matcher(str);
		return m.find();
	}

}
