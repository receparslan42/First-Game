package com.receparslan.firstgame;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.receparslan.firstgame.databinding.ActivityMainBinding;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    // View binding is used to bind the views in the layout to the code
    ActivityMainBinding binding;

    // SharedPreferences is used to store data in key-value pairs
    SharedPreferences sharedPreferences;

    // Declare the views
    EditText nameEditText; // EditText to get the name of the player
    TextView highScorePointTextView; // TextView to display the high score
    Button startButton; // Button to start the game

    // String variables to store the name of the player and the name of the player with the high score
    String playerName;
    String highScorePlayerName;

    // Integer variable to store the score of the player
    int highScorePoint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityMainBinding.inflate(getLayoutInflater()); // Inflate the layout using view binding
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Inflate the layout using view binding
        nameEditText = binding.nameEditText;
        startButton = binding.startButton;
        highScorePointTextView = binding.highScorePointTextView;

        // Set an onClickListener for the start button to start the game
        startButton.setOnClickListener(v -> {
            System.out.println("Start button clicked");
            // Get the name of the player
            playerName = String.valueOf(nameEditText.getText());

            // Start the game
            Intent goToTheGame = new Intent(MainActivity.this, GameActivity.class);
            goToTheGame.putExtra("name", playerName);
            startActivity(goToTheGame);
            finish();
        });

        // Set the shared preferences
        sharedPreferences = this.getSharedPreferences("com.receparslan.firstgame", MODE_PRIVATE);

        // Get the high score data from the shared preferences
        highScorePlayerName = sharedPreferences.getString("Name", "Empty");
        highScorePoint = sharedPreferences.getInt("HighScorePoint", 0);

        // Set the high score data to the text view
        highScorePointTextView.setText(String.format(Locale.getDefault(), "%s -> %d", highScorePlayerName, highScorePoint));
    }
}