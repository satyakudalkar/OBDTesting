package com.example.obdtesting;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class MqttConnection {
    MqttAndroidClient client;
    String topic="obd",broker;
    public MqttConnection(String broker_url)
    {
        this.broker="tcp://"+broker_url+":1883";
        Log.i("Broker","Broker Url is: "+broker);
    }
    public void connect(final Context context, final TripActivity object) {

        String clientId = MqttClient.generateClientId();

        MqttConnectOptions options = new MqttConnectOptions();

        options.setMqttVersion(MqttConnectOptions.MQTT_VERSION_DEFAULT);
        options.setCleanSession(true);
        client = new MqttAndroidClient(context, broker, clientId);
        try {
            IMqttToken token = client.connect(options);
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    // We are connected
                    Log.i("Mqtt","Connection Successful");
                    try {
                        client.subscribe(topic, 2, null, new IMqttActionListener() {
                            @Override
                            public void onSuccess(IMqttToken asyncActionToken) {
                                Log.i("Mqtt","Subscription Successful");
                            }
                            @Override
                            public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                                Log.i("Mqtt","Subscription Failure");
                            }
                        });
                    }catch (Exception e){
                        Log.i("Exception","Exception Occured: "+e.getMessage());
                    }
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    // Something went wrong e.g. connection timeout or firewall problems
                    Log.i("Mqtt","Connection Failure");
                }
            });
        } catch (
                MqttException e) {
            e.printStackTrace();
        }

        client.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {

               Toast.makeText(context,"Car not Connected",Toast.LENGTH_LONG).show();
            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                Log.i("Mqtt","Message is "+message.toString());
                object.setView(message.toString());
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {
                Log.i("Mqtt","Delivery Complete");
            }
        });


    }
}
