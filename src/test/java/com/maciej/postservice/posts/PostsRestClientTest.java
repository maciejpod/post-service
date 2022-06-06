package com.maciej.postservice.posts;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import com.maciej.postservice.exceptions.PostSupplierException;
import com.maciej.postservice.helpers.InterruptedExceptionHttpTestClient;
import org.junit.jupiter.api.Test;
import com.maciej.postservice.posts.PostsRestClient.PostResponseBodyConverter;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static java.lang.String.format;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;


@WireMockTest
class PostsRestClientTest {
    private static final PostResponseBodyConverter CONVERTER = PostResponseBodyConverter.of(new ObjectMapper());
    private static final String VALID_URL_PATTERN = "http://localhost:%s/posts";
    private static final String URL_WITH_INVALID_PROTOCOL = "1234://localhost:%s/com.maciej.postservice.posts";

    @Test
    void shouldReturnPosts(WireMockRuntimeInfo wmRuntimeInfo) {
        // given
        final var url = format(VALID_URL_PATTERN, wmRuntimeInfo.getHttpPort());
        final var client = new PostsRestClient(url, CONVERTER);

        stubFor(get("/posts").willReturn(
                ok().withBody("""
                        [
                          {
                            "userId": 1,
                            "id": 1,
                            "title": "title 1",
                            "body": "body 1"
                          },
                          {
                            "userId": 1,
                            "id": 2,
                            "title": "title 2",
                            "body": "body 2"
                          }
                        ]
                        """
                )
        ));

        // when
        final var result = client.fetchPosts();

        // then
        assertThat(result)
                .containsExactly(
                        new Post(1, 1, "title 1", "body 1"),
                        new Post(1, 2, "title 2", "body 2")
                );
    }

    @Test
    void shouldThrowExceptionWhenResponseIs5xx(WireMockRuntimeInfo wmRuntimeInfo) {
        // given
        final var url = format(VALID_URL_PATTERN, wmRuntimeInfo.getHttpPort());
        final var client = new PostsRestClient(url, CONVERTER);

        stubFor(get("/posts").willReturn(
                serverError().withBody("""
                        {
                            "message": "invalid request"
                        }
                        """
                )
        ));

        // when & then
        assertThatExceptionOfType(PostSupplierException.class)
                .isThrownBy(client::fetchPosts)
                .withMessageStartingWith("Could not GET posts - status: '500' body: ");
    }

    @Test
    void shouldThrowExceptionWhenCouldNotExecuteRequest() {
        // given
        final var client = new PostsRestClient(URL_WITH_INVALID_PROTOCOL, CONVERTER);

        // when & then
        assertThatExceptionOfType(PostSupplierException.class)
                .isThrownBy(client::fetchPosts)
                .withMessage("Could not fetch posts!");
    }

    @Test
    void shouldThrowExceptionWhenThreadIsInterrupted(WireMockRuntimeInfo wmRuntimeInfo) {
        final var url = format(VALID_URL_PATTERN, wmRuntimeInfo.getHttpPort());
        final var testClient = new InterruptedExceptionHttpTestClient();
        final var client = new PostsRestClient(testClient, url, CONVERTER);

        // when & then
        assertThatExceptionOfType(PostSupplierException.class)
                .isThrownBy(client::fetchPosts)
                .withMessage("Could not fetch posts!");
    }

}