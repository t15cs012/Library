package gaebook.util;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletResponse;

/* エラーページを生成するヘルパクラス */
public class ErrorPage {
    public static class ErrorPageException extends Exception {
        String message;
        String redirectURL;
        public ErrorPageException(String message, String redirectURL) {
            this.message = message;
            this.redirectURL = redirectURL;
        }
    }

    public static void create(HttpServletResponse res, ErrorPageException e) 
    throws IOException {
        create(res, e.message, e.redirectURL);        
    }
    
    public static void create(HttpServletResponse res, String message,
            String redirectURL) throws IOException {
        res.setContentType("text/html");
        res.setCharacterEncoding("utf-8");        
        PrintWriter pw = res.getWriter();
        pw.println("<html><head>");    
        pw.println("<link rel=\"stylesheet\" href=\"/blog.css\" media=\"all\" type=\"text/css\">");
        pw.println("</head><body>");    
        pw.println("<h2>"+ message + "</h2>");
        pw.println("<a href=\"" + redirectURL +"\"> 進む </a>");
        pw.println("</body></html>");    
    }
    
}
