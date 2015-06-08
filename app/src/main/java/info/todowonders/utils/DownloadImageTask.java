package info.todowonders.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by ptanwar on 08/06/15.
 */
public class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {

    private static final String TAG = DownloadImageTask.class.getName();
    private DefaultHttpClient client;

    private ImageView bmImage;

    public DownloadImageTask(ImageView bmImage) {
        this.bmImage = bmImage;
        createClient();
    }


    private void createClient() {
        BasicHttpParams params = new BasicHttpParams();
        SchemeRegistry schemeRegistry = new SchemeRegistry();
        schemeRegistry.register( new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
        //schemeRegistry.register( new Scheme("https", SSLSocketFactory.getSocketFactory(), 443));
        ClientConnectionManager ccm = new ThreadSafeClientConnManager( params, schemeRegistry);
    }


    protected void onPreExecute() {
        //mDialog = ProgressDialog.show(ChartActivity.this, "Please wait...", "Retrieving data ...", true);
    }

    protected Bitmap doInBackground(String... urls) {
        String urldisplay = urls[0];
        Bitmap mIcon11 = null;
        try {
            //InputStream in = new java.net.URL(urldisplay).openStream();
            //mIcon11 = BitmapFactory.decodeStream(in);
            HttpURLConnection con = (HttpURLConnection)(new URL( urldisplay ).openConnection());
            con.setInstanceFollowRedirects( false );
            con.connect();
            int responseCode = con.getResponseCode();
            System.out.println( responseCode );
            String location = con.getHeaderField( "Location" );



            URL obj = new URL(location);
            HttpURLConnection conn = (HttpURLConnection) obj.openConnection();
            conn.setInstanceFollowRedirects(true);  //you still need to handle redirect manully.
            HttpURLConnection.setFollowRedirects(true);
            InputStream in = conn.getInputStream();
            mIcon11 = BitmapFactory.decodeStream(in);
            in.close();
            Log.e(TAG, "Done 11");
        } catch (Exception e) {
            Log.e(TAG, "image download error");
            Log.e(TAG, e.getMessage());
            e.printStackTrace();
        }
        return mIcon11;
    }

    protected void onPostExecute(Bitmap result) {
        //set image of your imageview
        Log.e(TAG, "Image completed set.");
        bmImage.setImageBitmap(result);
    }
}