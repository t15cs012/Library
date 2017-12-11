package gaebook.blog;

import gaebook.util.*;
import javax.jdo.*;
import com.google.appengine.api.users.*;
import java.io.IOException;
import javax.servlet.http.*;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.context.Context;

@SuppressWarnings("serial")
public class CreateBlogEntry extends HttpServlet {

    public void doGet(HttpServletRequest req, HttpServletResponse res)
            throws IOException {
        UserService userService = UserServiceFactory.getUserService();
        User user = userService.getCurrentUser();
        if (user == null) {  // ログインしていない
            ErrorPage.create(res, "ログインしてください", "/home"); 
            return;
        }
        String entryId   = req.getParameter("entryId");    
        String blogName = req.getParameter("blogName");
        if (blogName == null)  // セッションをチェック
            blogName = (String)req.getSession().getAttribute("blogName");
        
        if (blogName == null || blogName.trim().isEmpty()) {
            ErrorPage.create(res, "ブログ名が確認できません", "/home");
            return;
        }
        req.getSession().setAttribute("blogName", blogName);
        // 
        Context context = new VelocityContext();
        context.put("blogName", blogName);
        if (entryId == null) {  // new Entry;
            context.put("title", "");
            context.put("text", "");
            context.put("tags", "");
        } else {
            req.getSession().setAttribute("entryId", entryId);
            PersistenceManager pm = null;
            try {
                pm = PMF.get().getPersistenceManager();
                BlogEntry entry = BlogEntry.getEntry(pm, entryId);
                context.put("title", entry.getTitle());
                context.put("text", entry.getText().getValue());
                context.put("tags", entry.getTagsString()); 
                context.put("images", entry.getImages());
                pm.detachCopy(entry);
            } finally {
                if (pm != null && !pm.isClosed())
                    pm.close();
            }
        }
        res.setContentType("text/html");
        res.setCharacterEncoding("utf-8");
        
        String template = 
            req.getHeader("User-Agent").contains("iPhone") ?
            "WEB-INF/createBlogEntry.iphone.vm" :  
            "WEB-INF/createBlogEntry.vm" ;  
        Renderer.render(template, context, res.getWriter());    
    }
}
