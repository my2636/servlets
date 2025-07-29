package ru.netology.servlet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;
import ru.netology.controller.PostController;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/*
curl -X POST -H "Content-Type: application/json" -d "{"id": 0, "content": "1234"}" http://localhost:8080/api/posts
curl http://localhost:8080/api/posts
curl -X DELETE http://localhost:8080/api/posts/2
* */

public class MainServlet extends HttpServlet {

    private static final String GET = "GET";
    private static final String POST = "POST";
    private static final String DELETE = "DELETE";
    private static final String POST_PATH = "/api/posts";
    private static final String POST_ID_PATH = "/api/posts/\\d+";


    PostController controller;


    @Override
    final public void init() throws ServletException {
        super.init();
        SpringBeanAutowiringSupport.processInjectionBasedOnCurrentContext(this);
    }

    @Override
    final protected void service(HttpServletRequest req, HttpServletResponse resp) {
        // если деплоились в root context, то достаточно этого
        try {
            final var path = req.getRequestURI();
            final var method = req.getMethod();
            // primitive routing
            if (method.equals(GET)) {
                doGet(path, resp);
            } else if (POST.equals(method)) {
                doPost(path, req, resp);
            } else if (method.equals(DELETE)) {
                doDelete(path, resp);
            } else {System.out.println(method);
            resp.setStatus(HttpServletResponse.SC_FORBIDDEN);}
        } catch (Exception e) {
            e.printStackTrace();
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    final protected void doGet(String path, HttpServletResponse resp) throws IOException {
        if (path.equals(POST_PATH)) {
            controller.all(resp);
            return;
        }
        if (path.matches(POST_ID_PATH)) {
            // easy way
            final var id = Long.parseLong(path.substring(path.lastIndexOf("/")).replace("/", ""));
            controller.getById(id, resp);
        }
    }

    final protected void doPost(String path, HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if (path.equals(POST_PATH)) {
            controller.save(req.getReader(), resp);
        }
    }

    final protected void doDelete(String path, HttpServletResponse resp) {
        if (path.matches(POST_ID_PATH)) {
            // easy way
            final var id = Long.parseLong(path.substring(path.lastIndexOf("/")).replace("/", ""));
            controller.removeById(id, resp);
        }
    }
}

