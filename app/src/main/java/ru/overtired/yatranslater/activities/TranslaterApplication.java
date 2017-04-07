package ru.overtired.yatranslater.activities;

import android.app.Application;

import com.yandex.metrica.YandexMetrica;

import ru.yandex.speechkit.SpeechKit;


public class TranslaterApplication extends Application
{
    private static final String API_YANDEX_METRICA = "d97b7089-3786-4f4d-b495-6ef05566948c";
    private static final String API_YANDEX_SPEECHKIT = "e195f985-29cb-471f-9b8d-0ae71a38bd06";

    @Override
    public void onCreate()
    {
        super.onCreate();
        YandexMetrica.activate(getApplicationContext(),API_YANDEX_METRICA);
        YandexMetrica.enableActivityAutoTracking(this);

        SpeechKit.getInstance().configure(getApplicationContext(),API_YANDEX_SPEECHKIT);
    }
}
