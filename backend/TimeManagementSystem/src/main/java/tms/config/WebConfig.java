package tms.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

  @Override
  public void addCorsMappings(CorsRegistry registry) {
    registry
        .addMapping("/**").allowedOrigins("http://localhost:3000").allowedMethods("GET", "POST", "OPTIONS", "PUT", "OPTIONS").allowedHeaders("Content-Type",
            "X-Requested-With", "accept", "Origin", "Access-Control-Request-Method", "Access-Control-Request-Headers", "x-csrf-token")
        .exposedHeaders("Access-Control-Allow-Origin", "Access-Control-Allow-Credentials").allowCredentials(true).maxAge(3600);
  }

}
