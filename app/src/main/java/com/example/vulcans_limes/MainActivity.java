package com.example.vulcans_limes;

import static com.example.vulcans_limes.RustDef.cryptoManager;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;

import java.security.Security;
import java.util.Arrays;

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



    @Override
    /**
     * This will run upon starting the app. It initializes the screen with its components.
     *
     */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        System.out.println(Security.getProviders());
        imageView = findViewById(R.id.idIVimage);
        encButton = findViewById(R.id.idBtnEncrypt);
        decButton = findViewById(R.id.idBtnDecrypt);
        signButton = findViewById(R.id.idBtnSign);
        verifyButton = findViewById(R.id.idBtnVerify);
        loadButton = findViewById(R.id.idBtnLoad);
        createButton = findViewById(R.id.idBtnCreate);

        // Activity for encryption on button press
        launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),result -> {
            if (result.getResultCode() == RESULT_OK) {
                Intent data = result.getData();
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
              // TODO:  decrypt();
                System.out.println("CheckCall");
                byte[] data = new byte[3];
                data[0] = 3;
                System.out.println(Arrays.toString(RustDef.demoEncrypt(data)));
            } catch (Exception e) {
                Toast.makeText(MainActivity.this, "Fail to decrypt image", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        });

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
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && null != data) {
            Uri imgUri = data.getData();

            String[] filePath = {MediaStore.Images.Media.DATA};

            Cursor cursor = getContentResolver().query(imgUri, filePath, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePath[0]);

            String picPath = cursor.getString(columnIndex);

            cursor.close();

            try {
            // TODO: Splice File into Byte array for encryption    encrypt(picPath);
                Toast.makeText(this, "Image encrypted..", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "Fail to encrypt image : " + e, Toast.LENGTH_SHORT).show();
            }
        }
    }
}