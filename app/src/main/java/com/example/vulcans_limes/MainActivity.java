package com.example.vulcans_limes;


import android.app.AlertDialog;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
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
    private ActivityResultLauncher<Intent> encryptLauncher;
    private ActivityResultLauncher<Intent> decryptLauncher;
    private String key_id;
    private TextView textViewSigned;
    private TextView textViewVerify;

    /**
     * This will run upon starting the app. It initializes the screen with its components.
     *
     * @param savedInstanceState If the activity is being re-initialized after
     *                           previously being shut down then this Bundle contains the data it most
     *                           recently supplied in {@link #onSaveInstanceState}.  <b><i>Note: Otherwise it is null.</i></b>
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //executes a number of functionality tests and prints the result to the console
        System.out.println(RustDef.testMethod());

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
        // Button testButton = findViewById(R.id.idBtnTest);

        textViewSigned = findViewById(R.id.textViewSignedBytes);
        textViewVerify = findViewById(R.id.textViewVerify);


        final String[] algorithms = {
                // RSA algorithms
                "RSA;2048;SHA-256;PKCS1",
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
/*                "AES;192;GCM;NoPadding",
                "AES;192;CBC;PKCS7Padding",
                "AES;192;CTR;NoPadding",*/
                "AES;256;GCM;NoPadding",
                "AES;256;CBC;PKCS7Padding",
                "AES;256;CTR;NoPadding"
        };

        // Activity for encryption on button press
        encryptLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == RESULT_OK && result.getData() != null) {
            Uri uri = result.getData().getData();
            handleEncryptedImage(uri);
            }
        });

        // Activity for decryption on button press
        decryptLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                Uri uri = result.getData().getData();
                handleDecryptedImage(uri);
            }
        });

        // When test button is pressed
        // testButton.setOnClickListener((v -> System.out.println(RustDef.callRust())));

        // When encrypt button is pressed
        encButton.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.setType("*/*");
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            encryptLauncher.launch(intent);
        });

        // When decrypt button is pressed
        decButton.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.setType("*/*");
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            decryptLauncher.launch(intent);

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
                    try {
                        if (RustDef.demoLoad(key_id))
                            Snackbar.make(v, "Key with ID " + key_id + " was successfully loaded!", Snackbar.LENGTH_LONG).show();
                        else
                            Snackbar.make(v, "Key with ID " + key_id + " does not exist.", Snackbar.LENGTH_LONG).show();
                    } catch (Exception e) {
                        e.printStackTrace();
                        Snackbar.make(v, "Key with ID " + key_id + " does not exist.", Snackbar.LENGTH_LONG).show();
                    }
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
                        try {
                            key_id = keyNameInput.getText().toString();
                            String selectedAlgorithm = spinnerAlgorithm.getSelectedItem().toString();
                            if (RustDef.demoCreate(key_id, selectedAlgorithm))
                                Snackbar.make(v, "Key " + key_id + " was created!", Snackbar.LENGTH_LONG).show();
                            else
                                Snackbar.make(v, "Key " + key_id + " already exists.", Snackbar.LENGTH_LONG).show();
                        } catch (Exception e) {
                            e.printStackTrace();
                            Snackbar.make(v, "Key " + key_id + " already exists.", Snackbar.LENGTH_LONG).show();
                        }
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
                    try {
                        if (signText(signText))
                            Snackbar.make(v, "Text signed!", Snackbar.LENGTH_LONG).show();
                        else
                            Snackbar.make(v, "Failed to sign Text.", Snackbar.LENGTH_LONG).show();
                    } catch (IOException e) {
                        Snackbar.make(v, "Failed to sign Text.", Snackbar.LENGTH_LONG).show();
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
                boolean verify = verifyText();
                if (verify)
                    Snackbar.make(v, "Successful verify!", Snackbar.LENGTH_LONG).show();
                else
                    Snackbar.make(v, "Failed to verify Text", Snackbar.LENGTH_LONG).show();

                textViewVerify.setText("\n Verify: " + verify);
            } catch (Exception e) {
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
            if(signedBytes.length == 0) return false;

            createFileFromByteArray(signedBytes, signedTxtFile);
            createFileFromByteArray(unsignedBytes, unsignedTxtFile);
            String textViewText = "Signed Bytes: " + Arrays.toString(signedBytes);
            textViewSigned.setText(textViewText);
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


    private boolean pictureEncrypt(Uri uri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(uri);
            assert inputStream != null;
            byte[] fileData = readBytes(inputStream);

            byte[] encryptedData = RustDef.demoEncrypt(fileData, key_id);
            File downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            File encryptedFile = new File(downloadsDir, "encrypted_file.enc");
            FileOutputStream fos = new FileOutputStream(encryptedFile);
            fos.write(encryptedData);
            fos.close();

                return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean decryptPicture(Uri uri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(uri);
            assert inputStream != null;
            byte[] encryptedData = readBytes(inputStream);
            byte[] decryptedData = RustDef.demoDecrypt(encryptedData, key_id);
            File tempFile = File.createTempFile("decrypted_image", ".jpg", getCacheDir());
            FileOutputStream fos = new FileOutputStream(tempFile);
            fos.write(decryptedData);
            fos.close();

            File imgFile = new File(tempFile.getPath());
            if (imgFile.exists()) {
                Bitmap bitmap = BitmapFactory.decodeFile(imgFile.getPath());
                imageView.setImageBitmap(bitmap);
                return true;
            }
            return false;

        } catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    private void handleEncryptedImage(Uri uri) {
            try {
                View view = findViewById(android.R.id.content);

                if(pictureEncrypt(uri))
                    Snackbar.make(view, "File encrypted!", Snackbar.LENGTH_LONG).show();
                else
                    Snackbar.make(view, "Encrypt failed, please check key.", Snackbar.LENGTH_LONG).show();
            } catch (Exception e) {
                e.printStackTrace();
            }

    }

    private void handleDecryptedImage(Uri uri) {
            try {
                View view = findViewById(android.R.id.content);

                if (decryptPicture(uri))
                    Snackbar.make(view, "Decryption successful", Snackbar.LENGTH_LONG).show();
                else
                    Snackbar.make(view, "Decryption failed", Snackbar.LENGTH_LONG).show();
            } catch (Exception e) {
                e.printStackTrace();
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
        } catch (IOException e) {
            e.printStackTrace();
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

    private byte[] readBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len;
        while ((len = inputStream.read(buffer)) != -1) {
            byteArrayOutputStream.write(buffer, 0, len);
        }
        return byteArrayOutputStream.toByteArray();
    }
}




