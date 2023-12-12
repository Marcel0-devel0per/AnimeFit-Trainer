package com.example.animefittrainer;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;
public class MqttCallbackHandler implements MqttCallback {
    @Override
    public void connectionLost(Throwable cause) {
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        String payload = new String(message.getPayload());
        System.out.println("Message received on topic " + topic + ": " + payload);
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
    }
}