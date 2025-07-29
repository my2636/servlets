package ru.netology;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import ru.netology.controller.PostController;

public class Main {
    public static void main(String[] args) {
        var context = new AnnotationConfigApplicationContext("ru.netology");
        var postController = context.getBean(PostController.class);
    }
}
