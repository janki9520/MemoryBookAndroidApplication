package com.patel.memorybookproject;


import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;


public class ChangeFontFamilyFragment extends Fragment {

    Button btnBack, btnChangeFont;
    RadioButton rb1, rb2, rb3, rb4, rb5;
    EditText et1, et2;
    String radioToast;
    Typeface tf1, tf2, tf3, tf4, tf5;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_change_font_family, container, false);

        View vChanage = inflater.inflate(R.layout.activity_add_memories, container, false);

        //button back called here
        btnBack = (Button) v.findViewById(R.id.backToSettings);
        btnChangeFont = (Button) v.findViewById(R.id.btn_change);
        rb1 = (RadioButton) v.findViewById(R.id.rb1);
        rb2 = (RadioButton) v.findViewById(R.id.rb2);
        rb3 = (RadioButton) v.findViewById(R.id.rb3);
        rb4 = (RadioButton) v.findViewById(R.id.rb4);
        rb5 = (RadioButton) v.findViewById(R.id.rb5);
        et1 = (EditText) vChanage.findViewById(R.id.titleEditText);
        et2 = (EditText) vChanage.findViewById(R.id.descriptionEditText);

        tf1 = Typeface.createFromAsset(getActivity().getAssets(), "fonts/OpenSansCondensed-Light.ttf");
        tf2 = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Roboto-Italic.ttf");
        tf3 = Typeface.createFromAsset(getActivity().getAssets(), "fonts/VollkornSC-Regular.ttf");
        tf4 = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Dosis-Regular.ttf");
        tf5 = Typeface.createFromAsset(getActivity().getAssets(), "fonts/JosefinSans-Regular.ttf");


        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent newIntent = new Intent(getActivity(), SettingsActivity.class);
                getActivity().startActivity(newIntent);
            }
        });

        btnChangeFont.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (rb1.isChecked()) {
                    AddMemoriesActivity.overrideFont(getActivity().getApplicationContext(), "SERIF", "fonts/OpenSansCondensed-Bold.ttf");
                    MainActivity.overrideFont(getActivity().getApplicationContext(), "SERIF", "fonts/OpenSansCondensed-Bold.ttf");
                    radioToast = rb1.getText().toString();
                } else if (rb2.isChecked()) {
                    AddMemoriesActivity.overrideFont(getActivity().getApplicationContext(), "SERIF", "fonts/Roboto-Italic.ttf");
                    MainActivity.overrideFont(getActivity().getApplicationContext(), "SERIF", "fonts/OpenSansCondensed-Bold.ttf");
                    radioToast = rb2.getText().toString();
                } else if (rb3.isChecked()) {
                    AddMemoriesActivity.overrideFont(getActivity().getApplicationContext(), "SERIF", "fonts/VollkornSC-Regular.ttf");
                    MainActivity.overrideFont(getActivity().getApplicationContext(), "SERIF", "fonts/OpenSansCondensed-Bold.ttf");
                    radioToast = rb3.getText().toString();
                } else if (rb4.isChecked()) {
                    AddMemoriesActivity.overrideFont(getActivity().getApplicationContext(), "SERIF", "fonts/Dosis-Regular.ttf");
                    MainActivity.overrideFont(getActivity().getApplicationContext(), "SERIF", "fonts/OpenSansCondensed-Bold.ttf");
                    radioToast = rb4.getText().toString();
                } else if (rb5.isChecked()) {
                    AddMemoriesActivity.overrideFont(getActivity().getApplicationContext(), "SERIF", "fonts/JosefinSans-Regular.ttf");
                    MainActivity.overrideFont(getActivity().getApplicationContext(), "SERIF", "fonts/OpenSansCondensed-Bold.ttf");
                    radioToast = rb5.getText().toString();
                }
                Toast.makeText(getActivity().getApplicationContext(), "You have selected " + radioToast + ". Go to Home Screen and click on displayed memory to see the change", Toast.LENGTH_SHORT).show();
            }
        });

        return v;
    }
}

