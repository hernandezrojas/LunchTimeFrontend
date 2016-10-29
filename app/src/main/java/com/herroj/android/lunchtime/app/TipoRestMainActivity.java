package com.herroj.android.lunchtime.app;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class TipoRestMainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tipo_rest_main);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.tipo_rest_conteiner, new TipoRestFragment())
                    .commit();

        }

    }
}
