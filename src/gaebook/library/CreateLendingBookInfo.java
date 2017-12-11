package gaebook.blog;

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
public class CreateLendingBookInfo extends HttpServlet {
    static Logger logger = Logger.getLogger(CreateBlogEntryHandler.class
            .getName());
    public static class TemporalImage {
        byte [] bytes;
        String name;
        public TemporalImage(byte[] bytes, String name) {
            super();
            this.bytes = bytes;
            this.name = name;
        }
    }
    
    public void doPost(HttpServletRequest req, HttpServletResponse res)
            throws IOException {
        UserService userService = UserServiceFactory.getUserService();
        final User user = userService.getCurrentUser();
        if (user == null) { // ログインしていない
            ErrorPage.create(res, "ログインしてください", "/home");
            return;
        }
        final String blogName = 
            (String) req.getSession().getAttribute("blogName");
        final String entryId = 
            (String) req.getSession().getAttribute("entryId");
        if (blogName == null || blogName.trim().isEmpty()) {
            ErrorPage.create(res, "ブログ名が確認できません", "/home");
            return;
        }
        Map<String, Object> map = readMultiform(req);
        
        final String tags = (String)map.get("tags");
        final String text = (String)map.get("content");
        final String title = (String)map.get("title");
               
        final List<String> preservePic = new ArrayList<String>();
        for (String key: map.keySet()) {
            if (key.startsWith("preservePic"))
                preservePic.add((String)map.get(key));
        }
        final List<TemporalImage> tempImages = new ArrayList<TemporalImage>();
        for (String key: map.keySet()) {
            if (key.startsWith("upfile"))
                tempImages.add((TemporalImage)map.get(key));
        }

        req.getSession().setAttribute("entryId", null); // clear entryId

        /* トランザクション内で実行する部分を無名インナークラスで宣言 */
        Body body = new Body() {
            public void run(PersistenceManager pm, Transaction tx)
                    throws ErrorPageException {
                Blog blog = Blog.getBlog(pm, blogName);
                if (blog == null)
                    throw new ErrorPageException("該当するブログが見つかりません",
                            "/home");
                if (!blog.getOwner().equals(user))
                    throw new ErrorPageException(
                            "ブログに書き込む権限がありません", "/home");
                
                List<String> tagList;
                if (tags.trim().isEmpty())
                    tagList = new ArrayList<String>();                    
                else
                    tagList = Arrays.asList(tags.trim().split("[, ]+"));
                        
                BlogEntry entry;
                if (entryId == null || entryId.trim().isEmpty()) {
                    // 新規エントリ
                    logger.info("new entry");
                    entry = new BlogEntry();
                    entry.setDate(new Date());
                    blog.getEntries().add(entry);
                } else {
                    logger.info("updating entry");
                    entry = BlogEntry
                            .getEntry(pm, entryId);
                    blog.decrementTags(pm, entry.getTags());

                    // チェックが外された写真を削除
                    List<ImageEntity> toRemove = new ArrayList<ImageEntity>();
                    for (ImageEntity image: entry.getImages())  
                        if (!preservePic.contains(image.getKeyAsString()))
                            toRemove.add(image);
                    for (ImageEntity image: toRemove)
                        entry.getImages().remove(image);
                }
                for (TemporalImage tempImage: tempImages)  
                    entry.getImages().add(new ImageEntity(tempImage.bytes, 
                                                          tempImage.name));
                entry.setTags(tagList);
                entry.setText(new Text(text));
                entry.setTitle(title);
                blog.incrementTags(pm, tagList);

                blog.updateTagList();
            }
        };

        /* 上で定義した部分をトランザクション内で実行．3回までリトライする */
        try {
            if (!TransactionManager.start(3, body)) {
                ErrorPage.create(res, "コミットに失敗しました", 
                        "/blogs/" + URLEncoder.encode(blogName, "UTF-8"));
                return;
            }
        } catch (ErrorPageException e) {
            ErrorPage.create(res, e);
        }

        // もとのページにリダイレクト
        res.sendRedirect("/blogs/" + URLEncoder.encode(blogName, "UTF-8"));
    }

    /* ファイルアップロードと，フォーム入力を解釈して，マップに格納 */
    private Map<String, Object> readMultiform(HttpServletRequest req) 
    throws IOException{
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
                } else {                   /* ファイルアップロード */
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
        } catch (FileUploadException e) {
            throw new IOException(e);
        }
    }
}
