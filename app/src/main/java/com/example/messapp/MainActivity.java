package com.example.messapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.net.Uri;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.app.AlertDialog;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private Spinner spinnerHostel;
    private TextView menuText;
    private Button mealChoiceButton, refreshVotesButton;

    // Sample menu items for hostels
    private final List<String> hostelAMenu = Arrays.asList("Dal Tadka, Rice, Chapati, Salad");
    private final List<String> hostelBMenu = Arrays.asList("Paneer Butter Masala, Rice, Chapati, Curry");
    private final List<String> hostelCMenu = Arrays.asList("Dal Fry, Rice, Paratha, Raita");
    private final List<String> hostelZMenu = Arrays.asList("Choose an option");

    // Backend URL (replace with your EC2 public IP)
    private static final String     BACKEND_URL = "http://3.110.196.233:8080/votes";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize UI elements
        spinnerHostel = findViewById(R.id.spinnerHostel);
        menuText = findViewById(R.id.menuText);
        mealChoiceButton = findViewById(R.id.mealChoiceButton);
        refreshVotesButton = findViewById(R.id.refreshVotesButton);

        // Define hostel options
        String[] hostels = {"Agira", "Anantam", "Vyan", "Choose an Option"};

        // Create an ArrayAdapter to populate the Spinner
        ArrayAdapter<String> hostelAdapter = new ArrayAdapter<>(this, R.layout.spinner_item, hostels);
        hostelAdapter.setDropDownViewResource(R.layout.spinner_item);
        spinnerHostel.setAdapter(hostelAdapter);
        spinnerHostel.setSelection(3);

        // Set listener for Spinner to display menu when hostel is selected
        spinnerHostel.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                displayMenu(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Optional: Handle case where nothing is selected
            }
        });

        // Listener for meal choice button
        mealChoiceButton.setOnClickListener(v -> showMealChoiceDialog());

        // Listener for refresh votes button
        refreshVotesButton.setOnClickListener(v -> fetchVoteCounts());
    }

    // Display the menu for the selected hostel
    private void displayMenu(int hostelIndex) {
        List<String> menu;
        switch (hostelIndex) {
            case 0:
                menu = hostelAMenu;
                break;
            case 1:
                menu = hostelBMenu;
                break;
            case 2:
                menu = hostelCMenu;
                break;
            default:
                menu = hostelZMenu;
                break;
        }

        Random rand = new Random();
        String selectedMenu = menu.get(rand.nextInt(menu.size()));

        if (hostelIndex == 3) {
            menuText.setText("");
        } else {
            menuText.setText("Menu for selected hostel: " + selectedMenu);
        }
    }

    // Show dialog for meal choice
    private void showMealChoiceDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Do you want to have this meal?")
                .setCancelable(false)
                .setPositiveButton("Yes", (dialog, id) -> {
                    int selectedHostelIndex = spinnerHostel.getSelectedItemPosition();
                    if (selectedHostelIndex != 3) {
                        sendVoteToServer(spinnerHostel.getSelectedItem().toString(), "yes");
                        Toast.makeText(MainActivity.this, "Vote sent successfully!", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("No", (dialog, id) -> {
                    Toast.makeText(MainActivity.this, "Vote canceled", Toast.LENGTH_SHORT).show();
                });

        builder.create().show();
    }

    // Send vote to the backend
    private void sendVoteToServer(String hostel, String vote) {
        RequestQueue queue = Volley.newRequestQueue(this);

        JSONObject voteData = new JSONObject();
        try {
            voteData.put("hostel", hostel);
            voteData.put("vote", vote);
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, BACKEND_URL, voteData,
                response -> Toast.makeText(MainActivity.this, "Vote sent to backend!", Toast.LENGTH_SHORT).show(),
                error -> Toast.makeText(MainActivity.this, "Failed to send vote: " + error.getMessage(), Toast.LENGTH_SHORT).show()
        );

        queue.add(request);
    }

    // Fetch vote counts from the backend
    private void fetchVoteCounts() {
        RequestQueue queue = Volley.newRequestQueue(this);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, BACKEND_URL, null,
                response -> {
                    StringBuilder voteCounts = new StringBuilder("Current Vote Counts:\n");
                    response.keys().forEachRemaining(key -> {
                        try {
                            voteCounts.append(key).append(": ").append(response.getInt(key)).append("\n");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    });
                    menuText.setText(voteCounts.toString());
                },
                error -> Toast.makeText(MainActivity.this, "Failed to fetch votes: " + error.getMessage(), Toast.LENGTH_SHORT).show()
        );

        queue.add(request);
    }
}