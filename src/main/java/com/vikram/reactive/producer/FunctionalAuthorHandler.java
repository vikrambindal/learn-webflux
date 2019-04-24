package com.vikram.reactive.producer;

import com.vikram.reactive.pojo.Author;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class FunctionalAuthorHandler {

    private AuthorRepository authorRepository;

    public FunctionalAuthorHandler(AuthorRepository authorRepository) {
        this.authorRepository = authorRepository;
    }

    public Mono<ServerResponse> deleteAuthor(ServerRequest serverRequest) {
        Long id = Long.valueOf(serverRequest.pathVariable("id"));
        Mono<Boolean> deleteMono = authorRepository.deleteAuthor(id);
        return ServerResponse.ok().body(deleteMono, Boolean.class);
    }

    public Mono<ServerResponse> updateAuthor(ServerRequest serverRequest) {
        Mono<Author> authorToUpdate = serverRequest.bodyToMono(Author.class);
        Mono<Author> updatedAuthor = authorRepository.updateAuthor(authorToUpdate);

        return ServerResponse.ok().body(updatedAuthor, Author.class);
    }

    public Mono<ServerResponse> createAuthor(ServerRequest serverRequest) {
        Mono<Author> author = serverRequest.bodyToMono(Author.class);
        Mono<Author> persistedAuthor = authorRepository.save(author);

        return ServerResponse.ok().body(persistedAuthor, Author.class);
    }

    public Mono<ServerResponse> getAuthors(ServerRequest serverRequest) {

        Flux<Author> authors = authorRepository.getAuthors();

        return ServerResponse.ok().body(authors, Author.class);
    }

    public Mono<ServerResponse> getAuthorById(ServerRequest serverRequest) {
        Long id = Long.valueOf(serverRequest.pathVariable("id"));

        try {
            Mono<Author> author = authorRepository.findAuthorById(id);
            return ServerResponse.ok().body(author, Author.class);
        } catch (Exception e) {
            return ServerResponse.badRequest().body(Mono.just("No author found with id " + id), String.class);
        }
    }
}
