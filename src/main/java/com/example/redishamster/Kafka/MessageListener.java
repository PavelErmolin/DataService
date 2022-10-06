package com.example.redishamster.Kafka;

import com.example.orchestrator.model.JsonHamsterItem;
import com.example.orchestrator.model.JsonHamsterOrder;
import com.example.orchestrator.model.JsonHamsterUser;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.kafka.annotation.KafkaListener;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@CacheConfig(cacheNames = "hc")
public class MessageListener {
    @Autowired
    private MongoTemplate mt;
    @Autowired
    private MessageProducer mp;

    @KafkaListener(topics = "saveProductDB", containerFactory = "kafkaListenerContainerFactory")
    public void SaveProduct(String product) {
        if (!mt.exists(Query.query(Criteria.where("_id").is(Integer.parseInt(findId(product)))), product)) {
            mt.insert(new JsonHamsterItem(Integer.parseInt(findId(product)), product));
            log.info("Product {} save", product);
        } else log.warn("Duplicated Id! Check if {} is correct", Integer.parseInt(findId(product)));
    }

    @KafkaListener(topics = "getProductFromDB", containerFactory = "kafkaListenerContainerFactory")
    @Cacheable(value = "JsonHamsterItem", key = "#id")
    public void GetProduct(String id) {
        int jsonId = Integer.parseInt(id);
        JsonHamsterItem jhi = mt.findById(jsonId, JsonHamsterItem.class);
        assert jhi != null;
        mp.sendMessage("sendProductFromDB", jhi.getItemJson());
        log.info("Product with id {} find", id);
    }

    @KafkaListener(topics = "getAllProductsDB", containerFactory = "kafkaListenerContainerFactory")
    @Cacheable(value="JsonHamsterItem")
    public void GetAllHamsters(){
        List<JsonHamsterItem> list= mt.findAll(JsonHamsterItem.class);
        StringBuilder message = new StringBuilder();
        for (JsonHamsterItem jsonHamsterItem : list) {
            message.append(jsonHamsterItem.getItemJson());
        }
        mp.sendMessage("sendALlProductsDB", message.toString());
        System.out.println(message);
    }

    @KafkaListener(topics = "save", containerFactory = "kafkaListenerContainerFactory")
    public void SaveHamsters(String hamsters) {
        //System.out.println(hamsters);
        Pattern p = Pattern.compile("\\W\\s+\\\"id\\\"");
        String[] splitted = p.split(hamsters);
        Matcher m = p.matcher(hamsters);
        m.find();

        for (int i = 1; i < splitted.length; i++) {
            splitted[i] = m.group() + splitted[i];
            if (!mt.exists(Query.query(Criteria.where("_id").is(Integer.parseInt(findId(splitted[i])))), splitted[i])) {
                mt.insert(new JsonHamsterItem(Integer.parseInt(findId(splitted[i])), splitted[i]));
            } else log.warn("Duplicated Id! Check if {} is correct", Integer.parseInt(findId(splitted[i])));
        }
        log.info("Products save");
    }

    @KafkaListener(topics = "deleteProductDB", containerFactory = "kafkaListenerContainerFactory")
    @CacheEvict(value = "JsonHamsterItem", key = "#id")
    public void DeleteHamster(String id) {
        mt.findAllAndRemove(Query.query(Criteria.where("_id").is(Integer.parseInt(id))), JsonHamsterItem.class);
        log.info("Product with id {} delete", id);
    }

    @KafkaListener(topics = "frontUpdateProduct", containerFactory = "kafkaListenerContainerFactory")
    @CachePut(value = "Hamster", key = "#id")
    public void UpdateHamster(String id, String product) {
        mt.findAndReplace(Query.query(Criteria.where("_id").is(Integer.parseInt(id))), product);
        log.info("Product with id {} update", id);
    }

    @KafkaListener(topics = "saveOrderDB", containerFactory = "kafkaListenerContainerFactory")
    public void SaveOrder(String order) {
        if (!mt.exists(Query.query(Criteria.where("_id").is(Integer.parseInt(findId(order)))), order)) {
            mt.insert(new JsonHamsterOrder(Integer.parseInt(findId(order)), order));
            log.info("Order {} save", order);

        } else log.warn("Duplicated Id! Check if {} is correct", Integer.parseInt(findId(order)));
    }

    @KafkaListener(topics = "SaveOrders", containerFactory = "kafkaListenerContainerFactory")
    public void SaveOrders(String orders) {

        Pattern p = Pattern.compile("\\W\\s+\\\"id\\\"");
        String[] splitted = p.split(orders);
        Matcher m = p.matcher(orders);
        m.find();
        for (int i = 1; i < splitted.length; i++) {
            splitted[i] = m.group() + splitted[i];
            if (!mt.exists(Query.query(Criteria.where("_id").is(Integer.parseInt(findId(splitted[i])))), splitted[i])) {
                mt.insert(new JsonHamsterOrder(Integer.parseInt(findId(splitted[i])), splitted[i]));
                log.info("Orders save");
            } else log.warn("Duplicated Id! Check if {} is correct", Integer.parseInt(findId(splitted[i])));
        }
    }

