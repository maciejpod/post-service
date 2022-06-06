package com.maciej.postservice.posts;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.maciej.postservice.posts.PostsFileWriter.FileNameStrategy;

import java.nio.file.Path;

import static java.lang.String.format;

public class PostsFacadeFactory {
    private static final String URL = "https://jsonplaceholder.typicode.com/posts";
    static final FileNameStrategy FILE_NAME_STRATEGY = post -> format("%s.json", post.id());

    public static PostsFacade postsFacade(Path postsOutputDirectory) {
        final var objectMapper = new ObjectMapper();
        final var bodyConverter = PostsRestClient.PostResponseBodyConverter.of(objectMapper);
        final var postPostsRestClient = new PostsRestClient(URL, bodyConverter);
        final var postsConsumer = new PostsFileWriter(FILE_NAME_STRATEGY, postsOutputDirectory, objectMapper);

        return new PostsFacade(postPostsRestClient, postsConsumer);
    }

}
