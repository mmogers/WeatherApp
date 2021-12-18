package lv.example.weatherapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.content.AsyncTaskLoader;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    //polya
    //pole dlya vvoda texta
    private EditText user_field;
    private Button main_btn;
    private TextView result_info;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //ustanovit'znacheniya k polyam
        user_field = findViewById(R.id.user_field);
        main_btn = findViewById(R.id.main_btn);
        result_info = findViewById(R.id.result_info);

        //pri nazhatii na knopka dolzhno otobrazhatšya sobytiya, sozdat'obrabotchik sobytiya
        //vydelyaem pamyat'
        main_btn.setOnClickListener(view -> { //etot method budet srabatyvat'kazhdyj raz pri nazhatii na knopry main-btn
            if(user_field.getText().toString().trim().equals("")){ //esli poluchaem pustuyu stroku, hotim vsplyvajuschee okno- vvedite gorod, text preobrazujem v string, trim - bez probelov
                Toast.makeText(MainActivity.this, R.string.no_user_input, Toast.LENGTH_LONG).show();//toast- vsplyvajuschee okno, MainActivity.this- v etom okne budet vsplyvat'okno, text etogo okna, tretij parametr - skolķo milisekund budet podskazka u nas okolo 3 sec -length-long
            }else {
                String city = user_field.getText().toString();
                String key = "812bcbad886cc0d75da74fada0b58aef";
                String url = "https://api.openweathermap.org/data/2.5/weather?q=" + city + "&appid=" + key + "&units=metric";

                new GetUrlData().execute(url);
            }
        });

    }

    //schityvat'dannye, nado otpravit'zapros po url , schityvat'json, perebirat'ego i vyvodit

    //zapros po url addressu
    //kogda budet prilozhenie, budem parallelņo schityvat'dannye s url addressa
    private class GetUrlData extends AsyncTask<String, String, String> {

        //kogda otpravlyaem dannye po url budet srabatyvat'etot method

        protected void onPreExecute(){
            super.onPreExecute();
            result_info.setText(R.string.waiting); //kogda tolķo rolķo otpravlyaem zapros
        }

        @Override
        protected String doInBackground(String... strings) { //neogranichennoe kollvo strings
            HttpURLConnection connection = null;
            BufferedReader reader = null;
            try {
                //sozdali object na osnove kotorogo smozhem obraxchatšya po url addressu
                URL url = new URL(strings[0]); //1 element
                connection = (HttpURLConnection) url.openConnection();
                connection.connect(); //otkryli soedinenie

                InputStream stream = connection.getInputStream();//Input stream class kotoryj pozvolyaet schityvat'dannye iz potoka
                reader = new BufferedReader(new InputStreamReader(stream));

                StringBuilder buffer = new StringBuilder();//stroka s metodami buffera
                String line;

                while ((line = reader.readLine()) != null) { //poka est' linii chto mozhno schityvat'
                    buffer.append(line).append("\n"); //k stroke dobavlyaem kazhdyj raz esche odny liniju i perevod na novujy stroku
                }
                return buffer.toString();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (connection != null){
                    connection.disconnect();
                }
                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
            return null;
        }

            @SuppressLint("SetTextI18n")
            @Override
        protected void onPostExecute(String result){
            super.onPostExecute(result);

                try {
                    JSONObject jsonObject = new JSONObject(result);
                    result_info.setText("Temperature" + jsonObject.getJSONObject("main").getDouble("temp") +
                            "\nFeels like: " + jsonObject.getJSONObject("main").getDouble("feels_like") +
                             "\nHumidity: " + jsonObject.getJSONObject("main").getDouble("humidity") +
                            "\nWind speed: " + jsonObject.getJSONObject("wind").getDouble("speed") +
                            "\nClouds: " + jsonObject.getJSONObject("clouds").getDouble("all"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

        }
    }

}