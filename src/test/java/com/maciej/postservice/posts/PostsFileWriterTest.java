package com.maciej.postservice.posts;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.maciej.postservice.exceptions.PostStorageException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import com.maciej.postservice.posts.PostsFileWriter.FileNameStrategy;

import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static com.maciej.postservice.posts.PostsFacadeFactory.FILE_NAME_STRATEGY;


class PostsFileWriterTest {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Test
    void shouldStorePostInFile(@TempDir Path tempDir) {
        // given
        final var post = new Post(1, 2, "test", "test");
        final var fileWriter = new PostsFileWriter(FILE_NAME_STRATEGY, tempDir, MAPPER);

        // when
        fileWriter.store(post);

        // then
        assertThat(tempDir.resolve("2.json"))
                .exists()
                .hasContent("""
                        {
                            "userId": 1,
                            "id": 2,
                            "title": "test",
                            "body": "test"
                        }
                        """.replaceAll("\\s", ""));
    }

    @Test
    void shouldThrowExceptionWhenCouldNotStorePost(@TempDir Path tempDir) {
        // given
        final var post = new Post(1, 2, "test", "test");
        final var invalidFileName = "\\/*$#@$*()*!";
        final var strategy = (FileNameStrategy) p -> invalidFileName;
        final var fileWriter = new PostsFileWriter(strategy, tempDir, MAPPER);

        // when & then
        assertThatExceptionOfType(PostStorageException.class)
                .isThrownBy(() -> fileWriter.store(post))
                .withMessage("Could not store post with id '%s' to file with name '%s'", post.id(), invalidFileName);
    }
}