package gaebook.blog;

import gaebook.util.*;
import javax.jdo.*;
import javax.jdo.Query;
import com.google.appengine.api.users.*;

import java.io.IOException;
import java.net.*;
import java.util.*;
import java.util.logging.Logger;

import javax.servlet.http.*;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.context.Context;

/* ブログのメインページを描画 */
@SuppressWarnings("serial")
public class Blogs extends HttpServlet {
    static Logger logger = Logger.getLogger(Blogs.class.getName());
    static final String prefix = "/blogs/";
    
    @SuppressWarnings("unchecked")
    public void doGet(HttpServletRequest req, HttpServletResponse res)
            throws IOException {
        UserService userService = UserServiceFactory.getUserService();
        User user = userService.getCurrentUser();

        String userAgent = req.getHeader("User-Agent");
        boolean isIPhone = userAgent.indexOf("iPhone") > 0;
        
        String uri = req.getRequestURI();
        if (!uri.startsWith(prefix)){
            ErrorPage.create(res, "リクエストされたブログはありません", "/home");
            return;
        }
        String blogName = uri.substring(prefix.length());
        blogName = URLDecoder.decode(blogName, "UTF-8");
        logger.info("blogName = " + blogName);
        
        String searchTag = req.getParameter("searchTag");
        String entryId   = req.getParameter("entryId");
        String before    = req.getParameter("before");
        
        PersistenceManager pm = null;
        try {
            pm = PMF.get().getPersistenceManager();
            Blog blog = pm.getObjectById(Blog.class, blogName);

            req.getSession().setAttribute("blogName", blogName);
            req.getSession().setAttribute("entryId", null);
            
            Query query = pm.newQuery(BlogEntry.class);
            List<BlogEntry> entries;
            BlogEntry next = null;
            
            if (entryId !=null) { // 特定のエントリに対するパーマネントリンクの処理
                BlogEntry entry = BlogEntry.getEntry(pm, entryId);
                if (entry == null) {
                    ErrorPage.create(res, "該当するエントリはありません", 
                            "/blogs/" + URLEncoder.encode(blogName, "UTF-8"));
                    return;
                }
                entries = new ArrayList<BlogEntry>();
                entries.add(entry);
            } else {
                List <Object> params = new ArrayList<Object>();
                String filterString = "blog == blogParam"; 
                String paramDecr = "gaebook.blog.Blog blogParam";
                params.add(blog);
                if (searchTag != null) {   // サーチタグがある場合 
                    filterString += " && tags == searchTag";
                    paramDecr += ", String searchTag";
                    params.add(searchTag);
                }
                if (before != null) {      // サーチタグのない場合
                    filterString += " && date <= before";
                    paramDecr += ", java.util.Date before";                    
                    params.add(new Date(Long.parseLong(before)));
                }
                query.setFilter(filterString);
                query.setOrdering("date desc");
                // １ページ分 + 1を取得
                query.setRange(0, blog.getPerPage() + 1);
                query.declareParameters(paramDecr);
                entries = (List<BlogEntry>) 
                    query.executeWithArray(params.toArray(new Object [0])); 
                if (entries.size() > blog.getPerPage()) {
                    int index = entries.size() - 1;
                    next = entries.get(index);
                    List<BlogEntry> newEntries = new ArrayList<BlogEntry>();
                    for (int i = 0; i < index; i++) 
                        newEntries.add(entries.get(i));
                    entries = newEntries;
                }
            }

            /* 以下，レンダリングのためのコンテクスト設定 */
            String signInUrl = userService.createLoginURL(req.getRequestURI());
            String signOutUrl = userService.createLogoutURL(req.getRequestURI());

            Context context = new VelocityContext();
            if (user != null) 
                context.put("isOwner", user.equals(blog.getOwner()));
            else
                context.put("isOwner", false);
            context.put("blog", blog);
            context.put("entries", entries);
            context.put("signInUrl", signInUrl);
            context.put("signOutUrl", signOutUrl);
            context.put("user", user);
            context.put("searchTag", searchTag);
            context.put("next", next);
            context.put("withBackLink", before != null);

            /* iPhone とそれ以外でテンプレートを切り替え */
            String template = 
                req.getHeader("User-Agent").contains("iPhone") ? 
                "WEB-INF/blogs.iphone.vm":                                                
                "WEB-INF/blogs.vm" ;                                               

            res.setContentType("text/html");
            res.setCharacterEncoding("utf-8");
            Renderer.render(template, context, res.getWriter());
        } catch (JDOObjectNotFoundException e) {
            ErrorPage.create(res, "リクエストされたブログはありません: " + blogName, "/home");
        } finally {
            if (pm != null && !pm.isClosed())
                pm.close();
        }
    
    }
    
}
