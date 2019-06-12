package com.rakoon.restaurant;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class LanguageActivity extends AppCompatActivity {

    Unbinder unbinder;

    @BindView(R.id.english)
    TextView english;
    @BindView(R.id.arabic)
    TextView arabic;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_language);
        unbinder = ButterKnife.bind(this);

        SharedPreferences s1 = getSharedPreferences("userdata", MODE_PRIVATE);
        if (s1.getString("lang","").equalsIgnoreCase(getResources().getString(R.string.english)) ||
                s1.getString("lang","").equalsIgnoreCase(getResources().getString(R.string.english)))
        {
            if (new AllPath().isUserLogedin(this))
            {
                Intent in = new Intent(LanguageActivity.this, DashBoardActivity2.class);
                startActivity(in);
                finish();
            }
        }
        english.setOnClickListener((v) -> {
            SharedPreferences sp = getSharedPreferences("userdata", MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            editor.putString("lang", getResources().getString(R.string.english));
            editor.commit();
           // Intent in = new Intent(LanguageActivity.this, MainActivity.class);
            Intent in = new Intent(LanguageActivity.this, SliderActivity.class);
            startActivity(in);
            finish();

        });

        arabic.setOnClickListener((v) -> {
            SharedPreferences sp = getSharedPreferences("userdata", MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            editor.putString("lang", getResources().getString(R.string.arabic));
            editor.commit();
            //Intent in = new Intent(LanguageActivity.this, MainActivity.class);
            Intent in = new Intent(LanguageActivity.this, SliderActivity.class);
            startActivity(in);
            finish();
        });
    }
}
