package com.maciej.postservice.posts;


import java.util.List;

public class PostsFacade {
    private final PostsSupplier postsSupplier;
    private final PostsStorage postsStorage;

    public PostsFacade(PostsSupplier postsSupplier, PostsStorage postsStorage) {
        this.postsSupplier = postsSupplier;
        this.postsStorage = postsStorage;
    }

    public List<Post> fetchAndStorePosts() {
        return postsSupplier.fetchPosts()
                .map(postsStorage::store)
                .toList();
    }
}
