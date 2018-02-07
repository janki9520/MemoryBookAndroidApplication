package com.patel.memorybookproject;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.patel.memorybookproject.common.Util;
import com.patel.memorybookproject.model.AddMemoriesModel;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;


public class AddMemoriesActivity extends AppCompatActivity {

    private static final int REQUEST_TAKE_IMAGE = 100;
    private static final int RC_STORAGE = 123;
    private FloatingActionButton fab;
    Typeface tf1, tf2, tf3, tf4, tf5;
    EditText ed1, ed2;
    static String pass;
    static Integer size = 18;
    static EditText editTxt1, editTxt2;
    Button btnBackToHome;
    Button btnUploadPhoto;
    private Uri outputFileUri;
    ImageView ivSelectedImage;
    private DatabaseReference dbRef;
    private FirebaseUser mCurrentUser;
    private FirebaseAuth auth;

    private static final String TAG = "AddMemoriesActivity";

    Button btnDatePicker;
    EditText txtDate;
    private int mYear, mMonth, mDay;

    //Firebase Reference
    private FirebaseStorage firebaseStorage;
    private StorageReference storageRef;
    private StorageReference videoUploadRef;
    private StorageReference imageUploadRef;
    private Uri downloadImageUrl;
    private String mCurrentPhotoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_memories);

        hideFloatingActionButton();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.app_name));
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        setSupportActionBar(toolbar);

        ed1 = (EditText) findViewById(R.id.titleEditText);
        ed2 = (EditText) findViewById(R.id.descriptionEditText);

        ed1.setTypeface(Typeface.SERIF, Typeface.BOLD);
        ed2.setTypeface(Typeface.SERIF, Typeface.NORMAL);

        editTxt1 = (EditText) findViewById(R.id.titleEditText);
        editTxt2 = (EditText) findViewById(R.id.descriptionEditText);

        ed1.setTextSize(TypedValue.COMPLEX_UNIT_SP, size);
        ed2.setTextSize(TypedValue.COMPLEX_UNIT_SP, size);

        btnUploadPhoto = findViewById(R.id.btnUploadPhoto);
        ivSelectedImage = findViewById(R.id.ivSelectedImage);

        firebaseStorage = FirebaseStorage.getInstance();
        storageRef = firebaseStorage.getReference();

        btnUploadPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View fView) {
                requireStoragePermission();
            }
        });

        // previous onCreate code
        if (getIntent().getExtras() != null) {
            Bundle extras = getIntent().getExtras();
           final AddMemoriesModel toAdd = (AddMemoriesModel) extras.get("toAddMemories");


            if (toAdd != null) {
                EditText titleEdtText = (EditText) findViewById(R.id.titleEditText);
                EditText descriptionEditText = (EditText) findViewById(R.id.descriptionEditText);
                DatePicker datePicker = (DatePicker) findViewById(R.id.datePicker);
                ImageView imageViewPreview = (ImageView) findViewById(R.id.ivSelectedImage);

                titleEdtText.setText(toAdd.getName());
                descriptionEditText.setText(toAdd.getMessage());

//                SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                try {

                    final Long dateTime = toAdd.getDate();
                    System.out.println("Current selected date: " +dateTime);

                    Calendar calendar = Calendar.getInstance();
                    calendar.setTimeInMillis(dateTime);

                    int year = calendar.get(Calendar.YEAR);
                    int month = calendar.get(Calendar.MONTH);
                    int day = calendar.get(Calendar.DAY_OF_MONTH);
                    datePicker.updateDate(year, month, day);

                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (getIntent().getExtras() != null){
                    String imageUrl = toAdd.getImageUrl();
                    Glide.with(AddMemoriesActivity.this).load(imageUrl).into(imageViewPreview);
                }

            }

            //delete floating action button functionality here
            showFloatingActionButton();
            FloatingActionButton fabDelete= (FloatingActionButton) findViewById(R.id.fabDelete);
            fabDelete.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view) {
                    //notify users to wait patiently till the data gets stored
                    Toast.makeText(AddMemoriesActivity.this, "Your selected memory is deleted",
                            Toast.LENGTH_LONG).show();

                    auth = FirebaseAuth.getInstance();
                    mCurrentUser = auth.getCurrentUser();

                    dbRef = FirebaseDatabase.getInstance().getReference("users").child(mCurrentUser.getUid()).child("addMemories");

                    dbRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (DataSnapshot objSnapshot: dataSnapshot.getChildren()){
                                objSnapshot.getRef().setValue(null);
                            }
                        }
                        @Override
                        public void onCancelled(DatabaseError firebaseError) {
                            Log.e("Read failed", firebaseError.getMessage());
                        }
                    });

                    Intent goBackToMainActivity = new Intent(AddMemoriesActivity.this, MainActivity.class);
                    startActivity(goBackToMainActivity);
            }
        });

        }

        fab = (FloatingActionButton) findViewById(R.id.fabUpload);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //notify users to wait patiently till the data gets stored
                Toast.makeText(AddMemoriesActivity.this, "Your created memory is getting stored. Please wait till it is reflected",
                        Toast.LENGTH_LONG).show();
                saveToMemories();


