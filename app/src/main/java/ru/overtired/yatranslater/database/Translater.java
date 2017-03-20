package ru.overtired.yatranslater.database;

//Этот класс будет заниматься загрузкой данных от сервиса
//Он должен получать список языков и выдавать его, а также переводить текст

import android.content.Context;
import android.util.Log;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;
import ru.overtired.yatranslater.R;
import ru.overtired.yatranslater.structure.Language;

public class Translater
{
    private static final String API_KEY = "trnsl.1.1.20170314T173731Z.9298c44b1c36e629.17d92fd0e44157b4fd53f1fa6f5a2fab7d129d6e";
    private static final String BASE_URL = "https://translate.yandex.net/api/v1.5/tr.json/";
    private static final String TAG = "Translater:";

    private interface TranslateService
    {
        @GET("getLangs?key=" + API_KEY)
        Call<String> getLanguages(@Query("ui") String ui);

        @GET("translate?key=" + API_KEY)
        Call<String> getTranslation(@Query("text") String text, @Query("lang") String language);
    }

    public static List<String> getDirections(Context context, String jsonResponse)
    {
        List<String> directions = new ArrayList<>();

        JsonParser parser = new JsonParser();

        JsonArray jsonArray = parser.parse(jsonResponse).getAsJsonObject().getAsJsonArray("dirs");
        for(int i = 0;i<jsonArray.size();i++)
        {
            directions.add(jsonArray.get(i).getAsString());
        }

        return directions;
    }

    public static List<Language> getLanguages(Context context, String jsonResponse)
    {
        List<Language> languages = new ArrayList<>();

        JsonParser parser = new JsonParser();

        JsonObject jsonObject = parser.parse(jsonResponse).getAsJsonObject();
        jsonObject = jsonObject.getAsJsonObject("langs");

        String[] shortages = context.getResources().getStringArray(R.array.languages);
        for (String shortage : shortages)
        {
            languages.add(new Language(shortage,jsonObject.get(shortage).getAsString()));
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
            String responseJson = service.getTranslation(text, language).execute().body();
            JsonParser parser = new JsonParser();
            JsonObject jsonObject = parser.parse(responseJson).getAsJsonObject();
            translations[0] = jsonObject.getAsJsonArray("text").get(0).getAsString();
        }
        catch (IOException ioe)
        {
            Log.e(TAG, ":" + ioe.getMessage());
        }

        return translations;
    }

    public String getLangAndDirResponse(String ui)
    {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();

        TranslateService service = retrofit.create(TranslateService.class);

        String response = "";

        try
        {
            response = service.getLanguages(ui).execute().body();
        }
        catch (IOException ioe)
        {
            Log.e(TAG, ":" + ioe.getMessage());
        }

        return response;
    }
}
