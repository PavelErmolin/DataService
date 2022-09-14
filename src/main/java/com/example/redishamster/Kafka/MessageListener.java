package com.example.redishamster.Kafka;


import Model.JsonHamsterItem;
import com.example.redishamster.Repository.HamsterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.kafka.annotation.KafkaListener;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class MessageListener {

    @Autowired
    private HamsterService hamsterService;

//    @KafkaListener(topics = "SaveHamster", containerFactory = "kafkaListenerContainerFactory")
//    public void listener(Hamster ham) {
//        System.out.println("Recieved message: " + ham);
//        hamsterService.saveHamster(ham);
//    }

    @Autowired
    private MongoTemplate mt;
    @Autowired
    private MessageProducer mp;

    @KafkaListener(topics = "SaveHamster", containerFactory = "kafkaListenerContainerFactory")
    public void SaveHamster(String hamster){
        mt.insert(new JsonHamsterItem(Integer.parseInt(findId(hamster)), hamster));

    }
    @KafkaListener(topics = "GetHamster", containerFactory = "kafkaListenerContainerFactory")
    public void GetHamster(String id){
        int jsonId = Integer.parseInt(id);
        JsonHamsterItem jhi = mt.findById(jsonId, JsonHamsterItem.class);

        System.out.println(jhi);
        assert jhi != null;
        System.out.println(jhi.getId());
        mp.sendMessage("SendHamster", jhi.getItemJson());
    }
    @KafkaListener(topics = "SaveHamsters", containerFactory = "kafkaListenerContainerFactory")
    public void SaveHamsters(String hamsters){
        String[] splitted = hamsters.split("\\W\\s+\\\"id\\\"");
        Pattern p = Pattern.compile("\\W\\s+\\\"id\\\"");
        Matcher m = p.matcher(hamsters);
        for (int i = 1; i<splitted.length; i++){
            splitted[i] = (m.group() + splitted[i]);
            mt.insert(new JsonHamsterItem(Integer.parseInt(findId(splitted[i])), splitted[i]));
        }
    }
    @KafkaListener(topics = "DeleteHamster", containerFactory = "kafkaListenerContainerFactory")
    public void DeleteHamster(String id){
        mt.findAllAndRemove(Query.query(Criteria.where("_id").is(Integer.parseInt(id))), JsonHamsterItem.class);
    }

    @KafkaListener(topics = "UpdateHamster", containerFactory = "kafkaListenerContainerFactory")
    public void UpdateHamster(String id, String hamster){
        mt.findAndReplace(Query.query(Criteria.where("_id").is(Integer.parseInt(id))), hamster);
    }
    public String findId(String hamster){
        Pattern p = Pattern.compile("\\d+");
        Matcher m = p.matcher(hamster);
        if (m.find())
        {
            return m.group();
        }
        else {
            System.out.println("Json doesn't have an id");
            return null;
        }
    }
}
