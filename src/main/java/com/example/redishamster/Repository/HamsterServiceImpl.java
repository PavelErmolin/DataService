package com.example.redishamster.Repository;

import com.example.redishamster.Model.Hamster;
import com.example.redishamster.HamsterNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@CacheConfig(cacheNames = "hc")
public class HamsterServiceImpl implements HamsterService{

    private final HamsterRepository hamsterRepo;

    @Autowired
    public HamsterServiceImpl(HamsterRepository hamsterRepo) {
        this.hamsterRepo = hamsterRepo;
    }

    @Override
    @CachePut(value="Hamster", key="#ham.hamId")
    public Hamster saveHamster(Hamster ham) {
        return hamsterRepo.save(ham);
    }

    @Override
    @CachePut(value="Hamster", key="#ham.hamId")
    public Hamster updateHamster(Hamster ham, Integer hamId) {
        Hamster hamster = hamsterRepo.findById(hamId)
                .orElseThrow(() -> new HamsterNotFoundException("Hamster Not Found"));
        hamster.setHamsterName(ham.getHamsterName());
        hamster.setHamsterWeight(ham.getHamsterWeight());
        return hamsterRepo.save(hamster);
    }

    @Override
    @CacheEvict(value="Hamster", key="#hamId")
    public void deleteHamster(Integer hamId) {
        Hamster hamster = hamsterRepo.findById(hamId)
                .orElseThrow(() -> new HamsterNotFoundException("Hamster Not Found"));
        hamsterRepo.delete(hamster);
    }

    @Override
    @Cacheable(value="Hamster", key="#hamId")
    public Hamster getHamster(Integer hamId) {
        return hamsterRepo.findById(hamId)
                .orElseThrow(() -> new HamsterNotFoundException("Hamster Not Found"));
    }

    @Override
    @Cacheable(value="Hamster")
    public List<Hamster> getAllHamsters() {
        return hamsterRepo.findAll();
    }
}
