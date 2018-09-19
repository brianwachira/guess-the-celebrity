package com.example.brianwachira.guessthecelebrity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    Button button1,button2,button3,button4;
    ArrayList <String> celebURLs,celebnames ;
    ImageView imageView;
    String result;
    int chosenCeleb,locationOfCorrectAnswer,IncorrectAnswerLocation;
    Bitmap CelebImage;
    String [] answers = new String[4];
    Random random;


    public class ImageDownloader extends AsyncTask<String,Void,Bitmap>{


        @Override
        protected Bitmap doInBackground(String... urls) {

            try{
                URL url = new URL(urls[0]);

                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                connection.connect();

                InputStream inputStream = connection.getInputStream();

                Bitmap myBitmap = BitmapFactory.decodeStream(inputStream);

                return myBitmap;

            }catch(Exception e){

                e.printStackTrace();
            }
            return null;
        }
    }

    public class DownloadTask extends AsyncTask<String,Void,String>{
        @Override
        protected String doInBackground(String... urls) {

            String result = "";
            URL url;
            HttpURLConnection urlConnection;

            try{
                url = new URL(urls[0]);

                urlConnection = (HttpURLConnection)url.openConnection();

                InputStream in= urlConnection.getInputStream();

                InputStreamReader reader = new InputStreamReader(in);

                int data = reader.read();

                while(data!= -1){

                    char current = (char) data;

                    result += current;

                     data = reader.read();

                }
                return result;
            }catch(Exception e){

                e.printStackTrace();
                return "failed";
            }

        }
    }

    public void celebchosen(View view){

        if(view.getTag().toString().equals(Integer.toString(locationOfCorrectAnswer))){

            Toast.makeText(this, "Correct!", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(this, "Wrong! Answer :" + celebnames.get(chosenCeleb), Toast.LENGTH_SHORT).show();
        }

        createnewquestion();
    }

    public void createnewquestion()
    {

        chosenCeleb = random.nextInt(celebURLs.size());

        ImageDownloader ImageTask = new ImageDownloader();

        try {

            CelebImage =ImageTask.execute(celebURLs.get(chosenCeleb)).get();

            imageView.setImageBitmap(CelebImage);

            locationOfCorrectAnswer = random.nextInt(4);

            for (int i = 0; i < 4; i++) {

                if (i == locationOfCorrectAnswer) {

                    answers[i] = celebnames.get(chosenCeleb);
                } else {

                    IncorrectAnswerLocation = random.nextInt(celebURLs.size());

                    while (IncorrectAnswerLocation == chosenCeleb) {

                        IncorrectAnswerLocation = random.nextInt(celebURLs.size());

                    }
                    answers[i] = celebnames.get(IncorrectAnswerLocation);
                }
                button1.setText(answers[0]);
                button2.setText(answers[1]);
                button3.setText(answers[2]);
                button4.setText(answers[3]);

            }
          //  Log.i("RESULT", result);
        } catch (Exception e) {
            e.printStackTrace();

        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        celebURLs = new ArrayList<String>();
        celebnames = new ArrayList<String>();
        DownloadTask task = new DownloadTask();
        imageView = findViewById(R.id.imageView);
        button1 = findViewById(R.id.button);
        button2 = findViewById(R.id.button2);
        button3 = findViewById(R.id.button3);
        button4 = findViewById(R.id.button4);
         random = new Random();


        try{

            result = task.execute("http://www.posh24.se/kandisar").get();

            String[] splitResult = result.split("<div class=\"sidebarContainer\">");

            Pattern p = Pattern.compile("<img src=\"(.*?)\"");
            Matcher m = p.matcher(splitResult[0]);

            while(m.find()){

                celebURLs.add(m.group(1));
               // Log.i("RESULT",m.group(1));

            }

             p = Pattern.compile("alt=\"(.*?)\"");
             m = p.matcher(splitResult[0]);

            while(m.find()){

               // Log.i("RESULT",m.group(1));
            celebnames.add(m.group(1));
            }

            }catch(Exception e) {

            e.printStackTrace();
        }
        createnewquestion();
    }
}
