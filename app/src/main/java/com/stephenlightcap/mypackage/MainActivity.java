package com.stephenlightcap.mypackage;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.SyncStateContract;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;
import com.joaquimley.faboptions.FabOptions;
import com.stephenlightcap.mypackage.model.Package;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Date;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    String typeOfText = "FROM";
    private int type = 0;
    private String dummyText;
    private Realm realm;
    private String toAddress, fromAddress;
    SurfaceView cameraView;
    TextView textView;
    CameraSource cameraSource;
    Button takePhoto;
    final int RequestCameraPermissionID = 1001;
    private static final int CAMERA_REQUEST = 1888;
    private FabOptions fabOptions;
    TextRecognizer textRecognizer;
    SharedPreferences pref;
    int primaryKey;


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantRequest) {
        switch (requestCode) {
            case RequestCameraPermissionID:
                if (grantRequest[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    try {
                        cameraSource.start(cameraView.getHolder());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
                break;
            default:

                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Realm.init(this);
        realm = Realm.getDefaultInstance();
        pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }


        cameraView = (SurfaceView) findViewById(R.id.surface_view);
        textView = (TextView) findViewById(R.id.text_view);


        fabOptions = (FabOptions) findViewById(R.id.fab_options);
        fabOptions.setButtonsMenu(R.menu.menu);


        textRecognizer = new TextRecognizer.Builder(getApplicationContext()).build();

        configureListeners();

        if (!textRecognizer.isOperational()) {
            Log.w("MainActivity", "Detector  are not yet available!");
        } else {
            cameraSource = new CameraSource.Builder(getApplicationContext(), textRecognizer)
                    .setFacing(CameraSource.CAMERA_FACING_BACK)
                    .setRequestedPreviewSize(1280, 1024)
                    .setAutoFocusEnabled(true)
                    .setRequestedFps(2.0f)

                    .build();

            cameraView.getHolder().addCallback(new SurfaceHolder.Callback() {
                @Override
                public void surfaceCreated(SurfaceHolder holder) {
                    try {
                        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(MainActivity.this,
                                    new String[]{Manifest.permission.CAMERA},
                                    RequestCameraPermissionID);
                        }
                        cameraSource.start(cameraView.getHolder());
                    } catch (Exception ex) {

                    }
                }

                @Override
                public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

                }

                @Override
                public void surfaceDestroyed(SurfaceHolder holder) {
                    cameraSource.stop();
                }
            });

            textRecognizer.setProcessor(new Detector.Processor<TextBlock>() {
                @Override
                public void release() {

                }

                @Override
                public void receiveDetections(Detector.Detections<TextBlock> detections) {

                }
            });
        }

    }

    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();
        SharedPreferences.Editor editor = pref.edit();
        // Check which radio button was clicked
        switch (view.getId()) {
            case R.id.radioButton_scanner_barcode:
                if (checked) {
                    editor.putString("key", "BARCODE");
                    editor.commit();
                }
                break;
            case R.id.radioButton_scanner_from:
                if (checked) {
                    editor.putString("key", "FROM");
                    editor.commit();
                }
                break;
            case R.id.radioButton_scanner_to:
                if (checked) {
                    editor.putString("key", "TO");
                    editor.commit();
                }
                break;
            case R.id.radioButton_scanner_trck:
                if (checked) {
                    editor.putString("key", "TRACK");
                    editor.commit();
                }
                break;

        }
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.faboptions_take_photo:

                cameraSource.takePicture(null, new CameraSource.PictureCallback() {
                    @Override
                    public void onPictureTaken(byte[] bytes) {
                        Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

                        cropPicFile(bmp);
                    }
                });

                break;

            case R.id.faboptions_save:

                createDialogSaveInfo();
                break;

            default:

                break;
        }
    }

    private void createDialogSaveInfo() {
        final Dialog dialog = new Dialog(MainActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_confirm_address_scan);

        //Establish Dialog Views
        Button submit = (Button) dialog.findViewById(R.id.button_dialog_scanner_submit);
        Button cancel = (Button) dialog.findViewById(R.id.button_dialog_scanner_cancel);
        final EditText fromEditText = (EditText) dialog.findViewById(R.id.editText_dialog_scanner_from);
        final EditText toEditText = (EditText) dialog.findViewById(R.id.editText_dialog_scanner_to);
        final EditText trackingEditText = (EditText) dialog.findViewById(R.id.editText_dialog_scanner_tracking);
        ImageView barcode = (ImageView) dialog.findViewById(R.id.imageView_dialog_scanner_barcode);

        //Set text from captured strings in surface view
        fromEditText.setText(pref.getString("FROM", null));
        toEditText.setText(pref.getString("TO", null));
        trackingEditText.setText(pref.getString("TRACK", null));
        final byte[] newArray;

        if (pref.getString("BARCODE", null) == null) {
            newArray = new byte[100];
        } else {
            newArray = Base64.decode(pref.getString("BARCODE", null), Base64.NO_WRAP);
        }


        barcode.setImageBitmap(convertByteArrayBackToBitmap(newArray));

        //Setup listeners
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO save info to realm

                saveLabelInfoIntoRealm(fromEditText.getText().toString(), toEditText.getText().toString(), trackingEditText.getText().toString(),
                        newArray);

                dialog.dismiss();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });


        dialog.show();

    }

    private Bitmap convertByteArrayBackToBitmap(byte[] barcode) {

        return BitmapFactory.decodeByteArray(barcode, 0, barcode.length);

    }

    private void saveLabelInfoIntoRealm(final String from, final String to, final String tracking, final byte[] barcode) {
        SharedPreferences.Editor editor = pref.edit();
        primaryKey= pref.getInt("primary", 0);

        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                Package userPackage = realm.createObject(Package.class, primaryKey);
                userPackage.setFromAddress(from);
                userPackage.setToAddress(to);
                userPackage.setTracking(tracking);
                userPackage.setBarcode(barcode);

            }
        });

        primaryKey++;
        editor.putInt("primary", primaryKey);
        editor.commit();

    }

