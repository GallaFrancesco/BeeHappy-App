package ami.beehappy.beehappy;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.widget.TextViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.loopj.android.http.*;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.UnsupportedEncodingException;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.entity.StringEntity;


public class DisplayActivity extends AppCompatActivity {
    RestHttpHandler restHandle = new RestHttpHandler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display);

        // Get the intent that created this activity
        Intent intent = getIntent();
        // extract its message
        String msg = intent.getStringExtra(BeeHappyMain.ID_MESSAGE);

        // capture the textview layout and set the string as its text
        TextView textView = (TextView) findViewById(R.id.textView);
        textView.setText(msg);

        ToggleButton humToggle = newToggleButton(R.id.humToggle, "humidity");
        ToggleButton tempToggle = newToggleButton(R.id.tempToggle, "temperature");


        // check if the address was previously saved
        String addr = checkAddressSaved();
        if (addr != null){
            // it was: set it
            this.restHandle.setBASE_URL("http://"+addr);
        } else {
            // it wasn't: restore default
            this.restHandle.restoreBASE_URL();
            addr = this.restHandle.getBASE_URL().split("://")[1];
        }
        // show current address
        EditText editText = (EditText) findViewById(R.id.editText2);
        editText.setText(addr);

        // query for infos
        refreshValues();
    }

    private ToggleButton newToggleButton(final int id, String endpoint) {
        // return a toggleButton with enabled / disabled function set
        // requires an id to determine the button, an endpoint to associate
        ToggleButton toggle = (ToggleButton) findViewById(id);
        toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // toggle enabled
                    try {
                        putVal(endpoint, "active", "on");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                } else {
                    // The toggle is disabled
                    try {
                        putVal(endpoint, "active", "off");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        return toggle;
    }

    public void refreshValues(){
        GetValues v = new GetValues();
        queryVal("humidity", v.humKeys, val -> v.setHum(val));
        queryVal("temperature", v.tempKeys, val -> v.setTemp(val));
    }

    public void refreshValues(String endpoint){
        GetValues v = new GetValues();
        if (endpoint.equals("humidity")) {
            queryVal("humidity", v.humKeys, val -> v.setHum(val));
        } else if (endpoint.equals("temperature")) {
            queryVal("temperature", v.tempKeys, val -> v.setTemp(val));
        }
    }

    public void changeIP(View view) {
        EditText editText = (EditText) findViewById(R.id.editText2);
        String id = editText.getText().toString();
        // save the address read from the textview
        saveAddress(id);
        // use the provided address as baseurl
        this.restHandle.setBASE_URL("http://"+id);
        refreshValues();
    }

    private void saveAddress(String addr) {
        // create / retrieve a file on which preferences can be stored
        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        // edit it
        SharedPreferences.Editor editor = sharedPref.edit();
        // here we provide to putString the pair (key, value)
        // put the ID of the hive as key and the address as value
        editor.putString(BeeHappyMain.ID_MESSAGE, addr);
        // finish by committing the changes to the shared preferences file
        editor.commit();
    }

    private String checkAddressSaved(){
        // check if the address corresponding to the shown ID is saved
        // if so, return it
        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        if (sharedPref.contains(BeeHappyMain.ID_MESSAGE)) {
            return sharedPref.getString(BeeHappyMain.ID_MESSAGE, null);
        } else {
            return null;
        }
    }

    public void startFoodActivity(View view) {
        // start the food scheduler
        Intent intent = new Intent(this, FeedActivity.class);
        startActivity(intent);
    }


    private void queryVal(final String endpoint, String [] keys, final ValInterface val){
        // uses the restHandle to send a GET request to the server
        // the baseurl of the server is currently hardcoded int RestHttpHandler.java, a setter can modify it
        // the response is supposed to be in a json
        // the method from the interface provided sets the info received on the TextView object
        restHandle.get("/"+endpoint, new JsonHttpResponseHandler() {
            // here we declare the methods WHOSE DECLARATION CANNOT BE CHANGED
            // they are executed in response to the HTTP status of the request
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                // called when response HTTP status is "200 OK"
                JSONObject json = null;
                try {
                    json = (JSONObject) new JSONTokener(response.toString()).nextValue();
                    // for string (key) in list of keys
                    // update the gui to the received values
                    for (String key: keys) {
                        if (!key.equals("active")) { // key = active is used for toggles
                            String respVal = json.getString(key);
                            val.set(json.getString(key));
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    // val.set("Json decoding error");
                }
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable t, JSONObject response) {
                // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                val.set("ConnectionError");
            }

        });
    }

    private void putVal(final String endpoint, String key, String value) throws JSONException, UnsupportedEncodingException {
        // uses the restHandle to send a POST request to the server
        // the baseurl of the server is currently hardcoded int RestHttpHandler.java, a setter can modify it
        // the response is supposed to be in a json
        // the method from the interface provided sets the info received on the TextView object

        // we build a json with the key-value pair given
        JSONObject jsonParams = new JSONObject();
        jsonParams.put(key, value);
        HttpEntity entity = new StringEntity(jsonParams.toString());

        restHandle.put("/"+endpoint, entity, new JsonHttpResponseHandler() {
            // here we declare the methods WHOSE DECLARATION CANNOT BE CHANGED
            // they are executed in response to the HTTP status of the request
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                // called when response HTTP status is "200 OK"
                JSONObject json = null;
                try {
                    json = (JSONObject) new JSONTokener(response.toString()).nextValue();
                    // check if the response is OK
                    String status = json.getString("status");
                    String active = json.getString("active");
                    if(status.equals("err")){
                        String err = json.getString("err");
                        // TODO it should notify the error
                    }
                    if(status.equals("ok")){
                        if (active.equals("on")){
                            refreshValues(endpoint);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    // val.set("Json decoding error");
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable t, JSONObject response) {
                // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                // TODO it should notify the error
                return;
            }

        });
    }

    // these are the methods used by queryVal
    public class GetValues implements ValInterface {
        private String[] tempKeys = {"temperature", "active"};
        private String[] humKeys = {"humidity", "active"};

        private boolean isValid(String s) {
            if ( s == null || s == "" ) {
                return false;
            }
            return true;
        }

        public void setTemp(String temp) {
            TextView text = (TextView) findViewById(R.id.tempVal);
            if (isValid(temp)) {
                text.setText(temp + " °C");
            } else {
                text.setText("Request Failed");
            }
        }

        public void setHum(String hum) {
            TextView text = (TextView) findViewById(R.id.humVal);
            if (isValid(hum)) {
                text.setText(hum + " %");
            } else {
                text.setText("Request Failed");
            }
        }

    @Override
    public void set(String s) {
        return;
    }
}
}

// to use lambdas in queryVal, DisplayActivity.java
interface ValInterface {
    public void set(String s);
}

