package gaebook.library;

import gaebook.util.*;

import java.io.*;
import java.nio.charset.Charset;
import java.util.*;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.*;

import org.apache.commons.fileupload.servlet.*;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.context.Context;

import org.apache.commons.fileupload.*;

// 新しい図書情報を作成
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
		logger.info("CreateBookInfoHandler start.");

		Map<String, Object> map = readMultiform(req);
		final String ISBN = (String) map.get("isbn");
		final String name = (String) map.get("name");
		final String kanaName = (String) map.get("kanaName");
		final String author = (String) map.get("author");
		final String kanaAuthor = (String) map.get("kanaAuthor");
		final String publisher = (String) map.get("publisher");
		final String kanaPublisher = (String) map.get("kanaPublisher");

		Context context = new VelocityContext();

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

		List<ImageEntity> entity = new ArrayList<ImageEntity>();
		for (TemporalImage tempImage : tempImages) {
			entity.add(new ImageEntity(tempImage.bytes, tempImage.name));
		}

		context.put("ISBN", ISBN);
		context.put("name", name);
		context.put("kanaName", kanaName);
		context.put("author", author);
		context.put("kanaAuthor", kanaAuthor);
		context.put("publisher", publisher);
		context.put("kanaPublisher", kanaPublisher);

		boolean errorFormat = false; // 入力された情報にフォーマットエラーがあるか保存するフラグ

		if (ISBN.isEmpty() || !checkFormatISBN(ISBN)) {
			context.put("isbnError", "978で始まる13桁か12桁の数字で入力してください.");
			errorFormat = true;
		}

		if (name.isEmpty() || name.length() < 1) {
			context.put("nameError", "適切に入力してください.");
			errorFormat = true;
		}

		if (author.isEmpty() || author.length() < 1) {
			context.put("authorError", "適切に入力してください.");
			errorFormat = true;
		}

		if (publisher.isEmpty() || publisher.length() < 1) {
			context.put("publisherError", "適切に入力してください.");
			errorFormat = true;
		}

		boolean registComplete = false; // 情報を登録できるか保存するフラグ

		if (errorFormat) {
		}
		else if (!BookInfo.createBookInfoIfNotExist(ISBN, name, kanaName, author, kanaAuthor, publisher, kanaPublisher,
				entity)) {
			context.put("ISBNError", "指定されたユーザIDは利用できません．別の名前を試してみてください.");
		}
		else {
			registComplete = true;
		}

		if (registComplete) {
			logger.info("registComplete redirect.");

			context.put("infoName", "図書情報");

			/* iPhone とそれ以外でテンプレートを切り替え */
			String template = req.getHeader("User-Agent").contains("iPhone") ? "WEB-INF/registComplete.iphone.vm"
					: "WEB-INF/registComplete.vm";

			res.setContentType("text/html");
			res.setCharacterEncoding("utf-8");
			Renderer.render(template, context, res.getWriter());
		}
		else {
			logger.info("CreateBookInfoHandler redirect.");

			/* iPhone とそれ以外でテンプレートを切り替え */
			String template = req.getHeader("User-Agent").contains("iPhone") ? "WEB-INF/createBookInfo.iphone.vm"
					: "WEB-INF/createBookInfo.vm";

			res.setContentType("text/html");
			res.setCharacterEncoding("utf-8");
			Renderer.render(template, context, res.getWriter());
		}
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

	/* ISBNコードのフォーマットチェック 13桁か12桁の978で始まる数字 */
	public boolean checkFormatISBN(String str) {
		Pattern p = Pattern.compile("^978\\d{9,10}$");
		Matcher m = p.matcher(str);
		return m.find();
	}
}
