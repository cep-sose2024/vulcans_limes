package com.example.vulcans_limes;

import static com.example.vulcans_limes.RustDef.cryptoManager;

import android.app.AlertDialog;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.material.snackbar.Snackbar;

import java.io.FileOutputStream;
import java.security.Security;
import java.util.Arrays;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import android.Manifest;
import android.content.pm.PackageManager;


/**
 * Main Activity for Android Device App.
 * The class displays and manages the devices screen.
 * It also generates a key which, depending on what method gets used, is stored in a KeyStore.
 * This is just an example test app, for testing of our methods.
 *
 * @author Erik Fribus
 */
public class MainActivity extends AppCompatActivity {


    private ImageView imageView;
    private Button encButton, decButton, signButton, verifyButton, loadButton, createButton;
    private ActivityResultLauncher<Intent> launcher;

    private CryptoManager cryptoManager;

    private String toSign;

    @Override
    /**
     * This will run upon starting the app. It initializes the screen with its components.
     *
     */
    protected void onCreate(Bundle savedInstanceState) {
        initDemo();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        imageView = findViewById(R.id.idIVimage);
        encButton = findViewById(R.id.idBtnEncrypt);
        decButton = findViewById(R.id.idBtnDecrypt);
        //signButton = findViewById(R.id.idBtnSign);
        //verifyButton = findViewById(R.id.idBtnVerify);
        loadButton = findViewById(R.id.idBtnLoad);
        createButton = findViewById(R.id.idBtnCreate);

        // Activity for encryption on button press
        launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),result -> {
            if (result.getResultCode() == RESULT_OK) {
                Intent data = result.getData();
                handleActivityResult(data);
            }
        } );

        // When encrypt button is pressed
        encButton.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            launcher.launch(intent);
        });

        // When decrypt button is pressed
        decButton.setOnClickListener(v -> {
            try {
              if(decryptPicture()){
                  Toast.makeText(MainActivity.this, "Succesful decrypt!", Toast.LENGTH_SHORT).show();
              }

            } catch (Exception e) {
                Toast.makeText(MainActivity.this, "Fail to decrypt image", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        });

        /*
        //When sign button is pressed
        signButton.setOnClickListener(v -> {
            try{
                //TODO: sign();


                Snackbar.make(v, "The String \"Sign me!\" got signed!", Snackbar.LENGTH_SHORT).show();

            } catch (Exception e){
                e.printStackTrace();
            }
        });


        //When verify button is pressed
        verifyButton.setOnClickListener(v -> {
            try{
                //TODO: verify();

                Snackbar.make(v, "Successful verification!", Snackbar.LENGTH_SHORT).show();
            } catch (Exception e){
                e.printStackTrace();
            }
        });
        */

        //When load key button is pressed
        loadButton.setOnClickListener(v -> {
            try{


                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Name the ID of the key to load:");
                final EditText input = new EditText(this);
                builder.setView(input);
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String keyId = input.getText().toString();
                        cryptoManager.setKEY_NAME(keyId);
                        Snackbar.make(v, "The key with ID \"" + keyId + "\" was successfully loaded!", Snackbar.LENGTH_SHORT).show();
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();



            } catch (Exception e){
                e.printStackTrace();
            }
        });


        //When create key button is pressed
        createButton.setOnClickListener(v -> {
            try{
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Name the ID of the key to create:");
                final EditText input = new EditText(this);
                builder.setView(input);
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String keyId = input.getText().toString();
                        boolean generatedSuccess = cryptoManager.genKey(keyId);
                        if(generatedSuccess){
                            Snackbar.make(v, "The key with ID \"" + keyId + "\" was successfully created!", Snackbar.LENGTH_SHORT).show();
                        }

                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();

            } catch (Exception e){
                e.printStackTrace();
            }
        });


    }

    private boolean decryptPicture() throws Exception {

        ContextWrapper contextWrapper = new ContextWrapper(getApplication());
        File photoDir = contextWrapper.getExternalFilesDir(Environment.DIRECTORY_DCIM);
        File encFile = new File(photoDir, "encfile" + ".jpg");

        byte[] bytes = toByteArray(encFile.getPath());

        File decFile = new File(photoDir,"decfile.jpg");

        byte[] decBytes = cryptoManager.decryptData(bytes);

        createFileFromByteArray(decBytes, decFile);


        File imgFile = new File(decFile.getPath());
        if(imgFile.exists()){
            Bitmap bitmap = BitmapFactory.decodeFile(imgFile.getPath());
            imageView.setImageBitmap(bitmap);
        }
        return true;

    }

    private void handleActivityResult(Intent data) {
        if (data != null) {
            Uri imgUri = data.getData();

            String[] filePath = {MediaStore.Images.Media.DATA};

            Cursor cursor = getContentResolver().query(imgUri, filePath, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePath[0]);

            String picPath = cursor.getString(columnIndex);

            cursor.close();

            try {
                boolean didItWork = pictureEncrypt(picPath);
                if(didItWork){
                    Toast.makeText(this, "Image encrypted..", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "Fail to encrypt image : " + e, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private boolean pictureEncrypt(String path) throws Exception {
        try {
            ContextWrapper contextWrapper = new ContextWrapper(getApplication());
            File photoDir = contextWrapper.getExternalFilesDir(Environment.DIRECTORY_DCIM);
            File encFile = new File(photoDir, "encfile" + ".jpg");
            byte[] encryptedData = cryptoManager.encryptData(toByteArray(path));
            createFileFromByteArray(encryptedData, encFile);

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            View view = findViewById(android.R.id.content);
            Snackbar.make(view, "encrypt failed!", Snackbar.LENGTH_SHORT).show();
            return false;
        }
    }
    private byte[] toByteArray(String path) throws IOException {
        FileInputStream fis = new FileInputStream(path);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] buf = new byte[1024];
        int bytesRead;
        while ((bytesRead = fis.read(buf)) != -1) {
            bos.write(buf, 0, bytesRead);
        }
        byte[] bytes = bos.toByteArray();
        fis.close();
        bos.close();
        return bytes;
    }

    private void createFileFromByteArray(byte[] bytes, File file) {
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            fos.write(bytes);
            Toast.makeText(this, "File created successfully at: " + file.getPath(), Toast.LENGTH_SHORT).show();
            System.out.println("File created successfully at: " + file.getPath());
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to create file at " + file.getPath() +": " + e.getMessage(), Toast.LENGTH_SHORT).show();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * This method gets called upon when an Action is launched (e.g. the encrypt button is pressed)
     * The user picks a picture out of their Media file system
     * @param requestCode The integer request code originally supplied to
     *                    startActivityForResult(), allowing you to identify who this
     *                    result came from.
     * @param resultCode The integer result code returned by the child activity
     *                   through its setResult().
     * @param data An Intent, which can return result data to the caller
     *               (various data can be attached to Intent "extras").
     *
     */


    /**
     * This method initializes everything important for the demo.
     */
    private void initDemo(){
        cryptoManager = new CryptoManager("AES");
        toSign = "Sign me!";
    }

}