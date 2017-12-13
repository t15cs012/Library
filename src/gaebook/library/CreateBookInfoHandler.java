package gaebook.library;

import gaebook.util.*;

import java.io.*;
import java.nio.charset.Charset;
import java.util.*;
import java.util.logging.Logger;

import javax.servlet.http.*;

import org.apache.commons.fileupload.servlet.*;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.context.Context;
import org.apache.commons.fileupload.*;

// 新しい書籍情報を作成
@SuppressWarnings("serial")
public class CreateBookInfoHandler extends HttpServlet {
	static Logger logger = Logger.getLogger(CreateBookInfoHandler.class.getName());

	public static class TemporalImage {
		byte[] bytes;
		String name;

		public TemporalImage(byte[] bytes, String name) {
			super();
			this.bytes = bytes;
			this.name = name;
		}
	}

	public void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
		final int ISBN = Integer.parseInt(req.getParameter("ISBN"));
		final String name = req.getParameter("name");
		final String author = req.getParameter("author");
		final String publisher = req.getParameter("publisher");

		Map<String, Object> map = readMultiform(req);

		final List<String> preservePic = new ArrayList<String>();
		for (String key : map.keySet()) {
			if (key.startsWith("preservePic"))
				preservePic.add((String) map.get(key));
		}
		final List<TemporalImage> tempImages = new ArrayList<TemporalImage>();
		for (String key : map.keySet()) {
			if (key.startsWith("upfile"))
				tempImages.add((TemporalImage) map.get(key));

		}
		TemporalImage tempImage = tempImages.get(0);

		Context context = new VelocityContext();

		// context.put("userID", userID);
		// context.put("name", name);
		// context.put("email", email);
		// context.put("phoneNum", phoneNum);

		// if (userID.isEmpty() || pass.isEmpty() || passConf.isEmpty() ||
		// name.isEmpty() || email.isEmpty()
		// || phoneNum.isEmpty()) {
		// if (userID.isEmpty())
		// context.put("userIDError", "半角英数値のみの4文字以上で入力してください.");
		// if (pass.isEmpty())
		// context.put("passError", "半角英数値のみの4文字以上で入力してください.");
		// if (passConf.isEmpty())
		// context.put("passConfError", "半角英数値のみの4文字以上で入力してください.");
		// if (name.isEmpty())
		// context.put("nameError", "半角英数値, 漢字, 平仮名, カタカナで入力してください.");
		// if (email.isEmpty())
		// context.put("emailError", "正しく入力してください.");
		// if (phoneNum.isEmpty())
		// context.put("phoneNumError", "空白やハイフンを入れずに正しく入力してください.");
		//
		// }
		// else if (!checkFormatUserID(userID)) {
		// context.put("userIDError", "半角英数値のみの4文字以上で入力してください.");
		// }
		// else if (userID.length() < 4) {
		// context.put("userIDError", "4文字以上で入力してください.");
		// }
		// else if (!checkFormatPass(pass)) {
		// context.put("passError", "半角英数値のみの4文字以上で入力してください.");
		// }
		// else if (pass.length() < 4) {
		// context.put("passError", "4文字以上で入力してください.");
		// }
		// else if (!pass.equals(passConf)) {
		// context.put("passError", "2つのパスワードが一致しません.");
		// }
		// else if (!checkFormatName(name)) {
		// context.put("nameError", "半角英数値, 漢字, 平仮名, カタカナで入力してください.");
		// }
		// else if (!checkFormatEmail(email)) {
		// context.put("emailError", "正しく入力してください.");
		// }
		// else if (!checkFormatPhoneNum(phoneNum)) {
		// context.put("phoneNumError", "空白や‐を入れずに正しく入力してください.");
		// }
		// else
		if (!BookInfo.createBookInfoIfNotExist(ISBN, name, author, publisher, new ImageEntity(tempImage.bytes, 
                tempImage.name))) {
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

	private Map<String, Object> readMultiform(HttpServletRequest req) throws IOException {
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			ServletFileUpload upload = new ServletFileUpload();
			FileItemIterator iterator = upload.getItemIterator(req);
			while (iterator.hasNext()) {
				FileItemStream item = iterator.next();
				InputStream stream = item.openStream();
				if (item.isFormField()) { /* フォームデータ */
					logger.fine("Got a form field: " + item.getFieldName());
					map.put(item.getFieldName(), new String(ImageUtil.readBytes(stream), Charset.forName("UTF-8")));
				}
				else { /* ファイルアップロード */
					logger.fine("Got an uploaded file: " + item.getFieldName() + ", name = " + item.getName());
					byte[] bytes = ImageUtil.readBytes(stream);
					if (bytes.length == 0)
						continue;
					map.put(item.getFieldName(), new TemporalImage(bytes, item.getName()));
				}
			}
			return map;
		} catch (FileUploadException e) {
			throw new IOException(e);
		}
	}

}
