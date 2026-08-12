package MessageBrokerLLD;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/*
Problem Statement
Design and implement a Publish-Subscribe (Pub/Sub) system that allows 
- publishers to send messages to topics, 
- and subscribers to receive messages from topics they are interested in. 
The system should support multiple topics, multiple subscribers per topic, and asynchronous message delivery.
 */

class Topic{
    private String topicName;
    private List<Subscriber> subscribers=new ArrayList<>();
    private List<Message> messages=new ArrayList<>();

    public Topic(String topicName){
        this.topicName=topicName;
    }

    public String getTopicName() {
        return topicName;
    };

    void addSubscriber(Subscriber subscriber){
        if(!subscribers.contains(subscriber)){
            subscribers.add(subscriber);
        }
    }

    void addMessage(Message message){
        this.messages.add(message);

        for (Subscriber subscriber : this.subscribers) {
            subscriber.receiveMessage(message);
        }
    }
}

class Message{
    private String value;

    public Message(String value){
        this.value=value;
    }

    public String getValue() {
        return value;
    }
}

class Producer{
    String id;
    MessageBroker messageBroker;

    public Producer(String id,MessageBroker messageBroker){
        this.id=id;
        this.messageBroker=messageBroker;
    }

    public void sendMessage(String topicName,Message message) throws Exception{
        messageBroker.publishMessage(topicName,message);
    }

}


class Subscriber{
    String id;

    public Subscriber(String id){
        this.id=id;
    }

    public void receiveMessage(Message message) {
        System.out.println(message.getValue());
    }

}


class MessageBroker{
    Map<String,Topic> topicRegistry=new HashMap<>();

    public void createTopic(String topicName) throws Exception{
        if(!topicRegistry.containsKey(topicName)){
            topicRegistry.put(topicName, new Topic(topicName));
        }
        else{
            throw new Exception("Topic already present with same name");
        }
    }

    public void subscribeToTopic(String topicName, Subscriber subscriber) throws Exception{
        Topic topic=topicRegistry.get(topicName);
        if(topic!=null){
            topic.addSubscriber(subscriber);
        }
        else{
            throw new Exception("Topic not found with the name");
        }
    }

    public void publishMessage(String topicName,Message message) throws Exception{
        Topic topic=topicRegistry.get(topicName);

        if(topic==null){
            throw new Exception("Topic not found with the topic name");
        }

        topic.addMessage(message);
    }

}

public class Main {
    public static void main(String[] args) throws Exception {
        MessageBroker messageBroker=new MessageBroker();

        Subscriber subscriber1=new Subscriber("1");
        Subscriber subscriber2= new Subscriber("2");

        messageBroker.createTopic("FirstTopic");

        Producer producer=new Producer("1", messageBroker);

        producer.sendMessage("FirstTopic", new Message("FirstMessage"));

        messageBroker.subscribeToTopic("FirstTopic", subscriber1);

        producer.sendMessage("FirstTopic", new Message("SecondMessage"));        

        messageBroker.subscribeToTopic("FirstTopic", subscriber2);

        producer.sendMessage("FirstTopic", new Message("ThirdMessage"));

    }
}
// javac MessageBrokerLLD/Main.java && java MessageBrokerLLD/Main