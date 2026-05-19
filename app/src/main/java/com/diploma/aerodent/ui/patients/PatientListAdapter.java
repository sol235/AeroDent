package com.diploma.aerodent.ui.patients;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.diploma.aerodent.R;
import com.diploma.aerodent.data.local.entity.Patient;
import com.diploma.aerodent.util.NameUtils;

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

        holder.textInitials.setText(NameUtils.getInitials(patient));
        holder.textPatientName.setText(NameUtils.formatFullName(patient));

        String phone = patient.getPhoneNumber();
        if (phone != null && !phone.isEmpty()) {
            holder.textPatientPhone.setVisibility(View.VISIBLE);
            holder.textPatientPhone.setText(holder.itemView.getContext().getString(R.string.patient_phone_format, phone));
        } else {
            holder.textPatientPhone.setVisibility(View.GONE);
        }

        holder.textStatus.setText(R.string.nzok_short);

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
