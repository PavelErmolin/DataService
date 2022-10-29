package com.example.redishamster.Kafka;

import com.bezkoder.spring.security.mongodb.models.User;
import com.example.orchestrator.model.JsonHamsterComment;
import com.example.orchestrator.model.JsonHamsterItem;
import com.example.orchestrator.model.JsonHamsterOrder;


import com.example.orchestrator.model.JsonReview;
import lombok.extern.slf4j.Slf4j;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.decimal4j.util.DoubleRounder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.util.BsonUtils;
import org.springframework.kafka.annotation.KafkaListener;


import java.util.ArrayList;
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
    @Autowired
    private UserProducer userKafkaTemplate;
    @Autowired
    private ReviewProducer kafkaReviewTemplate;

    @KafkaListener(topics = "SaveHamster", containerFactory = "kafkaListenerContainerFactory")
    public void SaveHamster(String hamster){
        if (!mt.exists(Query.query(Criteria.where("_id").is(Integer.parseInt(findId(hamster)))), hamster)) {
            mt.insert(new JsonHamsterItem(Integer.parseInt(findId(hamster)), hamster));
            log.info("Product {} save", hamster);
        }
        else log.warn("Duplicated Id! Check if {} is correct", Integer.parseInt(findId(hamster)));
    }

    @KafkaListener(topics = "getProductFromDB", containerFactory = "kafkaListenerContainerFactory")
//    @Cacheable(value = "JsonHamsterItem", key = "#id")
    public void GetProduct(String id) {
        log.info("Get message");
        int jsonId = Integer.parseInt(id);
        JsonHamsterItem jhi = mt.findById(jsonId, JsonHamsterItem.class);
        assert jhi != null;
        mp.sendMessage("sendProductFromDB", jhi.getItemJson());
        log.info("Product with id {} find", id);
    }
    @KafkaListener(topics = "getAllProductsDB", containerFactory = "kafkaListenerContainerFactory")
