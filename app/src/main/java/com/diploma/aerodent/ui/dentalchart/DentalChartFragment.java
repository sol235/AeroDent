package com.diploma.aerodent.ui.dentalchart;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
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

import com.diploma.aerodent.R;

public class DentalChartFragment extends Fragment {

    private static final String TAG = "DentalChartFragment";
    private static final String ARG_PATIENT_ID = "patient_id";
    private int patientId;
    private DentalChartViewModel viewModel;
    private WebView webView;
    private boolean isChartLoaded = false;

    public static DentalChartFragment newInstance(int patientId) {
        DentalChartFragment fragment = new DentalChartFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PATIENT_ID, patientId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            patientId = getArguments().getInt(ARG_PATIENT_ID);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_dental_chart, container, false);

        viewModel = new ViewModelProvider(this).get(DentalChartViewModel.class);
        viewModel.setPatientId(patientId);

        webView = root.findViewById(R.id.webview_dental_chart);
        webView.setBackgroundColor(0);
        setupToolbar(root);
        setupWebView();

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

    @SuppressLint("SetJavaScriptEnabled")
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
    }

    private void observeViewModel() {
        viewModel.getToothColorsJson().observe(getViewLifecycleOwner(), jsonColors -> {
            if (isChartLoaded && jsonColors != null) {
                updateAllTeethColors(jsonColors);
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
}
