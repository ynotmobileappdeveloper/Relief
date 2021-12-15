package com.ynot.relief.ui.RegistrationPage;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.ynot.relief.DistributorsRegisteration;
import com.ynot.relief.DoctorRegistration;
import com.ynot.relief.LaboratoryRegistration;
import com.ynot.relief.PharamacyRegistration;
import com.ynot.relief.R;


public class RegistrationFragment extends Fragment implements View.OnClickListener {
    LinearLayout doctors, lab, pharamcy, distributors;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root=inflater.inflate(R.layout.fragment_registration, container, false);

        doctors = root.findViewById(R.id.doctors);
        lab = root.findViewById(R.id.lab);
        pharamcy = root.findViewById(R.id.pharmacy);
        distributors = root.findViewById(R.id.distributors);

        doctors.setOnClickListener(this);
        lab.setOnClickListener(this);
        pharamcy.setOnClickListener(this);
        distributors.setOnClickListener(this);



        return root;
    }

    @Override
    public void onClick(View view) {

        if (view == doctors) {
            startActivity(new Intent(getContext(), DoctorRegistration.class));
        }
        if (view == lab) {
            startActivity(new Intent(getContext(), LaboratoryRegistration.class));
        }
        if (view == pharamcy) {
            startActivity(new Intent(getContext(), PharamacyRegistration.class));
        }
        if (view == distributors) {
            startActivity(new Intent(getContext(), DistributorsRegisteration.class));
        }

    }
}