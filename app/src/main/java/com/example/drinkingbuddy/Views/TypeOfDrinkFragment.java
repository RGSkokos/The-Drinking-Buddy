package com.example.drinkingbuddy.Views;


import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.drinkingbuddy.Controllers.DBHelper;
import com.example.drinkingbuddy.R;
import androidx.fragment.app.DialogFragment;



public class TypeOfDrinkFragment extends DialogFragment {

    protected Button Submit;
    protected Button Liquor;
    protected Button Beer;
    protected Button Wine;
    String Choice;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.fragment_type_of_drink, container, false);

        Submit = view.findViewById(R.id.SubmitButton);
        Liquor = view.findViewById(R.id.LiquorButton);
        Wine = view.findViewById(R.id.WineButton);
        Beer = view.findViewById(R.id.BeerButton);

        Liquor.setBackgroundColor(Color.BLACK);
        Wine.setBackgroundColor(Color.BLACK);
        Beer.setBackgroundColor(Color.BLACK);
        Submit.setBackgroundColor(Color.BLACK);

        Submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity) getActivity()).setTypeOfDrink(Choice);
                dismiss();
            }
        });

        Liquor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Liquor.setBackgroundColor(Color.RED);
                Wine.setBackgroundColor(Color.BLACK);
                Beer.setBackgroundColor(Color.BLACK);
                Choice = "liquor";
            }
        });

        Wine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Liquor.setBackgroundColor(Color.BLACK);
                Wine.setBackgroundColor(Color.RED);
                Beer.setBackgroundColor(Color.BLACK);
                Choice = "wine";
            }
        });

        Beer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Liquor.setBackgroundColor(Color.BLACK);
                Wine.setBackgroundColor(Color.BLACK);
                Beer.setBackgroundColor(Color.RED);
                Choice = "beer";
            }
        });

        // Inflate the layout for this fragment
        return view;
    }

}