package gaebook.util;

import java.util.regex.*;

/* ページ内にコンテンツを表示する際のHTMLエスケープなどを行うヘルパクラス */
public class HtmlEscape {
    private static HtmlEscape instance = new HtmlEscape();
    public static HtmlEscape get() {
        return instance;
    }
    
    public String escape(String input) {
        String tmp = input;
        tmp = tmp.replace("\"", "&quot;");
        tmp = tmp.replace("&", "&amp;");
        tmp = tmp.replace(">", "&gt;");
        tmp = tmp.replace("<", "&lt;");
        return tmp;
    }

    /* 改行を <br> に変換 */
    public String replaceCR(String input) {
        return input.replace("\n", "<br/>\n");
    }

    public String escapeAndTag(String input) {
        input = escape(input);
        input = autoTag(input);
        return replaceCR(input);
    }

    /* URLをリンクとして表示 */
    public String autoTag(String input) {
        Pattern pat = Pattern.compile("https?://[^ \t\n]*");
        Matcher m = pat.matcher(input);
        int start = 0;
        StringBuffer sb = new StringBuffer();
        while (m.find(start)) {
            sb.append(input, start, m.start());
            String tmp = input.substring(m.start(), m.end());
            sb.append("<a href=\"" + tmp + "\">" + tmp + "</a>" );
            start = m.end();
        }
        sb.append(input, start, input.length());
        return sb.toString();
    }
}
