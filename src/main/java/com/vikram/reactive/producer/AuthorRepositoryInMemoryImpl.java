package com.vikram.reactive.producer;

import com.vikram.reactive.pojo.Author;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class AuthorRepositoryInMemoryImpl implements AuthorRepository {

    private static long i = 0;
    private List<Author> authors = new ArrayList<>();
    {
        authors.add(new Author( ++i, "Dan Brows", "USA"));
        authors.add(new Author( ++i, "Chetan Bhagat", "India"));
    }

    @Override
    public Flux<Author> getAuthors() {
        return Flux.fromIterable(authors);
    }

    @Override
    public Mono<Author> save(Mono<Author> author) {
        return author.doOnNext(author1 -> {
            author1.setId(++i);
            authors.add(author1);
        });
    }

    @Override
    public Mono<Author> findAuthorById(Long id) throws Exception {
        Optional<Author> searchedAuthor = authors.stream()
                .filter(author -> author.getId().equals(id))
                .findFirst();

        return Mono.justOrEmpty(searchedAuthor.orElseThrow(() -> new Exception("Unable to find author")));
    }

    @Override
    public Mono<Author> updateAuthor(Mono<Author> authorToUpdate) {

        return authorToUpdate.doOnNext(author -> authors.stream()
                .filter(author1 -> author1.getId().equals(author.getId()))
                .findFirst()
                .ifPresent(oldAuthor -> {
                    int pos = authors.indexOf(oldAuthor);
                    authors.remove(oldAuthor);
                    authors.add(pos, new Author(oldAuthor.getId(), author.getName(), author.getCountry()));
                }));
    }

    @Override
    public Mono<Boolean> deleteAuthor(Long id) {
        try {
            Mono<Author> authorMono = findAuthorById(id);
            return authorMono.flatMap(author -> {
                boolean removed = authors.remove(author);
                return Mono.just(removed);
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Mono.just(false);
    }
}
