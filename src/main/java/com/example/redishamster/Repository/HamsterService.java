package com.example.redishamster.Repository;

import com.example.redishamster.Model.Hamster;

import java.util.List;

public interface HamsterService {

    public Hamster saveHamster(Hamster ham);
    public Hamster updateHamster(Hamster ham, Integer hamId);
    public void deleteHamster(Integer hamId);
    public Hamster getHamster(Integer hamId);
    public List<Hamster> getAllHamsters();
}
