package org.catsonkeyboard.controller;

import io.jooby.Route;
import io.jooby.annotations.GET;
import io.jooby.annotations.Path;

public class MyController {
    @GET
    public String sayHi() {
        return "Hello Jooby!";
    }
}
