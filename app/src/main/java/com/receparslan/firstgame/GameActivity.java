package com.receparslan.firstgame;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.receparslan.firstgame.databinding.ActivityGameBinding;

import java.util.Locale;
import java.util.Objects;
import java.util.Random;

public class GameActivity extends AppCompatActivity {

    // Array to store the cat image views
    final ImageView[] catImageViews = new ImageView[16];
    // View binding is used to bind the views in the layout to the code
    ActivityGameBinding binding;
    // SharedPreferences is used to store high score
    SharedPreferences sharedPreferences;
    // Declare the views
    TextView highScoreTextView;
    TextView timeTextView;
    TextView nameTextView;
    TextView scoreTextView;

    // Game variables
    String playerName;
    int score;
    int highScorePoint;

    // Handler and Runnable to update the cat images
    Handler handler;
    Runnable runnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityGameBinding.inflate(getLayoutInflater()); // Inflate the layout using view binding
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Inflate the layout using view binding
        highScoreTextView = binding.highScoreTextView;
        timeTextView = binding.timeTextView;
        nameTextView = binding.nameTextView;
        scoreTextView = binding.scoreTextView;

        // Initialize the cat image views
        for (int i = 0; i < 16; i++) {
            try {
                catImageViews[i] = (ImageView) binding.getClass().getDeclaredField("catImageView" + ((i == 0) ? "" : i + 1)).get(binding);
                Objects.requireNonNull(catImageViews[i]).setOnClickListener(this::increaseScore);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }

        // Get the high score from the shared preferences and set it to the text view
        sharedPreferences = this.getSharedPreferences("com.receparslan.firstgame", Context.MODE_PRIVATE);
        highScorePoint = sharedPreferences.getInt("HighScorePoint", 0);
        highScoreTextView.setText(String.valueOf(highScorePoint));

        // Set player name
        playerName = getIntent().getStringExtra("name"); // Get the player name from the first activity
        nameTextView.setText(playerName); // Set the player name to the text view

        // Set the score to the text view
        score = 0;
        scoreTextView.setText(getString(R.string.score, score));

        // Start the timer
        new CountDownTimer(180000, 100) {
            @Override
            public void onTick(long millisUntilFinished) {
                // Update the time text view
                String time = String.format(Locale.getDefault(), "%02d:%02d:%02d", millisUntilFinished / 60000, millisUntilFinished % 60000 / 1000, millisUntilFinished % 1000 / 100);
                timeTextView.setText(time);
            }

            @Override
            public void onFinish() {
                handler.removeCallbacks(runnable); // Stop the image update

                // Check if the score is greater than the high score
                if (score >= highScorePoint) {
                    sharedPreferences.edit().putString("Name", playerName).apply();
                    sharedPreferences.edit().putInt("HighScorePoint", highScorePoint).apply();
                }

                // Show the play again dialog
                playAgain();
            }
        }.start();

        startUpdateImages(); // Start updating the cat images
    }

    // Increase the score when the visible cat image is clicked
    public void increaseScore(View view) {
        if (view.getVisibility() == View.VISIBLE) {
            score++;
            scoreTextView.setText(getString(R.string.score, score));
            view.setVisibility(View.INVISIBLE);

            if (score >= highScorePoint) {
                highScorePoint = score;
                highScoreTextView.setText(String.valueOf(highScorePoint));
            }
        }
    }

    // Show the play again dialog
    public void playAgain() {
        AlertDialog.Builder playAgain = new AlertDialog.Builder(this);
        playAgain.setTitle("Time's Up!");
        playAgain.setCancelable(false);
        playAgain.setMessage("Do you want to play again ?");

        // Set the buttons
        playAgain.setPositiveButton("Yes", (dialogInterface, i) -> {
            //Restart the game
            Intent intent = new Intent(this, getClass());
            intent.putExtra("name", playerName);
            startActivity(intent);
            finish();
        });
        playAgain.setNegativeButton("No", (dialogInterface, i) -> {
            // Go to the first activity
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        });

        playAgain.show(); // Show the dialog
    }

    // Start the update of the cat images
    public void startUpdateImages() {
        handler = new Handler(Looper.getMainLooper());
        runnable = new Runnable() {
            @Override
            public void run() {
                // Hide all the cat images
                for (ImageView imageView : catImageViews) {
                    imageView.setVisibility(View.INVISIBLE);
                }

                // Show a random cat image
                Random random = new Random();
                int randomCatIndex = random.nextInt(16);
                catImageViews[randomCatIndex].setVisibility(View.VISIBLE);

                handler.postDelayed(this, 1000); // Update the cat images every second
            }
        };
        handler.post(runnable);
    }
}