package com.diploma.aerodent;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.diploma.aerodent.ui.calendar.CalendarFragment;
import com.diploma.aerodent.ui.home.HomeFragment;
import com.diploma.aerodent.ui.patients.PatientsFragment;
import com.diploma.aerodent.ui.settings.SettingsFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Locale;

import android.view.View;
import com.diploma.aerodent.ui.appointments.AddAppointmentFragment;
import com.diploma.aerodent.ui.patients.AddPatientFragment;
import com.diploma.aerodent.ui.appointments.SelectAppointmentDialogFragment;
import com.diploma.aerodent.util.CameraHelper;
import com.diploma.aerodent.ui.photos.PhotoViewModel;
import com.diploma.aerodent.ui.user.SetupAdminFragment;
import com.diploma.aerodent.ui.user.LoginFragment;
import com.diploma.aerodent.ui.user.AuthViewModel;
import com.diploma.aerodent.ui.user.UserViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {

    private PhotoViewModel photoViewModel;
    private CameraHelper cameraHelper;

    @Override
    protected void attachBaseContext(Context newBase) {
        Locale locale = new Locale("bg", "BG");
        Locale.setDefault(locale);
        Configuration config = newBase.getResources().getConfiguration();
        config.setLocale(locale);
        super.attachBaseContext(newBase.createConfigurationContext(config));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        setupCameraHelper(savedInstanceState);
        setupWindowInsets();
        
        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigationView);
        setupBottomNavigation(bottomNav);
        setupQuickActionsFab();
        setupFragmentLifecycleCallbacks(bottomNav);

        if (savedInstanceState == null) {
            checkAuthStatus(bottomNav);
        }
    }

    private void setupCameraHelper(Bundle savedInstanceState) {
        photoViewModel = new ViewModelProvider(this).get(PhotoViewModel.class);
        cameraHelper = new CameraHelper(this, photoViewModel);
        cameraHelper.setShowSuccessToast(true);

        if (savedInstanceState != null) {
            cameraHelper.onRestoreInstanceState(savedInstanceState);
        }
    }

    private void setupWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);

            findViewById(R.id.bottomNavigationView).setPadding(0, 0, 0, systemBars.bottom);
            findViewById(R.id.fab_quick_actions).setTranslationY(-systemBars.bottom);

            return insets;
        });
    }

    private void setupBottomNavigation(BottomNavigationView bottomNav) {
        bottomNav.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int itemId = item.getItemId();

            if (itemId == R.id.nav_home) {
                selectedFragment = new HomeFragment();
            } else if (itemId == R.id.nav_patients) {
                selectedFragment = new PatientsFragment();
            } else if (itemId == R.id.nav_calendar) {
                selectedFragment = new CalendarFragment();
            } else if (itemId == R.id.nav_settings) {
                selectedFragment = new SettingsFragment();
            }

            if (selectedFragment != null) {
                getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_fragment, selectedFragment)
                        .commit();
            }
            return true;
        });
    }

    private void setupQuickActionsFab() {
        FloatingActionButton fab = findViewById(R.id.fab_quick_actions);
        fab.setOnClickListener(v -> {
            View popupView = getLayoutInflater().inflate(R.layout.view_quick_actions, null);
            android.widget.PopupWindow popupWindow = new android.widget.PopupWindow(popupView,
                    android.view.ViewGroup.LayoutParams.WRAP_CONTENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
                    true);

            popupWindow.setBackgroundDrawable(
                    new android.graphics.drawable.ColorDrawable(android.graphics.Color.TRANSPARENT));

            popupView.findViewById(R.id.btn_quick_new_patient).setOnClickListener(btn -> {
                popupWindow.dismiss();
                getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_fragment, new AddPatientFragment())
                        .addToBackStack(null).commit();
            });

            popupView.findViewById(R.id.btn_quick_new_appointment).setOnClickListener(btn -> {
                popupWindow.dismiss();
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.nav_host_fragment, new AddAppointmentFragment()).addToBackStack(null).commit();
            });

            popupView.findViewById(R.id.btn_quick_take_photo).setOnClickListener(btn -> {
                popupWindow.dismiss();
                SelectAppointmentDialogFragment dialog = new SelectAppointmentDialogFragment();
                dialog.setOnAppointmentSelectedListener(appointment -> {
                    cameraHelper.takePhoto(appointment.getPatientId(), appointment.getId(), null);
                });
                dialog.show(getSupportFragmentManager(), "SelectAppointmentDialog");
            });

            popupView.findViewById(R.id.btn_quick_logout).setOnClickListener(btn -> {
                popupWindow.dismiss();
                UserViewModel userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
                userViewModel.logout();
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.nav_host_fragment, new LoginFragment())
                        .commit();
            });

            popupView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
            int popupWidth = popupView.getMeasuredWidth();
            int popupHeight = popupView.getMeasuredHeight();

            int xOffset = (fab.getWidth() - popupWidth) / 2;
            int gapPixels = (int) (32 * getResources().getDisplayMetrics().density);
            int yOffset = -(fab.getHeight() + popupHeight) - gapPixels;

            popupWindow.showAsDropDown(fab, xOffset, yOffset);
        });
    }

    private void setupFragmentLifecycleCallbacks(BottomNavigationView bottomNav) {
        FloatingActionButton fab = findViewById(R.id.fab_quick_actions);
        getSupportFragmentManager().registerFragmentLifecycleCallbacks(new androidx.fragment.app.FragmentManager.FragmentLifecycleCallbacks() {
            @Override
            public void onFragmentViewCreated(@NonNull androidx.fragment.app.FragmentManager fm, @NonNull Fragment f, @NonNull View v, @Nullable Bundle savedInstanceState) {
                if (f instanceof SetupAdminFragment || f instanceof LoginFragment) {
                    bottomNav.setVisibility(View.GONE);
                    fab.setVisibility(View.GONE);
                } else {
                    bottomNav.setVisibility(View.VISIBLE);
                    fab.setVisibility(View.VISIBLE);
                }
            }
        }, true);
    }

    private void checkAuthStatus(BottomNavigationView bottomNav) {
        AuthViewModel authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);
        authViewModel.getAuthState().observe(this, state -> {
            if (state == null) return;
            switch (state) {
                case NEEDS_SETUP:
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.nav_host_fragment, new SetupAdminFragment())
                            .commit();
                    break;
                case NEEDS_LOGIN:
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.nav_host_fragment, new LoginFragment())
                            .commit();
                    break;
                case LOGGED_IN:
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.nav_host_fragment, new HomeFragment())
                            .commit();
                    bottomNav.setSelectedItemId(R.id.nav_home);
                    break;
            }
        });
        authViewModel.checkAuthStatus();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        cameraHelper.onSaveInstanceState(outState);
    }
}