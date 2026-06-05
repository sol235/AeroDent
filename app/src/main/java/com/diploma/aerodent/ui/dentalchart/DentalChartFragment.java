package com.diploma.aerodent.ui.dentalchart;

import com.diploma.aerodent.AeroDentApplication;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.tabs.TabLayout;

import com.diploma.aerodent.R;
import com.diploma.aerodent.data.local.entity.ProcedureLog;
import com.diploma.aerodent.data.local.entity.ToothStatus;
import com.diploma.aerodent.ui.appointments.AppointmentDetailFragment;
import com.diploma.aerodent.util.DialogUtils;

public class DentalChartFragment extends Fragment {

    private static final String TAG = "DentalChartFragment";
    private static final String ARG_PATIENT_ID = "patient_id";
    private static final String ARG_APPOINTMENT_ID = "appointment_id";
    private int patientId;
    private Integer appointmentId;
    private DentalChartViewModel viewModel;
    private WebView webView;
    private boolean isChartLoaded = false;
    private GlobalConditionsAdapter conditionsAdapter;
    private ProcedureLogAdapter historyAdapter;
    private BottomSheetBehavior<View> bottomSheetBehavior;

    public static DentalChartFragment newInstance(int patientId) {
        return newInstance(patientId, -1);
    }

    public static DentalChartFragment newInstance(int patientId, int appointmentId) {
        DentalChartFragment fragment = new DentalChartFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PATIENT_ID, patientId);
        args.putInt(ARG_APPOINTMENT_ID, appointmentId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            patientId = getArguments().getInt(ARG_PATIENT_ID);
            int argApptId = getArguments().getInt(ARG_APPOINTMENT_ID, -1);
            appointmentId = argApptId != -1 ? argApptId : null;
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_dental_chart, container, false);

        AeroDentApplication app = (AeroDentApplication) requireActivity().getApplication();
        viewModel = new ViewModelProvider(this, app.getViewModelFactory()).get(DentalChartViewModel.class);
        viewModel.setPatientId(patientId);
        if (appointmentId != null) {
            viewModel.setAppointmentId(appointmentId);
        }

        webView = root.findViewById(R.id.webview_dental_chart);
        webView.setBackgroundColor(0);
        setupToolbar(root);
        setupWebView();
        setupBottomSheet(root);

        observeViewModel();

