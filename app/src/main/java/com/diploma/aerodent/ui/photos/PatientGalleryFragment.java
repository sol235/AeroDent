package com.diploma.aerodent.ui.photos;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.diploma.aerodent.R;
import com.diploma.aerodent.data.local.entity.Photo;

public class PatientGalleryFragment extends Fragment {

    private static final String ARG_PATIENT_ID = "patient_id";
    private int patientId;
    private PhotoViewModel photoViewModel;
    private PhotoAdapter adapter;
    private RecyclerView recyclerView;

    public static PatientGalleryFragment newInstance(int patientId) {
        PatientGalleryFragment fragment = new PatientGalleryFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PATIENT_ID, patientId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            patientId = getArguments().getInt(ARG_PATIENT_ID);
        }
        
        photoViewModel = new ViewModelProvider(requireActivity()).get(PhotoViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_patient_gallery, container, false);

        recyclerView = root.findViewById(R.id.recycler_patient_gallery);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
        
        adapter = new PhotoAdapter();
        adapter.setOnPhotoClickListener(this::openFullScreen);
        recyclerView.setAdapter(adapter);

        photoViewModel.getPhotosForPatient(patientId).observe(getViewLifecycleOwner(), photos -> {
            if (photos != null) {
                adapter.setPhotos(photos);
            }
        });

        return root;
    }

    private void openFullScreen(Photo photo) {
        FullScreenPhotoDialogFragment dialog = FullScreenPhotoDialogFragment.newInstance(photo.getId());
        dialog.show(getChildFragmentManager(), "FullScreenPhoto");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        recyclerView = null;
    }
}
