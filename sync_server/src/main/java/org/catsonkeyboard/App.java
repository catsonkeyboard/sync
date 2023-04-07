package org.catsonkeyboard;

import io.jooby.Jooby;
import static io.jooby.Jooby.runApp;
import org.catsonkeyboard.controller.MyController;

public class App extends Jooby {
    public static void main(String[] args) {
        runApp(args, app -> {
            app.get("/", ctx -> "Hello Jooby!");
        });
    }
}