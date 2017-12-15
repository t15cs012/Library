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
	static Logger logger = Logger.getLogger(CreateBookInfoHandler.class.getName());

	public void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
		logger.info("CreateUserInfoHandler start.");

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

		boolean errorFormat = false; // 入力された情報にフォーマットエラーがあるか保存するフラグ

		/* フォーマットチェク */
		if (userID.isEmpty() || !checkFormatUserID(userID) || userID.length() < 4) {
			context.put("userIDError", "半角英数値のみの4文字以上で入力してください.");
			errorFormat = true;
		}

		if (pass.isEmpty() || !checkFormatPass(pass) || pass.length() < 4) {
			context.put("passError", "半角英数値のみの4文字以上で入力してください.");
			errorFormat = true;
		}
		else if (!pass.equals(passConf)) {
			context.put("passError", "2つのパスワードが一致しません.");
			errorFormat = true;
		}

		if (name.isEmpty() || !checkFormatName(name)) {
			context.put("nameError", "半角英数値, 漢字, 平仮名, カタカナで入力してください.");
			errorFormat = true;
		}

		if (email.isEmpty() || !checkFormatEmail(email)) {
			context.put("emailError", "正しく入力してください.");
			errorFormat = true;
		}

		if (phoneNum.isEmpty() || !checkFormatPhoneNum(phoneNum)) {
			context.put("phoneNumError", "空白やハイフンを入れずに正しく入力してください.");
			errorFormat = true;
		}

		boolean registComplete = false; // 情報を登録できるか保存するフラグ

		if (errorFormat) {
		}
		else if (!UserInfo.createUserInfoIfNotExist(userID, pass, name, email, phoneNum)) {
			context.put("userIDError", "指定されたユーザIDは利用できません．別の名前を試してみてください.");
		}
		else {
			registComplete = true;
		}

		if (registComplete) {
			logger.info("registComplete redirect.");
			
			context.put("infoName", "利用者情報");

			/* iPhone とそれ以外でテンプレートを切り替え */
			String template = req.getHeader("User-Agent").contains("iPhone") ? "WEB-INF/registComplete.iphone.vm"
					: "WEB-INF/registComplete.vm";

			res.setContentType("text/html");
			res.setCharacterEncoding("utf-8");
			Renderer.render(template, context, res.getWriter());
		}
		else {
			logger.info("CreateUserInfoHandler redirect.");
			
			/* iPhone とそれ以外でテンプレートを切り替え */
			String template = req.getHeader("User-Agent").contains("iPhone") ? "WEB-INF/createUserInfo.iphone.vm"
					: "WEB-INF/createUserInfo.vm";

			res.setContentType("text/html");
			res.setCharacterEncoding("utf-8");
			Renderer.render(template, context, res.getWriter());
		}
	}

	/* ユーザIDのフォーマットチェック 英数値のみ */
	public boolean checkFormatUserID(String str) {
		Pattern p = Pattern.compile("^[0-9a-zA-Z]*$");
		Matcher m = p.matcher(str);
		return m.find();
	}

	/* パスワードのフォーマットチェック 英数値のみ */
	public boolean checkFormatPass(String str) {
		Pattern p = Pattern.compile("^[0-9a-zA-Z]*$");
		Matcher m = p.matcher(str);
		return m.find();
	}

	/* 氏名のフォーマットチェック  数字, 記号以外*/
	public boolean checkFormatName(String str) {
		Pattern p = Pattern.compile("\\Q^[0-9]*$\\E");
		Matcher m = p.matcher(str);
		return m.find();
	}

	/* メールアドレスのフォーマットチェック  (英数値と-+._)@(英数値と-.).(英数値と-.)*/
	public boolean checkFormatEmail(String str) {
		Pattern p = Pattern.compile("^\\w+([-+._]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$");
		Matcher m = p.matcher(str);
		return m.find();
	}

	/* 電話番号のフォーマットチェック 9桁か10桁の数字のみ */
	public boolean checkFormatPhoneNum(String str) {
		Pattern p = Pattern.compile("^0\\d{8,9}$");
		Matcher m = p.matcher(str);
		return m.find();
	}

}