    @KafkaListener(topics = "getOrderFromDB", containerFactory = "kafkaListenerContainerFactory")
    @Cacheable(value = "JsonHamsterOrder", key = "#id")
    public void GetOrder(String id) {
        int jsonId = Integer.parseInt(id);
        JsonHamsterOrder jho = mt.findById(jsonId, JsonHamsterOrder.class);
        assert jho != null;
        mp.sendMessage("SendOrder", jho.getOrderItems());
        log.info("Order with id {} find", id);
    }

    @KafkaListener(topics = "deleteProductDB", containerFactory = "kafkaListenerContainerFactory")
    @CacheEvict(value = "JsonHamsterOrder", key = "#id")
    public void DeleteOrder(String id) {
        mt.findAndRemove(Query.query(Criteria.where("_id").is(Integer.parseInt(id))), JsonHamsterOrder.class);
        log.info("Order with id {} delete", id);
    }

    @KafkaListener(topics = "updateOrderDB", containerFactory = "kafkaListenerContainerFactory")
    @CachePut(value = "JsonHamsterOrder", key = "#id")
    public void UpdateOrder(String id, String order) {
        mt.findAndReplace(Query.query(Criteria.where("_id").is(Integer.parseInt(id))), order);
        log.info("Order with id {} update", id);
    }

    @KafkaListener(topics = "saveUserDB", containerFactory = "kafkaListenerContainerFactory")
    public void SaveUser(String user) {
        if (!mt.exists(Query.query(Criteria.where("_id").is(Integer.parseInt(findId(user)))), user)) {
            mt.insert(new JsonHamsterUser(Integer.parseInt(findId(user)), user));
            log.info("User {} save", user);
        } else log.warn("Duplicated Id! Check if {} is correct", Integer.parseInt(findId(user)));
    }

    @KafkaListener(topics = "SaveUsers", containerFactory = "kafkaListenerContainerFactory")
    public void SaveUsers(String users) {

        Pattern p = Pattern.compile("\\W\\s+\\\"id\\\"");
        String[] splitted = p.split(users);
        Matcher m = p.matcher(users);
        m.find();
        for (int i = 1; i < splitted.length; i++) {
            splitted[i] = m.group() + splitted[i];
            if (!mt.exists(Query.query(Criteria.where("_id").is(Integer.parseInt(findId(splitted[i])))), splitted[i])) {
                mt.insert(new JsonHamsterUser(Integer.parseInt(findId(splitted[i])), splitted[i]));
                log.info("Users save");
            } else log.warn("Duplicated Id! Check if {} is correct", Integer.parseInt(findId(splitted[i])));
        }

    }

    @KafkaListener(topics = "getUserFromDB", containerFactory = "kafkaListenerContainerFactory")
    @Cacheable(value = "JsonHamsterUser", key = "#id")
    public void GetUser(String id) {
        int jsonId = Integer.parseInt(id);
        JsonHamsterUser jhu = mt.findById(jsonId, JsonHamsterUser.class);
        assert jhu != null;
        mp.sendMessage("SendUser", jhu.getUserDetails());
        log.info("User with id {} find", id);
    }

    @KafkaListener(topics = "deleteUserDB", containerFactory = "kafkaListenerContainerFactory")
    @CacheEvict(value = "JsonHamsterUser", key = "#id")
    public void DeleteUser(String id) {
        mt.findAndRemove(Query.query(Criteria.where("_id").is(Integer.parseInt(id))), JsonHamsterUser.class);
        log.info("User with id {} delete", id);
    }

    @KafkaListener(topics = "updateUserDB", containerFactory = "kafkaListenerContainerFactory")
    @CachePut(value = "JsonHamsterUser", key = "#id")
    public void UpdateUser(String id, String user) {
        mt.findAndReplace(Query.query(Criteria.where("_id").is(Integer.parseInt(id))), user);
        log.info("User with id {} update", id);
    }


    public String findId(String hamster) {
        Pattern p = Pattern.compile("\\d+");
        Matcher m = p.matcher(hamster);
        if (m.find()) {
            return m.group();
        } else {
            log.warn("Json doesn't have an id");
            return null;
        }
    }

    public String findUsername(String user){
        Pattern p = Pattern.compile("(?<=username\\\"\\:\\s\\\").*(?=\\\",)");
        Matcher m = p.matcher(user);
        if (m.find())
        {
            return m.group();
        }
        else {
            System.out.println("Json doesn't contain an username");
            return null;
        }
    }
    public String findEmail(String user){
        Pattern p = Pattern.compile("(?<=\\\"email\\\"\\:\\s\\\").*(?=\\\",)");
        Matcher m = p.matcher(user);
        if (m.find())
        {
            return m.group();
        }
        else {
            System.out.println("Json doesn't contain an username");
            return null;
        }
    }
    public String findPassword(String user){
        Pattern p = Pattern.compile("(?<=\\\"password\\\"\\:\\s\\\").*(?=\\\")");
        Matcher m = p.matcher(user);
        if (m.find())
        {
            return m.group();
        }
        else {
            System.out.println("Json doesn't contain an username");
            return null;
        }
    }
}
