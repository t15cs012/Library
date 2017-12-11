package gaebook.util;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/* 確認ダイアログを表示するサーブレット． */
public class ConfirmationDialog extends HttpServlet {
    static final List<String> reserved = Arrays.asList("accept", "cancel", "message");
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res)
        throws ServletException, IOException {
        
        String acceptUrl = req.getParameter("accept");
        String cancelUrl = req.getParameter("cancel");        
        String message   = req.getParameter("message");        
        
        Set<String> keys = new HashSet<String>(); 
        for (Object key: req.getParameterMap().keySet()) 
            keys.add((String)key);
        keys.removeAll(reserved);                       
        String paramPortion = createParamPortion(req.getParameterMap(), keys);
        ConfirmationDialog.create(
                res, 
                message,
                concatUrl(acceptUrl, paramPortion),
                concatUrl(cancelUrl, paramPortion));        
    }

    private String createParamPortion(Map<String, String[]> map , Set<String> keys) {    
        StringBuffer sb = new StringBuffer();
        boolean first = true;
        for (String key: keys) {
            for (String val: map.get(key)) {
                if (first)
                    first =false;
                else
                    sb.append("&");
                sb.append(key);
                sb.append("=");
                sb.append(val);
            }
        }
        return sb.toString();
    }

    private String concatUrl(String urlStr, String paramPortion) {
        if (urlStr.indexOf("?") < 0)
            return urlStr + "?" + paramPortion;
        else
            return urlStr + "&" + paramPortion;
    }
    
    public static void create(HttpServletResponse res, String message,
            String acceptURL, String cancelURL) throws IOException {
        res.setContentType("text/html");
        res.setCharacterEncoding("utf-8");        
        PrintWriter pw = res.getWriter();
        pw.println("<html><head>");    
        pw.println("<link rel=\"stylesheet\" href=\"/blog.css\" media=\"all\" type=\"text/css\">");
        pw.println("</head><body>");    
        pw.println("<h2>"+ message + "</h2>");
        pw.println("<a href=\"" + acceptURL +"\"> 実行 </a></br>");
        pw.println("<a href=\"" + cancelURL +"\"> キャンセル </a>");
        pw.println("</body></html>");    
    }
    
}
