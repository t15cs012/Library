package gaebook.blog;

import gaebook.util.*;
import javax.jdo.*;
import com.google.appengine.api.users.*;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import javax.servlet.http.*;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.context.Context;

/* ブログ システムのホーム画面．ログインしていればそのユーザのブログを列挙
 * していなければすべてのブログを列挙
 */
@SuppressWarnings("serial")
public class Home extends HttpServlet {

    public void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        UserService userService = UserServiceFactory.getUserService();
        User user = userService.getCurrentUser();

        req.getSession().removeAttribute("blogName");
        PersistenceManager pm = null;
        try {
            pm = PMF.get().getPersistenceManager();
            Query query = pm.newQuery(Blog.class);
            List<Blog> blogs;
            String mailPrefix = null;
            if (user != null) {  // ログインしているので，そのユーザのブログを取得
                query.setFilter("owner == user_name");
                query.declareParameters("com.google.appengine.api.users.User" +
                		" user_name");
                blogs= (List<Blog>) query.execute(user);
                mailPrefix =     // メイルプレフィックスを取得．描画用 
                    user.getEmail().substring(0, user.getEmail().indexOf('@'));
            } else {             // ログインしていない．すべてのブログを取得
                blogs= (List<Blog>) query.execute();
            }
            
            URL url = new URL(req.getRequestURL().toString());
            String mailDomain = url.getHost().replace("appspot", "appspotmail");

            String signInUrl = userService.createLoginURL(req.getRequestURI());
            String signOutUrl = userService.createLogoutURL(req.getRequestURI());

            Context context = new VelocityContext();
            context.put("blogs", blogs);
            context.put("signInUrl", signInUrl);
            context.put("signOutUrl", signOutUrl);
            context.put("user", user);
            context.put("mailDomain", mailDomain);
            context.put("mailPrefix", mailPrefix);
            
            String template = 
                req.getHeader("User-Agent").contains("iPhone") ?
                "WEB-INF/home.iphone.vm":                                                
                "WEB-INF/home.vm" ;                                               

            resp.setContentType("text/html");
            resp.setCharacterEncoding("utf-8");
            Renderer.render(template, context, resp.getWriter());
        } finally {
            if (pm != null && !pm.isClosed())
                pm.close();
        }
    
    }
}
