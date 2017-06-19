package ami.beehappy.beehappy

import android.app.IntentService
import android.content.Intent

class BackgroundService(name: String?) : IntentService(name) {

    protected override fun onHandleIntent (workIntent: Intent) {
        // Gets data from the incoming Intent (not used ATM)
        // var dataString: String = workIntent.getDataString();
        // Loop around the following :
        // 1. send a request to the server
        // 2. wait for response
        // 3. accept response: the server should notify the state of things, by setting data fields in a json
        //    (booleans, swarming: no/yes or stuff)'
        //    It would be best to have ALL parameters in a single json
        // 5. parse the json
        // 6. for any field which is positive to a critical situation, show a notification
        // 7. wait (1,10,60,600)? seconds and repeat

    }

}