package com.vikram.reactive.producer;

import com.vikram.reactive.pojo.Author;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface AuthorRepository {

    Flux<Author> getAuthors();

    Mono<Author> save(Mono<Author> author);

    Mono<Author> findAuthorById(Long id) throws Exception;

    Mono<Author> updateAuthor(Mono<Author> authorToUpdate);

    Mono<Boolean> deleteAuthor(Long id);
}
