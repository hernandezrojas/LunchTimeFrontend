package com.herroj.android.lunchtime.app;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
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
public class TipoRestFragment extends Fragment {


    public TipoRestFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_tipo_rest_main, container, false);

        ImageButton imgBtnCafeterias = (ImageButton) view.findViewById(R.id.botonCafeterias);

        imgBtnCafeterias.setOnClickListener(new AdapterView.OnClickListener() {

            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getContext(), RestaurantMainActivity.class);
                intent.putExtra("FiltroSeleccionado", 1);

                startActivity(intent);


            }

        }

        );

        ImageButton imgBtnPuestos = (ImageButton) view.findViewById(R.id.botonPuestos);

        imgBtnPuestos.setOnClickListener(new AdapterView.OnClickListener() {

            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getContext(), RestaurantMainActivity.class);
                intent.putExtra("FiltroSeleccionado", 2);

                startActivity(intent);

            }

        }

        );

        // Inflate the layout for this fragment
        return view;
    }
}
