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
import ru.overtired.yatranslater.structure.dictionary.Dictionary;
import ru.overtired.yatranslater.structure.Language;
import ru.overtired.yatranslater.structure.dictionary.Example;
import ru.overtired.yatranslater.structure.dictionary.Translation;

public class Translater
{
    private static final String API_KEY_TRANSLATER = "trnsl.1.1.20170314T173731Z.9298c44b1c36e629.17d92fd0e44157b4fd53f1fa6f5a2fab7d129d6e";
    private static final String API_KEY_DICTIONARY = "dict.1.1.20170317T164034Z.0705554d1ce95305.5b492a8e06d1d8b7a37e1add26ef4c843e7f9ac3";

    private static final String BASE_URL_TRANSLATER = "https://translate.yandex.net/api/v1.5/tr.json/";
    private static final String BASE_URL_DICTIONARY = "https://dictionary.yandex.net/api/v1/dicservice.json/";
    private static final String TAG = "Translater:";

    private interface TranslateService
    {
        @GET("getLangs?key=" + API_KEY_TRANSLATER)
        Call<String> getLanguages(@Query("ui") String ui);

        @GET("translate?key=" + API_KEY_TRANSLATER)
        Call<String> getTranslation(@Query("text") String text, @Query("lang") String language);

        @GET("lookup?key=" + API_KEY_DICTIONARY + "&lang=en-ru&text=time")
        Call<String> getFromDictionary(@Query("text") String text, @Query("lang") String direction);
    }

    public static List<String> getDirections(String jsonResponse)
    {
        List<String> directions = new ArrayList<>();

        JsonParser parser = new JsonParser();

        JsonArray jsonArray = parser.parse(jsonResponse).getAsJsonObject().getAsJsonArray("dirs");
        for (int i = 0; i < jsonArray.size(); i++)
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
            languages.add(new Language(shortage, jsonObject.get(shortage).getAsString()));
        }

        return languages;
    }

    public String getLangAndDirResponse(String ui)
    {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL_TRANSLATER)
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

    public String getTranslation(String text, String language)
    {
        String translation = "";

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL_TRANSLATER)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();

        TranslateService service = retrofit.create(TranslateService.class);

        try
        {
            String responseJson = service.getTranslation(text, language).execute().body();
            JsonParser parser = new JsonParser();
            JsonObject jsonObject = parser.parse(responseJson).getAsJsonObject();
            translation = jsonObject.getAsJsonArray("text").get(0).getAsString();
        }
        catch (IOException ioe)
        {
            Log.e(TAG, ":" + ioe.getMessage());
        }

        return translation;
    }

    public Dictionary getDictionary(Context context, String text, String direction)
    {
        Dictionary dictionary = new Dictionary(null,null,null);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL_DICTIONARY)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();

        TranslateService service = retrofit.create(TranslateService.class);

        String response = "";

        try
        {
            response = service.getFromDictionary(text, direction).execute().body();
            JsonArray jDefinitions = new JsonParser()
                    .parse(response)
                    .getAsJsonObject()
                    .getAsJsonArray("def");

            if(jDefinitions.size() == 0)
            {
//                В случае, если в словаре ничего не найдено
                return null;
            }

            if(jDefinitions.get(0).getAsJsonObject().has("ts"))
            {
                dictionary = new Dictionary(text, jDefinitions.get(0).getAsJsonObject().get("ts").getAsString(), direction);
            }else
            {
                dictionary = new Dictionary(text, context.getString(R.string.no_transcription), direction);
            }

            Log.d(TAG, response);

//            Перебор дефиниций
            for (int i = 0; i < jDefinitions.size(); i++)
            {
                JsonObject jDefinition = jDefinitions.get(i).getAsJsonObject();
                JsonArray jTranslations = jDefinition.get("tr").getAsJsonArray();

//                Перебор переводов
                for (int j = 0; j < jTranslations.size(); j++)
                {
                    JsonObject jTranslation = jTranslations.get(j).getAsJsonObject();
                    Translation translation = new Translation(jTranslation
                            .get("text").getAsString());
//                    Добавление синонимов, если они есть
                    if (jTranslation.has("syn"))
                    {
                        JsonArray jSynonyms = jTranslation.getAsJsonArray("syn");
                        for (int k = 0; k < jSynonyms.size(); k++)
                        {
                            translation.addSynonym(jSynonyms.get(k).getAsJsonObject()
                                    .get("text")
                                    .getAsString());
                        }
                    }
//                    Добавление значений, если есть
                    if (jTranslation.has("mean"))
                    {
                        JsonArray jMean = jTranslation.getAsJsonArray("mean");
                        for (int k = 0; k < jMean.size(); k++)
                        {
                            translation.addMean(jMean.get(k).getAsJsonObject()
                                    .get("text")
                                    .getAsString());
                        }
                    }
//                    Добавление примеров, если они есть
                    if (jTranslation.has("ex"))
                    {
                        JsonArray jExamples = jTranslation.getAsJsonArray("ex");
                        for (int k = 0; k < jExamples.size(); k++)
                        {
                            JsonObject jExample = jExamples.get(k).getAsJsonObject();
                            translation.addExample(new Example(
                                    jExample.get("text").getAsString(),
                                    jExample.getAsJsonArray("tr").get(0)
                                            .getAsJsonObject()
                                            .get("text")
                                            .getAsString()
                            ));
                        }
                    }

                    dictionary.addTranslation(translation);
                }
            }
        }
        catch (IOException ioe)
        {
            Log.e(TAG, ":" + ioe.getMessage());
        }

        return dictionary;
    }
}