//    private int getPrimaryKey() {
//
//        if (realm.where(Package.class).max("primaryKey") == null) {
//            return 0;
//        } else {
//
//            return realm.where(Package.class).max("primaryKey").intValue() + 1;
//        }
//
//    }

    private void configureListeners() {

        fabOptions.setOnClickListener(this);

    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);

        Log.d("bitmap", "" + inImage.getByteCount());


        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);

        if (path == null) {
            //Log.d("TAG", "" + path.toString());
        }

        return Uri.parse(path);
    }

    private void cropPicFile(Bitmap file) {

        Uri imageToCrop = getImageUri(this, file);
        CropImage.activity(imageToCrop)
                .setFixAspectRatio(true)
                .setGuidelines(CropImageView.Guidelines.ON)
                .start(this);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK) {

                Uri photo = result.getUri();

                readImage(photo);

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

    private void readImage(Uri photo) {
        StringBuilder stringBuilder = new StringBuilder();
        SharedPreferences.Editor editor = pref.edit();

        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), photo);
            Frame imageFrame = new Frame.Builder()
                    .setBitmap(bitmap)
                    .build();

            final SparseArray<TextBlock> items = textRecognizer.detect(imageFrame);


            for (int i = 0; i < items.size(); i++) {
                TextBlock item = items.valueAt(i);
                stringBuilder.append(item.getValue());
                stringBuilder.append("\n");
            }

            String checkForOperationSave = pref.getString("key", null);

            if (checkForOperationSave.equals("FROM")) {
                editor.putString("FROM", stringBuilder.toString());
                editor.commit();
            } else if (checkForOperationSave.equals("TO")) {
                editor.putString("TO", stringBuilder.toString());
                editor.commit();
            } else if (checkForOperationSave.equals("BARCODE")) {
                String barcode = Base64.encodeToString(convertBitmapToString(bitmap), Base64.NO_WRAP);
                Log.d("TEST", "" + barcode);
                editor.putString("BARCODE", barcode);
                editor.commit();
            } else if (checkForOperationSave.equals("TRACK")) {
                editor.putString("TRACK", stringBuilder.toString());
                editor.commit();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    /**
     * May not need string.
     *
     * @param bitmap
     * @return
     */
    private byte[] convertBitmapToString(Bitmap bitmap) {

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, stream);
        return stream.toByteArray();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        realm.close();
    }

    @Override
    public void onPause() {
        super.onPause();

        realm.close();
    }

    @Override
    public void onResume() {
        super.onResume();

        realm = Realm.getDefaultInstance();
    }
}

