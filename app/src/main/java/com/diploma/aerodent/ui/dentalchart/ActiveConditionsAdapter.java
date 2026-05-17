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

import java.util.ArrayList;
import java.util.List;

public class ActiveConditionsAdapter extends RecyclerView.Adapter<ActiveConditionsAdapter.ViewHolder> {
    private List<ToothStatus> statuses = new ArrayList<>();
    private final OnDeleteClickListener deleteListener;

    public interface OnDeleteClickListener {
        void onDeleteClick(ToothStatus status);
    }

    public ActiveConditionsAdapter(OnDeleteClickListener deleteListener) {
        this.deleteListener = deleteListener;
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
            details = "(" + details + ")";
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

        holder.btnDelete.setOnClickListener(v -> deleteListener.onDeleteClick(status));
    }

    @Override
    public int getItemCount() {
        return statuses.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        View viewColor;
        TextView textName, textDetails;
        View btnDelete;

        ViewHolder(View view) {
            super(view);
            viewColor = view.findViewById(R.id.view_condition_color);
            textName = view.findViewById(R.id.text_condition_name);
            textDetails = view.findViewById(R.id.text_condition_details);
            btnDelete = view.findViewById(R.id.btn_delete_condition);
        }
    }
}
