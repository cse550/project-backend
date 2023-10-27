package com.cse550.projectbackend.post.repository;

import com.cse550.projectbackend.post.model.Post;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends MongoRepository<Post, String> {
    List<Post> findByUserIdIn(List<String> userIds);

}
