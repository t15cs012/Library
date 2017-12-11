package gaebook.library;

import gaebook.util.*;
import gaebook.util.ErrorPage.ErrorPageException;
import gaebook.util.TransactionManager.Body;

import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.*;
import java.util.logging.Logger;

import javax.jdo.*;
import javax.servlet.http.*;

import org.apache.commons.fileupload.servlet.*;
import org.apache.commons.fileupload.*;
import com.google.appengine.api.datastore.Text;
import com.google.appengine.api.users.*;

// 新しいブログを作成
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
		final int ISBN = Integer.parseInt((String) req.getSession().getAttribute("ISBN"));
		final String name = (String) req.getSession().getAttribute("name");
		final String author = (String) req.getSession().getAttribute("author");
		final String publisher = (String) req.getSession().getAttribute("publisher");

		final TemporalImage tempImages;
		try {
			ServletFileUpload upload = new ServletFileUpload();
			FileItemIterator iterator = upload.getItemIterator(req);
			while (iterator.hasNext()) {
				FileItemStream item = iterator.next();
				InputStream stream = item.openStream();
				if (!item.isFormField()) { /* ファイルアップロード */
					logger.fine("Got an uploaded file: " + item.getFieldName() + ", name = " + item.getName());
					byte[] bytes = ImageUtil.readBytes(stream);
					if (bytes.length == 0)
						continue;
					tempImages = new TemporalImage(bytes, item.getName());
				}
			}
		}
		catch (FileUploadException e) {
			throw new IOException(e);
		}

		/* トランザクション内で実行する部分を無名インナークラスで宣言 */
		Body body = new Body() {
			public void run(PersistenceManager pm, Transaction tx) throws ErrorPageException {
				BookInfo bookInfo;
				if (entryId == null || entryId.trim().isEmpty()) {
					// 新規エントリ
					logger.info("new entry");
					bookInfo = new BlogEntry();
					bookInfo.setDate(new Date());
					blog.getEntries().add(bookInfo);
				}
				else {
					logger.info("updating entry");
					bookInfo = BlogEntry.getEntry(pm, entryId);
					blog.decrementTags(pm, bookInfo.getTags());

					// チェックが外された写真を削除
					List<ImageEntity> toRemove = new ArrayList<ImageEntity>();
					for (ImageEntity image : bookInfo.getImages())
						if (!preservePic.contains(image.getKeyAsString()))
							toRemove.add(image);
					for (ImageEntity image : toRemove)
						bookInfo.getImages().remove(image);
				}
				for (TemporalImage tempImage : tempImages)
					bookInfo.getImages().add(new ImageEntity(tempImage.bytes, tempImage.name));
				bookInfo.setTags(tagList);
				bookInfo.setText(new Text(text));
				bookInfo.setTitle(title);
				blog.incrementTags(pm, tagList);

				blog.updateTagList();
			}
		};

		/* 上で定義した部分をトランザクション内で実行．3回までリトライする */
		try {
			if (!TransactionManager.start(3, body)) {
				ErrorPage.create(res, "コミットに失敗しました", "/blogs/" + URLEncoder.encode(blogName, "UTF-8"));
				return;
			}
		}
		catch (ErrorPageException e) {
			ErrorPage.create(res, e);
		}

		// もとのページにリダイレクト
		res.sendRedirect("/blogs/" + URLEncoder.encode(blogName, "UTF-8"));
	}

	private Map<String, Object> readMultiform(HttpServletRequest req)
	throws IOException {
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			ServletFileUpload upload = new ServletFileUpload();
			FileItemIterator iterator = upload.getItemIterator(req);
			while (iterator.hasNext()) {
				FileItemStream item = iterator.next();
				InputStream stream = item.openStream();
				if (item.isFormField()) {  /* フォームデータ */
					logger.fine("Got a form field: " + item.getFieldName());
					map.put(item.getFieldName(),
					        new String(ImageUtil.readBytes(stream),
					                   Charset.forName("UTF-8")));
				}
				else {                     /* ファイルアップロード */
					logger.fine("Got an uploaded file: "
					            + item.getFieldName() + ", name = "
					            + item.getName());
					byte [] bytes = ImageUtil.readBytes(stream);
					if (bytes.length == 0)
						continue;
					map.put(item.getFieldName(),
					        new TemporalImage(bytes, item.getName()));
				}
			}
			return map;
		}
		catch (FileUploadException e) {
			throw new IOException(e);
		}
	}

}
