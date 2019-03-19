package tms.config;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.filter.GenericFilterBean;

import tms.bl.SecurityService;

public class AuthFilter extends GenericFilterBean {

  private SecurityService securityService;

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
    HttpServletRequest httpRequest = (HttpServletRequest) request;
    HttpServletResponse httpResponse = (HttpServletResponse) response;

    if (!(httpRequest.getRequestURL().toString().contains("/api/security/login")
        || httpRequest.getRequestURL().toString().contains("/api/security/register"))) {
      String authorizationHeader = httpRequest.getHeader("Authorization");
      if (authorizationHeader != null) {
        if (securityService == null) {
          ServletContext servletContext = request.getServletContext();
          WebApplicationContext webApplicationContext = WebApplicationContextUtils.getWebApplicationContext(servletContext);
          securityService = webApplicationContext.getBean(SecurityService.class);
        }
        if (!securityService.isValidAuthToken(authorizationHeader)) {
          httpResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED);
          return;
        }
      } else {
        httpResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED);
        return;
      }
    }

    chain.doFilter(request, response);
  }

}
