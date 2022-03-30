package com.example.drinkingbuddy.Views;


import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.drinkingbuddy.R;


public class TypeOfDrinkFragment extends DialogFragment {

    //instance variables
    protected Button Submit;
    protected Button Liquor;
    protected Button Beer;
    protected Button Wine;
    protected ImageButton CancelButton;
    String Choice;

//region FragmentMethods
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.fragment_type_of_drink, container, false);

        initializeComponents(view);

        SetupButtonListeners();


        // Inflate the layout for this fragment
        return view;
    }
//endregion

//region Helper Methods
    // Link Variables to Components in .XML file
    protected void initializeComponents(View view) {
        Submit = view.findViewById(R.id.SubmitButton);
        Liquor = view.findViewById(R.id.LiquorButton);
        Wine = view.findViewById(R.id.WineButton);
        Beer = view.findViewById(R.id.BeerButton);
        CancelButton = view.findViewById(R.id.CancelButton);

        Liquor.setBackgroundColor(Color.WHITE);
        Wine.setBackgroundColor(Color.WHITE);
        Beer.setBackgroundColor(Color.WHITE);
        Submit.setBackgroundColor(Color.GREEN);
    }

    //setup all button listeners
    protected void SetupButtonListeners() {
     //each button is set up to change to red and change the rest to white if clicked
     //Choice string is also updated with the selected button
     //drink choice is set once submit button is clicked

        CancelButton.setOnClickListener(view -> dismiss());

        Submit.setOnClickListener(view -> {
            //set variable for drink type in home page
            ((HomePage) requireActivity()).setTypeOfDrink(Choice);
            dismiss();
        });

        Liquor.setOnClickListener(view -> {
            Liquor.setBackgroundColor(Color.RED);
            Wine.setBackgroundColor(Color.WHITE);
            Beer.setBackgroundColor(Color.WHITE);
            Choice = "Liquor";
        });

        Wine.setOnClickListener(view -> {
            Liquor.setBackgroundColor(Color.WHITE);
            Wine.setBackgroundColor(Color.RED);
            Beer.setBackgroundColor(Color.WHITE);
            Choice = "Wine";
        });

        Beer.setOnClickListener(view -> {
            Liquor.setBackgroundColor(Color.WHITE);
            Wine.setBackgroundColor(Color.WHITE);
            Beer.setBackgroundColor(Color.RED);
            Choice = "Beer";
        });
    }
//endregion
}