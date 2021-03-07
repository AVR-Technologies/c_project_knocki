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
    Button toggleBulbAButton;
    Button toggleBulbBButton;

    String ip = "192.168.4.1";
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
        toggleBulbAButton = findViewById(R.id.bt_bulb_a_toggle);
        toggleBulbBButton = findViewById(R.id.bt_bulb_b_toggle);

        ipTextView.setText(ip);
        connectButton.setOnClickListener(v -> {
            client.connect(ip, port, response -> {
//                if(response.equals("p") || response.equals("k")){
//                    String title  = response.equals("p") ?  "Found" : "Knock knock";
//                    String message = response.equals("p") ?  "Close to stop music." : "Someone on the door.";
//                    startAudio();
//                    runOnUiThread(() -> new MaterialAlertDialogBuilder(this)
//                            .setTitle(title)
//                            .setMessage(message)
//                            .setPositiveButton("CLOSE", (d, which) ->{})
//                            .setOnDismissListener((d)-> stopAudio())
//                            .show());
//                }
                if(response.equals("p")){
                    startAudio();
                    runOnUiThread(() -> {
                        new MaterialAlertDialogBuilder(this)
                                .setTitle("Found")
                                .setMessage("Close to stop music.")
                                .setPositiveButton("CLOSE", (d, which) -> {
                                })
                                .setOnDismissListener((d) -> stopAudio())
                                .show();
                    });
                }
            });
            connectButton.setVisibility(View.GONE);
            disConnectButton.setVisibility(View.VISIBLE);

            toggleBulbAButton.setEnabled(true);
            toggleBulbBButton.setEnabled(true);
        });
        disConnectButton.setOnClickListener(v ->{
            client.close();
            connectButton.setVisibility(View.VISIBLE);
            disConnectButton.setVisibility(View.GONE);
            toggleBulbAButton.setEnabled(false);
            toggleBulbBButton.setEnabled(false);
        });
        toggleBulbAButton.setOnClickListener(v -> client.write("a"));
        toggleBulbBButton.setOnClickListener(v -> client.write("b"));
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
