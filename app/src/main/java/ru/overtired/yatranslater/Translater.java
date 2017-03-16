package ru.overtired.yatranslater;

//Этот класс будет заниматься загрузкой данных от сервиса
//Он должен получать список языков и выдавать его, а также переводить текст

import android.content.Context;
import android.util.Log;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.io.StringBufferInputStream;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public class Translater
{
    private static final String API_KEY = "trnsl.1.1.20170314T173731Z.9298c44b1c36e629.17d92fd0e44157b4fd53f1fa6f5a2fab7d129d6e";
    private static final String BASE_URL = "https://translate.yandex.net/api/v1.5/tr.json/";
    private static final String TAG = "Translater:";

    private interface TranslateService
    {
        @GET("getLangs?key=" + API_KEY)
        Call<String> getLanguages(@Query("ui") String ui);

        @GET("translate?key="+API_KEY)
        Call<String> getTranslation(@Query("text") String text,@Query("lang") String language);
    }

    //Вот идеально было бы вообще как-то брать локаль телефона, но видимо будет только на русском
    public List<Language> getLanguages(Context context, String ui)
    {
        List<Language> languages = new ArrayList<>();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();

        TranslateService service = retrofit.create(TranslateService.class);

        try
        {
            String responseJson = service.getLanguages(ui).execute().body();
            JsonParser parser = new JsonParser();
            JsonObject jsonObject = parser.parse(responseJson).getAsJsonObject();
            jsonObject = jsonObject.getAsJsonObject("langs");

            String[] shortages = context.getResources().getStringArray(R.array.languages);
            for(int i=0;i<shortages.length;i++)
            {
                languages.add(new Language(jsonObject.get(shortages[i]).toString().replace("\"",""),shortages[i]));
            }
        }
        catch (IOException ioe)
        {
            Log.e(TAG, ":" + ioe.getMessage());
        }

        return languages;
    }

    public String[] getTranslation(String text, String language)
    {
        String[] translations = new String[10];

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();

        TranslateService service = retrofit.create(TranslateService.class);

        try
        {
            String responseJson = service.getTranslation(text,language).execute().body();
            JsonParser parser = new JsonParser();
            JsonObject jsonObject = parser.parse(responseJson).getAsJsonObject();
            translations[0] = jsonObject.getAsJsonArray("text").get(0).toString();

        }
        catch (IOException ioe)
        {
            Log.e(TAG, ":" + ioe.getMessage());
        }

        return translations;
    }

}
