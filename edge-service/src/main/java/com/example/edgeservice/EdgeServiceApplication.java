package com.example.edgeservice;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.oauth2.client.EnableOAuth2Sso;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.context.annotation.Bean;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.resource.OAuth2ProtectedResourceDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@EnableOAuth2Sso
@EnableZuulProxy
@SpringBootApplication
public class EdgeServiceApplication {
    @LoadBalanced
    @Bean
    OAuth2RestTemplate restTemplate(OAuth2ProtectedResourceDetails resource, OAuth2ClientContext context) {
        // Note that this reports that there are (now) multiple beans of type OAuth2ClientContext...so I
        // replaced this bean creation method with the following, which provides a DefaultOAuth2ClientContext,
        // and get the same results. :/
        return new OAuth2RestTemplate(resource, context);
    }
//    @LoadBalanced
//    @Bean
//    OAuth2RestTemplate restTemplate(OAuth2ProtectedResourceDetails resource) {
//        return new OAuth2RestTemplate(resource);
//    }

    public static void main(String[] args) {
        SpringApplication.run(EdgeServiceApplication.class, args);
    }
}

@RefreshScope
@RestController
class ESQuoteController {
    private OAuth2RestTemplate restTemplate;

    @Value("${quote}")
    private String defaultQuote;

    ESQuoteController(OAuth2RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @HystrixCommand(fallbackMethod = "getDefaultQuote")
    @GetMapping("/quotorama")
    public Quote getRandomQuote() {
        return this.restTemplate.getForObject("http://quote-service/random", Quote.class);
    }

    public Quote getDefaultQuote() {
        return new Quote(defaultQuote, "Me, here, in Boston!");
    }
}

@Data
@NoArgsConstructor
@RequiredArgsConstructor
class Quote {
    private Long id;
    @NonNull
    private String text,source;
}