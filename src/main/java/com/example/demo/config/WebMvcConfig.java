package com.example.demo.config;

// 잘 모르는(확실하게 알지 못하는) 영역이라 일단 주석처리.
// import org.apache.tomcat.util.http.Rfc6265CookieProcessor;
//import org.apache.tomcat.util.http.SameSiteCookies;
//import org.springframework.boot.web.embedded.tomcat.TomcatContextCustomizer;
//import org.springframework.boot.web.servlet.view.MustacheViewResolver;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.web.servlet.config.annotation.ViewResolverRegistry;
//import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
//
//@Configuration
//public class WebMvcConfig implements WebMvcConfigurer {

//    @Override
//    public void configureViewResolvers(ViewResolverRegistry registry) {
//
//        @Bean
//        public TomcatContextCustomizer sameSiteCookiesConfig() {
//            return context -> {
//                final Rfc6265CookieProcessor cookieProcessor = new Rfc6265CookieProcessor();
//                cookieProcessor.setSameSiteCookies(SameSiteCookies.NONE.getValue());
//                context.setCookieProcessor(cookieProcessor);
//            };
//        }

//        MustacheViewResolver resolver = new MustacheViewResolver();
//
//        resolver.setCharset("UTF-8");
//        resolver.setContentType("text/html;charset=UTF-8");
//        resolver.setPrefix("classpath:/templates/");
//        resolver.setSuffix(".html");
//
//        registry.viewResolver(resolver);
//    }
//}