//              prevent multiple clicks
                preventMultipleClicks(view);
            }

        });

        btnBackToHome = (Button) findViewById(R.id.backToHome);
        btnBackToHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent newIntent = new Intent(AddMemoriesActivity.this, MainActivity.class);
                AddMemoriesActivity.this.startActivity(newIntent);

            }
        });

    }

    //prevent multiple clicks of users
    private void preventMultipleClicks(final View view) {
        view.setEnabled(false);
        view.postDelayed(new Runnable() {
            @Override
            public void run() {
                view.setEnabled(true);
            }
        },1200);
    }

    public void showFloatingActionButton() {
        FloatingActionButton fabDelete = (FloatingActionButton) findViewById(R.id.fabDelete);
        fabDelete.show();
    };

    public void hideFloatingActionButton() {
        FloatingActionButton fabDelete = (FloatingActionButton) findViewById(R.id.fabDelete);
        fabDelete.hide();
    };


    private void openImageIntent() {

/*// Determine Uri of camera image to save.
        final File root = new File(Environment.getExternalStorageDirectory() + File.separator + "MyDir" + File.separator);
        root.mkdirs();
        final String fname = System.currentTimeMillis()+".img";
        final File sdImageMainDirectory = new File(root, fname);
        outputFileUri = Uri.fromFile(sdImageMainDirectory);

        Log.d("Output",""+outputFileUri);*/

        // Camera.
        final List<Intent> cameraIntents = new ArrayList<Intent>();
        final Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        final PackageManager packageManager = getPackageManager();
        final List<ResolveInfo> listCam = packageManager.queryIntentActivities(captureIntent, 0);
        for (ResolveInfo res : listCam) {
            final String packageName = res.activityInfo.packageName;
            final Intent intent = new Intent(captureIntent);
            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            intent.setPackage(packageName);

            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
            }
            // Continue only if the File was successfully created
            try {

                if (photoFile != null) {
                    Uri photoURI = FileProvider.getUriForFile(this,
                            "com.patel.memorybookproject.provider",
                            photoFile);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                    outputFileUri = photoURI;
                    cameraIntents.add(intent);
                }
            } catch (Exception foE) {
                foE.printStackTrace();
            }

        }

        // Filesystem.
        final Intent galleryIntent = new Intent();
        galleryIntent.setType("image/*");
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);

        // Chooser of filesystem options.
        final Intent chooserIntent = Intent.createChooser(galleryIntent, getString(R.string.app_name));

        // Add the camera options.
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, cameraIntents.toArray(new Parcelable[cameraIntents.size()]));

        startActivityForResult(chooserIntent, REQUEST_TAKE_IMAGE);
    }

    public static void overrideFont(Context context, String defaultFontNameToOverride, String customFontFileNameInAssets) {

        try {
            final Typeface customFontTypeface = Typeface.createFromAsset(context.getAssets(), customFontFileNameInAssets);

            final Field defaultFontTypefaceField = Typeface.class.getDeclaredField(defaultFontNameToOverride);
            defaultFontTypefaceField.setAccessible(true);
            defaultFontTypefaceField.set(null, customFontTypeface);
            pass = customFontFileNameInAssets;
            Log.e(pass, pass);
        } catch (Exception e) {
            Log.e("Not Implemented", "Font can't be changed");
        }
    }


    public static void overrideFontSize(Context context, String defaultFontSizeToOverride, String customFontSizeToSet) {
        try {
            SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
            String textSize = settings.getString("customSize", customFontSizeToSet);
            size = Integer.parseInt(textSize);
        } catch (Exception e) {
            Log.e("Not Implemented", "Font size can't be changed");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @AfterPermissionGranted(RC_STORAGE)
    private void requireStoragePermission() {
        String[] perms = {android.Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if (EasyPermissions.hasPermissions(this, perms)) {
            openImageIntent();
        } else {
            // Do not have permissions, request them now
            EasyPermissions.requestPermissions(this, getString(R.string.storage_permission),
                    RC_STORAGE, perms);
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }


    public void saveToMemories() {
        // first section
        // get the data to save in our firebase db
        final EditText titleEditText = (EditText) findViewById(R.id.titleEditText);
        final EditText descriptionEditText = (EditText) findViewById(R.id.descriptionEditText);
        DatePicker datePicker = (DatePicker) findViewById(R.id.datePicker);
        Calendar lCalendar = Calendar.getInstance();
        lCalendar.set(Calendar.YEAR,datePicker.getYear());
        lCalendar.set(Calendar.MONTH,datePicker.getMonth());
        lCalendar.set(Calendar.DAY_OF_MONTH,datePicker.getDayOfMonth());

        SimpleDateFormat format = new SimpleDateFormat("MMM d,yyyy HH:mm");

        final Long dateTime = lCalendar.getTimeInMillis();
        //make the modal object and convert it into hashmap

        //second section
        //save it to the firebase db

        Long time = System.currentTimeMillis();
        storageRef = firebaseStorage.getReferenceFromUrl("gs://" + getString(R.string.google_storage_bucket));
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final String key = database.getReference(Util.getPathForEntries(Util.getAuthUser()
                .getUid())).push().getKey();
        if (outputFileUri != null) {
            imageUploadRef = storageRef.child(Util.getAuthUser().getUid()).child("images").child(time.toString())
                    .child(outputFileUri.getLastPathSegment());

            UploadTask uploadTaskImage = imageUploadRef.putFile(outputFileUri);

            uploadTaskImage.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    downloadImageUrl = taskSnapshot.getDownloadUrl();
                    uploadToFirebase(titleEditText, descriptionEditText, dateTime, database, key);

                }
            });
        } else {
            uploadToFirebase(titleEditText, descriptionEditText, dateTime, database, key);
        }


    }

    private void uploadToFirebase(EditText fTitleEditText, EditText fDescriptionEditText, Long fDateTime, FirebaseDatabase fDatabase, String fKey) {
        AddMemoriesModel addMemories = new AddMemoriesModel();
        addMemories.setName(fTitleEditText.getText().toString().trim());
        addMemories.setMessage(fDescriptionEditText.getText().toString().trim());
        addMemories.setDate(fDateTime);
        if (!TextUtils.isEmpty(downloadImageUrl.toString())) {
            addMemories.setImageUrl(downloadImageUrl.toString());
        }


        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put(fKey, addMemories.toFirebaseObject());
        fDatabase.getReference(Util.getPathForEntries(Util.getAuthUser().getUid()))
                .updateChildren(childUpdates, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                        if (databaseError == null) {
                            finish();
                        }
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == RESULT_OK && requestCode == REQUEST_TAKE_IMAGE) {
            try {
                if (data.getData() != null) {
                    outputFileUri = data.getData();
                }
                Glide.with(this)
                        .load(outputFileUri)
                        .into(ivSelectedImage);

            } catch (Exception foE) {
                foE.printStackTrace();
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
}
