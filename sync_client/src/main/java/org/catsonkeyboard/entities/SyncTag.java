package org.catsonkeyboard.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.catsonkeyboard.annotation.ClientTable;

@Entity
@Table(name = "sync_tag")
@ClientTable
public class SyncTag {

    @Id
    private String topic;

    private Long lut;

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public Long getLut() {
        return lut;
    }

    public void setLut(Long lut) {
        this.lut = lut;
    }
}
