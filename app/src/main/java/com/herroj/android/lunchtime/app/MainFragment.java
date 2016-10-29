package com.herroj.android.lunchtime.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;

/**
 *
 */
public class MainFragment extends Fragment {

    public MainFragment() {
        // Required empty public constructor
    }

    /**
     * implementacion que se usa cuando se infla con LayoutInflater
     *
     * @param inflater           nombre de etiqueta a ser inflado
     * @param container          el contexto donde la view se crea
     * @param savedInstanceState atributos de infar especificados en un archivo XML
     * @return la view creada
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_main, container, false);

        ImageButton imgBtnRestaurant = (ImageButton) view.findViewById(R.id.botonLugares);

        imgBtnRestaurant.setOnClickListener(new AdapterView.OnClickListener() {

            @Override
            public void onClick(View view) {

                startActivity(new Intent(getContext(), TipoRestMainActivity.class));

            }

        }

        );

        // Inflate the layout for this fragment
        return view;
    }

}
