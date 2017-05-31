package ami.beehappy.beehappy;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;


public class BeeHappyMain extends AppCompatActivity {
    public static String ID_MESSAGE;
    // the array that will be used to retreive previously saved IDs
    private ArrayAdapter<String> adapter;
    private List idList = new ArrayList<String>();

    // this creates the activity
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bee_happy_main);
        // retrieve previously saved ids
        retreiveIdArray();
        this.adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, idList);

        ListView listView = (ListView) findViewById(R.id.list);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(MessageClickedHandler);
    }

    // called when the user taps the Enter button on the Select Id screen
    // onClick attribute requires: public void and View view as only argument
    public void enterId(View view) {
        // an intent is the ability to do something (start the displayMessageActivity
        EditText editText = (EditText) findViewById(R.id.editText);
        String id = editText.getText().toString();
        // add msg to the list of ids
        if(!idList.contains(id)) {
            idList.add(id);
        }
        this.saveId();
        // start the info activity with the entered id
        startInfoActivity(id);
    }

    public void clearID(View view) {
        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        // clear the id file
        editor.clear();
        editor.commit();
    }

    private void startInfoActivity(String id) {
        Intent intent = new Intent(this, DisplayActivity.class);
        intent.putExtra(ID_MESSAGE, id);
        startActivity(intent);
    }

    // save the entered IDs (it saves the whole list everytime)
    private void saveId() {
        // create / retrieve a file on which preferences can be stored
        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        // edit it
        SharedPreferences.Editor editor = sharedPref.edit();
        // for every element in the list of ids saved in memory
        // here we provide to putString the pair (key, value)
        // we also save the size of the id
        editor.putInt("Id_size", idList.size());
        for (int i = 0; i < idList.size(); ++i) {
            String id = (String) this.idList.get(i);
            editor.remove("Id_" + i);
            editor.putString("Id_" + i, id);
        }
        // finish by committing the changes to the shared preferences file
        editor.commit();
    }

    // retrieve an array of IDs from the shared preferences file
    private void retreiveIdArray() {
        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        // clear the idList (is going to be filled with the new values
        this.idList.clear();
        // here we retrieve each id
        // we then put it in idList to be loaded in the ListView adapter
        int size = sharedPref.getInt("Id_size", 0);
        for (int i = 0; i < size; ++i) {
            String id = sharedPref.getString("Id_" + i, null);
            this.idList.add(id);
        }
    }

    // Create a message handling object as an anonymous class, useful to handle click events on ListView
    private AdapterView.OnItemClickListener MessageClickedHandler = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView parent, View v, int position, long id) {
            // Start activity in response to the click
            String entry = (String) parent.getItemAtPosition(position);
            startInfoActivity(entry);
        }
    };
}
