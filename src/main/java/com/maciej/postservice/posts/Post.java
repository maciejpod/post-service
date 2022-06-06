package com.maciej.postservice.posts;

public record Post(long userId, long id, String title, String body) {
}
