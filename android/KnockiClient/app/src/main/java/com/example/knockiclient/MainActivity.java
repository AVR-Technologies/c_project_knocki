package com.example.knockiclient;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class MainActivity extends AppCompatActivity {
    TextView ipTextView;
    Button connectButton, disConnectButton;
    Button onBulbAButton, offBulbAButton;
    Button onBulbBButton, offBulbBButton;
    String ip = "192.168.0.13";
    int port = 7777;
    Client client = new Client();
    MediaPlayer mediaPlayer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ipTextView = findViewById(R.id.tv_ip);
        connectButton = findViewById(R.id.bt_connect);
        disConnectButton = findViewById(R.id.bt_disconnect);
        onBulbAButton = findViewById(R.id.bt_bulb_a_on);
        offBulbAButton = findViewById(R.id.bt_bulb_a_off);
        onBulbBButton = findViewById(R.id.bt_bulb_b_on);
        offBulbBButton = findViewById(R.id.bt_bulb_b_off);

        ipTextView.setText(ip);
        connectButton.setOnClickListener(v -> {
            client.connect(ip, port, response -> {
                if(response.equals("p") || response.equals("k")){
                    String title  = response.equals("p") ?  "Found" : response.equals("k") ?  "Knock knock" :   "";
                    String message = response.equals("p") ?  "Close to stop music." : response.equals("k") ?  "Someone on the door." :   "";
                    startAudio();
                    runOnUiThread(() -> new MaterialAlertDialogBuilder(this)
                            .setTitle(title)
                            .setMessage(message)
                            .setPositiveButton("CLOSE", (d, which) ->{})
                            .setOnDismissListener((d)-> stopAudio())
                            .show());
                }
            });
            connectButton.setVisibility(View.GONE);
            disConnectButton.setVisibility(View.VISIBLE);
            onBulbAButton.setEnabled(true);
            offBulbAButton.setEnabled(true);
            onBulbBButton.setEnabled(true);
            offBulbBButton.setEnabled(true);
        });
        disConnectButton.setOnClickListener(v ->{
            client.close();
            connectButton.setVisibility(View.VISIBLE);
            disConnectButton.setVisibility(View.GONE);
            onBulbAButton.setEnabled(false);
            offBulbAButton.setEnabled(false);
            onBulbBButton.setEnabled(false);
            offBulbBButton.setEnabled(false);
        });
        onBulbAButton.setOnClickListener(v -> client.write("A"));
        offBulbAButton.setOnClickListener(v -> client.write("a"));
        onBulbBButton.setOnClickListener(v -> client.write("B"));
        offBulbBButton.setOnClickListener(v -> client.write("b"));
    }
    void startAudio(){
        mediaPlayer = MediaPlayer.create(this, R.raw.iphone_6_original);
        mediaPlayer.start();
    }

    void stopAudio(){
        mediaPlayer.stop();
        mediaPlayer.release();
        mediaPlayer = null;
    }
}
