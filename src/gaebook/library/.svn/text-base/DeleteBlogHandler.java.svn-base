package gaebook.blog;

import gaebook.util.ErrorPage;
import java.io.IOException;
import java.util.logging.Logger;
import javax.servlet.http.*;
import com.google.appengine.api.users.*;


// ブログを削除
public class DeleteBlogHandler extends HttpServlet {
    Logger logger = Logger.getLogger(DeleteBlogHandler.class.getName());
    public void doGet(HttpServletRequest req, HttpServletResponse res)
            throws IOException {
        UserService userService = UserServiceFactory.getUserService();
        User user = userService.getCurrentUser();
        logger.info("doPost");
        if (user == null) {  // ログインしていない
            ErrorPage.create(res, "ログインしてください", "/home"); 
            return;
        }
        String blogName = req.getParameter("blogName");
        if (blogName == null || blogName.trim().isEmpty()) {
            ErrorPage.create(res, "ブログ名を指定してください", "/createBlog.html");
            return;
        }
        if (!Blog.deleteBlog(blogName, user)) {
            ErrorPage.create(res, "削除できませんでした. 権限を確認してください．", 
                             "/home");
            return;
        }
        // もとのページにリダイレクト
        res.sendRedirect("/home");
    }
}
