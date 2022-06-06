package com.maciej.postservice.posts;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.maciej.postservice.exceptions.PostConversionException;
import com.maciej.postservice.exceptions.PostSupplierException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.stream.Stream;

import static java.lang.String.format;
import static java.net.HttpURLConnection.HTTP_OK;

class PostsRestClient implements PostsSupplier {
    private static final Logger log = LoggerFactory.getLogger(PostsRestClient.class);
    private final String allPostsUrl;
    private final HttpClient client;
    private final ResponseBodyConverter<Post> responseBodyConverter;

    public PostsRestClient(String allPostsUrl, ResponseBodyConverter<Post> responseBodyConverter) {
        this.allPostsUrl = allPostsUrl;
        this.responseBodyConverter = responseBodyConverter;
        this.client = HttpClient.newHttpClient();
    }

    PostsRestClient(HttpClient httpClient, String allPostsUrl, ResponseBodyConverter<Post> responseBodyConverter) {
        this.allPostsUrl = allPostsUrl;
        this.client = httpClient;
        this.responseBodyConverter = responseBodyConverter;
    }

    @Override
    public Stream<Post> fetchPosts() {
        log.info("Attempt to fetch com.maciej.postservice.posts from {}", allPostsUrl);

        try {
            return getPosts();
        } catch (URISyntaxException | IOException exc) {
            throw new PostSupplierException("Could not fetch com.maciej.postservice.posts!", exc);
        } catch (InterruptedException exc) {
            Thread.currentThread().interrupt();

            throw new PostSupplierException("Could not fetch com.maciej.postservice.posts!", exc);
        }
    }

    private Stream<Post> getPosts() throws URISyntaxException, IOException, InterruptedException {
        final var request = request();
        final var response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == HTTP_OK) {
            log.info("Posts successfully fetched!");

            return responseBodyConverter.convert(response.body());
        }

        throw new PostSupplierException(
                format("Could not GET com.maciej.postservice.posts - status: '%s' body: '%s'", response.statusCode(), response.body())
        );
    }

    private HttpRequest request() throws URISyntaxException {
        final var uri = new URI(allPostsUrl);

        return HttpRequest.newBuilder()
                .uri(uri)
                .GET()
                .build();
    }

    static class PostResponseBodyConverter implements ResponseBodyConverter<Post> {
        private final ObjectMapper objectMapper;

        static PostResponseBodyConverter of(ObjectMapper objectMapper) {
            return new PostResponseBodyConverter(objectMapper);
        }

        private PostResponseBodyConverter(ObjectMapper objectMapper) {
            this.objectMapper = objectMapper;
        }

        @Override
        public Stream<Post> convert(String value) {
            try {
                final var posts = objectMapper.readValue(value, new TypeReference<List<Post>>() {
                });

                log.info("Posts {}", posts.size());

                return posts.stream();
            } catch (JsonProcessingException exc) {
                throw new PostConversionException("Could not deserialize post!", exc);
            }
        }
    }

    interface ResponseBodyConverter<T> {
        Stream<T> convert(String value);
    }

}
