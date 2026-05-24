package com.diploma.aerodent.ui.dentalchart;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.diploma.aerodent.R;
import com.diploma.aerodent.data.local.entity.ToothStatus;
import com.diploma.aerodent.data.local.model.DentalCondition;
import com.diploma.aerodent.util.FormatUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class GlobalConditionsAdapter extends RecyclerView.Adapter<GlobalConditionsAdapter.ViewHolder> {

    public interface OnConditionInteractionListener {
        void onDeleteClick(ToothStatus status);
        void onConditionClick(ToothStatus status);
    }

    private List<ToothStatus> conditionList = new ArrayList<>();
    private final OnConditionInteractionListener listener;

    public GlobalConditionsAdapter(OnConditionInteractionListener listener) {
        this.listener = listener;
    }

    public void setConditions(List<ToothStatus> conditions) {
        List<ToothStatus> filtered = new ArrayList<>();
        if (conditions != null) {
            for (ToothStatus status : conditions) {
                if (status.getCondition() != DentalCondition.HEALTHY) {
                    filtered.add(status);
                }
            }
            filtered.sort(Comparator.comparingInt(ToothStatus::getToothNumber));
        }
        this.conditionList = filtered;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_global_condition, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ToothStatus status = conditionList.get(position);

        holder.textToothNumber.setText(String.valueOf(status.getToothNumber()));

        if (status.getCondition() != null) {
            holder.textConditionName.setText(status.getCondition().getDisplayName(holder.itemView.getContext()));

            int colorResId = status.getCondition().getColorResId();
            if (colorResId != 0) {
                int color = androidx.core.content.ContextCompat.getColor(holder.itemView.getContext(), colorResId);
                holder.viewConditionColor.setBackgroundTintList(android.content.res.ColorStateList.valueOf(color));
            } else {
                holder.viewConditionColor.setBackgroundTintList(android.content.res.ColorStateList.valueOf(android.graphics.Color.TRANSPARENT));
            }
        } else {
            holder.textConditionName.setText("Unknown");
        }

        if (status.getSurfaces() != null && !status.getSurfaces().isEmpty()) {
            String localizedSurfaces = FormatUtils.formatSurfaces(holder.itemView.getContext(), status.getSurfaces());
            holder.textConditionDetails.setText(holder.itemView.getContext().getString(R.string.surfaces_label, localizedSurfaces));
            holder.textConditionDetails.setVisibility(View.VISIBLE);
        } else {
            holder.textConditionDetails.setVisibility(View.GONE);
        }

        if (holder.btnAnnulCondition != null) {
            holder.btnAnnulCondition.setVisibility(View.VISIBLE);
            holder.btnAnnulCondition.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onDeleteClick(status);
                }
            });
        }
        
        if (holder.badgeAnnulled != null) {
            holder.badgeAnnulled.setVisibility(View.GONE);
        }
        
        if (holder.textEntryContext != null) {
            String formattedContext = FormatUtils.formatEntryContext(
                holder.itemView.getContext(), status.getDateRecorded(), status.getAppointmentId()
            );
            holder.textEntryContext.setText(formattedContext);
            holder.textEntryContext.setVisibility(View.VISIBLE);
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onConditionClick(status);
            }
        });
    }

    @Override
    public int getItemCount() {
        return conditionList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textToothNumber;
        View viewConditionColor;
        TextView textConditionName;
        TextView textConditionDetails;
        android.widget.ImageButton btnAnnulCondition;
        TextView textEntryContext;
        TextView badgeAnnulled;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textToothNumber = itemView.findViewById(R.id.text_tooth_number);
            viewConditionColor = itemView.findViewById(R.id.view_condition_color);
            textConditionName = itemView.findViewById(R.id.text_condition_name);
            textConditionDetails = itemView.findViewById(R.id.text_condition_details);
            btnAnnulCondition = itemView.findViewById(R.id.btn_annul_condition);
            textEntryContext = itemView.findViewById(R.id.text_entry_context);
            badgeAnnulled = itemView.findViewById(R.id.badge_annulled);
        }
    }
}
