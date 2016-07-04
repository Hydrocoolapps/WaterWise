package hydrocoolapps.waterwise.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.os.AsyncTask;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;

public class HttpRequestAsyncTask extends AsyncTask<Void, Void, Void> {

    // declare variables needed
    private String requestReply,ipAddress, portNumber;
    private Context context;
    private String parameter;
    private String parameterValue;

    /**
     * Description: The asyncTask class constructor. Assigns the values used in its other methods.
     * @param context the application context, needed to create the dialog
     * @param parameterValue the pin number to toggle
     * @param ipAddress the ip address to send the request to
     * @param portNumber the port number of the ip address
     */
    public HttpRequestAsyncTask(Context context, String parameterValue, String ipAddress, String portNumber, String parameter)
    {
        this.context = context;

        this.ipAddress = ipAddress;
        this.parameterValue = parameterValue;
        this.portNumber = portNumber;
        this.parameter = parameter;
    }

    /**
     * Name: doInBackground
     * Description: Sends the request to the ip address
     * @param voids
     * @return
     */
    @Override
    protected Void doInBackground(Void... voids) {
        requestReply = sendRequest();
        return null;
    }

    /**
     * Name: onPostExecute
     * Description: This function is executed after the HTTP request returns from the ip address.
     * The function sets the dialog's message with the reply text from the server and display the dialog
     * if it's not displayed already (in case it was closed by accident);
     * @param aVoid void parameter
     */
    @Override
    protected void onPostExecute(Void aVoid) {
    }

    /**
     * Name: onPreExecute
     * Description: This function is executed before the HTTP request is sent to ip address.
     * The function will set the dialog's message and display the dialog.
     */
    @Override
    protected void onPreExecute() {
    }

    public String sendRequest() {
        String serverResponse = "ERROR";

        try {

            HttpClient httpclient = new DefaultHttpClient(); // create an HTTP client
            // define the URL e.g. http://myIpaddress:myport/?pin=13 (to toggle pin 13 for example)
            URI website = new URI("http://"+ipAddress+":"+portNumber+"/?"+parameter+"="+parameterValue);
            HttpGet getRequest = new HttpGet(); // create an HTTP GET object
            getRequest.setURI(website); // set the URL of the GET request

            //getRequest.setHeader("Connection", "close");

            System.out.println("http://"+ipAddress+":"+portNumber+"/?"+parameter+"="+parameterValue);

            getRequest.setHeader("User-Agent", "WaterWise Mobile Application");
            getRequest.setHeader("Accept-Encoding", "gzip, deflate, sdch");
            getRequest.setHeader("Accept-Language", "en-US, en;q=0.8");

            HttpResponse response = httpclient.execute(getRequest); // execute the request
            // get the ip address server's reply

            InputStream content = null;
            content = response.getEntity().getContent();
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    content
            ));

            serverResponse = in.readLine();
            // Close the connection
            content.close();
        } catch (ClientProtocolException e) {
            // HTTP error
            serverResponse = e.getMessage();
            e.printStackTrace();
        } catch (IOException e) {
            // IO error
            serverResponse = e.getMessage();
            e.printStackTrace();
        } catch (URISyntaxException e) {
            // URL syntax error
            serverResponse = e.getMessage();
            e.printStackTrace();
        }

        System.out.println(serverResponse);

        // return the server's reply/response text
        return serverResponse;
    }

    public String getReply() { return requestReply; }

}