package com.whizhomespilot;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
/**
 * Created by smarhas on 12/25/2017.
 */

public class TestMQTT {
    MemoryPersistence persistence = new MemoryPersistence();
    MqttClient client;
    public void doDemo(String topic,String status,String deviceId, String securityToken) {
        try {
            // System.setProperty("http.proxyHost", "www-proxy.idc.oracle.com");
            //  System.setProperty("http.proxyPort", "80");

            client = new MqttClient("tcp://45.113.138.18:1883", topic,persistence);
            client.connect();
            MqttMessage message = new MqttMessage();
            message.setPayload((securityToken+"-"+deviceId+status).getBytes());
            client.publish(topic, message);
            client.disconnect();
            System.out.println("After");
        } catch (MqttException e) {
            e.printStackTrace();
            e.getMessage();
        }
    }
}