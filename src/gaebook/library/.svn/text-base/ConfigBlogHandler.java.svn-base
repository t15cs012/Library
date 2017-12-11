package gaebook.blog;

import gaebook.util.*;
import gaebook.util.TransactionManager.Body;
import gaebook.util.ErrorPage.ErrorPageException;
import java.io.IOException;
import java.util.logging.Logger;

import javax.jdo.*;
import javax.servlet.http.*;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

// 新しいブログを作成
public class ConfigBlogHandler extends HttpServlet {
    Logger logger = Logger.getLogger(ConfigBlogHandler.class.getName());
    public void doPost(HttpServletRequest req, HttpServletResponse res)
            throws IOException {
        UserService userService = UserServiceFactory.getUserService();
        User user = userService.getCurrentUser();
            
        if (user == null) {  // ログインしていない
            ErrorPage.create(res, "ログインしてください", "/home"); 
            return;
        }
        
        final String blogName = req.getParameter("blogName");
        if (blogName == null || blogName.trim().isEmpty()) {
            ErrorPage.create(res, "ブログ名を指定してください", "/home");
            return;
        }
        final String perPage = req.getParameter("perPage");        
        final boolean useMail = req.getParameter("useMail") != null && 
                                req.getParameter("useMail").equals("on"); 
        final String mailPostFix = req.getParameter("mailPostFix");

        Body body = new Body() {
            public void run(PersistenceManager pm, Transaction tx)
            throws ErrorPageException {
                Blog blog = Blog.getBlog(pm, blogName);
                if (blog == null)                
                    throw new ErrorPageException(
                            "指定されたブログがありません " + blogName, "/home");
                
                blog.setPerPage(Integer.valueOf(perPage));
                blog.setUseMail(useMail);
                blog.setMailPostFix(mailPostFix);
            }
        };
        
        try {
            if (!TransactionManager.start(3, body)) {
                ErrorPage.create(res, "コミットに失敗しました", "/home");
                return;
            }
        } catch (ErrorPageException e) {
            ErrorPage.create(res, e);
        }
        // もとのページにリダイレクト
        res.sendRedirect("/home");
    }
}
