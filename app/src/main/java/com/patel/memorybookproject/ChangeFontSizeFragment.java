package com.patel.memorybookproject;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;


public class ChangeFontSizeFragment extends Fragment {

    Button btnBack, btnChangeFontSize;
    RadioButton rb1, rb2, rb3;
    EditText et1, et2;
    String radioToast;


    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_change_font_size, container, false);
        View vChange = inflater.inflate(R.layout.activity_add_memories, container, false);
        //button back called here
        btnBack = (Button) v.findViewById(R.id.backToSettings);
        btnChangeFontSize = (Button) v.findViewById(R.id.btn_changeSize);
        rb1 = (RadioButton) v.findViewById(R.id.rb1);
        rb2 = (RadioButton) v.findViewById(R.id.rb2);
        rb3 = (RadioButton) v.findViewById(R.id.rb3);

        et1 = (EditText) vChange.findViewById(R.id.titleEditText);
        et2 = (EditText) vChange.findViewById(R.id.descriptionEditText);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent newIntent = new Intent(getActivity(), SettingsActivity.class);
                getActivity().startActivity(newIntent);
            }
        });

        
        btnChangeFontSize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (rb1.isChecked()) {
                    radioToast = rb1.getText().toString();
                    AddMemoriesActivity.overrideFontSize(getActivity().getApplicationContext(), "18", "15");
                    MainActivity.overrideFontSize(getActivity().getApplicationContext(), "18", "15");
                } else if (rb2.isChecked()) {
                    radioToast = rb2.getText().toString();
                    AddMemoriesActivity.overrideFontSize(getActivity().getApplicationContext(), "18", "22");
                    MainActivity.overrideFontSize(getActivity().getApplicationContext(), "18", "15");
                } else if (rb3.isChecked()) {
                    radioToast = rb3.getText().toString();
                    AddMemoriesActivity.overrideFontSize(getActivity().getApplicationContext(), "18", "29");
                    MainActivity.overrideFontSize(getActivity().getApplicationContext(), "18", "15");
                }
                Toast.makeText(getActivity().getApplicationContext(), "You have selected " + radioToast + ". Go to Home Screen and click on displayed memory to see the change", Toast.LENGTH_SHORT).show();
            }
        });

        return v;
    }
}

