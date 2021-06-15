package ru.netology.cloud_drive.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebMvc
public class WebConfiguration implements WebMvcConfigurer {

    @Value("{cors.url}")
    private String origins;

    @Value("{cors.methods}")
    private String methods;

//    @Value("{cors.mapping}")
//    private String mapping;

//    @Value("{cors.credentials}")
//    private Boolean credentials;


//    cors.origins=http://localhost:8081
//    cors.methods=*
//    cor.credentials=true

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowCredentials(true)
//                .allowedOrigins(origins)
//                .allowedMethods(methods);
//        registry.addMapping("/**")
//                .allowCredentials(true)
                .allowedOrigins("http://localhost:8081")
                .allowedMethods("*");
    }

}

