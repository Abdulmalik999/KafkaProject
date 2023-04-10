package org.example;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Properties;
  
public class Consumer {  
	String TopicName;
	String GroupID;
	KafkaConsumer<String,String> consumer;
	public Properties ConfigConsumer(String grp_id) {
        String bootstrapServers="127.0.0.1:9092";
        Properties properties=new Properties();
        properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,bootstrapServers);
        properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,   StringDeserializer.class.getName());
        properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,StringDeserializer.class.getName());
        properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG,grp_id);
        properties.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG,"earliest");
        return properties;
	}
	public Consumer(String TopicName,String GroupID) {
		this.TopicName = TopicName;
		this.GroupID = GroupID;
        //creating consumer

        consumer = new KafkaConsumer<String,String>(ConfigConsumer(GroupID));
        //Assign  
        //TopicPartition part = new TopicPartition(TopicName, Partation);
        //consumer.assign(Arrays.asList(part));
        consumer.subscribe(Arrays.asList(TopicName));
	}
	public ArrayList<TradeManager.StocksPriceReciver> Consume() {
        //polling 
		ArrayList<TradeManager.StocksPriceReciver> Values = new ArrayList<TradeManager.StocksPriceReciver>();
        ConsumerRecords<String,String> records=consumer.poll(Duration.ofMillis(100));  
        for(ConsumerRecord<String,String> record: records){
        	Values.add(new TradeManager.StocksPriceReciver(record.key(), record.value()));
        	//System.out.println("Key: "+ record.key() + ", Value:" +record.value());
        	//System.out.println("Partition:" + record.partition()+",Offset:"+record.offset());  
        }
        return Values;
	}
}
