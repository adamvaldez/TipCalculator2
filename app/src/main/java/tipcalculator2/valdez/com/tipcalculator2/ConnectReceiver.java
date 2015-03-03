package tipcalculator2.valdez.com.tipcalculator2;

/**
 * Created by adamvaldez on 3/1/15.
 */

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

//Add a receiver for the connectivity changed broadcast
public class ConnectReceiver extends BroadcastReceiver
{

    boolean wifiConnection;
    String connection;

    @Override
    public void onReceive(Context context, Intent intent)
    {

        ConnectivityManager connectionManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectionManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        if(networkInfo != null)
            wifiConnection = networkInfo.isConnected();//true if network connectivity exists, false otherwise.

        //String to be displayed in toast
        if(wifiConnection == true)
            connection = "Connected";
        else
            connection = "Not Connected";

        //Toast
        Toast.makeText(context, connection, Toast.LENGTH_LONG).show();
    }
}
