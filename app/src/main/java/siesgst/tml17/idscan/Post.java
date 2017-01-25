package siesgst.tml17.idscan;

import android.util.Log;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by leprechaun on 09/01/17.
 */

public class Post {
    String user;
    String pass;
    private Request request;
    String responseString;


    public Post(String user, String pass) {
        this.user = user;
        this.pass = pass;

    }

    String postToDb() {
        Log.d("tag","inside post");
        String url = "http://192.168.137.219";
        OkHttpClient client = new OkHttpClient();
        RequestBody body = new FormBody.Builder()
                .add("username", user)
                .add("password", pass)
                .build();
        Log.d("url",body.toString());
        request = new Request.Builder()
                .url(url)
                .method("POST", body.create(null, new byte[0]))
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                responseString = response.body().string();
                Log.v("response", responseString);
            }
        });
        return responseString;
    }
}
