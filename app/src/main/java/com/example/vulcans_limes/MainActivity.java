package com.example.vulcans_limes;

import android.app.AlertDialog;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
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
    private ActivityResultLauncher<Intent> launcher;
    private String key_id;
    /**
     * This will run upon starting the app. It initializes the screen with its components.
     *
     * @param savedInstanceState If the activity is being re-initialized after
     *                           previously being shut down then this Bundle contains the data it most
     *                           recently supplied in {@link #onSaveInstanceState}.  <b><i>Note: Otherwise it is null.</i></b>
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        RustDef.demoInit();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // initialize buttons
        imageView = findViewById(R.id.idIVimage);
        Button encButton = findViewById(R.id.idBtnEncrypt);
        Button decButton = findViewById(R.id.idBtnDecrypt);
        Button loadButton = findViewById(R.id.idBtnLoad);
        Button createButton = findViewById(R.id.idBtnCreate);
        Button signButton = findViewById(R.id.idBtnSign);
        Button verifyButton = findViewById(R.id.idBtnVerify);
        Button testButton = findViewById(R.id.idBtnTest);

        final String[] algorithms = {
                // RSA algorithms
                "RSA;512;SHA-256;PKCS1",
                "RSA;1024;SHA-256;PKCS1",
                "RSA;2048;SHA-256;PKCS1",
                "RSA;3072;SHA-256;PKCS1",
                "RSA;4096;SHA-256;PKCS1",
                "RSA;8192;SHA-256;PKCS1",
                // EC algorithms
                "EC;secp256r1;SHA-256",
                "EC;secp384r1;SHA-256",
                "EC;secp521r1;SHA-256",
                // 3DES algorithms
                "DESede;168;CBC;PKCS7Padding",
                // AES algorithms
                "AES;128;GCM;NoPadding",
                "AES;128;CBC;PKCS7Padding",
                "AES;128;CTR;NoPadding",
                "AES;192;GCM;NoPadding",
                "AES;192;CBC;PKCS7Padding",
                "AES;192;CTR;NoPadding",
                "AES;256;GCM;NoPadding",
                "AES;256;CBC;PKCS7Padding",
                "AES;256;CTR;NoPadding"
        };

        // Activity for encryption on button press
        launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == RESULT_OK) {
                Intent data = result.getData();
                handleActivityResult(data);
            }
        });

        // When test button is pressed
        testButton.setOnClickListener((v -> System.out.println(RustDef.callRust())));

        // When encrypt button is pressed
        encButton.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            launcher.launch(intent);
        });

        // When decrypt button is pressed
        decButton.setOnClickListener(v -> {
            try {
                if (decryptPicture()) {
                    Toast.makeText(MainActivity.this, "Successful decrypt!", Toast.LENGTH_SHORT).show();
                }

            } catch (Exception e) {
                Toast.makeText(MainActivity.this, "Fail to decrypt image", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        });

        //When load key button is pressed
        loadButton.setOnClickListener(v -> {
            try {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Name the ID of the key to load:");
                final EditText input = new EditText(this);
                builder.setView(input);
                builder.setPositiveButton("OK", (dialog, which) -> {
                    key_id = input.getText().toString();
                    RustDef.demoLoad(key_id);
                    Snackbar.make(v, "The key with ID \"" + key_id + "\" was successfully loaded!", Snackbar.LENGTH_SHORT).show();
                });
                builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
                builder.show();


            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        //When create key button is pressed
        createButton.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            LayoutInflater inflater = getLayoutInflater();

            View dialogView = inflater.inflate(R.layout.dialog_layout, null);

            Spinner spinnerAlgorithm = dialogView.findViewById(R.id.spinnerAlgorithm);
            ArrayAdapter<String> adapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_spinner_item, algorithms);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerAlgorithm.setAdapter(adapter);

            // Set up the AlertDialog
            builder.setView(dialogView)
                    .setPositiveButton("OK", (dialog, id) -> {
                        EditText keyNameInput = dialogView.findViewById(R.id.keyNameInput);
                        key_id = keyNameInput.getText().toString();
                        String selectedAlgorithm = spinnerAlgorithm.getSelectedItem().toString();
                        RustDef.demoCreate(key_id, selectedAlgorithm);
                        Toast.makeText(MainActivity.this, "Key created with name: " + key_id+ ", using algorithm: " + selectedAlgorithm, Toast.LENGTH_LONG).show();
                    })
                    .setNegativeButton("Cancel", (dialog, id) -> dialog.dismiss());

            // Create and show the AlertDialog
            AlertDialog dialog = builder.create();
            dialog.show();
        });

        //When sign button is pressed
        signButton.setOnClickListener(v -> {
            try {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Set the Text to Sign:");
                final EditText input = new EditText(this);
                builder.setView(input);
                builder.setPositiveButton("OK", (dialog, which) -> {
                    String signText = input.getText().toString();
                    Snackbar.make(v, "Signing Text...", Snackbar.LENGTH_SHORT).show();

                    try {
                        if (signText(signText)) {
                            Toast.makeText(this, "Text signed..", Toast.LENGTH_SHORT).show();
                        }
                    } catch (IOException e) {
                        Toast.makeText(this, "Fail to sign Text: " + e, Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                });

                builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
                builder.show();


            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        //When verify button is pressed
        verifyButton.setOnClickListener(v -> {
            try {
                if (verifyText())
                    Toast.makeText(MainActivity.this, "Successful verify!", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(MainActivity.this, "Fail to verify Text", Toast.LENGTH_SHORT).show();

            } catch (Exception e) {
                Toast.makeText(MainActivity.this, "Fail to verify Text", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }

        });

    }


    private boolean signText(String text) throws IOException {
        try {
            ContextWrapper contextWrapper = new ContextWrapper(getApplication());
            File txtDir = contextWrapper.getExternalFilesDir(Environment.DIRECTORY_DCIM);
            File unsignedTxtFile = new File(txtDir, "unsignedfile" + ".txt");
            File signedTxtFile = new File(txtDir, "signedfile" + ".txt");

            byte[] unsignedBytes = text.getBytes(StandardCharsets.UTF_8);
            byte[] signedBytes = RustDef.demoSign(text.getBytes(StandardCharsets.UTF_8), key_id);

            createFileFromByteArray(signedBytes, signedTxtFile);
            createFileFromByteArray(unsignedBytes, unsignedTxtFile);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean verifyText() {
        try {
            ContextWrapper contextWrapper = new ContextWrapper(getApplication());
            File txtDir = contextWrapper.getExternalFilesDir(Environment.DIRECTORY_DCIM);
            File unsignedTxtFile = new File(txtDir, "unsignedfile" + ".txt");
            File signedTxtFile = new File(txtDir, "signedfile" + ".txt");

            byte[] unsignedBytes = toByteArray(unsignedTxtFile.getPath());
            byte[] signedBytes = toByteArray(signedTxtFile.getPath());
            return RustDef.demoVerify(unsignedBytes, signedBytes, key_id);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    private boolean pictureEncrypt(String path) {
        try {
            ContextWrapper contextWrapper = new ContextWrapper(getApplication());
            File photoDir = contextWrapper.getExternalFilesDir(Environment.DIRECTORY_DCIM);
            File encFile = new File(photoDir, "encfile" + ".jpg");
            byte[] encryptedData = RustDef.demoEncrypt(toByteArray(path), key_id);

            createFileFromByteArray(encryptedData, encFile);

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            View view = findViewById(android.R.id.content);
            Snackbar.make(view, "encrypt failed!", Snackbar.LENGTH_SHORT).show();
            return false;
        }
    }

    private boolean decryptPicture() throws Exception {

        ContextWrapper contextWrapper = new ContextWrapper(getApplication());
        File photoDir = contextWrapper.getExternalFilesDir(Environment.DIRECTORY_DCIM);
        File encFile = new File(photoDir, "encfile" + ".jpg");

        byte[] bytes = toByteArray(encFile.getPath());

        File decFile = new File(photoDir, "decfile.jpg");

        byte[] decBytes = RustDef.demoDecrypt(bytes, key_id);
        createFileFromByteArray(decBytes, decFile);


        File imgFile = new File(decFile.getPath());
        if (imgFile.exists()) {
            Bitmap bitmap = BitmapFactory.decodeFile(imgFile.getPath());
            imageView.setImageBitmap(bitmap);
        }
        return true;
    }

    private void handleActivityResult(Intent data) {
        if (data != null) {
            Uri imgUri = data.getData();

            String[] filePath = {MediaStore.Images.Media.DATA};

            assert imgUri != null;
            Cursor cursor = getContentResolver().query(imgUri, filePath, null, null, null);
            assert cursor != null;
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePath[0]);

            String picPath = cursor.getString(columnIndex);

            cursor.close();

            try {
                if (pictureEncrypt(picPath)) {
                    Toast.makeText(this, "Image encrypted..", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "Fail to encrypt image : " + e, Toast.LENGTH_SHORT).show();
            }
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
            Toast.makeText(this, "Failed to create file at " + file.getPath() + ": " + e.getMessage(), Toast.LENGTH_SHORT).show();
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
}