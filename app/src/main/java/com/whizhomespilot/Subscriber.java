package com.whizhomespilot;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;

/**
 * Created by smarhas on 6/2/2018.
 */

public class Subscriber {
    public void subscribe(String topic){
        System.out.println("== START SUBSCRIBER ==");
        MqttClient client= null;
        try {
            client = new MqttClient(StaticValues.MQTT_BROKER_URL, MqttClient.generateClientId());
        } catch (MqttException e) {
            e.printStackTrace();
        }
        client.setCallback( new SimpleMqttCallBack() );
        try {
            client.connect();
            client.subscribe(topic);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
}