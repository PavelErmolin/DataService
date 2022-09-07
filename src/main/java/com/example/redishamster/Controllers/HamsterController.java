package com.example.redishamster.Controllers;

import com.example.redishamster.Model.Hamster;
import com.example.redishamster.Repository.HamsterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/hamsters")
public class HamsterController {


    private final HamsterService hamsterService;

    @Autowired
    public HamsterController(HamsterService hamsterService) {
        this.hamsterService = hamsterService;
    }

    @GetMapping
    public ResponseEntity<List<Hamster>> findAll() {
        List<Hamster> hamsters = hamsterService.getAllHamsters();
        return ResponseEntity.status(HttpStatus.OK)
                .body(hamsters);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Hamster> findById(@PathVariable int id) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(hamsterService.getHamster(id));
    }

    @PostMapping
    public ResponseEntity<Hamster> create(@RequestBody Hamster hamster) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(hamsterService.saveHamster(hamster));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Hamster> update(@PathVariable int id, @RequestBody Hamster hamster) {
        return ResponseEntity.status(HttpStatus.ACCEPTED)
                .body(hamsterService.saveHamster(hamster));
    }

    public ResponseEntity delete(@PathVariable int id) {
        hamsterService.deleteHamster(id);
        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }
}