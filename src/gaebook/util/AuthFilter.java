package gaebook.util;

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.Filter;
import javax.servlet.FilterChain;

public class AuthFilter implements Filter {
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) {
		try {
			String target = ((HttpServletRequest) req).getRequestURI();

			HttpSession session = ((HttpServletRequest) req).getSession();

			if (session == null) {
				/* まだ認証されていない */
				session = ((HttpServletRequest) req).getSession(true);
				session.setAttribute("target", target);

				((HttpServletResponse) res).sendRedirect("/Login");
			} else {
				Object loginCheck = session.getAttribute("login");
				if (loginCheck == null) {
					/* まだ認証されていない */
					session.setAttribute("target", target);
					((HttpServletResponse) res).sendRedirect("/Login");
				}
			}

			chain.doFilter(req, res);
		} catch (ServletException se) {
		} catch (IOException e) {
		}
	}

	public void init(FilterConfig filterConfig) throws ServletException {
	}

	public void destroy() {
	}
}
