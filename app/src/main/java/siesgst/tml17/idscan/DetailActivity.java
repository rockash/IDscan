package siesgst.tml17.idscan;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class DetailActivity extends AppCompatActivity {

    Button scan;
    String barcode_scan;
    private Request request;
    String responseString;
    SessionManager session;
    TextView EventIdName;
    TextView Email;
    TextView Event;
    TextView Contact_number;
    Button logout;
    TextView add;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        session=new SessionManager(DetailActivity.this);
        scan = (Button)findViewById(R.id.Scan);
        EventIdName=(TextView) findViewById(R.id.head_name);
        Email=(TextView) findViewById(R.id.head_email);
        Event=(TextView) findViewById(R.id.event_name);
        Contact_number=(TextView) findViewById(R.id.head_contact);
        logout=(Button)findViewById(R.id.logout_id);
        add=(TextView)findViewById(R.id.manual_addition);
        EventIdName.setText(session.getName());
        Email.setText(session.getEmail());
        Event.setText(session.getEventName());
        Contact_number.setText(session.getContact());
        scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scan();
            }
        });
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(DetailActivity.this,MainActivity.class));

            }
        });
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(DetailActivity.this);
                builder.setTitle("Input PRN");
                // Set up the input
                final EditText input = new EditText(DetailActivity.this);
                // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                input.setInputType(InputType.TYPE_CLASS_TEXT );
                builder.setView(input);
                // Set up the buttons
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        barcode_scan = input.getText().toString();
                        Toast.makeText(DetailActivity.this, "Input="+barcode_scan, Toast.LENGTH_LONG).show();
                        play(barcode_scan);
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();
            }
        });
    }
    public void scan(){
        final Activity activity = this;
        IntentIntegrator integrator = new IntentIntegrator(activity);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
        integrator.setPrompt("Set over barcode of SIES GST College ID");
        integrator.setCameraId(0);
        integrator.setBeepEnabled(true);
        integrator.setBarcodeImageEnabled(false);
        integrator.initiateScan();
        integrator.setOrientationLocked(true);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                Log.d("MainActivity", "cancelled scan");
                Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
            } else {
                Log.d("MainActivity", "Scanned");
                barcode_scan = result.getContents();
                Toast.makeText(this, "Scanned: " + barcode_scan, Toast.LENGTH_LONG).show();
                play(barcode_scan);
            }
        }
    }
    public void play(String prn)
    {
        Intent intent = getIntent();
        String id1 = intent.getStringExtra("id");
        String url = "http://192.168.1.35/play.php";
        OkHttpClient client = new OkHttpClient();
        RequestBody body = new FormBody.Builder()
                .add("prn", prn)
                .add("event_id", id1)
                .build();
        request = new Request.Builder()
                .url(url)
                .method("POST", body.create(null, new byte[0]))
                .post(body)
                .build();
        Log.v("url",body.toString());
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                responseString = response.body().string();
                Log.v("response", responseString);
                try {
                    JSONObject root = new JSONObject(responseString);
                    String status = root.optString("status");
                    String message = root.optString("message");
                    if(message.equalsIgnoreCase("play"))
                    {
                        Log.v("tag","Can play");
                    }
                    else{
                        Log.v("tag","Cannot play");
                    }
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                }
                scan();
            }
        });
    }
}
