package com.diploma.aerodent.ui.calendar;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.diploma.aerodent.R;
import com.diploma.aerodent.data.local.entity.Appointment;
import com.diploma.aerodent.data.local.entity.Patient;
import com.diploma.aerodent.util.NameUtils;
import com.google.android.material.card.MaterialCardView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class CalendarAppointmentAdapter extends RecyclerView.Adapter<CalendarAppointmentAdapter.ViewHolder> {

    public interface OnAppointmentClickListener {
        void onAppointmentClick(Appointment appointment);
    }

    private List<Appointment> appointments = new ArrayList<>();
    private Map<Integer, Patient> patientMap = new HashMap<>();
    private SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
    private OnAppointmentClickListener listener;

    public void setOnAppointmentClickListener(OnAppointmentClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_calendar_appointment, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Appointment appointment = appointments.get(position);
        Patient patient = patientMap.get(appointment.getPatientId());

        String patientName = holder.itemView.getContext().getString(R.string.unknown_patient);
        if (patient != null) {
            patientName = NameUtils.formatFirstLastName(patient);
        }

        String timeStr = appointment.getDateTime() != null ? timeFormat.format(appointment.getDateTime()) : "00:00";
        holder.textTimePatient.setText(timeStr + " - " + patientName);
        holder.textTreatment.setText(appointment.getTreatmentType() != null ? appointment.getTreatmentType() : holder.itemView.getContext().getString(R.string.unknown));

        String status = appointment.getStatus() != null ? appointment.getStatus() : Appointment.STATUS_SCHEDULED;
        holder.textStatus.setText(status);

        if (Appointment.STATUS_COMPLETED.equals(status)) {
            holder.cardStatus.setCardBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.chip_green_bg));
            holder.textStatus.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.chip_green_text));
        } else if (Appointment.STATUS_CANCELLED.equals(status)) {
            holder.cardStatus.setCardBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.chip_red_bg));
            holder.textStatus.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.chip_red_text));
        } else { // SCHEDULED
            holder.cardStatus.setCardBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.chip_orange_bg));
            holder.textStatus.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.chip_orange_text));
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onAppointmentClick(appointment);
            }
        });
    }

    @Override
    public int getItemCount() {
        return appointments.size();
    }

    public void setAppointments(List<Appointment> appointments) {
        this.appointments = appointments;
        notifyDataSetChanged();
    }

    public void setPatients(List<Patient> patients) {
        patientMap.clear();
        if (patients != null) {
            for (Patient p : patients) {
                patientMap.put(p.getId(), p);
            }
        }
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textTimePatient;
        TextView textTreatment;
        TextView textStatus;
        MaterialCardView cardStatus;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textTimePatient = itemView.findViewById(R.id.text_time_patient);
            textTreatment = itemView.findViewById(R.id.text_treatment);
            textStatus = itemView.findViewById(R.id.text_status);
            cardStatus = itemView.findViewById(R.id.card_status);
        }
    }
}
