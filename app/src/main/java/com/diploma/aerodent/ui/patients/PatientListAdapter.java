package com.diploma.aerodent.ui.patients;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.diploma.aerodent.R;
import com.diploma.aerodent.data.local.entity.Patient;

import java.util.ArrayList;
import java.util.List;

public class PatientListAdapter extends RecyclerView.Adapter<PatientListAdapter.ViewHolder> {

    public interface OnPatientClickListener {
        void onPatientClick(Patient patient);
    }

    private List<Patient> patients = new ArrayList<>();
    private OnPatientClickListener listener;

    public void setOnPatientClickListener(OnPatientClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_patient_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Patient patient = patients.get(position);

        String first = patient.getFirstName() != null ? patient.getFirstName() : "";
        String last = patient.getLastName() != null ? patient.getLastName() : "";
        String patientName = first + " " + last;
        
        String initials = "?";
        if (!first.isEmpty() && !last.isEmpty()) {
            initials = first.substring(0, 1) + last.substring(0, 1);
        } else if (!first.isEmpty()) {
            initials = first.substring(0, 1);
        }

        holder.textInitials.setText(initials.toUpperCase());
        holder.textPatientName.setText(patientName.trim());

        String phone = patient.getPhoneNumber() != null ? patient.getPhoneNumber() : "No phone number";
        holder.textPatientPhone.setText(phone);

        holder.textStatus.setText("NZOK");

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onPatientClick(patient);
            }
        });
    }

    @Override
    public int getItemCount() {
        return patients.size();
    }

    public void setPatients(List<Patient> patients) {
        this.patients = patients;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textInitials;
        TextView textPatientName;
        TextView textPatientPhone;
        TextView textStatus;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textInitials = itemView.findViewById(R.id.text_avatar_initials);
            textPatientName = itemView.findViewById(R.id.text_patient_name);
            textPatientPhone = itemView.findViewById(R.id.text_patient_phone);
            textStatus = itemView.findViewById(R.id.text_status);
        }
    }
}
