package com.vikram.reactive.producer;

import com.sun.org.apache.xpath.internal.operations.Bool;
import com.vikram.reactive.pojo.Author;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Configuration
public class AuthorRouterFunctionConfig {

    @Autowired
    private AuthorRepository authorRepository;

    @Bean
    public RouterFunction<ServerResponse> authorRoutes() {
        return RouterFunctions
                .route(RequestPredicates.GET("/authors"),
                        this::getAuthors)
                .andRoute(RequestPredicates.GET("/author/{id}"),
                        this::getAuthorById)
                .andRoute(RequestPredicates.POST("/author")
                                .and(RequestPredicates.accept(MediaType.APPLICATION_JSON)),
                        this::createAuthor)
                .andRoute(RequestPredicates.PUT("/author")
                                .and(RequestPredicates.accept(MediaType.APPLICATION_JSON)),
                        this::updateAuthor)
                .andRoute(RequestPredicates.DELETE("/author/{id}"),
                        this::deleteAuthor);
    }

    private Mono<ServerResponse> deleteAuthor(ServerRequest serverRequest) {
        Long id = Long.valueOf(serverRequest.pathVariable("id"));
        Mono<Boolean> deleteMono = authorRepository.deleteAuthor(id);
        return ServerResponse.ok().body(deleteMono, Boolean.class);
    }

    private Mono<ServerResponse> updateAuthor(ServerRequest serverRequest) {
        Mono<Author> authorToUpdate = serverRequest.bodyToMono(Author.class);
        Mono<Author> updatedAuthor = authorRepository.updateAuthor(authorToUpdate);

        return ServerResponse.ok().body(updatedAuthor, Author.class);
    }

    private Mono<ServerResponse> createAuthor(ServerRequest serverRequest) {
        Mono<Author> author = serverRequest.bodyToMono(Author.class);
        Mono<Author> persistedAuthor = authorRepository.save(author);

        return ServerResponse.ok().body(persistedAuthor, Author.class);
    }

    private Mono<ServerResponse> getAuthors(ServerRequest serverRequest) {

        Flux<Author> authors = authorRepository.getAuthors();

        return ServerResponse.ok().body(authors, Author.class);
    }

    private Mono<ServerResponse> getAuthorById(ServerRequest serverRequest) {
        Long id = Long.valueOf(serverRequest.pathVariable("id"));

        try {
            Mono<Author> author = authorRepository.findAuthorById(id);
            return ServerResponse.ok().body(author, Author.class);
        } catch (Exception e) {
            return ServerResponse.badRequest().syncBody("Unable to find author");
        }
    }
}
