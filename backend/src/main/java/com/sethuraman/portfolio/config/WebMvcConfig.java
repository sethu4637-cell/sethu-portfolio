package com.sethuraman.portfolio.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;

/**
 * Spring MVC Configuration to serve portfolio frontend files seamlessly.
 *
 * Maps routes to their static HTML counterparts:
 *   /                 -> /index.html
 *   /admin            -> /admin/login.html
 *   /admin/login      -> /admin/login.html
 *   /admin/dashboard  -> /admin/dashboard.html
 *
 * Resource locations support both packaged JAR (classpath:/static/)
 * and live local development (workspace directory).
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/").setViewName("forward:/index.html");
        registry.addViewController("/admin").setViewName("forward:/admin/login.html");
        registry.addViewController("/admin/").setViewName("forward:/admin/login.html");
        registry.addViewController("/admin/login").setViewName("forward:/admin/login.html");
        registry.addViewController("/admin/login/").setViewName("forward:/admin/login.html");
        registry.addViewController("/admin/dashboard").setViewName("forward:/admin/dashboard.html");
        registry.addViewController("/admin/dashboard/").setViewName("forward:/admin/dashboard.html");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        File currentDir = new File("").getAbsoluteFile();
        File workspaceRoot = currentDir.getName().equalsIgnoreCase("backend")
                ? currentDir.getParentFile()
                : currentDir;

        String rawPath = workspaceRoot.getAbsolutePath().replace('\\', '/');
        if (!rawPath.endsWith("/")) {
            rawPath += "/";
        }
        String fileLocation = "file:///" + rawPath;
        String uriLocation = workspaceRoot.toURI().toString();
        if (!uriLocation.endsWith("/")) {
            uriLocation += "/";
        }

        registry.addResourceHandler("/**")
                .addResourceLocations(
                        fileLocation,
                        uriLocation,
                        fileLocation + "admin/",
                        uriLocation + "admin/",
                        "classpath:/static/",
                        "classpath:/META-INF/resources/",
                        "classpath:/resources/",
                        "classpath:/public/"
                );
    }
}
