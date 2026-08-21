package com.example.othermaps;

import androidx.appcompat.app.AppCompatActivity;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.widget.SeekBar;
import android.widget.TextView;

public class EditActivity extends AppCompatActivity {

    private SeekBar seekBar1, seekBar2, seekBar3;
    private TextView leftText, rightText, doneText;

    int left,right,done = 1;

    private SingletonData singletonData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        singletonData = SingletonData.getInstance();

        left = singletonData.getleft();
        right = singletonData.getright();
        done = singletonData.getdone();


        seekBar1 = findViewById(R.id.seekBar1);
        seekBar1.setProgress(left-1);
        seekBar2 = findViewById(R.id.seekBar2);
        seekBar2.setProgress(right-1);

        seekBar3 = findViewById(R.id.seekBar3);
        seekBar3.setProgress(done-1);

        leftText = findViewById(R.id.seekBar1Value);
        rightText = findViewById(R.id.seekBar2Value);
        doneText = findViewById(R.id.seekBar3Value);
        leftText.setText((left*5)+"");
        rightText.setText((right*5)+"");
        doneText.setText((done)+"");


        // Set up SeekBar listeners
        seekBar1.setOnSeekBarChangeListener(seekBarChangeListener);
        seekBar2.setOnSeekBarChangeListener(seekBarChangeListener);
        seekBar3.setOnSeekBarChangeListener(seekBarChangeListener);
    }

    private SeekBar.OnSeekBarChangeListener seekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            int value = progress ;
            String seekBarId = getResources().getResourceEntryName(seekBar.getId());
            Log.d("SeekBar", seekBarId + ": " + value);

            left = getValueFromSeekBar(seekBar1);
            right = getValueFromSeekBar(seekBar2);
            done = getValueFromSeekBar(seekBar3);
            leftText.setText((left*5)+"");
            singletonData.setleft(left);
            rightText.setText((right*5)+"");
            singletonData.setright(right);

            doneText.setText(done+"");
            singletonData.setdone(done);

        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            // Not needed for this example
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            // Not needed for this example
        }
    };

    private int getValueFromSeekBar(SeekBar seekBar) {
        return seekBar.getProgress() + 1;
    }
}