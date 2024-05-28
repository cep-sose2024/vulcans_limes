package com.example.vulcans_limes;

import android.app.AlertDialog;
import android.content.ContextWrapper;
import android.content.Intent;
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
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.spec.InvalidKeySpecException;
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
    private CryptoManager cm;


    /**
     * This will run upon starting the app. It initializes the screen with its components.
     *
     * @param savedInstanceState If the activity is being re-initialized after
     *                           previously being shut down then this Bundle contains the data it most
     *                           recently supplied in {@link #onSaveInstanceState}.  <b><i>Note: Otherwise it is null.</i></b>
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //RustDef.demoInit();
        try {
            cm = new CryptoManager();
        } catch (KeyStoreException e) {
            throw new RuntimeException(e);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
/*

        // generate asymmetric key
        String keyGenInfo = "DESede;CBC;PKCS7Padding";
        String toSign = "Sign me!";
        byte[] signed;

        //RustDef.demoCreate("KeyPair2", keyGenInfo);
        try {
            cm.generateKeyPair("KeyPair1", keyGenInfo);
        } catch (CertificateException | IOException | NoSuchAlgorithmException |
                 InvalidAlgorithmParameterException | NoSuchProviderException |
                 KeyStoreException e) {
            throw new RuntimeException(e);
        }
        try {
            signed = cm.signData(toSign.getBytes());
        } catch (NoSuchAlgorithmException | UnrecoverableKeyException | KeyStoreException |
                 InvalidKeyException | SignatureException | InvalidKeySpecException |
                 NoSuchProviderException e) {
            throw new RuntimeException(e);
        }
        //signed = Arrays.toString(RustDef.demoSign(toSign.getBytes()));
        try {
            System.out.println("Verified? " + cm.verifySignature(toSign.getBytes(), signed));
        } catch (Exception e) {
            e.printStackTrace();
        }
*/


        imageView = findViewById(R.id.idIVimage);
        Button encButton = findViewById(R.id.idBtnEncrypt);
        Button decButton = findViewById(R.id.idBtnDecrypt);
        Button loadButton = findViewById(R.id.idBtnLoad);
        Button createButton = findViewById(R.id.idBtnCreate);

        // Activity for encryption on button press
        launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == RESULT_OK) {
                Intent data = result.getData();
                handleActivityResult(data);
            }
        });

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
                    String keyId = input.getText().toString();
                    try {
                        cm.loadKey(keyId);
                    } catch (KeyStoreException | UnrecoverableKeyException e) {
                        throw new RuntimeException(e);
                    }
                    //RustDef.demoLoad(keyId);
                    Snackbar.make(v, "The key with ID \"" + keyId + "\" was successfully loaded!", Snackbar.LENGTH_SHORT).show();
                });
                builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
                builder.show();


            } catch (Exception e) {
                e.printStackTrace();
            }
        });


        //When create key button is pressed
        createButton.setOnClickListener(v -> {
            try {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Name the ID of the key to create:");
                final EditText input = new EditText(this);
                builder.setView(input);
                builder.setPositiveButton("OK", (dialog, which) -> {
                    String keyId = input.getText().toString();
                    // generate symmetric key
                    String keyGenInfoSYM = "AES;256;CTR;NoPadding";
                    try {
                        cm.genKey(keyId, keyGenInfoSYM);
                        cm.showKeyInfo();
                    } catch (CertificateException | IOException | NoSuchAlgorithmException |
                             NoSuchProviderException | InvalidAlgorithmParameterException |
                             KeyStoreException | UnrecoverableKeyException |
                             InvalidKeySpecException e) {
                        throw new RuntimeException(e);
                    }
                    //RustDef.demoCreate(keyId, keyGenInfoSYM);
                    Snackbar.make(v, "The key with ID \"" + keyId + "\" was successfully created!", Snackbar.LENGTH_SHORT).show();

                });
                builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
                builder.show();

            } catch (Exception e) {
                e.printStackTrace();
            }
        });


    }

    private boolean decryptPicture() throws Exception {

        ContextWrapper contextWrapper = new ContextWrapper(getApplication());
        File photoDir = contextWrapper.getExternalFilesDir(Environment.DIRECTORY_DCIM);
        File encFile = new File(photoDir, "encfile" + ".jpg");

        byte[] bytes = toByteArray(encFile.getPath());

        File decFile = new File(photoDir, "decfile.jpg");

//        byte[] decBytes = RustDef.demoDecrypt(bytes);
        byte[] decBytes = cm.decryptData(bytes);
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
                boolean didItWork = pictureEncrypt(picPath);
                if (didItWork) {
                    Toast.makeText(this, "Image encrypted..", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "Fail to encrypt image : " + e, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private boolean pictureEncrypt(String path) {
        try {
            ContextWrapper contextWrapper = new ContextWrapper(getApplication());
            File photoDir = contextWrapper.getExternalFilesDir(Environment.DIRECTORY_DCIM);
            File encFile = new File(photoDir, "encfile" + ".jpg");
//            byte[] encryptedData = RustDef.demoEncrypt(toByteArray(path));
            byte[] encryptedData = cm.encryptData(toByteArray(path));

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