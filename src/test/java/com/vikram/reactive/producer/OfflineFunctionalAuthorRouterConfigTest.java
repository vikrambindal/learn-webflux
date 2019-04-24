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
public class OfflineFunctionalAuthorRouterConfigTest {

    private static final Long IRRELEVANT_ID = 1L;
    private Author irrelevantAuthor = new Author(IRRELEVANT_ID, "irrelevant_author", "irrelevant_country");

    @Autowired
    RouterFunction<ServerResponse> routerFunction;
    @MockBean
    private AuthorRepository authorRepository;

    private WebTestClient webTestClient;

    @Before
    public void before() {
        webTestClient = WebTestClient.bindToRouterFunction(routerFunction).build();
    }

    @Test
    public void testGetAuthors() {

        Author expectedAuthor = irrelevantAuthor;

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
    public void testGetAuthorById_shouldReturnSearchedAuthor() throws Exception {

        Author expectedAuthor = irrelevantAuthor;

        when(authorRepository.findAuthorById(IRRELEVANT_ID)).thenReturn(Mono.just(irrelevantAuthor));

        webTestClient.get().uri("/author/" + IRRELEVANT_ID)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON_UTF8)
                .expectBody(Author.class)
                .isEqualTo(expectedAuthor);
    }

    @Test
    public void testGetAuthorById_shouldShowBadRequestWithInvalidAuthor() throws Exception {

        when(authorRepository.findAuthorById(IRRELEVANT_ID)).thenThrow(Exception.class);

        webTestClient.get().uri("/author/" + IRRELEVANT_ID)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(String.class)
                .isEqualTo("No author found with id " + IRRELEVANT_ID);
    }

    @Test
    public void testCreateAuthor() {

        Mono<Author> unsavedAuthor = Mono.just(new Author(null, "TestAuthor", "TestCountry"));
        Mono<Author> savedAuthorMono = Mono.just(irrelevantAuthor);

        when(authorRepository.save(any())).thenReturn(savedAuthorMono);

        webTestClient.post().uri("/author")
                .contentType(MediaType.APPLICATION_JSON)
                .body(unsavedAuthor, Author.class)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON_UTF8)
                .expectBody(Author.class)
                .isEqualTo(irrelevantAuthor);
    }

    @Test
    public void testDeleteAuthor_authorDeletedSuccessfully() {

        when(authorRepository.deleteAuthor(IRRELEVANT_ID)).thenReturn(Mono.just(true));

        webTestClient.delete().uri("/author/" + IRRELEVANT_ID)
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .isEqualTo("true");
    }

    @Test
    public void testDeleteAuthor_authorDeletedFailed() {

        when(authorRepository.deleteAuthor(IRRELEVANT_ID)).thenReturn(Mono.just(false));

        webTestClient.delete().uri("/author/" + IRRELEVANT_ID)
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .isEqualTo("false");
    }

    @Test
    public void testUpdateAuthor() {

        Mono<Author> authorToUpdate = Mono.just(irrelevantAuthor);

        when(authorRepository.updateAuthor(any())).thenReturn(Mono.just(irrelevantAuthor));

        webTestClient.put().uri("/author")
                .contentType(MediaType.APPLICATION_JSON)
                .body(authorToUpdate, Author.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Author.class)
                .isEqualTo(irrelevantAuthor);
    }
}