//    @Cacheable(value="JsonHamsterItem")
    public void GetAllHamsters(){
        List<JsonHamsterItem> list= mt.findAll(JsonHamsterItem.class);
        StringBuilder message = new StringBuilder();
        for (JsonHamsterItem jsonHamsterItem : list) {
            message.append(jsonHamsterItem.getItemJson());
        }
        log.info("Products receive for Front");
        mp.sendMessage("sendALlProducts", "["+message.toString().trim()+"]");

    }

    @KafkaListener(topics = "SaveHamsters", containerFactory = "kafkaListenerContainerFactory")
    public void SaveProducts(String hamsters){
        System.out.println(hamsters);
        Pattern p = Pattern.compile("\\{'id': \\w+");
        String[] splitted = hamsters.split("\\{'id': \\w+");
        for (String str : splitted) {
            System.out.println(str);
        }
        System.out.println(splitted.length);
        List<String> allMatches = new ArrayList<>();

        Matcher m = p.matcher(hamsters);
        while (m.find()) {
            allMatches.add(m.group());
        }
        System.out.println(allMatches);

        for (int i = 1; i < splitted.length; i++) {
            String product = allMatches.get(i - 1) + splitted[i];
            if (i + 1 < splitted.length) {
                splitted[i] = product.substring(0, product.length() - 2);
            } else {
                splitted[i] = product.substring(0, product.length() - 1);
            }

            if (!mt.exists(Query.query(Criteria.where("_id").is(Integer.parseInt(findId(splitted[i])))), splitted[i])) {
                mt.insert(new JsonHamsterItem(Integer.parseInt(findId(splitted[i])), splitted[i]));
                log.info("Product saved");
            } else log.warn("Duplicated Id! Check if {} is correct", Integer.parseInt(findId(splitted[i])));
        }
    }

    @KafkaListener(topics = "save", containerFactory = "kafkaListenerContainerFactory")
    public void SaveHamsters(String hamsters) {
        System.out.println(hamsters);
        Pattern p = Pattern.compile("\\W\\s+\\\"id\\\"");
        String[] splitted = p.split(hamsters);
        Matcher m = p.matcher(hamsters);
        m.find();
        for (int i = 1; i < splitted.length; i++) {
            splitted[i] = m.group() + splitted[i];
            if(splitted[i]==splitted[splitted.length-1]) {
                splitted[i] =  splitted[i];
                StringBuilder builder = new StringBuilder(splitted[i]);
                builder.deleteCharAt(splitted[i].lastIndexOf("]"));
                splitted[i] = builder.toString();
            }
            if (!mt.exists(Query.query(Criteria.where("_id").is(Integer.parseInt(findId(splitted[i])))), splitted[i])) {
                mt.insert(new JsonHamsterItem(Integer.parseInt(findId(splitted[i])), splitted[i]));
            } else log.warn("Duplicated Id! Check if {} is correct", Integer.parseInt(findId(splitted[i])));
        }
        log.info("Products saved");
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
        String ord = order.substring(0, order.length() - 1).substring(0, order.length() - 2).replace("\\","").substring(10);
        long orderId = System.currentTimeMillis();

       if (mt.exists(Query.query(Criteria.where("id").is(orderId)), ord)) {
            orderId += System.currentTimeMillis();
        }

        mt.insert(new JsonHamsterOrder((int) orderId, ord));
        log.info("Order {} save", order);
    }

    @KafkaListener(topics = "saveRateDB", containerFactory = "kafkaListenerContainerFactory")
    public void saveRate(String str) {
        String rate = str.substring(9, 10);
        String idProduct = str.substring(24, str.length()-1);
        JsonHamsterItem jhi = mt.findById(Integer.parseInt(idProduct), JsonHamsterItem.class);
        String firstJsonPart = jhi.getItemJson().substring(0,jhi.getItemJson().indexOf("rating") +9);
        String secondJsonPart = jhi.getItemJson().substring(jhi.getItemJson().indexOf("rating") +13);
        if (mt.exists(Query.query(Criteria.where("id").is(idProduct)), rate)) {
            idProduct += System.currentTimeMillis();
        }

        List<JsonHamsterComment> listComments= mt.findAll(JsonHamsterComment.class);
        double sum = 0;
        int count = 1;
        for (var comment : listComments) {
            if(comment.getProductId() == Integer.parseInt(idProduct)) {
            sum += comment.getRate();
            count++;
            }
        }
        sum = sum + Integer.parseInt(rate);
        double sumRating = sum/count;
        double result = Math.rint(100.0 * sumRating) / 100.0;

        String jsonUpdating = firstJsonPart + result + secondJsonPart;
        jhi.setItemJson(jsonUpdating);
        mt.findAndReplace(Query.query(Criteria.where("_id").is(Integer.parseInt(idProduct))), jhi);
        JsonHamsterComment comment = new JsonHamsterComment(Integer.parseInt(idProduct), Integer.parseInt(rate));
        mt.insert(comment);

        log.info("Rate and ID {} save", str);

    }

    @KafkaListener(topics = "SaveOrders", containerFactory = "kafkaListenerContainerFactory")
    public void SaveOrders(String orders) {
        log.info("Starting saving orders");
        String[] splitted = orders.split("\\{'id': \\w+, 'products'");
        Pattern p = Pattern.compile("\\{'id': \\w+, 'products'");
        List<String> allMatches = new ArrayList<String>();
        Matcher m = p.matcher(orders);
        while (m.find()) {
            allMatches.add(m.group());
        }

        for (int i = 1; i < splitted.length; i++) {
            String order = allMatches.get(i - 1) + splitted[i];
            if (i + 1 < splitted.length) {
                splitted[i] = order.substring(0, order.length() - 2);
            } else {
                splitted[i] = order.substring(0, order.length() - 1);
            }

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

    @KafkaListener(topics = "DeleteOrder", containerFactory = "kafkaListenerContainerFactory")
    @CacheEvict(value="JsonHamsterOrder", key="#id")
    public void DeleteOrder(String id){
        mt.findAndRemove(Query.query(Criteria.where("_id").is(Integer.parseInt(id))), JsonHamsterOrder.class);
        log.info("Order with id {} delete", id);
    }

    @KafkaListener(topics = "updateOrderDB", containerFactory = "kafkaListenerContainerFactory")
    @CachePut(value = "JsonHamsterOrder", key = "#id")
    public void UpdateOrder(String id, String order) {
        mt.findAndReplace(Query.query(Criteria.where("_id").is(Integer.parseInt(id))), order);
        log.info("Order with id {} update", id);
    }
    @KafkaListener(topics = "SaveUser", containerFactory = "userKafkaListenerContainerFactory")
    public void SaveUser(User user){
        long userId = System.currentTimeMillis();
        if (mt.exists(Query.query(Criteria.where("_id").is(userId)), User.class)) {
            userId += System.currentTimeMillis();
        }
        user.setId(String.valueOf(userId));
        mt.insert(user);
//        System.out.println(mt.find(Query.query(Criteria.where("_id").is(userId)), User.class));
        log.info("User {} save", user);
    }
//    @KafkaListener(topics = "SaveUsers", containerFactory = "userKafkaListenerContainerFactory")
//    public void SaveUsers(String users){
//
//        Pattern p = Pattern.compile("\\W\\s+\\\"id\\\"");
//        String[] splitted = p.split(users);
//        Matcher m = p.matcher(users);
//        m.find();
//        for (int i = 1; i<splitted.length; i++){
//            splitted[i] = m.group() + splitted[i];
//            if (!mt.exists(Query.query(Criteria.where("_id").is(Integer.parseInt(findId(splitted[i])))), splitted[i])) {
//                mt.insert(new JsonHamsterUser(Integer.parseInt(findId(splitted[i])), splitted[i]));
//            }
//            else System.out.println("Duplicated Id! Check if "+ Integer.parseInt(findId(splitted[i])) +" is correct");
//        }
//    }
    @KafkaListener(topics = "GetUser", containerFactory = "kafkaListenerContainerFactory")
//    @Cacheable(value="User", key="#id")
    public void GetUser(String username){
        User jhu = mt.findOne(Query.query(Criteria.where("username").is(username)), User.class);
        assert jhu != null;
        userKafkaTemplate.sendMessage("SendUser", jhu);
    }
//    @KafkaListener(topics = "GetAllUsers", containerFactory = "kafkaListenerContainerFactory")
//    @Cacheable(value="JsonHamsterUser")
//    public void GetAllUsers(){
//        List<JsonHamsterUser> list= mt.findAll(JsonHamsterUser.class);
//        StringBuilder message = new StringBuilder();
//        for (JsonHamsterUser jsonHamsterUser : list) {
//            message.append(jsonHamsterUser);
//        }
//        mp.sendMessage("SendHamster", message.toString());
//    }

//    @KafkaListener(topics = "deleteUserDB", containerFactory = "kafkaListenerContainerFactory")
//    @CacheEvict(value = "JsonHamsterUser", key = "#id")
//    public void DeleteUser(String id) {
//        mt.findAndRemove(Query.query(Criteria.where("_id").is(Integer.parseInt(id))), JsonHamsterUser.class);
//        log.info("User with id {} delete", id);
//    }

    @KafkaListener(topics = "updateUserDB", containerFactory = "userKafkaListenerContainerFactory")
//    @CachePut(value = "JsonHamsterUser", key = "#id")
    public void UpdateUser(User user) {
        String id = user.getId();
        mt.findAndReplace(Query.query(Criteria.where("_id").is(id)), user);
        log.info("User with id {} update", id);
    }
//    @KafkaListener(topics = "GetAllOrders", containerFactory = "kafkaListenerContainerFactory")
////    @Cacheable(value="JsonHamsterOrder")
//    public void GetAllOrders(){
//        List<JsonHamsterOrder> list= mt.findAll(JsonHamsterOrder.class);
//        StringBuilder message = new StringBuilder();
//        message.append("[");
//        for (JsonHamsterOrder jsonHamsterOrder : list) {
//            message.append("{ id:"+jsonHamsterOrder.getId()+","+jsonHamsterOrder.getOrderItems().substring(1,jsonHamsterOrder.getOrderItems().length()));
//        }
//        message.append("]");
//        mp.sendMessage("SendHamster", message.toString());
//        System.out.println(message);
//    }

    @KafkaListener(topics = "requestOrdersDataFromDB", containerFactory = "kafkaListenerContainerFactory")
//    @Cacheable(value = "JsonHamsterOrder")
    public void getOrders(ConsumerRecord<String, String> record) {
        log.info(record.value());
        List<JsonHamsterOrder> ordersList = mt.findAll(JsonHamsterOrder.class);
        StringBuilder message = new StringBuilder();
        message.append("[");
        for (JsonHamsterOrder jsonHamsterOrder : ordersList) {
            message.append("{'id':"+jsonHamsterOrder.getId()+","+jsonHamsterOrder.getOrderItems().substring(1,jsonHamsterOrder.getOrderItems().length()));
            message.append(",");
        }
        String orders = message.substring(0,message.length()-1)+"]";
        assert ordersList != null;
        mp.sendMessage("sendOrdersDataFromDB", orders);
        log.info("Send order list: " + orders);
    }

    @KafkaListener(topics = "requestProductsDataFromDB", containerFactory = "kafkaListenerContainerFactory")
//    @Cacheable(value = "JsonHamsterItem")
    public void getAllProducts(ConsumerRecord<String, String> record) {
        log.info(record.value());
        List<JsonHamsterItem> productsList = mt.findAll(JsonHamsterItem.class);
        assert productsList != null;
        mp.sendMessage("sendProductsDataFromDB", String.valueOf(productsList));
        log.info("Send product list: " + productsList);
    }

    @KafkaListener(topics = "requestProductsAndOrdersDataFromDB", containerFactory = "kafkaListenerContainerFactory")
//    @Cacheable(value = "JsonHamsterItem", key = "#id")
    public void getAllProductsAndOrders(ConsumerRecord<String, String> record) {
        log.info(record.value());
        List<JsonHamsterItem> productsList = mt.findAll(JsonHamsterItem.class);
        assert productsList != null;
        mp.sendMessage("sendProductsDataFromDBForBasket", productsList.toString());

        List<JsonHamsterOrder> ordersList = mt.findAll(JsonHamsterOrder.class);
        assert ordersList != null;
        mp.sendMessage("sendOrdersDataFromDBForBasket", ordersList.toString());

        log.info("Send product list: " + productsList);
        log.info("Send order list: " + ordersList);
    }


    public String findId(String hamster) {
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


    @KafkaListener(topics = "SaveReview", containerFactory = "reviewKafkaListenerContainerFactory")
    public void SaveUser(JsonReview review){
        long reviewId = System.currentTimeMillis();
        if (mt.exists(Query.query(Criteria.where("_id").is(reviewId)), User.class)) {
            reviewId += System.currentTimeMillis();
        }
        review.setId(String.valueOf(reviewId));
        mt.insert(review);
        log.info("Review {} save", review);
    }

    @KafkaListener(topics = "GetReview", containerFactory = "kafkaListenerContainerFactory")
    public void GetReview(String id){
        JsonReview review = mt.findOne(Query.query(Criteria.where("id").is(id)), JsonReview.class);
        assert review != null;
        kafkaReviewTemplate.sendMessage("SendReview", review);
    }
}

