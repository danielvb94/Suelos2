package com.example.danie.suelos2;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import data.DBHelper;
import data.Floor;


public class MainActivity extends AppCompatActivity {
    //public final static String EXTRA_MESSAGE = "com.example.danie.suelos2.MESSAGE";
    private DBHelper db;
    //keep track of camera capture intent
    final int CAMERA_CAPTURE = 1;

    final int GALERY_CAPTURE = 1;
    //captured picture uri;
    private Uri picUri = null;
    //keep track of cropping intent
    final int PIC_CROP = 2;

    Button b1;
    Button b2;
    ImageView i1;

    private String RUTACOMPLETA;
    private String text = "No hay conexión con el servidor";

    private ProgressDialog dialog;
    private Bitmap thePic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = DBHelper.getInstance(this);

        dialog = new ProgressDialog(this);
        dialog.setTitle("Cargando");
        dialog.setMessage("Procesando imagen...");
        dialog.setCancelable(false);

        //b1 = (Button) findViewById(R.id.capture_btn);
        //b2 = (Button) findViewById(R.id.galery_btn);
        i1 = (ImageView) findViewById(R.id.picture);
        //b1.setOnClickListener(CamaraGaleria);
        //b2.setOnClickListener(CamaraGaleria);
        i1.setOnClickListener(CamaraGaleria);

        //retrieve a reference to the UI button

        ActivityCompat.requestPermissions(MainActivity.this,
                new String[]{
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);


    }

    View.OnClickListener CamaraGaleria = new View.OnClickListener() {
        public void onClick(View v) {
            /*if (v.getId() == R.id.capture_btn) {
                //use standard intent to capture an image
                Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                //we will handle the returned data in onActivityResult
                startActivityForResult(captureIntent, CAMERA_CAPTURE);

            } else if (v.getId() == R.id.galery_btn) {
                Intent gallerypickerIntent = new Intent(Intent.ACTION_PICK);
                gallerypickerIntent.setType("image/*");
                startActivityForResult(gallerypickerIntent, GALERY_CAPTURE);

            } else */if (v.getId() == R.id.picture && picUri != null) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.setDataAndType(picUri, "image/*");
                startActivity(intent);
            }
        }
    };

    @SuppressLint("SimpleDateFormat")
    private String getCode() {

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyymmddhhmmss");
        String date = dateFormat.format(new Date());
        String photoCode = "pic_" + date;
        RUTACOMPLETA = Environment.getExternalStorageDirectory().toString() + "/suelos/" + photoCode + ".jpg";
        return photoCode;
    }

    private String getRealPathFromURI(Uri contentURI) {
        String result;
        Cursor cursor = getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) { // Source is Dropbox or other similar local file path
            result = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            //user is returning from capturing an image using the camera
            if (requestCode == CAMERA_CAPTURE) {
                //get the Uri for the captured image
                picUri = data.getData();

                performCrop();


            } else if (requestCode == PIC_CROP) {


                //get the returned data
                Bundle extras = data.getExtras();
                //get the cropped bitmap
                thePic = extras.getParcelable("data");

                Toast toast1 =
                        Toast.makeText(getApplicationContext(),
                                "Recorte hecho", Toast.LENGTH_SHORT);

                toast1.show();


                //Guardar
                try {
                    Guardar(thePic);
                } catch (IOException e) {
                    e.printStackTrace();
                }


                //retrieve a reference to the ImageView
                ImageView picView = (ImageView) findViewById(R.id.picture);
                //display the returned cropped image
                picView.setImageBitmap(thePic);


                //try {
                //uploadFile(RUTACOMPLETA);
                //new MiTarea().execute();
                //  } catch (IOException e) {
                //     e.printStackTrace();
                // }
            }
        }
    }


    private void Guardar(Bitmap bitmap) throws IOException {
        Bitmap bitmap2 = bitmap;
        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root + "/suelos");
        myDir.mkdirs();
        String fname = getCode() + ".jpg";
        File file = new File(myDir, fname);

        if (file.exists())
            file.delete();
        try {
            FileOutputStream out = new FileOutputStream(file);
            bitmap2.compress(Bitmap.CompressFormat.JPEG, 90, out);

            out.flush();
            out.close();
            //sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://" + Environment.getExternalStorageDirectory())));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void performCrop() {
        try {
            //call the standard crop action intent (the user device may not support it)
            Intent cropIntent = new Intent("com.android.camera.action.CROP");
            //indicate image type and Uri
            cropIntent.setDataAndType(picUri, "image/*");
            //set crop properties
            cropIntent.putExtra("crop", "true");
            //indicate aspect of desired crop
            cropIntent.putExtra("aspectX", 1);
            cropIntent.putExtra("aspectY", 1);
            //indicate output X and Y
            cropIntent.putExtra("outputX", 96);
            cropIntent.putExtra("outputY", 96);
            //retrieve data on return
            cropIntent.putExtra("return-data", true);
            //start the activity - we handle returning in onActivityResult
            startActivityForResult(cropIntent, PIC_CROP);
        } catch (ActivityNotFoundException anfe) {
            //display an error message
            String errorMessage = "Whoops - your device doesn't support the crop action!";
            Toast toast = Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT);
            toast.show();

        }
    }


    private class MiTarea extends AsyncTask<String, Float, Integer> {

        protected void onPreExecute() {
            dialog.show();
        }

        protected Integer doInBackground(String... urls) {
            int serverResponseCode = 0;

            HttpURLConnection connection;
            DataOutputStream dataOutputStream;
            String lineEnd = "\r\n";
            String twoHyphens = "--";
            String boundary = "*****";


            int bytesRead, bytesAvailable, bufferSize;
            byte[] buffer;
            int maxBufferSize = 1 * 1024 * 1024;
            File selectedFile = new File(RUTACOMPLETA);

            try {
                FileInputStream fileInputStream = new FileInputStream(selectedFile);
                URL url = new URL("http://192.168.1.16/tfg/ficheros/ejecutar.php");
                connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);//Allow Inputs
                connection.setDoOutput(true);//Allow Outputs
                connection.setUseCaches(false);//Don't use a cached Copy
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Connection", "Keep-Alive");
                connection.setRequestProperty("ENCTYPE", "multipart/form-data");
                connection.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                connection.setRequestProperty("uploaded_file", RUTACOMPLETA);

                //creating new dataoutputstream
                dataOutputStream = new DataOutputStream(connection.getOutputStream());

                //writing bytes to data outputstream
                dataOutputStream.writeBytes(twoHyphens + boundary + lineEnd);
                dataOutputStream.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename=\""
                        + RUTACOMPLETA + "\"" + lineEnd);


                dataOutputStream.writeBytes(lineEnd);

                //returns no. of bytes present in fileInputStream
                bytesAvailable = fileInputStream.available();
                //selecting the buffer size as minimum of available bytes or 1 MB
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                //setting the buffer as byte array of size of bufferSize
                buffer = new byte[bufferSize];

                //reads bytes from FileInputStream(from 0th index of buffer to buffersize)
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                //loop repeats till bytesRead = -1, i.e., no bytes are left to read
                while (bytesRead > 0) {
                    //write the bytes read from inputstream
                    dataOutputStream.write(buffer, 0, bufferSize);
                    bytesAvailable = fileInputStream.available();
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                }

                dataOutputStream.writeBytes(lineEnd);
                dataOutputStream.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                serverResponseCode = connection.getResponseCode();
                String serverResponseMessage = connection.getResponseMessage();


                if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuilder sb = new StringBuilder();
                    String line = null;

                    // Read Server Response
                    while ((line = reader.readLine()) != null) {
                        // Append server response in string
                        sb.append(line + "\n");
                    }
                    text = sb.toString();
                    reader.close();

                }

                //closing the input and output streams
                fileInputStream.close();
                dataOutputStream.flush();
                dataOutputStream.close();

