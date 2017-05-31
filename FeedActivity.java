package ami.beehappy.beehappy;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.TimePicker;

public class FeedActivity extends AppCompatActivity {

    private int hour;
    private int minutes;
    private int days;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);

        // retrieve schedule from file
        retreiveSched();
        setTime();
        // create a numberpicker
        NumberPicker np = (NumberPicker) findViewById(R.id.numberPicker);
        np.setMinValue(0);
        np.setMaxValue(30);
        np.setValue(this.days);
        //np.setWrapSelectorWheel(true);
        np.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        //Set a value change listener for NumberPicker
        np.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal){
                //Display the newly selected value from picker
                setDay(newVal);
                saveSchedule();
            }
        });

    }

    public void setHour(int hour){
        this.hour = hour;
    }

    public void setDay(int d){
        this.days = d;
    }

    public void setMinutes(int min){
        this.minutes = min;
    }

    public void setTime() {
        TextView text = (TextView) findViewById(R.id.feedingTime);
        text.setText(this.hour + ":" + this.minutes);
    }

    private void saveSchedule() {
        // create / retrieve a file on which preferences can be stored
        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        // edit it
        SharedPreferences.Editor editor = sharedPref.edit();
        // write days, hour, minutes
        editor.putInt("dayGap", this.days);
        editor.putInt("hour", this.hour);
        editor.putInt("minutes", this.minutes);
        // finish by committing the changes to the shared preferences file
        editor.commit();
    }

    private void retreiveSched() {
        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        // clear the idList (is going to be filled with the new values
        // here we retrieve each id
        // we then put it in idList to be loaded in the ListView adapter
        this.hour = sharedPref.getInt("hour", 0);
        this.minutes = sharedPref.getInt("minutes", 0);
        this.days = sharedPref.getInt("dayGap", 0);
    }

    public void showTimePickerDialog(View v) {
        final TimePickerDialog timePicker = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                // as time is set, save it and display it
                setHour(hourOfDay);
                setMinutes(minute);
                setTime();
                saveSchedule();
            }
        }, 0, 0, true);

        timePicker.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                // This is hiding the "Cancel" button:
                timePicker.getButton(Dialog.BUTTON_NEGATIVE).setVisibility(View.GONE);
            }
        });
        timePicker.show();
    }
}
