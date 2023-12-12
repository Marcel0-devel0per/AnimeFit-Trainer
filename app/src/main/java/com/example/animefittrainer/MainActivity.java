package com.example.animefittrainer;
import com.example.animefittrainer.MqttManager;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class MainActivity extends AppCompatActivity {
    FirebaseAuth auth;
    Button button;
    TextView textView;
    FirebaseUser user;
    GoogleSignInClient googleSignInClient;
    MqttManager mqttManager;
    private String currentCharacterTopic = "default_topic";
    String brokerUrl = "tcp://animefit.cloud.shiftr.io:1883";
    String clientId = "animefit";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mqttManager = new MqttManager(brokerUrl, clientId);
        MqttConnectOptions options = new MqttConnectOptions();
        options.setUserName("animefit");
        options.setPassword("QJYLFLmFjaiCEi7H".toCharArray());

        auth = FirebaseAuth.getInstance();
        button = findViewById(R.id.logout);
        textView = findViewById(R.id.user_details);
        user = auth.getCurrentUser();

        ImageView imageView1 = findViewById(R.id.imageView1);
        ImageView imageView2 = findViewById(R.id.imageView2);
        ImageView imageView3 = findViewById(R.id.imageView3);

        imageView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentCharacterTopic = "zoro_topic";
                connectAndSubscribe();
                Intent intent = new Intent(MainActivity.this, ZoroActivity.class);
                startActivity(intent);
            }
        });

        imageView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentCharacterTopic = "mikasa_topic";
                connectAndSubscribe();
                Intent intent = new Intent(MainActivity.this, MikasaActivity.class);
                startActivity(intent);
            }
        });

        imageView3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentCharacterTopic = "ippo_topic";
                connectAndSubscribe();
                Intent intent = new Intent(MainActivity.this, IppoActivity.class);
                startActivity(intent);
            }
        });

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, gso);

        if (user == null) {
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
            finish();
        } else {
            textView.setText("Bienvenido: " + user.getEmail());
        }

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (GoogleSignIn.getLastSignedInAccount(getApplicationContext()) != null) {
                    googleSignInClient.signOut().addOnCompleteListener(MainActivity.this,
                            new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    FirebaseAuth.getInstance().signOut();
                                    redirectToLogin();
                                }
                            });
                } else {
                    FirebaseAuth.getInstance().signOut();
                    redirectToLogin();
                }
            }
        });
    }

    private void connectAndSubscribe() {
        MqttCallback mqttCallback = new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {
                // Manejar la pérdida de conexión
            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                // Manejar los mensajes MQTT entrantes
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {
                // Manejar la entrega completa (para mensajes QoS 1)
            }
        };

        IMqttActionListener connectionListener = new IMqttActionListener() {
            @Override
            public void onSuccess(IMqttToken asyncActionToken) {
                // Manejar la conexión exitosa
                showUIToast("Conexión exitosa a MQTT");
            }

            @Override
            public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                // Manejar la falla de conexión
                showUIToast("Error de conexión a MQTT");
            }
        };

        MqttConnectOptions options = new MqttConnectOptions();
        options.setUserName("animefit");
        options.setPassword("QJYLFLmFjaiCEi7H".toCharArray());

        mqttManager.connect(options, connectionListener);
        mqttManager.subscribe(currentCharacterTopic, 1, mqttCallback);
    }

    private void showUIToast(final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void redirectToLogin() {
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(intent);
        finish();
    }

}
