package com.maciej.postservice.posts;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

import static java.util.Collections.unmodifiableList;
import static org.assertj.core.api.Assertions.assertThat;

class PostsFacadeTest {

    @Test
    void shouldFetchAndStorePosts() {
        // given
        final var posts = List.of(
                new Post(1, 1, "title 1", "body 1"),
                new Post(1, 2, "title 2", "body 2"),
                new Post(2, 3, "title 3", "body 3")
        );

        final var postsSupplier = new TestPostSupplier(posts);
        final var postStorage = new TestInMemoryPostsStorage();

        final var facade = new PostsFacade(postsSupplier, postStorage);

        // when
        final var result = facade.fetchAndStorePosts();

        // then
        assertThat(result)
                .containsExactlyElementsOf(posts);

        assertThat(postStorage.posts())
                .containsExactlyElementsOf(posts);
    }

    static class TestPostSupplier implements PostsSupplier {
        private final Collection<Post> posts;

        TestPostSupplier(Collection<Post> posts) {
            this.posts = posts;
        }

        @Override
        public Stream<Post> fetchPosts() {
            return posts.stream();
        }
    }

    static class TestInMemoryPostsStorage implements PostsStorage {
        private final List<Post> posts = new ArrayList<>();

        @Override
        public Post store(Post post) {
            posts.add(post);

            return post;
        }

        public List<Post> posts() {
            return unmodifiableList(posts);
        }
    }
}