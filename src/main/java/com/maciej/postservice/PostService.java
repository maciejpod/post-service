package com.maciej.postservice;

import com.maciej.postservice.posts.PostsFacadeFactory;

class PostService {

    public static void main(String[] args) {
        final var postOutputPath = new PostServiceArgsValidator(args).validate();

        PostsFacadeFactory.postsFacade(postOutputPath)
                .fetchAndStorePosts();
    }

}
