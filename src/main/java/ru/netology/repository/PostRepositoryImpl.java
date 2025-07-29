package ru.netology.repository;

import org.springframework.stereotype.Repository;
import ru.netology.exception.NotFoundException;
import ru.netology.model.Post;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/*
 Задача
Осуществите рефакторинг кода.
Реализуйте репозиторий с учётом того, что методы репозитория могут вызываться конкурентно, т. е. в разных потоках.
Как должен работать save:

Если от клиента приходит пост с id=0, значит, это создание нового поста. Вы сохраняете его в списке и присваиваете ему
новый id. Достаточно хранить счётчик с целым числом и увеличивать на 1 при создании каждого нового поста.

Если от клиента приходит пост с id !=0, значит, это сохранение (обновление) существующего поста. Вы ищете его в списке
по id и обновляете. Продумайте самостоятельно, что вы будете делать, если поста с таким id не оказалось: здесь могут быть
разные стратегии.



в PostController не реализованы методы getById и removeById
хранение данных в репозитории реализовано неверно. Во-первых, при сохранении поста вы должны потокобезопасно гарантировать ему уникальность идентификатора.
Для потокобезопасного генерирования нового id вам надо использовать atomic объект, например, AtomicLong.
Во-вторых, List для хранения данных в репозитории в данной задача работает неэффективно, вам надо уметь быстро по id получать и удалять пост.
Для этого надо использовать потокобезопасную реализацию Map. Используйте ConcurrentHashMap для этого.
*/
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
