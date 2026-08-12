package MessageBrokerLLD;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

/*
Problem Statement
Design and implement a Publish-Subscribe (Pub/Sub) system that allows 
- publishers to send messages to topics, 
- and subscribers to receive messages from topics they are interested in. 
The system should support multiple topics, multiple subscribers per topic, and asynchronous message delivery.
 */

class Message{
    private String id;
    private String value;

    public Message(String value){
        id=UUID.randomUUID().toString();
        this.value=value;
    }

    public String getId() {
        return id;
    }
    public String getValue() {
        return value;
    };
}

class Topic{
    private String id;
    private String topicName;

    Set<Subscriber> subscribers=ConcurrentHashMap.newKeySet();

    public Topic(String topicName){
        id=UUID.randomUUID().toString();
        this.topicName=topicName;
    }

    void registerSubscriber(Subscriber subscriber){
        subscribers.add(subscriber);
    }

    void deRegisterSubscriber(Subscriber subscriber){
        subscribers.remove(subscriber);
    }
}

class Producer{
    private String id;
    MessageBroker messageBroker;

    public Producer(String id, MessageBroker messageBroker){
        this.id=id;
        this.messageBroker=messageBroker;
    }

    public void publishMessage(String topicName, Message message) throws TopicNotFoundException{
        this.messageBroker.publishMessage(topicName,message);
    }
}

class Subscriber{
    private String id;

    public Subscriber(String id){
        this.id=id;
    }
    
    public void consumeMessage(Message message){
        System.out.println(id+ " "+message.getValue());
    }
}

class TopicNotFoundException extends Exception{
    private String message;

    public TopicNotFoundException(){

    }

    public TopicNotFoundException(String message) {
        this.message=message;
    }
}

class MessageBroker{

    Map<String,Topic> topicRegistry=new ConcurrentHashMap<>();

    public void createTopic(String topicName) throws TopicNotFoundException{
        Topic topic=topicRegistry.putIfAbsent(topicName, new Topic(topicName));
        if(topic!=null){
            throw new TopicNotFoundException("Topic with this name already exits");
        }
    }

    public void subscribe(String topicName, Subscriber subscriber) throws TopicNotFoundException{
        Topic topic=topicRegistry.get(topicName);

        if(topic!=null){
            topic.registerSubscriber(subscriber);
        }
        else{
            throw new TopicNotFoundException();
        }
    }

    public void unSubscribe(String topicName, Subscriber subscriber) throws TopicNotFoundException {
        Topic topic = topicRegistry.get(topicName);

        if (topic != null) {
            topic.deRegisterSubscriber(subscriber);
        } else {
            throw new TopicNotFoundException();
        }
    }

    public void publishMessage(String topicName, Message message) throws TopicNotFoundException{
        Topic topic = topicRegistry.get(topicName);

        if (topic != null) {
            Set<Subscriber> subscribers=topic.subscribers;

            for(Subscriber subscriber:subscribers){
                subscriber.consumeMessage(message);
            }

        } else {
            throw new TopicNotFoundException();
        }
    }
}


public class Main {
    public static void main(String[] args) throws Exception {
        MessageBroker messageBroker=new MessageBroker();
        Subscriber subscriber1=new Subscriber("1");
        Subscriber subscriber2 = new Subscriber("2");

        Message message1=new Message("Message 1");
        Message message2 = new Message("Message 2");
        Message message3 = new Message("Message 3");

        final String firstTopicName="FirstTopic";

        messageBroker.createTopic(firstTopicName);

        messageBroker.subscribe(firstTopicName, subscriber1);
        messageBroker.subscribe(firstTopicName, subscriber2);

        Producer producer=new Producer("1", messageBroker);

        producer.publishMessage(firstTopicName, message1);
        
        messageBroker.unSubscribe(firstTopicName, subscriber2);

        producer.publishMessage(firstTopicName, message2);



    }
}
// javac MessageBrokerLLD/Main.java && java MessageBrokerLLD/Main