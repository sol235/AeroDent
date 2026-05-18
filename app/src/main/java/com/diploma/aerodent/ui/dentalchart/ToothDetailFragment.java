package com.diploma.aerodent.ui.dentalchart;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.diploma.aerodent.R;
import com.diploma.aerodent.data.local.model.DentalCondition;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;

import java.util.List;
import java.util.Map;

public class ToothDetailFragment extends BottomSheetDialogFragment {

    private static final String ARG_TOOTH_NUMBER = "tooth_number";
    private int toothNumber;
    private DentalChartViewModel viewModel;

    private RecyclerView recyclerActiveConditions;
    private ActiveConditionsAdapter activeAdapter;
    private LinearLayout containerCategories;
    private LinearLayout layoutSurfaceSelector;
    private TextView textSelectedCondition;
    private ToggleButton[] surfaceToggles;
    private MaterialButton btnSaveCondition;

    private DentalCondition pendingCondition;
    private List<com.diploma.aerodent.data.local.entity.ToothStatus> currentStatuses;

    public static ToothDetailFragment newInstance(int toothNumber) {
        ToothDetailFragment fragment = new ToothDetailFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_TOOTH_NUMBER, toothNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            toothNumber = getArguments().getInt(ARG_TOOTH_NUMBER);
        }
        viewModel = new ViewModelProvider(requireParentFragment()).get(DentalChartViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_tooth_details, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView textTitle = view.findViewById(R.id.text_tooth_title);
        textTitle.setText(getString(R.string.dental_chart_tooth_title, toothNumber));

        setupActiveConditions(view);
        setupCategoryMenu(view);
        setupSurfaceSelector(view);

        observeToothData();
    }

    private void setupActiveConditions(View view) {
        recyclerActiveConditions = view.findViewById(R.id.recycler_active_conditions);
        recyclerActiveConditions.setLayoutManager(new LinearLayoutManager(getContext()));
        activeAdapter = new ActiveConditionsAdapter(status -> {
            viewModel.deleteToothStatus(toothNumber, status.getCondition());
        });
        recyclerActiveConditions.setAdapter(activeAdapter);
    }

    private void setupCategoryMenu(View view) {
        containerCategories = view.findViewById(R.id.container_categories);

        Map<DentalCondition.Category, List<DentalCondition>> grouped = viewModel.getGroupedConditions();

        for (DentalCondition.Category category : viewModel.getVisibleCategories()) {
            addCategoryToMenu(category, grouped.get(category));
        }
    }

    private void addCategoryToMenu(DentalCondition.Category category, List<DentalCondition> conditions) {
        if (conditions == null || conditions.isEmpty())
            return;

        TextView header = new TextView(getContext(), null, 0, R.style.CategoryHeaderStyle);
        header.setText(category.getTitle(getContext()));

        LinearLayout itemsContainer = new LinearLayout(getContext());
        itemsContainer.setOrientation(LinearLayout.VERTICAL);
        itemsContainer.setVisibility(View.GONE);

        header.setOnClickListener(v -> {
            boolean isVisible = itemsContainer.getVisibility() == View.VISIBLE;

            for (int i = 0; i < containerCategories.getChildCount(); i++) {
                View child = containerCategories.getChildAt(i);
                if (child instanceof LinearLayout) {
                    child.setVisibility(View.GONE);
                } else if (child instanceof TextView) {
                    ((TextView) child).setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_right, 0);
                }
            }

            if (!isVisible) {
                itemsContainer.setVisibility(View.VISIBLE);
                header.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_back, 0);
            }
        });

        for (DentalCondition condition : conditions) {
            TextView item = new TextView(getContext(), null, 0, R.style.ConditionButtonStyle);
            item.setText(condition.getDisplayName(getContext()));
            item.setOnClickListener(v -> onConditionClicked(condition));
            itemsContainer.addView(item);
        }

        containerCategories.addView(header);
        containerCategories.addView(itemsContainer);
    }

    private void setupSurfaceSelector(View view) {
        layoutSurfaceSelector = view.findViewById(R.id.layout_surface_selector);
        textSelectedCondition = view.findViewById(R.id.text_selected_condition);
        btnSaveCondition = view.findViewById(R.id.btn_save_condition);

        surfaceToggles = new ToggleButton[] { view.findViewById(R.id.toggle_m), view.findViewById(R.id.toggle_o),
                view.findViewById(R.id.toggle_d), view.findViewById(R.id.toggle_b), view.findViewById(R.id.toggle_l) };

        btnSaveCondition.setOnClickListener(v -> savePendingCondition());
    }

    private void onConditionClicked(DentalCondition condition) {
        if (condition.requiresSurfaces()) {
            pendingCondition = condition;
            textSelectedCondition
                    .setText(getString(R.string.dental_chart_surfaces_for, condition.getDisplayName(getContext())));
            layoutSurfaceSelector.setVisibility(View.VISIBLE);

            java.util.List<String> existingSurfaces = new java.util.ArrayList<>();
            if (currentStatuses != null) {
                for (com.diploma.aerodent.data.local.entity.ToothStatus status : currentStatuses) {
                    if (status.getCondition() == condition) {
                        String surfacesStr = status.getSurfaces();
                        if (surfacesStr != null && !surfacesStr.isEmpty()) {
                            java.util.Collections.addAll(existingSurfaces, surfacesStr.split(","));
                        }
                        break;
                    }
                }
            }

            String[] codes = viewModel.getSurfaceCodes();
            for (int i = 0; i < surfaceToggles.length; i++) {
                surfaceToggles[i].setChecked(existingSurfaces.contains(codes[i]));
            }
        } else {
            viewModel.updateToothStatus(toothNumber, condition, "");
            layoutSurfaceSelector.setVisibility(View.GONE);
        }
    }

    private void savePendingCondition() {
        if (pendingCondition == null)
            return;

        java.util.List<String> selectedSurfaces = new java.util.ArrayList<>();
        String[] codes = viewModel.getSurfaceCodes();
        for (int i = 0; i < surfaceToggles.length; i++) {
            if (surfaceToggles[i].isChecked()) {
                selectedSurfaces.add(codes[i]);
            }
        }

        viewModel.updateToothStatus(toothNumber, pendingCondition, selectedSurfaces);
        layoutSurfaceSelector.setVisibility(View.GONE);
        pendingCondition = null;
    }

    private void observeToothData() {
        viewModel.getToothStatusesForTooth(toothNumber).observe(getViewLifecycleOwner(), statuses -> {
            currentStatuses = statuses;
            activeAdapter.setStatuses(statuses);
        });
    }
}
