package ru.netology.repository;

import org.springframework.stereotype.Repository;
import ru.netology.exception.NotFoundException;
import ru.netology.model.Post;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;


@Repository
public class PostRepositoryImpl implements PostRepository{
    private AtomicLong postCount = new AtomicLong(0);
    private ConcurrentHashMap<Long, Post> postMap = new ConcurrentHashMap<>();

    public List<Post> all() {
        return postMap.values().parallelStream().collect(Collectors.toList());
    }

    public Optional<Post> getById(long id) {

        return Optional.ofNullable(postMap.get(id));
    }

    public Post save(Post post) {
        try {
            if (post.getId() == 0) {
                post.setId(postCount.incrementAndGet());
                return postMap.put(post.getId(), post);
            } else {
                Optional<Post> optionalPost = getById(post.getId());
                if (optionalPost.isPresent()) {
                    Post post1 = optionalPost.get();
                    post1.setContent(post.getContent());
                    return post1;
                } else {
                    throw new NotFoundException("Post with ID " + post.getId() + " not found");
                }
            }
        } catch (NotFoundException nfe) {
            System.err.println("An unexpected error occurred: " + nfe.getMessage());
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return post;
    }

    public void removeById(long id) {
        try {
            if (getById(id).isEmpty()) {
                throw new NotFoundException("Post with ID " + id + " not found");
            }
            postMap.remove(id);
        } catch (NotFoundException nfe) {
            System.err.println("An unexpected error occurred: " + nfe.getMessage());
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }
}
