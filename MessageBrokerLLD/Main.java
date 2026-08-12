package MessageBrokerLLD;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
        subscribers.add(subscriber);
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

interface Publisher{
    void sendMessage(Message message);
}

interface Subscriber {
    void receiveMessage(Message message);
}


class MessageBroker{
    List<Topic> topics=new ArrayList<>();

    void addTopic(String topicName){
        Optional<Topic> optionalTopic=topics.stream().filter((topic)->topic.getTopicName().equals(topicName)).findFirst();

        if(optionalTopic.isEmpty()){
            topics.add(new Topic(topicName));
        }
    }

}

public class Main {
    public static void main(String[] args) {
        
    }
}
