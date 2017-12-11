package gaebook.util;

import gaebook.util.ImageEntity;
import gaebook.util.ImageUtil;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.urlfetch.HTTPResponse;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserServiceFactory;

/* 画像表示サーブレット */
public class ImageDisplay extends HttpServlet{
    static final String prefix = "/image/";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String uri = req.getRequestURI();
        if (!uri.startsWith(prefix)){
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        String imageKey = uri.substring(prefix.length());

        ImageEntity entity = ImageUtil.getImageEntity(imageKey);

        if (entity == null) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
        } else { 
            resp.setContentType(entity.getContentType());
            resp.getOutputStream().write(entity.getBytes());
        }
    }
    
}
