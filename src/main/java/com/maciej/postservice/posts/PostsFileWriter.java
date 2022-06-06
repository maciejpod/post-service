package com.maciej.postservice.posts;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.maciej.postservice.exceptions.PostStorageException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;

import static java.lang.String.format;

class PostsFileWriter implements PostsStorage {
    private static final Logger log = LoggerFactory.getLogger(PostsFileWriter.class);
    private final FileNameStrategy fileNameStrategy;
    private final ObjectMapper mapper;
    private final Path postsOutputDirectory;

    PostsFileWriter(FileNameStrategy fileNameStrategy, Path postsOutputDirectory, ObjectMapper mapper) {
        this.fileNameStrategy = fileNameStrategy;
        this.postsOutputDirectory = postsOutputDirectory;
        this.mapper = mapper;
    }

    @Override
    public Post store(Post post) {
        final var fileName = fileNameStrategy.fileName(post);
        try {
            return saveFileContent(post, fileName);
        } catch (IOException | InvalidPathException exc) {
            throw new PostStorageException(
                    format("Could not store post with id '%s' to file with name '%s'", post.id(), fileName), exc
            );
        }
    }

    private Post saveFileContent(Post post, String fileName) throws IOException {
        log.info("Attempt to save post with id {} to file with name {}", post.id(), fileName);

        final var outputFile = postsOutputDirectory.resolve(fileName);
        final var fileContent = mapper.writeValueAsString(post);

        Files.write(outputFile, fileContent.getBytes());

        log.info("Successfully saved post with id {} to file {}", post.id(), outputFile);

        return post;
    }

    interface FileNameStrategy {
        String fileName(Post post);
    }
}
