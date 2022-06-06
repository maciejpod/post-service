package com.maciej.postservice;

import com.maciej.postservice.PostServiceArgsValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

class PostServiceArgsValidatorTest {

    @Test
    void shouldPassValidation(@TempDir Path tempDir) {
        // given
        final var args = new String[]{tempDir.toAbsolutePath().toString()};
        final var validator = new PostServiceArgsValidator(args);

        // when
        final var result = validator.validate();

        // then
        assertThat(result)
                .isEqualTo(tempDir);
    }

    @Test
    void shouldThrowExceptionWhenEmptyArgumentsGiven() {
        // given
        final var args = new String[]{};
        final var validator = new PostServiceArgsValidator(args);

        // when & then
        assertThatExceptionOfType(PostServiceArgsValidator.PostServiceArgsException.class)
                .isThrownBy(validator::validate)
                .withMessage("Expected argument with com.maciej.postservice.posts output directory must be given!");
    }

    @Test
    void shouldThrowExceptionWhenPathIsNotValid() {
        // given
        final var args = new String[]{"\\/&&**()%^"};
        final var validator = new PostServiceArgsValidator(args);

        // when & then
        assertThatExceptionOfType(PostServiceArgsValidator.PostServiceArgsException.class)
                .isThrownBy(validator::validate)
                .withMessage("Posts output directory is not a valid path!");
    }

    @Test
    void shouldThrowExceptionWhenPathIsNotADirectory() {
        // given
        final var args = new String[]{"file.txt"};
        final var validator = new PostServiceArgsValidator(args);

        // when & then
        assertThatExceptionOfType(PostServiceArgsValidator.PostServiceArgsException.class)
                .isThrownBy(validator::validate)
                .withMessage("Post output path must be a directory!");
    }
}
