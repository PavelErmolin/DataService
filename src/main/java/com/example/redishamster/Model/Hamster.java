package com.example.redishamster.Model;

import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Hamster implements Serializable {

    private static final long serialVersionUID = -7817224776021728682L;
    @Id
    @GeneratedValue
    private Integer hamId;
    private String hamsterName;
    private Double hamsterWeight;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Hamster hamster = (Hamster) o;
        return hamId != null && Objects.equals(hamId, hamster.hamId);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}