package x590.jmessenger.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Base64;

public class BasicAuthenticationFilter implements Filter {

	private static final String
			LOGIN = "user",
			PASSWORD = "user";

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		if (request instanceof HttpServletRequest httpRequest &&
			response instanceof HttpServletResponse httpResponse) {

			String authorizationHeader = httpRequest.getHeader("Authorization");

			if (authorizationHeader == null || !authorizationHeader.startsWith("Basic ")) {
				httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
				httpResponse.getOutputStream().println("Please log in");
				httpResponse.getOutputStream().flush();
				return;
			}


			String base64LoginAndPassword = authorizationHeader.split(" ")[1];
			String[] loginAndPassword = new String(Base64.getDecoder().decode(base64LoginAndPassword)).split(":");

			if (loginAndPassword.length != 2 || !LOGIN.equals(loginAndPassword[0]) || !PASSWORD.equals(loginAndPassword[1])) {
				httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
				httpResponse.getOutputStream().println("Incorrect login or password");
				httpResponse.getOutputStream().flush();
				return;
			}
		}

		chain.doFilter(request, response);
	}
}
