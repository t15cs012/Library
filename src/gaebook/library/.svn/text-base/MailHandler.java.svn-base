package gaebook.blog;

import gaebook.blog.CreateBlogEntryHandler.TemporalImage;
import gaebook.util.*;
import gaebook.util.ErrorPage.ErrorPageException;
import gaebook.util.TransactionManager.Body;

import java.io.*;
import java.util.*;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.jdo.Transaction;
import javax.mail.*;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.*;

import com.google.appengine.api.datastore.Text;

/* メイルでの投稿を処理 */
@SuppressWarnings("serial")
public class MailHandler extends HttpServlet {
    static Logger logger = Logger.getLogger(CreateBlogEntryHandler.class
            .getName());

    public void doPost(HttpServletRequest req, HttpServletResponse res)
            throws IOException {
        String uri = req.getRequestURI();
        String address = uri.substring("/_ah/mail/".length());
        final String gotAddress = address.substring(0, address.indexOf('@'));
        logger.info("got mail to '" + gotAddress + "'");
        
        StringBuffer sb = new StringBuffer();
        final List<TemporalImage> images = new ArrayList<TemporalImage>();
        final String title = parseMessage(req, sb, images);
        final String content = sb.toString();
        
        /* トランザクション内で実行する部分を無名インナークラスで宣言 */                
        Body body = new Body() {
            public void run(PersistenceManager pm, Transaction tx)
            throws ErrorPageException {
                Blog blog = Blog.getBlogFromEmail(pm, gotAddress);
                if (blog == null)                
                    throw new ErrorPageException(gotAddress  + 
                            " に対応するブログがありません" , "");                
                BlogEntry entry = new BlogEntry();
                entry.setDate(new Date());
                blog.getEntries().add(entry);
                
                entry.setText(new Text(content));
                entry.setTitle(title);                
                for (TemporalImage tempImage: images)  
                   entry.getImages().add(new ImageEntity(tempImage.bytes, 
                                                         tempImage.name));
            }
        };
        try {
            if (!TransactionManager.start(3, body)) {
                logger.severe("コミットに失敗しました");
                return;
            }
        } catch (ErrorPageException e) {
            logger.severe(e.getMessage());
        }
    }

    /* 下のparseMessageを呼び出す タイトルをリターン */
    private String parseMessage(HttpServletRequest req, StringBuffer sb,
            List<TemporalImage> images) throws IOException {
        Properties prop = System.getProperties();
        Session session = Session.getInstance(prop, null);
        MimeMessage msg;
        try {
            msg = new MimeMessage(session, req.getInputStream());
            String title = msg.getSubject();
            parseMessage(msg, sb, images);
            return title;
        } catch (MessagingException e) {
            e.printStackTrace();
            throw new IOException("Message Excetion: " + e.getMessage());
        }
    }

    /* メイルのボディを解析．StringBufferに文字を，Listにイメージを蓄積 */
    private void parseMessage(Part msg, StringBuffer sb,
                              List<TemporalImage> images) 
    throws IOException, MessagingException {
        if (msg.getContentType().startsWith("text/"))
            sb.append(msg.getContent().toString());
        else if (msg.getContentType().startsWith("image/")) {
            byte[] bytes = ImageUtil.readBytes((InputStream) msg.getContent());
            images.add(new TemporalImage(bytes, msg.getFileName()));
        } else if (msg.getContentType().startsWith("multipart/mixed")) {
            Multipart mp = (Multipart) msg.getContent();
            for (int i = 0; i < mp.getCount(); i++)
                parseMessage(mp.getBodyPart(i), sb, images);
        } else if (msg.getContentType().startsWith("multipart/alternative")) {
            // plain と HTMLの複合
            Multipart mp = (Multipart) msg.getContent();
            for (int i = 0; i < mp.getCount(); i++)
                // plain のみ処理．
                if (mp.getBodyPart(i).getContentType().startsWith("text/plain"))
                    parseMessage(mp.getBodyPart(i), sb, images);
        } else {
            throw new IOException("unexpected content type: '"
                    + msg.getContentType() + "'");
        }
    }

}
