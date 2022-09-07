package com.example.redishamster.Repository;


import com.example.redishamster.Model.Hamster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface HamsterRepository extends JpaRepository<Hamster, Integer> {
}
