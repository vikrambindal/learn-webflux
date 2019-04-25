package com.vikram.reactive.producer;

import com.vikram.reactive.pojo.Author;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class OnlineFunctionalAuthorRouterConfigTest
{

    private static final Long IRRELEVANT_ID = 100L;
    private static final Long RELEVANT_ID = 1L;
    private List<Author> authors;

    @Autowired
    private WebTestClient webTestClient;

    @Before
    public void before() {
        InMemoryAuthorRespositoryImpl.authors = new ArrayList<>();
        InMemoryAuthorRespositoryImpl.authors.add(new Author( 1l, "Dan Brows", "USA"));
        InMemoryAuthorRespositoryImpl.authors.add(new Author(2l, "Chetan Bhagat", "India"));

        authors = InMemoryAuthorRespositoryImpl.authors;
    }

    @After
    public void after () {
        InMemoryAuthorRespositoryImpl.authors.clear();
        authors.clear();
    }

    @Test
    public void testGetAuthors() {

        webTestClient.get().uri("/authors")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON_UTF8)
                .expectBodyList(Author.class)
                .contains(authors.get(0), authors.get(1));
    }

    @Test
    public void testGetAuthorById_shouldReturnSearchedAuthor() throws Exception {

        webTestClient.get().uri("/author/" + RELEVANT_ID)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON_UTF8)
                .expectBody(Author.class)
                .isEqualTo(authors.get(0));
    }

    @Test
    public void testGetAuthorById_shouldShowBadRequestWithInvalidAuthor() throws Exception {

        webTestClient.get().uri("/author/" + IRRELEVANT_ID)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(String.class)
                .isEqualTo("No author found with id " + IRRELEVANT_ID);
    }

    @Test
    public void testCreateAuthor() {

        Author data = new Author(null, "TestAuthor", "TestCountry");
        Mono<Author> unsavedAuthor = Mono.just(data);
        data.setId(3L);

        webTestClient.post().uri("/author")
                .contentType(MediaType.APPLICATION_JSON)
                .body(unsavedAuthor, Author.class)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON_UTF8)
                .expectBody(Author.class)
                .isEqualTo(data);
    }

    @Test
    public void testDeleteAuthor_authorDeletedSuccessfully() {

        webTestClient.delete().uri("/author/" + RELEVANT_ID)
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .isEqualTo("true");
    }

    @Test
    public void testDeleteAuthor_authorDeletedFailed() {

        webTestClient.delete().uri("/author/" + 999)
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .isEqualTo("false");
    }

    @Test
    public void testUpdateAuthor() {

        Author data = new Author(1L, "TestAuthor", "TestCountry");
        Mono<Author> authorToUpdate = Mono.just(data);

        webTestClient.put().uri("/author")
                .contentType(MediaType.APPLICATION_JSON)
                .body(authorToUpdate, Author.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Author.class)
                .isEqualTo(data);
    }
}
