package org.catsonkeyboard;

import java.util.function.Function;
import java.util.function.Supplier;

public class Subscriber<T> {

    public Subscriber(String topic, Supplier<String> serverFilter, Function<T, Boolean> clientFilter, Runnable action, Boolean syncToServer) {
        this.topic = topic;
        this.serverFilter = serverFilter;
        this.clientFilter = clientFilter;
        this.action = action;
        this.syncToServer = syncToServer;
    }

    private String topic;

    private Supplier<String> serverFilter;

    private Function<T, Boolean> clientFilter;

    private Runnable action;

    private Boolean syncToServer;

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public Supplier<String> getServerFilter() {
        return serverFilter;
    }

    public void setServerFilter(Supplier<String> serverFilter) {
        this.serverFilter = serverFilter;
    }

    public Function<T, Boolean> getClientFilter() {
        return clientFilter;
    }

    public void setClientFilter(Function<T, Boolean> clientFilter) {
        this.clientFilter = clientFilter;
    }

    public Runnable getAction() {
        return action;
    }

    public void setAction(Runnable action) {
        this.action = action;
    }

    public Boolean getSyncToServer() {
        return syncToServer;
    }

    public void setSyncToServer(Boolean syncToServer) {
        this.syncToServer = syncToServer;
    }
}
