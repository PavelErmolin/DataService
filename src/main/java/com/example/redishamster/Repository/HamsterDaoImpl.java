package com.example.redishamster.Repository;

import com.example.redishamster.Model.Hamster;
import com.example.redishamster.Model.IHamsterDao;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.Map;

@Repository
public class HamsterDaoImpl implements IHamsterDao {
    private final String hashReference= "Hamster";

    @Resource(name="redisTemplate")
    private HashOperations<String, Integer, Hamster> hashOperations;

    @Override
    public void saveHamster(Hamster ham) {
        hashOperations.putIfAbsent(hashReference, ham.getHamId(), ham);
    }

    @Override
    public void saveAllHamsters(Map<Integer, Hamster> map) {
        hashOperations.putAll(hashReference, map);
    }

    @Override
    public Hamster getOneHamster(Integer id) {
        return hashOperations.get(hashReference, id);
    }

    @Override
    public void updateHamster(Hamster ham) {
        hashOperations.put(hashReference, ham.getHamId(), ham);
    }

    @Override
    public Map<Integer, Hamster> getAllHamsters() {
        return hashOperations.entries(hashReference);
    }

    @Override
    public void deleteHamster(Integer id) {
        hashOperations.delete(hashReference, id);
    }
}
