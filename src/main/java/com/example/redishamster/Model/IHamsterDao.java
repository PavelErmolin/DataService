package com.example.redishamster.Model;

import java.util.Map;

public interface IHamsterDao {
    void saveHamster(Hamster ham);
    Hamster getOneHamster(Integer id);
    void updateHamster(Hamster ham);
    Map<Integer, Hamster> getAllHamsters();
    void deleteHamster(Integer id);
    void saveAllHamsters(Map<Integer, Hamster> map);
}
