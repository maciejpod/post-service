package com.maciej.postservice;

import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;

class PostServiceArgsValidator {
    private final String[] args;

    PostServiceArgsValidator(String[] args) {
        this.args = args;
    }

    Path validate() {
        validateArgumentsLength();
        return validatePostOutputDirectoryPath();
    }

    private Path validatePostOutputDirectoryPath() {
        final var path = validatePath();

        if (Files.isDirectory(path)) {
            return path;
        }

        throw new PostServiceArgsException("Post output path must be a directory!");
    }

    private Path validatePath() {
        try {
            return Paths.get(args[0]);
        } catch (InvalidPathException exc) {
            throw new PostServiceArgsException("Posts output directory is not a valid path!", exc);
        }
    }

    private void validateArgumentsLength() {
        if (args.length < 1) {
            throw new PostServiceArgsException("Posts output directory must be given!");
        }
    }

    static class PostServiceArgsException extends RuntimeException {
        public PostServiceArgsException(String message) {
            super(message);
        }

        public PostServiceArgsException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
