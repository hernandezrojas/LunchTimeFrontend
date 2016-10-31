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

import com.herroj.android.lunchtime.app.R;
import com.herroj.android.lunchtime.app.data.LunchTimeContract;

/**
 */
public class TipoPlatFragment extends Fragment {

    public TipoPlatFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_tipo_plat_main, container, false);

        ImageButton imgBtnPlatillos = (ImageButton) view.findViewById(R.id.botonComidas);

        imgBtnPlatillos.setOnClickListener(new AdapterView.OnClickListener() {

            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getContext(), PlatilloMainActivity.class);

                LunchTimeContract.s_filtroSeleccionado = LunchTimeContract.FILTRO_COMIDA;

                startActivity(intent);


            }

        }

        );

        ImageButton imgBtnDesayunos = (ImageButton) view.findViewById(R.id.botonDesayunos);

        imgBtnDesayunos.setOnClickListener(new AdapterView.OnClickListener() {

            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getContext(), PlatilloMainActivity.class);

                LunchTimeContract.s_filtroSeleccionado = LunchTimeContract.FILTRO_DESAYUNO;
                startActivity(intent);

            }

        }

        );

        // Inflate the layout for this fragment
        return view;
    }

}
