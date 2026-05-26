package com.diploma.aerodent.ui.dentalchart;

import android.content.res.ColorStateList;
import android.graphics.Color;
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
import java.util.List;

public class ActiveConditionsAdapter extends RecyclerView.Adapter<ActiveConditionsAdapter.ViewHolder> {
    private List<ToothStatus> statuses = new ArrayList<>();
    private final OnConditionInteractionListener listener;

    public interface OnConditionInteractionListener {
        void onDeleteClick(ToothStatus status);
        void onConditionClick(ToothStatus status);
    }

    public ActiveConditionsAdapter(OnConditionInteractionListener listener) {
        this.listener = listener;
    }

    public void setStatuses(List<ToothStatus> statuses) {
        this.statuses = statuses;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_active_condition, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ToothStatus status = statuses.get(position);
        DentalCondition condition = status.getCondition();
        
        holder.textName.setText(condition.getDisplayName(holder.itemView.getContext()));
        String details = status.getSurfaces();
        if (details != null && !details.isEmpty()) {
            details = "(" + FormatUtils.formatSurfaces(holder.itemView.getContext(), details) + ")";
        } else {
            details = "";
        }
        holder.textDetails.setText(details);

        int colorResId = condition.getColorResId();
        if (colorResId != 0) {
            holder.viewColor.setBackgroundTintList(ColorStateList.valueOf(holder.itemView.getContext().getColor(colorResId)));
            holder.viewColor.setVisibility(View.VISIBLE);
        } else {
            holder.viewColor.setVisibility(View.GONE);
        }

        if (holder.textEntryContext != null) {
            String formattedContext = FormatUtils.formatEntryContext(
                holder.itemView.getContext(), status.getDateRecorded(), status.getAppointmentId(), status.getCreatorName()
            );
            holder.textEntryContext.setText(formattedContext);
            holder.textEntryContext.setVisibility(View.VISIBLE);
        }

        holder.btnDelete.setOnClickListener(v -> listener.onDeleteClick(status));
        holder.itemView.setOnClickListener(v -> listener.onConditionClick(status));
    }

    @Override
    public int getItemCount() {
        return statuses.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        View viewColor;
        TextView textName, textDetails, textEntryContext;
        View btnDelete;

        ViewHolder(View view) {
            super(view);
            viewColor = view.findViewById(R.id.view_condition_color);
            textName = view.findViewById(R.id.text_condition_name);
            textDetails = view.findViewById(R.id.text_condition_details);
            textEntryContext = view.findViewById(R.id.text_entry_context);
            btnDelete = view.findViewById(R.id.btn_delete_condition);
        }
    }
}
