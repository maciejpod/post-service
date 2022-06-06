package com.maciej.postservice.posts;


import org.junit.jupiter.api.Test;

import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;

class PostsFacadeFactoryTest {

    @Test
    void shouldCreatePostFacade() {
        // given
        final var configuration = new PostsFacadeFactory();
        final var postsOutputDirectory = Paths.get("/");

        // when
        final var result = configuration.postsFacade(postsOutputDirectory);

        // then
        assertThat(result)
                .isNotNull();
    }
}