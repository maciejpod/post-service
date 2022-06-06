package com.maciej.postservice.posts;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.maciej.postservice.exceptions.PostConversionException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import com.maciej.postservice.posts.PostsRestClient.PostResponseBodyConverter;

import static org.assertj.core.api.Assertions.assertThat;

class PostResponseBodyConverterTest {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Test
    void shouldConvertResponseBody() {
        // given
        final var body = """
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
                """;

        final var converter = PostResponseBodyConverter.of(MAPPER);

        // when
        final var result = converter.convert(body);

        // then
        assertThat(result)
                .containsExactly(
                        new Post(1, 1, "title 1", "body 1"),
                        new Post(1, 2, "title 2", "body 2")
                );
    }

    @Test
    void shouldThrowExceptionWhenBodyHasWrongFormat() {
        // given
        final var invalidJsonBody = "{";

        final var converter = PostResponseBodyConverter.of(MAPPER);

        // when & then
        Assertions.assertThatExceptionOfType(PostConversionException.class)
                .isThrownBy(() -> converter.convert(invalidJsonBody))
                .withMessage("Could not deserialize post!");
    }
}
