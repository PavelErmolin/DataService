package com.example.redishamster;

import Model.Hamster;
import Model.IHamsterDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class Runner implements CommandLineRunner {

    @Autowired
    private IHamsterDao hamDao;


    @Override
    public void run(String... args) throws Exception {
        hamDao.saveHamster(new Hamster(500, "Thomas", 2.0));

        hamDao.saveAllHamsters(
                Map.of( 501, new Hamster(501, "Thomas", 23.0),
                        502, new Hamster(502, "Theodore", 24.5),
                        503, new Hamster(503, "Blackwood", 23.75)
                )
        );
    }

}