        return root;
    }

    private void setupToolbar(View root) {
        View btnBack = root.findViewById(R.id.btn_back);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> getParentFragmentManager().popBackStack());
        }

        TextView textPatientName = root.findViewById(R.id.text_patient_name);
        if (textPatientName != null) {
            viewModel.getPatientName().observe(getViewLifecycleOwner(), name -> {
                if (name != null && !name.isEmpty()) {
                    textPatientName.setText(name);
                }
            });
        }
    }

    private void setupBottomSheet(View root) {
        View bottomSheet = root.findViewById(R.id.bottom_sheet_conditions);
        if (bottomSheet != null) {
            bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        }

        RecyclerView recyclerConditions = root.findViewById(R.id.recycler_active_conditions_global);
        RecyclerView recyclerHistory = root.findViewById(R.id.recycler_treatment_history);
        TabLayout tabLayout = root.findViewById(R.id.tab_layout_bottom_sheet);

        if (recyclerConditions != null) {
            recyclerConditions.setLayoutManager(new LinearLayoutManager(getContext()));
            conditionsAdapter = new GlobalConditionsAdapter(
                    new GlobalConditionsAdapter.OnConditionInteractionListener() {
                        @Override
                        public void onDeleteClick(ToothStatus status) {
                            viewModel.deleteToothStatus(status.getToothNumber(), status.getCondition());
                        }

                        @Override
                        public void onConditionClick(ToothStatus status) {
                            navigateToAppointment(status.getAppointmentId());
                        }
                    });
            recyclerConditions.setAdapter(conditionsAdapter);
        }

        if (recyclerHistory != null) {
            recyclerHistory.setLayoutManager(new LinearLayoutManager(getContext()));
            historyAdapter = new ProcedureLogAdapter(new ProcedureLogAdapter.OnProcedureLogInteractionListener() {
                @Override
                public void onAnnulClick(ProcedureLog log) {
                    showAnnulDialog(log);
                }

                @Override
                public void onLogClick(ProcedureLog log) {
                    navigateToAppointment(log.getAppointmentId());
                }
            });
            recyclerHistory.setAdapter(historyAdapter);
        }

        if (tabLayout != null && recyclerConditions != null && recyclerHistory != null) {
            tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    if (bottomSheetBehavior != null) {
                        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                    }
                    if (tab.getPosition() == 0) {
                        recyclerConditions.setVisibility(View.VISIBLE);
                        recyclerHistory.setVisibility(View.GONE);
                    } else {
                        recyclerConditions.setVisibility(View.GONE);
                        recyclerHistory.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onTabUnselected(TabLayout.Tab tab) {}

                @Override
                public void onTabReselected(TabLayout.Tab tab) {
                    if (bottomSheetBehavior != null && bottomSheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
                        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                    }
                }
            });
        }
    }

    @SuppressLint({"SetJavaScriptEnabled", "ClickableViewAccessibility"})
    private void setupWebView() {
        if (webView == null)
            return;

        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setAllowFileAccess(true);
        settings.setAllowFileAccessFromFileURLs(true);
        settings.setAllowUniversalAccessFromFileURLs(true);
        settings.setLoadWithOverviewMode(true);
        settings.setUseWideViewPort(true);

        webView.addJavascriptInterface(new WebAppInterface(), "Android");
        webView.loadUrl("file:///android_asset/dental_chart/dental_chart.html");

        webView.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (bottomSheetBehavior != null && bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
            }
            return false;
        });
    }

    private void observeViewModel() {
        viewModel.getToothColorsJson().observe(getViewLifecycleOwner(), jsonColors -> {
            if (isChartLoaded && jsonColors != null) {
                updateAllTeethColors(jsonColors);
            }
        });

        viewModel.getToothStatuses().observe(getViewLifecycleOwner(), statuses -> {
            if (statuses != null && conditionsAdapter != null) {
                conditionsAdapter.setConditions(statuses);
            }
        });

        viewModel.getProcedureLogs().observe(getViewLifecycleOwner(), logs -> {
            if (logs != null && historyAdapter != null) {
                historyAdapter.setProcedureLogs(logs);
            }
        });
    }

    private void updateAllTeethColors(String jsonColors) {
        if (!isChartLoaded || webView == null || jsonColors == null)
            return;

        webView.post(() -> {
            try {
                webView.evaluateJavascript("updateChart(" + jsonColors + ")", null);
            } catch (Exception e) {
                Log.e(TAG, "Error updating chart", e);
            }
        });
    }

    public class WebAppInterface {
        @JavascriptInterface
        public void onToothClicked(String toothNumber) {
            if (webView != null) {
                webView.post(() -> showConditionDialog(Integer.parseInt(toothNumber)));
            }
        }

        @JavascriptInterface
        public void onChartLoaded() {
            isChartLoaded = true;
            webView.post(() -> updateAllTeethColors(viewModel.getToothColorsJson().getValue()));
        }
    }

    private void showConditionDialog(int toothNumber) {
        ToothDetailFragment dialog = ToothDetailFragment.newInstance(toothNumber);
        dialog.show(getChildFragmentManager(), "ToothDetailFragment");
    }

    private void showAnnulDialog(ProcedureLog log) {
        DialogUtils.showAnnulDialog(requireContext(), () -> {
            log.setAnnulled(true);
            viewModel.updateProcedureLog(log);
        });
    }

    public void navigateToAppointment(Integer apptId) {
        if (apptId != null) {
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.nav_host_fragment, AppointmentDetailFragment.newInstance(apptId))
                    .addToBackStack(null).commit();
        }
    }
}
