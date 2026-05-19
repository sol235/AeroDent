package com.diploma.aerodent.ui.appointments;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.diploma.aerodent.R;
import com.diploma.aerodent.data.local.entity.Patient;
import com.diploma.aerodent.util.NameUtils;

import java.util.ArrayList;
import java.util.List;

public class PatientSearchAdapter extends ArrayAdapter<Patient> {

    private List<Patient> patientListFull;

    public PatientSearchAdapter(@NonNull Context context, @NonNull List<Patient> patientList) {
        super(context, R.layout.spinner_item_custom, new ArrayList<>(patientList));
        this.patientListFull = new ArrayList<>(patientList);
    }

    @NonNull
    @Override
    public Filter getFilter() {
        return patientFilter;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        TextView view = (TextView) super.getView(position, convertView, parent);
        Patient patient = getItem(position);
        if (patient != null) {
            view.setText(NameUtils.formatFirstLastName(patient));
        }
        return view;
    }

    private Filter patientFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
            List<Patient> suggestions = (constraint == null || constraint.length() == 0)
                    ? patientListFull
                    : NameUtils.searchPatients(patientListFull, constraint.toString());
            
            results.values = suggestions;
            results.count = suggestions.size();
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            clear();
            if (results.values != null) {
                addAll((List<Patient>) results.values);
            }
            notifyDataSetChanged();
        }
        
        @Override
        public CharSequence convertResultToString(Object resultValue) {
            return NameUtils.formatFirstLastName((Patient) resultValue);
        }
    };
}
