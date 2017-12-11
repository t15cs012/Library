package gaebook.blog;

import gaebook.util.*;
import gaebook.util.ErrorPage.ErrorPageException;
import gaebook.util.TransactionManager.Body;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.logging.Logger;

import javax.jdo.*;
import javax.servlet.http.*;
import com.google.appengine.api.users.*;


// ブログのエントリを削除
public class DeleteBlogEntryHandler extends HttpServlet {
    static Logger logger = Logger.getLogger(DeleteBlogEntryHandler.class.getName());
    
    public void doGet(HttpServletRequest req, final HttpServletResponse res)
            throws IOException {
        UserService userService = UserServiceFactory.getUserService();
        final User user = userService.getCurrentUser();
        if (user == null) {  // ログインしていない
            ErrorPage.create(res, "ログインしてください", "/home"); 
            return;
        }
        final String blogName = (String)req.getSession().getAttribute("blogName");
        String entryId   = (String)req.getSession().getAttribute("entryId");        
        if (blogName == null || blogName.trim().isEmpty()) {
            ErrorPage.create(res, "ブログ名が確認できません", "/home");
            return;
        }
        if (entryId == null || entryId.trim().isEmpty()) {
            entryId = (String)req.getParameter("entryId"); 
            if (entryId == null) {
                ErrorPage.create(res, "エントリIDが確認できません", "/home");
                return;
            }
        }
        final String entryIdCopy = entryId;
        /* トランザクション内で実行する部分を無名インナークラスで宣言 */        
        Body body = new Body() {
            public void run(PersistenceManager pm, Transaction tx)
                    throws ErrorPageException {
                Blog blog = Blog.getBlog(pm, blogName);
                if (blog == null) 
                    throw new ErrorPageException("ブログ名が確認できません", "/home");
                if (!blog.getOwner().equals(user)) 
                    throw new ErrorPageException("ブログに書き込む権限がありません",                                     "/home");
                BlogEntry entry = BlogEntry
                        .getEntry(pm, entryIdCopy);
                blog.decrementTags(pm, entry.getTags());
                pm.deletePersistent(entry);
                blog.updateTagList();
            }
        };
        try {
            if (! TransactionManager.start(3, body)) { // 3回リトライ
                ErrorPage.create(res, "削除に失敗しました", "/blogs/" + 
                        URLEncoder.encode(blogName, "UTF-8"));
                return;
            }
        } catch (ErrorPageException e) {
            ErrorPage.create(res, e);
        }
        // もとのページにリダイレクト
        res.sendRedirect("/blogs/" + URLEncoder.encode(blogName, "UTF-8"));
    }
}
