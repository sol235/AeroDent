package com.diploma.aerodent;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.widget.TextView;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.diploma.aerodent.ui.home.DashboardAppointmentAdapter;
import com.diploma.aerodent.ui.home.HomeViewModel;

public class MainActivity extends AppCompatActivity {

    private HomeViewModel homeViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize ViewModel
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        // Bind UI elements
        TextView textTotalPatients = findViewById(R.id.text_total_patients);
        TextView textTodaysAppts = findViewById(R.id.text_todays_appointments);
        TextView textPlaceholder = findViewById(R.id.text_placeholder_stat);
        TextView textPlaceholder2 = findViewById(R.id.text_placeholder_stat2);

        homeViewModel.getTotalPatientsCount().observe(this, count -> {
            if (count != null) textTotalPatients.setText(String.valueOf(count));
        });

        homeViewModel.getTodaysAppointmentsCount().observe(this, count -> {
            if (count != null) textTodaysAppts.setText(String.valueOf(count));
        });


        
        textPlaceholder.setText("0");

        // Schedule RecyclerView Setup
        RecyclerView recyclerSchedule = findViewById(R.id.recycler_dashboard_schedule);
        DashboardAppointmentAdapter scheduleAdapter = new DashboardAppointmentAdapter();
        recyclerSchedule.setAdapter(scheduleAdapter);

        homeViewModel.getTodaysAppointments().observe(this, appointments -> {
            if (appointments != null) {
                scheduleAdapter.setAppointments(appointments);
            }
        });

        homeViewModel.getAllPatients().observe(this, patients -> {
            if (patients != null) {
                scheduleAdapter.setPatients(patients);
            }
        });
    }
}