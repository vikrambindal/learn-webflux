package com.vikram.reactive.producer;

import com.vikram.reactive.pojo.Author;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest
public class OfflineAuthorRouterFunctionConfigTest {

    @MockBean
    private AuthorRepository authorRepository;

    @Autowired
    RouterFunction<ServerResponse> routerFunction;

    private WebTestClient webTestClient;

    @Before
    public void before() {
        webTestClient = WebTestClient.bindToRouterFunction(routerFunction).build();
    }

    @Test
    public void testGetAuthors() {

        Author expectedAuthor = new Author(1L, "Josh Long", "USA");

        when(authorRepository.getAuthors()).thenReturn(Flux.just(expectedAuthor));

        webTestClient.get().uri("/authors")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON_UTF8)
                .expectBodyList(Author.class)
                .contains(expectedAuthor);
                //.expectBody().json("[{\"name\":\"Josh Long\",\"country\":\"USA\"},{\"name\":\"Chetan Bhagat\",\"country\":\"India\"}]");
    }

    @Test
    public void testCreateAuthor() {

        Mono<Author> unsavedAuthor = Mono.just(new Author(null, "TestAuthor", "TestCountry"));
        Author savedAuthor = new Author(1l, "TestAuthor", "TestCountry");
        Mono<Author> savedAuthorMono = Mono.just(savedAuthor);

        when(authorRepository.save(any())).thenReturn(savedAuthorMono);

        webTestClient.post().uri("/author")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(unsavedAuthor, Author.class)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON_UTF8)
                .expectBody(Author.class)
                .isEqualTo(savedAuthor);
    }
}
