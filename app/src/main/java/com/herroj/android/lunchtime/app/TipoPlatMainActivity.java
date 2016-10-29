package com.herroj.android.lunchtime.app;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class TipoPlatMainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tipo_plat_main);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.tipo_plat_conteiner, new TipoPlatFragment())
                    .commit();

        }
    }
}
