package gaebook.blog;

import gaebook.util.ErrorPage;
import java.io.IOException;
import java.util.logging.Logger;
import javax.servlet.http.*;
import com.google.appengine.api.users.*;


// 新しいブログを作成
public class CreateBlogHandler extends HttpServlet {
    Logger logger = Logger.getLogger(CreateBlogHandler.class.getName());
    public void doPost(HttpServletRequest req, HttpServletResponse res)
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
        
        if (!Blog.createBlogIfNotExist(blogName, user)) {
            ErrorPage.create(res, "指定されたブログ名は利用できません．" +
            		"別の名前を試してみてください", "/createBlog.html");
            return;
        }

        // もとのページにリダイレクト
        res.sendRedirect("/home");
    }
}
