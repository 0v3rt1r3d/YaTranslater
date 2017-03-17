package ru.overtired.yatranslater;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by overtired on 17.03.17.
 */

public class SplashActivity extends AppCompatActivity
{
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        
        Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);
        finish();
    }

    //Сюда можно вставить обновление языков
}
