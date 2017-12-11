package gaebook.util;

import java.io.IOException;
import java.io.Writer;
import java.text.DateFormat;
import java.util.Locale;
import java.util.TimeZone;
import java.util.logging.Logger;

import org.apache.velocity.Template;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.context.Context;
import org.apache.velocity.runtime.log.JdkLogChute;

public class Renderer {
	private static boolean initialized = false;
	private static void initializeVelocity() throws Exception {
	    Velocity.setProperty(
			Velocity.RUNTIME_LOG_LOGSYSTEM, new JdkLogChute());
	    Velocity.setProperty(
	        Velocity.INPUT_ENCODING, "UTF-8");              
	    Velocity.setProperty(
	        Velocity.OUTPUT_ENCODING, "UTF-8");              
		Velocity.init();
		initialized = true;
	}

    private static DateFormat dateTimeFormat = 
        DateFormat.getDateTimeInstance(DateFormat.MEDIUM, 
                                       DateFormat.MEDIUM, Locale.JAPAN); 
    private static DateFormat dateFormat = 
        DateFormat.getDateInstance(DateFormat.LONG, Locale.JAPAN); 
    private static DateFormat timeFormat = 
        DateFormat.getTimeInstance(DateFormat.MEDIUM, Locale.JAPAN); 
	static {
	    dateTimeFormat.setTimeZone(TimeZone.getTimeZone("JST"));
	    dateFormat.setTimeZone(TimeZone.getTimeZone("JST"));
	    timeFormat.setTimeZone(TimeZone.getTimeZone("JST"));
	}
    private static HtmlEscape htmlEscape = HtmlEscape.get();  

    
	public static void render(String filename, Context context, Writer writer) 
	throws IOException {
		try {
			synchronized (Renderer.class) { 
				if (!initialized)  
					initializeVelocity();
			}
		    context.put("_datetimeFormat", dateTimeFormat);
		    context.put("_dateFormat",     dateFormat);
		    context.put("_timeFormat",     timeFormat);
			context.put("_htmlEscape",     htmlEscape);
			Template template = Velocity.getTemplate(filename); 
			template.merge(context, writer);
		} catch (Exception e) {
			throw new IOException(e);
		}
	}
}
