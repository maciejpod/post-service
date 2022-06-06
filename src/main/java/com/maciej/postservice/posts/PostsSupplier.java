package com.maciej.postservice.posts;

import java.util.stream.Stream;

interface PostsSupplier {

    Stream<Post> fetchPosts();

}
