package com.diploma.aerodent.ui.dentalchart;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.diploma.aerodent.R;
import com.diploma.aerodent.data.local.entity.ProcedureLog;
import com.diploma.aerodent.data.local.model.DentalCondition;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;


public class ProcedureLogAdapter extends RecyclerView.Adapter<ProcedureLogAdapter.ViewHolder> {

    public interface OnProcedureLogInteractionListener {
        void onAnnulClick(ProcedureLog log);
        void onLogClick(ProcedureLog log);
    }

    private List<ProcedureLog> procedureLogList = new ArrayList<>();
    private final OnProcedureLogInteractionListener listener;

    public ProcedureLogAdapter(OnProcedureLogInteractionListener listener) {
        this.listener = listener;
    }

    public void setProcedureLogs(List<ProcedureLog> logs) {
        List<ProcedureLog> sorted = new ArrayList<>(logs);
        sorted.sort(Comparator.comparing(ProcedureLog::getDateLogged).reversed());
        this.procedureLogList = sorted;
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
        ProcedureLog log = procedureLogList.get(position);

        holder.textToothNumber.setText(String.valueOf(log.getToothNumber()));
        holder.textConditionName.setText(log.getDiagnosis());

        if (log.getSurfaces() != null && !log.getSurfaces().isEmpty()) {
            holder.textConditionDetails.setText(holder.itemView.getContext().getString(R.string.surfaces_label, log.getSurfaces()));
            holder.textConditionDetails.setVisibility(View.VISIBLE);
        } else if (log.getNotes() != null && !log.getNotes().isEmpty()) {
            holder.textConditionDetails.setText(log.getNotes());
            holder.textConditionDetails.setVisibility(View.VISIBLE);
        } else {
            holder.textConditionDetails.setVisibility(View.GONE);
        }

        DentalCondition condition = DentalCondition.HEALTHY;
        try {
            if (log.getActionTaken() != null && !log.getActionTaken().isEmpty()) {
                condition = DentalCondition.valueOf(log.getActionTaken());
            }
        } catch (IllegalArgumentException e) {
        }

        int colorResId = condition.getColorResId();
        if (colorResId != 0) {
            int color = ContextCompat.getColor(holder.itemView.getContext(), colorResId);
            holder.viewConditionColor.setBackgroundTintList(ColorStateList.valueOf(color));
        } else {
            holder.viewConditionColor.setBackgroundTintList(ColorStateList.valueOf(Color.GRAY));
        }

        if (holder.textEntryContext != null) {
            String formattedContext = com.diploma.aerodent.util.FormatUtils.formatEntryContext(
                holder.itemView.getContext(), log.getDateLogged(), log.getAppointmentId()
            );
            holder.textEntryContext.setText(formattedContext);
            holder.textEntryContext.setVisibility(View.VISIBLE);
        }

        if (log.isAnnulled()) {
            holder.badgeAnnulled.setVisibility(View.VISIBLE);
            holder.btnAnnulCondition.setVisibility(View.GONE);
            holder.textConditionName.setPaintFlags(holder.textConditionName.getPaintFlags() | android.graphics.Paint.STRIKE_THRU_TEXT_FLAG);
            holder.itemView.setAlpha(0.6f);
        } else {
            holder.badgeAnnulled.setVisibility(View.GONE);
            holder.btnAnnulCondition.setVisibility(View.VISIBLE);
            holder.textConditionName.setPaintFlags(holder.textConditionName.getPaintFlags() & (~android.graphics.Paint.STRIKE_THRU_TEXT_FLAG));
            holder.itemView.setAlpha(1.0f);
        }

        holder.btnAnnulCondition.setOnClickListener(v -> {
            if (listener != null) {
                listener.onAnnulClick(log);
            }
        });

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onLogClick(log);
            }
        });
    }

    @Override
    public int getItemCount() {
        return procedureLogList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textToothNumber;
        View viewConditionColor;
        TextView textConditionName;
        TextView textConditionDetails;
        TextView textEntryContext;
        TextView badgeAnnulled;
        android.widget.ImageButton btnAnnulCondition;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textToothNumber = itemView.findViewById(R.id.text_tooth_number);
            viewConditionColor = itemView.findViewById(R.id.view_condition_color);
            textConditionName = itemView.findViewById(R.id.text_condition_name);
            textConditionDetails = itemView.findViewById(R.id.text_condition_details);
            textEntryContext = itemView.findViewById(R.id.text_entry_context);
            badgeAnnulled = itemView.findViewById(R.id.badge_annulled);
            btnAnnulCondition = itemView.findViewById(R.id.btn_annul_condition);
        }
    }
}