/****************************************************************************/
                ContentValues values = new ContentValues();

                ExifInterface exifInterface = null;
                try {
                    exifInterface = new ExifInterface(getRealPathFromURI(picUri));
                } catch (IOException e) {
                    e.printStackTrace();
                }

                float[] LatLong = new float[2];
                if (exifInterface.getLatLong(LatLong)) {
                    values.put("Fecha",exifInterface.getAttribute(ExifInterface.TAG_DATETIME));
                    values.put("Latitud",LatLong[0]);
                    values.put("Longitud",LatLong[1]);
                    values.put("Url",getRealPathFromURI(picUri));
                    values.put("Suelo",text);
                    db.inserta(values);

                }


/****************************************************************************/
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, "File Not Found", Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (MalformedURLException e) {
                e.printStackTrace();
                Toast.makeText(MainActivity.this, "URL error!", Toast.LENGTH_SHORT).show();

            } catch (IOException e) {
                e.printStackTrace();
            }

            return 250;
        }

        protected void onProgressUpdate(Float... valores) {

        }

        protected void onPostExecute(Integer bytes) {
            dialog.dismiss();
            TextView resulta = (TextView) findViewById(R.id.cuadro);
            resulta.setText(text);
            ImageView picView = (ImageView) findViewById(R.id.picture);
            //display the returned cropped image
            picView.setImageBitmap(thePic);


        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();
        switch (id){
            case R.id.action_camara:
                Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(captureIntent, CAMERA_CAPTURE);
                break;
            case R.id.action_galeria:
                Intent gallerypickerIntent = new Intent(Intent.ACTION_PICK);
                gallerypickerIntent.setType("image/*");
                startActivityForResult(gallerypickerIntent, GALERY_CAPTURE);
                break;
            case R.id.action_mapa:
                Intent intent = new Intent(MainActivity.this,MapsActivity.class);
                startActivity(intent);
                break;


        }
        return true;
    }


}
