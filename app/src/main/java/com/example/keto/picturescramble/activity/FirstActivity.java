package com.example.keto.picturescramble.activity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Looper;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.keto.picturescramble.ImageGuessListener;
import com.example.keto.picturescramble.R;
import com.example.keto.picturescramble.adapter.FlickerImageAdapter;
import com.example.keto.picturescramble.api.FlickrAPI;
import com.example.keto.picturescramble.api.FlickrResponseInterceptor;
import com.example.keto.picturescramble.model.FlickrModel;
import com.example.keto.picturescramble.util.GridSpacingItemDecoration;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.Random;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.example.keto.picturescramble.util.IConstants.BASE_URL;
import static com.example.keto.picturescramble.util.IConstants.MAX_IMAGES;

public class FirstActivity extends AppCompatActivity  implements Callback<FlickrModel>, ImageGuessListener {

    public final String TAG = getClass().getCanonicalName();

    private FlickrAPI flickrAPI;
    private Retrofit retrofit;
    private FlickrModel.Item randomItem;
    ArrayList<FlickrModel.Item> list;
    ArrayList<FlickrModel.Item> originalList;
    ArrayList<FlickrModel.Item> updatingList;
    ArrayList<Integer> randomIndicesList;
    private int randomIndex;
    private int correctGuessCount;
    private int max_count;

    private RecyclerView recyclerView;
    private ImageView randomImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);
        initViews();
        initializeAPI();
        callAPI();
    }

    private void initViews(){
        recyclerView = (RecyclerView) findViewById(R.id.images_recyclerview);
        randomImage = (ImageView) findViewById(R.id.random_image_view);
        recyclerView.setLayoutManager(new GridLayoutManager(this,3));
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(3, 20, true, 0));


        initializeUpdatingList();
    }

    private void initializeAPI(){

        OkHttpClient.Builder client = new OkHttpClient.Builder();
        client.addInterceptor(new FlickrResponseInterceptor());

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(client.build())
                .build();
    }

    private void callAPI(){
        flickrAPI = retrofit.create(FlickrAPI.class);

        Call<FlickrModel> productListModelCall = flickrAPI.getImagesList("json");
        productListModelCall.enqueue(this);
    }

    @Override
    public void onResponse(Call<FlickrModel> call, Response<FlickrModel> response) {

        if(response.isSuccessful() && response.body() != null
                && response.body().getItems() != null) {
            originalList = (ArrayList<FlickrModel.Item>) response.body().getItems();

            list = originalList;

            FlickerImageAdapter flickerImageAdapter = new FlickerImageAdapter(this,originalList, false);
            recyclerView.setAdapter(flickerImageAdapter);

            new android.os.Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    flipAllImages();
                    showRandomImage();
                }
            }, 15000);
        }
    }

    private void initializeUpdatingList(){
        updatingList = new ArrayList<>(MAX_IMAGES);
        for (int i = 0; i < MAX_IMAGES;i++){
            updatingList.add(null);
        }

        randomIndicesList = new ArrayList<>(MAX_IMAGES);
        for (int i = 0; i < MAX_IMAGES;i++){
            randomIndicesList.add(i);
        }

        max_count = MAX_IMAGES;
        correctGuessCount= 0;
        randomImage.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.no_image));
    }

    public void flipAllImages(){
        initializeUpdatingList();
        FlickerImageAdapter flickerImageAdapter = new FlickerImageAdapter(this,updatingList, false);
        recyclerView.setAdapter(flickerImageAdapter);
    }

    public void showRandomImage(){
        if(max_count > 0) {
            Random random = new Random();
            int index = random.nextInt(max_count);
            randomIndex = randomIndicesList.get(index);
            Log.d(TAG, randomIndex + " randomIndex");
            randomItem = originalList.get(randomIndex);

            Glide.with(this)
                    .load(list.get(randomIndex).getMedia().getM())
                    .into(randomImage);

            randomIndicesList.remove(Integer.valueOf(randomIndex));
            max_count--;
        }
    }

    @Override
    public void onFailure(Call<FlickrModel> call, Throwable t) {
        Log.d(TAG, t.getMessage());

    }

    public void onNewGameClickListener(View view){
        callAPI();
        initializeUpdatingList();
    }

    @Override
    public void onImageGuessed(int position) {

        if(randomItem.getMedia().getM().equalsIgnoreCase(originalList.get(position).getMedia().getM())){
            correctGuessCount++;

            //Right Guess, Show particular image
            updatingList.remove(position);
            updatingList.add(position, randomItem);
            FlickerImageAdapter flickerImageAdapter = new FlickerImageAdapter(this, updatingList, false);
            recyclerView.setAdapter(flickerImageAdapter);

            //Show next random image
            showRandomImage();

            if(correctGuessCount == MAX_IMAGES){
                //Game completed.
                gameCompletedSetUp();
            }
        }
    }

    private void gameCompletedSetUp(){

        new AlertDialog.Builder(this)
                .setCancelable(false)
                .setPositiveButton("Start New game", positiveOnClickListener)
                .setNegativeButton("Exit", exitListener)
                .setTitle("You Guessed All!")
                .show();
    }


    DialogInterface.OnClickListener positiveOnClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            onNewGameClickListener(null);
        }
    };

    DialogInterface.OnClickListener exitListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            finish();
        }
    };

    @Override
    protected void onDestroy() {
        cleanUp();
        super.onDestroy();
    }

    private void cleanUp(){
        flickrAPI = null;
        retrofit = null;
        randomItem = null;
        list = null;
        originalList = null;
        updatingList = null;
        randomIndicesList = null;

        recyclerView = null;
        randomImage = null;
    }
}
