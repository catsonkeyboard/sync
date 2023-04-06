package org.catsonkeyboard;

import io.jooby.Jooby;
import org.catsonkeyboard.controller.MyController;

import static io.jooby.Jooby.runApp;

public class App extends Jooby {
    public static void main(String[] args) {
        runApp(args, app -> {
            app.get("/", ctx -> "Hello Jooby!");
        });
    }
}