package com.diploma.aerodent.ui.home;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.diploma.aerodent.R;
import com.diploma.aerodent.data.local.entity.Appointment;
import com.diploma.aerodent.data.local.entity.Patient;



public class HomeAppointmentAdapter extends RecyclerView.Adapter<HomeAppointmentAdapter.ViewHolder> {

    private List<Appointment> appointments = new ArrayList<>();
    private Map<Integer, Patient> patientMap = new HashMap<>();
    private SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_home_appointment, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Appointment appointment = appointments.get(position);
        Patient patient = patientMap.get(appointment.getPatientId());

        String patientName = "Unknown Patient";
        String initials = "?";

        if (patient != null) {
            String first = patient.getFirstName() != null ? patient.getFirstName() : "";
            String last = patient.getLastName() != null ? patient.getLastName() : "";
            patientName = first + " " + last;
            
            if (!first.isEmpty() && !last.isEmpty()) {
                initials = first.substring(0, 1) + last.substring(0, 1);
            } else if (!first.isEmpty()) {
                initials = first.substring(0, 1);
            }
        }

        holder.textInitials.setText(initials.toUpperCase());
        holder.textPatientName.setText(patientName.trim());

        String timeStr = appointment.getDateTime() != null ? timeFormat.format(appointment.getDateTime()) : "00:00";
        String treatmentStr = appointment.getTreatmentType() != null ? appointment.getTreatmentType() : "Unknown";
        holder.textTimeTreatment.setText(timeStr + " — " + treatmentStr);

        String status = appointment.getStatus() != null ? appointment.getStatus() : "SCHEDULED";
        holder.textStatus.setText(status);

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
        TextView textInitials;
        TextView textPatientName;
        TextView textTimeTreatment;
        TextView textStatus;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textInitials = itemView.findViewById(R.id.text_avatar_initials);
            textPatientName = itemView.findViewById(R.id.text_patient_name);
            textTimeTreatment = itemView.findViewById(R.id.text_time_treatment);
            textStatus = itemView.findViewById(R.id.text_status);
        }
    }
}
