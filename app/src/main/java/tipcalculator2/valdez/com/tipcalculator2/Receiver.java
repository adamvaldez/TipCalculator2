package tipcalculator2.valdez.com.tipcalculator2;

/**
 * Created by adamvaldez on 3/1/15.
 */

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

//Add a receiver for the boot completed broadcast
public class Receiver extends BroadcastReceiver
{

    @Override
    public void onReceive(Context context, Intent intent)
    {

        //getAction() - The action of this intent or null if none is specified
        //ACTION_BOOT_COMPLETED - This is broadcast once, after the system has finished booting
        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED))
        {
            Intent activity = new Intent(context, TipCalculator2.class);

            activity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            context.startActivity(activity);
        }
    }
}
