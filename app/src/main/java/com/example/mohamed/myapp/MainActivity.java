package com.example.mohamed.myapp;
import android.content.Context;
import android.hardware.Camera;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.ByteBuffer;

import static android.webkit.ConsoleMessage.MessageLevel.LOG;

public class MainActivity extends AppCompatActivity {
    Camera camera;
    static byte[] data1;
    private static Socket s;
    private static DataOutputStream dataoutputstream;
    private static  String massage;
    FrameLayout frameLayout;
    showCamera showCamera;
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        frameLayout = (FrameLayout) findViewById(R.id.frameLayout);

        camera = Camera.open();

        showCamera = new showCamera(this, camera);
        frameLayout.addView(showCamera);

    }

    Camera.PictureCallback mPictureCallback = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            File picture_file = getOutputMediafile();
            if (picture_file == null) {
                Log.v(TAG, "folder==null");

                return;
            } else {
                try {
                    FileOutputStream fos = new FileOutputStream(picture_file);
                    fos.write(data);
                    data1 = data;
                    fos.close();
                    camera.startPreview();

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }


    };

    private static File getOutputMediafile() {
        String state = Environment.getExternalStorageState();
        if (!state.equals(Environment.MEDIA_MOUNTED)) {
            Log.v(TAG, "Media not mounted");
            return null;
        } else {
            File folder_gui = new File(Environment.getExternalStorageDirectory() + File.separator + "GUI");
            if (!folder_gui.exists()) {
                folder_gui.mkdirs();
            }
            File outputFile = new File(folder_gui, "temp.jpg");
           // path = new String(outputFile.getPath());
            return outputFile;
        }
    }

    public void captureImage(View v) {
        if (camera != null) {
            camera.takePicture(null, null, mPictureCallback);

        }
    }

    public void send_image(View v) {
        EditText edit = (EditText)findViewById(R.id.editext1);
        String  IP = edit.getText().toString();
        Log.v(TAG,IP);
        Mytask mt = new Mytask(MainActivity.this,IP);              // create background task to send image
        mt.execute();                                                 //start the task
    }

    class Mytask extends AsyncTask<Void,Void,String>
    {
        private Context ctx;
        String IP;
        public Mytask (Context ctx, String IP){

            this.ctx=ctx;
            this.IP =IP;

        }
        @Override
        protected String doInBackground(Void... voids) {
            try{
                Log.v(TAG, "socket close");
                s = new Socket(IP,7000);
                dataoutputstream = new DataOutputStream(s.getOutputStream());
                Log.v(TAG, "Data output stream");
                byte[] size = ByteBuffer.allocate(4).putInt(data1.length).array();
                dataoutputstream.write(size);
               dataoutputstream.write(data1);
                Log.v(TAG, "Data write");
                dataoutputstream.flush();
                Log.v(TAG, "Data flush");
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(s.getInputStream()));
               massage = bufferedReader.readLine();
                Log.v(TAG,massage);

                s.close();
                Log.v(TAG, "socket close");



            }catch (IOException e){
                e.printStackTrace();
            }

            return massage;

        }
        protected void onPostExecute(String result) {
            Toast.makeText(ctx, result,
                    Toast.LENGTH_LONG).show();

        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        releaseCamera();              // release the camera immediately on pause event
    }
    private void releaseCamera(){
        if (camera != null){
            camera.release();        // release the camera for other applications
            camera = null;
        }
    }
    protected void onResume() {
        super.onResume();
        if (camera == null) {
        camera =Camera.open();
            showCamera = new showCamera(this, camera);
            frameLayout.addView(showCamera);
        }
    }
}
