package org.catsonkeyboard.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "students")
public class Student {
    @Id
    //@SequenceGenerator(name = "studentSeq", sequenceName = "student_id_seq", allocationSize = 1, initialValue = 1)
    //@GeneratedValue(generator = "studentSeq")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    public Long id() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    private String name;

    @Column(name = "name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(name = "sex")
    private Boolean sex;

    public Boolean getSex() {
        return sex;
    }

    public void setSex(Boolean sex) {
        this.sex = sex;
    }
}