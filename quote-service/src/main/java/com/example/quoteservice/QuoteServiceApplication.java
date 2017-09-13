package com.example.quoteservice;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.List;

@EnableResourceServer
@EnableDiscoveryClient
@SpringBootApplication
public class QuoteServiceApplication {
    @Bean
    CommandLineRunner demoData(QuoteRepository quoteRepository) {
        return args -> {
            quoteRepository.save(new Quote("The unexamined life is not worth living.", "Socrates"));
            quoteRepository.save(new Quote("What you do makes a difference, and you have to decide what kind of difference you want to make.", "Jane Goodall"));
            quoteRepository.save(new Quote("Do you want to know who you are? Don't ask. Act! Action will delineate and define you.", "Thomas Jefferson"));
            quoteRepository.save(new Quote("Love is the absence of judgment.", "Dalai Lama XIV"));
            quoteRepository.save(new Quote("You have power over your mind - not outside events. Realize this, and you will find strength.", "Marcus Aurelius, Meditations"));
            quoteRepository.save(new Quote("It's hard to beat a person who never gives up.", "Babe Ruth"));
            quoteRepository.save(new Quote("Imagination is the highest form of research.", "Albert Einstein"));

            quoteRepository.findAll().forEach(System.out::println);
        };
    }

    public static void main(String[] args) {
        SpringApplication.run(QuoteServiceApplication.class, args);
    }
}

@RepositoryRestResource
interface QuoteRepository extends CrudRepository<Quote, Long> {
    @Query("select q from Quote q order by RAND()")
    List<Quote> getQuotesRandomOrder();
}

@RestController
class QSQuoteController {
    private final QuoteRepository quoteRepository;

    QSQuoteController(QuoteRepository quoteRepository) {
        this.quoteRepository = quoteRepository;
    }

    @GetMapping("/random")
    public Quote getRandomQuote() {
        return this.quoteRepository.getQuotesRandomOrder().get(0);
    }
}

@Entity
@Data
@NoArgsConstructor
@RequiredArgsConstructor
class Quote {
    @Id
    @GeneratedValue
    private Long id;
    @NonNull
    private String text, source;
}