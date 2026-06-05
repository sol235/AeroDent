package com.diploma.aerodent.ui.dentalchart;

import com.diploma.aerodent.AeroDentApplication;

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

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.util.TypedValue;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.diploma.aerodent.R;
import com.diploma.aerodent.data.local.entity.ToothStatus;
import com.diploma.aerodent.data.local.model.DentalCondition;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ToothDetailFragment extends BottomSheetDialogFragment {

    private static final String ARG_TOOTH_NUMBER = "tooth_number";
    private int toothNumber;
    private DentalChartViewModel viewModel;

    private RecyclerView recyclerActiveConditions;
    private ActiveConditionsAdapter activeAdapter;
    private LinearLayout containerCategories;
    private LinearLayout layoutConfirmation;
    private TextView textSelectedCondition;
    private LinearLayout containerSurfaceToggles;
    private TextView textSurfaceLegend;
    private ToggleButton[] surfaceToggles;
    private MaterialButtonToggleGroup toggleContext;
    private MaterialButton btnContextAppointment;
    private MaterialButton btnSaveCondition;

    private DentalCondition pendingCondition;
    private TextView selectedConditionView;
    private List<ToothStatus> currentStatuses;
    private Map<DentalCondition, TextView> conditionViews = new HashMap<>();

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
        AeroDentApplication app = (AeroDentApplication) requireActivity().getApplication();
        viewModel = new ViewModelProvider(requireParentFragment(), app.getViewModelFactory()).get(DentalChartViewModel.class);
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
        activeAdapter = new ActiveConditionsAdapter(new ActiveConditionsAdapter.OnConditionInteractionListener() {
            @Override
            public void onDeleteClick(ToothStatus status) {
                viewModel.deleteToothStatus(toothNumber, status.getCondition());
            }

            @Override
            public void onConditionClick(ToothStatus status) {
                if (status.getAppointmentId() != null) {
                    dismiss();
                    Fragment parent = getParentFragment();
                    if (parent instanceof DentalChartFragment) {
                        ((DentalChartFragment) parent).navigateToAppointment(status.getAppointmentId());
                    }
                }
            }
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

        int categoryColorResId = 0;
        switch (category) {
            case PATHOLOGY:
                categoryColorResId = R.color.dental_red;
                break;
            case RESTORATION:
                categoryColorResId = R.color.dental_blue;
                break;
            case PERIODONTOLOGY:
                categoryColorResId = R.color.dental_green;
                break;
            case PROSTHETICS:
                categoryColorResId = R.color.dental_orange;
                break;
            default:
                break;
        }

        Drawable headerDot = null;
        if (categoryColorResId != 0) {
            headerDot = ContextCompat.getDrawable(requireContext(), R.drawable.dot_circle).mutate();
            headerDot.setTint(ContextCompat.getColor(requireContext(), categoryColorResId));
        }

        TextView header = new TextView(getContext(), null, 0, R.style.CategoryHeaderStyle);
        header.setText(category.getTitle(getContext()));
        header.setCompoundDrawablesWithIntrinsicBounds(
            headerDot,
            null,
            ContextCompat.getDrawable(requireContext(), R.drawable.ic_arrow_right),
            null
        );
        header.setCompoundDrawablePadding((int) (12 * getResources().getDisplayMetrics().density));

        LinearLayout itemsContainer = new LinearLayout(getContext());
        itemsContainer.setOrientation(LinearLayout.VERTICAL);
        itemsContainer.setVisibility(View.GONE);

        header.setOnClickListener(v -> {
            boolean isVisible = itemsContainer.getVisibility() == View.VISIBLE;

            for (int i = 0; i < containerCategories.getChildCount(); i++) {
                View child = containerCategories.getChildAt(i);
                if (child instanceof LinearLayout) {
                    child.setVisibility(View.GONE);
                } else if (child instanceof TextView childTv) {
                    Drawable[] drawables = childTv.getCompoundDrawables();
                    Drawable childDot = drawables[0];
                    childTv.setCompoundDrawablesWithIntrinsicBounds(
                        childDot,
                        null,
                        ContextCompat.getDrawable(requireContext(), R.drawable.ic_arrow_right),
                        null
                    );
                }
            }

            if (!isVisible) {
                itemsContainer.setVisibility(View.VISIBLE);
                Drawable[] drawables = header.getCompoundDrawables();
                Drawable currentDot = drawables[0];
                header.setCompoundDrawablesWithIntrinsicBounds(
                    currentDot,
                    null,
                    ContextCompat.getDrawable(requireContext(), R.drawable.ic_back),
                    null
                );
            }
        });

        for (DentalCondition condition : conditions) {
            TextView item = new TextView(getContext(), null, 0, R.style.ConditionButtonStyle);
            item.setText(condition.getDisplayName(getContext()));
            item.setOnClickListener(v -> onConditionClicked(condition, item));

            int itemColorResId = condition.getColorResId();
            if (itemColorResId != 0) {
                Drawable itemDot = ContextCompat.getDrawable(requireContext(), R.drawable.dot_circle).mutate();
                itemDot.setTint(ContextCompat.getColor(requireContext(), itemColorResId));
                item.setCompoundDrawablesWithIntrinsicBounds(itemDot, null, null, null);
                item.setCompoundDrawablePadding((int) (12 * getResources().getDisplayMetrics().density));
            }

            itemsContainer.addView(item);
            conditionViews.put(condition, item);
        }

        containerCategories.addView(header);
        containerCategories.addView(itemsContainer);
    }

    private void setupSurfaceSelector(View view) {
        layoutConfirmation = view.findViewById(R.id.layout_confirmation);
        textSelectedCondition = view.findViewById(R.id.text_selected_condition);
        containerSurfaceToggles = view.findViewById(R.id.container_surface_toggles);
        textSurfaceLegend = view.findViewById(R.id.text_surface_legend);
        toggleContext = view.findViewById(R.id.toggle_context);
        btnContextAppointment = view.findViewById(R.id.btn_context_appointment);
        btnSaveCondition = view.findViewById(R.id.btn_save_condition);

        surfaceToggles = new ToggleButton[] { view.findViewById(R.id.toggle_m), view.findViewById(R.id.toggle_o),
                view.findViewById(R.id.toggle_d), view.findViewById(R.id.toggle_b), view.findViewById(R.id.toggle_l) };

        btnSaveCondition.setOnClickListener(v -> savePendingCondition());
    }

    private void onConditionClicked(DentalCondition condition, TextView clickedView) {
        String conflictWarning = checkConditionConflict(condition);
        if (conflictWarning != null) {
            Snackbar snackbar = Snackbar.make(
                    requireView(), conflictWarning, Snackbar.LENGTH_LONG);
            TextView tv = snackbar.getView().findViewById(com.google.android.material.R.id.snackbar_text);
            if (tv != null) {
                tv.setMaxLines(5);
                tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
            }
            snackbar.show();
            return;
        }

        if (selectedConditionView != null) {
            selectedConditionView.setBackgroundColor(Color.TRANSPARENT);
        }

        selectedConditionView = clickedView;
        selectedConditionView.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.tertiary));
        selectedConditionView.setTypeface(null, Typeface.BOLD);
        selectedConditionView.setTextColor(ContextCompat.getColor(requireContext(), R.color.primary));
        selectedConditionView.setAlpha(1.0f);
        
        updateConditionViewsState();

        pendingCondition = condition;
        layoutConfirmation.setVisibility(View.VISIBLE);

        if (viewModel.getAppointmentId().getValue() == null) {
            btnContextAppointment.setEnabled(false);
            toggleContext.check(R.id.btn_context_previous);
        } else {
            btnContextAppointment.setEnabled(true);
            toggleContext.check(R.id.btn_context_appointment);
        }

        if (condition.requiresSurfaces()) {
            textSelectedCondition.setText(getString(R.string.dental_chart_surfaces_for, condition.getDisplayName(getContext())));
            textSelectedCondition.setVisibility(View.VISIBLE);
            containerSurfaceToggles.setVisibility(View.VISIBLE);
            textSurfaceLegend.setVisibility(View.VISIBLE);

            List<String> existingSurfaces = viewModel.getExistingSurfaces(currentStatuses, condition);

            String[] codes = viewModel.getSurfaceCodes();
            for (int i = 0; i < surfaceToggles.length; i++) {
                surfaceToggles[i].setChecked(existingSurfaces.contains(codes[i]));
            }
        } else {
            textSelectedCondition.setVisibility(View.GONE);
            containerSurfaceToggles.setVisibility(View.GONE);
            textSurfaceLegend.setVisibility(View.GONE);
        }
    }

    private void savePendingCondition() {
        if (pendingCondition == null)
            return;

        boolean isCurrentAppointment = toggleContext.getCheckedButtonId() == R.id.btn_context_appointment;

        List<String> selectedSurfaces = new ArrayList<>();
        if (pendingCondition.requiresSurfaces()) {
            String[] codes = viewModel.getSurfaceCodes();
            for (int i = 0; i < surfaceToggles.length; i++) {
                if (surfaceToggles[i].isChecked()) {
                    selectedSurfaces.add(codes[i]);
                }
            }
        }

        viewModel.updateToothStatus(toothNumber, pendingCondition, selectedSurfaces, isCurrentAppointment);
        layoutConfirmation.setVisibility(View.GONE);
        pendingCondition = null;

        if (selectedConditionView != null) {
            selectedConditionView.setBackgroundColor(Color.TRANSPARENT);
            selectedConditionView = null;
            updateConditionViewsState();
        }
    }

    private void observeToothData() {
        viewModel.getToothStatusesForTooth(toothNumber).observe(getViewLifecycleOwner(), statuses -> {
            currentStatuses = statuses;
            activeAdapter.setStatuses(statuses);
            updateConditionViewsState();
        });
    }

    private void updateConditionViewsState() {
        if (conditionViews == null || currentStatuses == null) return;
        Context context = getContext();
        if (context == null) return;

        int textSecondaryColor = ContextCompat.getColor(context, R.color.text_secondary);
        int blackColor = ContextCompat.getColor(context, R.color.black);

        for (Map.Entry<DentalCondition, TextView> entry : conditionViews.entrySet()) {
            DentalCondition condition = entry.getKey();
            TextView view = entry.getValue();

            if (view == selectedConditionView) continue;

            boolean hasConflict = checkConditionConflict(condition) != null;
            if (hasConflict) {
                view.setTextColor(textSecondaryColor);
                view.setTypeface(null, Typeface.NORMAL);
                view.setAlpha(0.5f);
            } else {
                view.setTextColor(blackColor);
                view.setTypeface(null, Typeface.BOLD);
                view.setAlpha(1.0f);
            }
        }
    }

    private String checkConditionConflict(DentalCondition newCondition) {
        if (getContext() == null) return null;
        return viewModel.checkConditionConflict(currentStatuses, newCondition, requireContext());
    }
}
