package com.vikram.reactive.producer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class FunctionalAuthorRouterConfig {

    @Autowired
    private FunctionalAuthorHandler functionalAuthorHandler;

    @Bean
    public RouterFunction<ServerResponse> authorRoutes() {
        return RouterFunctions
                .route(RequestPredicates.GET("/authors"),
                        functionalAuthorHandler::getAuthors)
                .andRoute(RequestPredicates.GET("/author/{id}"),
                        functionalAuthorHandler::getAuthorById)
                .andRoute(RequestPredicates.POST("/author")
                                .and(RequestPredicates.accept(MediaType.APPLICATION_JSON)),
                        functionalAuthorHandler::createAuthor)
                .andRoute(RequestPredicates.PUT("/author")
                                .and(RequestPredicates.accept(MediaType.APPLICATION_JSON)),
                        functionalAuthorHandler::updateAuthor)
                .andRoute(RequestPredicates.DELETE("/author/{id}"),
                        functionalAuthorHandler::deleteAuthor);
    }


}
