package com.example.mg_win.facebusiness.Activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.hardware.camera2.params.Face;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.Toolbar;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.text.Editable;
import android.text.Html;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;


import com.example.mg_win.facebusiness.FaceAPI.FaceEnroll;
import com.example.mg_win.facebusiness.FaceAPI.FaceEnrollResponse;
import com.example.mg_win.facebusiness.FaceAPI.FaceRecognition;
import com.example.mg_win.facebusiness.FaceAPI.FaceRecognitionResponse;
import com.example.mg_win.facebusiness.R;
import com.example.mg_win.facebusiness.Services.Register;
import com.example.mg_win.facebusiness.Services.RegisterResponse;
import com.example.mg_win.facebusiness.Utils.FormatStrings;
import com.example.mg_win.facebusiness.Utils.ImagePicker;
import com.example.mg_win.facebusiness.Utils.SetGetImage;
import com.example.mg_win.facebusiness.Utils.UserInfo;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.commons.validator.routines.EmailValidator;

public class RegisterActivity extends AppCompatActivity implements FaceRecognitionResponse, FaceEnrollResponse, RegisterResponse {

    private static final int PICK_IMAGE_ID = 666; // the number doesn't matter
    private static final String TAG = "RegisterActivity";

    EditText registeredPhone;
    EditText registeredPassword;
    EditText registeredRePassword;
    EditText registeredName;
    EditText registeredMiddleName;
    EditText registeredSurname;
    EditText registeredEmail;
    EditText registeredCompany;
    EditText registeredTitle;
    AppCompatImageButton registeredImage;
    TextView registeredImageText;

    ImageView registeredPasswordImage;
    ImageView registeredRePasswordImage;

    private Bitmap profileImageBitmap;
    private byte[] croppedProfileImage;
    private byte[] profileImageByteArray;
    boolean profileImageFlag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerAttempt();
               /* Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show(); */
            }
        });


        // EditText Phone Number Format
        registeredPhone = (EditText) findViewById(R.id.registerPhone);
        registeredPhone.addTextChangedListener(new PhoneNumberFormattingTextWatcher() {

            // User is erasing or input new char
            private boolean backSpacingFlag = false;
            // Block :afterTextChanges
            private boolean editedFlag = false;
            // Mark cursor position and restore it after edit
            private int cursorComplement;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                cursorComplement = s.length() - registeredPhone.getSelectionStart();

                if (count > after) {
                    backSpacingFlag = true;
                } else {
                    backSpacingFlag = false;
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //super.onTextChanged(s, start, before, count);
            }

            @Override
            public synchronized void afterTextChanged(Editable s) {
                String tempStr = s.toString();
                String phoneNumber = tempStr.replaceAll("[^\\d]", "");

                if (!editedFlag) {
                    if (phoneNumber.length() >= 8 && !backSpacingFlag) {
                        editedFlag = true;

                        String ans = "(" + phoneNumber.substring(0, 3) + ") " + phoneNumber.substring(3, 6) + " " + phoneNumber.substring(6, 8) + " " + phoneNumber.substring(8);
                        registeredPhone.setText(ans);
                        registeredPhone.setSelection(registeredPhone.getText().length() - cursorComplement);

                    } else if (phoneNumber.length() >= 6 && !backSpacingFlag) {
                        editedFlag = true;

                        String ans = "(" + phoneNumber.substring(0, 3) + ") " + phoneNumber.substring(3, 6) + " " + phoneNumber.substring(6);
                        registeredPhone.setText(ans);
                        registeredPhone.setSelection(registeredPhone.getText().length() - cursorComplement);

                    } else if (phoneNumber.length() >= 3 && !backSpacingFlag) {
                        editedFlag = true;

                        String ans = "(" + phoneNumber.substring(0, 3) + ") " + phoneNumber.substring(3);
                        registeredPhone.setText(ans);
                        registeredPhone.setSelection(registeredPhone.getText().length() - cursorComplement);

                    }
                } else {
                    editedFlag = false;
                }

            }
        });

        // EditText Name Field Format
        registeredName = (EditText) findViewById(R.id.registerName);
        registeredName.addTextChangedListener(new TextWatcher() {

            private boolean backSpacingFlag = false;
            // Block :afterTextChanges
            private boolean editedFlag = false;
            // Mark cursor position and restore it after edit
            private int cursorComplement;

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                cursorComplement = charSequence.length() - registeredName.getSelectionStart();

                if (i1 > i2) {
                    backSpacingFlag = true;
                } else {
                    backSpacingFlag = false;
                }
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String tempStr = editable.toString();
                String name = tempStr.replaceAll("[^A-Za-z]", "");

                if (!editedFlag) {
                    if (name.length() >= 1 && !backSpacingFlag) {
                        editedFlag = true;

                        String ans = name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase();
                        registeredName.setText(ans);
                        registeredName.setSelection(registeredName.getText().length() - cursorComplement);
                    }
                } else {
                    editedFlag = false;
                }
            }
        });

        // EditText MiddleName Field Format
        registeredMiddleName = (EditText) findViewById(R.id.registerMiddleName);
        registeredMiddleName.addTextChangedListener(new TextWatcher() {

            private boolean backSpacingFlag = false;
            // Block :afterTextChanges
            private boolean editedFlag = false;
            // Mark cursor position and restore it after edit
            private int cursorComplement;

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                cursorComplement = charSequence.length() - registeredMiddleName.getSelectionStart();

                if (i1 > i2) {
                    backSpacingFlag = true;
                } else {
                    backSpacingFlag = false;
                }
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String tempStr = editable.toString();
                String name = tempStr.replaceAll("[^A-Za-z]", "");

                if (!editedFlag) {
                    if (name.length() >= 1 && !backSpacingFlag) {
                        editedFlag = true;

                        String ans = name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase();
                        registeredMiddleName.setText(ans);
                        registeredMiddleName.setSelection(registeredMiddleName.getText().length() - cursorComplement);
                    }
                } else {
                    editedFlag = false;
                }
            }
        });

        // EditText Surname Field Format
        registeredSurname = (EditText) findViewById(R.id.registerSurname);
        registeredSurname.addTextChangedListener(new TextWatcher() {

            private boolean backSpacingFlag = false;
            // Block :afterTextChanges
            private boolean editedFlag = false;
            // Mark cursor position and restore it after edit
            private int cursorComplement;

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                cursorComplement = charSequence.length() - registeredSurname.getSelectionStart();

                if (i1 > i2) {
                    backSpacingFlag = true;
                } else {
                    backSpacingFlag = false;
                }
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String tempStr = editable.toString();
                String name = tempStr.replaceAll("[^A-Za-z]", "");

                if (!editedFlag) {
                    if (name.length() >= 1 && !backSpacingFlag) {
                        editedFlag = true;

                        String ans = name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase();
                        registeredSurname.setText(ans);
                        registeredSurname.setSelection(registeredSurname.getText().length() - cursorComplement);
                    }
                } else {
                    editedFlag = false;
                }
            }
        });

        // EditText E-mail Field Format
        registeredEmail = (EditText) findViewById(R.id.registerEmail);
        registeredEmail.addTextChangedListener(new TextWatcher() {

            private boolean backSpacingFlag = false;
            // Block :afterTextChanges
            private boolean editedFlag = false;
            // Mark cursor position and restore it after edit
            private int cursorComplement;

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                cursorComplement = charSequence.length() - registeredEmail.getSelectionStart();

                if (i1 > i2) {
                    backSpacingFlag = true;
                } else {
                    backSpacingFlag = false;
                }
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String tempStr = editable.toString();
                String name = tempStr.replaceAll(" ", "");

                if (!editedFlag) {
                    if (name.length() >= 1 && !backSpacingFlag) {
                        editedFlag = true;

                        //String ans = name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase();
                        registeredEmail.setText(name);
                        registeredEmail.setSelection(registeredEmail.getText().length() - cursorComplement);
                    }
                } else {
                    editedFlag = false;
                }
            }
        });

        // EditText Company Field Format
        registeredCompany = (EditText) findViewById(R.id.registerCompany);
        registeredCompany.addTextChangedListener(new TextWatcher() {

            private boolean backSpacingFlag = false;
            // Block :afterTextChanges
            private boolean editedFlag = false;
            // Mark cursor position and restore it after edit
            private int cursorComplement;

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                cursorComplement = charSequence.length() - registeredCompany.getSelectionStart();

                if (i1 > i2) {
                    backSpacingFlag = true;
                } else {
                    backSpacingFlag = false;
                }
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String company = editable.toString();

                if (!editedFlag) {
                    if (company.length() >= 1 && !backSpacingFlag) {
                        editedFlag = true;
                        String ans = org.apache.commons.lang3.text.WordUtils.capitalize(company);
                        registeredCompany.setText(ans);
                        registeredCompany.setSelection(registeredCompany.getText().length() - cursorComplement);
                    }
                } else {
                    editedFlag = false;
                }
            }
        });

        // EditText Title Field Format
        registeredTitle = (EditText) findViewById(R.id.registerTitle);
        registeredTitle.addTextChangedListener(new TextWatcher() {

            private boolean backSpacingFlag = false;
            // Block :afterTextChanges
            private boolean editedFlag = false;
            // Mark cursor position and restore it after edit
            private int cursorComplement;

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                cursorComplement = charSequence.length() - registeredTitle.getSelectionStart();

                if (i1 > i2) {
                    backSpacingFlag = true;
                } else {
                    backSpacingFlag = false;
                }
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String title = editable.toString();

                if (!editedFlag) {
                    if (title.length() >= 1 && !backSpacingFlag) {
                        editedFlag = true;
                        String ans = org.apache.commons.lang3.text.WordUtils.capitalize(title);
                        registeredTitle.setText(ans);
                        registeredTitle.setSelection(registeredTitle.getText().length() - cursorComplement);
                    }
                } else {
                    editedFlag = false;
                }
            }
        });

        // Get RegisterImage with OnClick From Cache
        registeredImage = (AppCompatImageButton) findViewById(R.id.registerImageButton);
        registeredImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Get Image
                selectImageFromImagePicker();
            }
        });

        // Show Password Until Button Pressed
        registeredPasswordImage = (ImageView) findViewById(R.id.registerPasswordImage);
        registeredPassword = (EditText) findViewById(R.id.registerPassword);
        registeredPasswordImage.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        registeredPassword.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
                        break;
                    case MotionEvent.ACTION_UP:
                        registeredPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                        break;
                }
                return true;
            }
        });

        // Show Re-Password Until Button Pressed
        registeredRePasswordImage = (ImageView) findViewById(R.id.registerRePasswordImage);
        registeredRePassword = (EditText) findViewById(R.id.registerRePassword);
        registeredRePasswordImage.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        registeredRePassword.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
                        break;
                    case MotionEvent.ACTION_UP:
                        registeredRePassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                        break;
                    default:
                        break;
                }
                return true;
            }
        });

    }

    // Open Camera-Library to Get Face Image
    public void selectImageFromImagePicker() {
        Intent chooseImageIntent = ImagePicker.getPickImageIntent(getApplicationContext());
        startActivityForResult(chooseImageIntent, PICK_IMAGE_ID);
    }

    // Override Method for selectImageFromImagePicker
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        profileImageFlag = false;

        if (requestCode != 0) {
            switch (requestCode) {
                case PICK_IMAGE_ID:
                    profileImageBitmap = ImagePicker.getImageFromResult(this, resultCode, data);

                    // Store profileImage
                    SetGetImage setGetImage = new SetGetImage();
                    boolean isStored = setGetImage.storeImage(this, profileImageBitmap);

                    if (isStored) {
                        File file = setGetImage.getOutputMediaFile(this);
                        profileImageByteArray = convertFileToByteArray(file);

                        // SetImage to imageview if byteArray is not null
                        if (profileImageByteArray != null) {
                            registeredImage = (AppCompatImageButton) findViewById(R.id.registerImageButton);
                            registeredImage.setImageBitmap(profileImageBitmap);

                            profileImageFlag = true;
                        }
                    }
            }
        }
    }

    public byte[] convertFileToByteArray(File file) {
        FileInputStream fileInputStream = null;

        if (file.length() > 0) {
            byte[] bFile = new byte[(int) file.length()];

            try {
                fileInputStream = new FileInputStream(file);
                fileInputStream.read(bFile);
                fileInputStream.close();


            } catch (FileNotFoundException e) {
                Log.d(TAG, "Cannot create fileInputStream: " + e.getMessage());
            } catch (IOException e) {
                Log.d(TAG, "Cannot read bFile: " + e.getMessage());
            }

            return bFile;
        } else {
            Log.d(TAG, "- ConvertFileToByteArray: File is Empty!");

            return null;
        }
    }

    public void registerAttempt() {

        // Check whether all fields are null or not
        if (checkPersonalInfoFields()) {
            Log.d(TAG, "No null fields exists");

            // Check email is valid or not
            if (EmailValidator.getInstance().isValid(registeredEmail.getText().toString())) {
                Log.d(TAG, "Email is valid");

                // Check face is found or not
                //TODO! Buraya bakarlar...
                if (checkPersonImageIsValid()) {

                }
            } else {
                Log.d(TAG, "Registered Field is Invalid: " + "E-mail");
                registeredEmail = (EditText) findViewById(R.id.registerEmail);
                registeredEmail.setError(Html.fromHtml(getResources().getString(R.string.error_reg_invalid_email)));
            }
        }
    }

    public boolean checkPersonImageIsValid() {
        // Face Cut Method and Resize Image
        Log.d(TAG, "Check whether image is valid or not!");
        // Clear error information.
        registeredImageText = (TextView) findViewById(R.id.registerImageText);
        registeredImageText.setText("");

        if (profileImageFlag) {
            // Check whether facecut detect 1 face or not!
            Log.d(TAG, "Start FaceRecognition with 'detect' parametrs.");

            FaceRecognition faceRecognition = new FaceRecognition();
            faceRecognition.delegate = this;
            faceRecognition.execute("detect", profileImageByteArray);

        } else {
            Log.d(TAG, "No Profile Image Selected!");
            registeredImageText = (TextView) findViewById(R.id.registerImageText);
            registeredImageText.setText(getResources().getString(R.string.error_reg_image_invalid));

            return false;
        }

        return false;
    }

    public boolean checkPersonalInfoFields() {
        boolean flag = true;

        registeredPhone = (EditText) findViewById(R.id.registerPhone);
        registeredPhone.setError(null);

        registeredPassword = (EditText) findViewById(R.id.registerPassword);
        registeredPassword.setError(null);

        registeredRePassword = (EditText) findViewById(R.id.registerRePassword);
        registeredRePassword.setError(null);

        registeredName = (EditText) findViewById(R.id.registerName);
        registeredName.setError(null);

        registeredSurname = (EditText) findViewById(R.id.registerSurname);
        registeredSurname.setError(null);

        registeredEmail = (EditText) findViewById(R.id.registerEmail);
        registeredEmail.setError(null);

        registeredCompany = (EditText) findViewById(R.id.registerCompany);
        registeredCompany.setError(null);

        registeredTitle = (EditText) findViewById(R.id.registerTitle);
        registeredTitle.setError(null);

        if (registeredPhone.getText().length() != 15) {
            Log.d(TAG, "Registered Field is Invalid: " + "Phone");
            registeredPhone.setError(Html.fromHtml(getResources().getString(R.string.error_reg_invalid_phone)));

            flag = false;
        }
        if (registeredPassword.getText().length() < 6) {
            Log.d(TAG, "Registered Field is Invalid: " + "Password");
            registeredPassword.setError(Html.fromHtml("Password must be greater than 6"));

            flag = false;
        }
        if (registeredPassword.getText().length() < 6) {
            Log.d(TAG, "Registered Field is Invalid: " + "RePassword");
            registeredRePassword.setError(Html.fromHtml("Password must be greater than 6"));

            flag = false;
        }
        if (!registeredPassword.getText().toString().equals(registeredRePassword.getText().toString())) {
            Log.d(TAG, "Registered Fields not equal: " + "Password");
            registeredPassword.setError(Html.fromHtml("Password not matched"));
            registeredRePassword.setError(Html.fromHtml("Password not matched"));

            flag = false;
        }
        if (registeredName.getText().length() == 0) {
            Log.d(TAG, "Registered Field is Invalid: " + "Name");
            registeredName.setError(Html.fromHtml(getResources().getString(R.string.error_reg_invalid_name)));

            flag = false;
        }
        if (registeredSurname.getText().length() == 0) {
            Log.d(TAG, "Registered Field is Invalid: " + "Surname");
            registeredSurname.setError(Html.fromHtml(getResources().getString(R.string.error_reg_invalid_surname)));

            flag = false;
        }
        if (registeredEmail.getText().length() == 0) {
            Log.d(TAG, "Registered Field is Invalid: " + "E-mail");
            registeredEmail.setError(Html.fromHtml(getResources().getString(R.string.error_reg_invalid_email)));

            flag = false;
        }
        if (registeredCompany.getText().length() == 0) {
            Log.d(TAG, "Registered Field is Invalid: " + "Company");
            registeredCompany.setError(Html.fromHtml(getResources().getString(R.string.error_reg_invalid_company)));

            flag = false;
        }
        if (registeredTitle.getText().length() == 0) {
            Log.d(TAG, "Registered Field is Invalid: " + "Title");
            registeredTitle.setError(Html.fromHtml(getResources().getString(R.string.error_reg_invalid_title)));

            flag = false;
        }

        return flag;
    }

    @Override
    public void processFaceRecognition(FaceRecognition.FaceRecognitionResult[] results) {

        if (results.length != 1) {
            Log.d(TAG, "More than one face detected.");
            registeredImageText = (TextView) findViewById(R.id.registerImageText);
            registeredImageText.setText(getResources().getString(R.string.error_reg_image_more_face));
        } else {
            // Only 1 face is detected...
            int x1, m_x1 = 0;
            int x2, m_x2 = 0;
            int y1, m_y1 = 0;
            int y2, m_y2 = 0;

            x1 = results[0].x1;
            x2 = results[0].x2;
            y1 = results[0].y1;
            y2 = results[0].y2;

            int tempDistanceX = (int) ((x2 - x1) * 0.15);
            int tempDistanceY_top = (int) ((y2 - y1) * 0.75);
            int tempDistanceY_bottom = (int) ((y2 - y1) * 0.25);

            if (x1 <= 0) {
                m_x1 = 0;
            } else {
                if (x1 - tempDistanceX <= 0) {
                    m_x1 = 0;
                } else {
                    m_x1 = x1 - tempDistanceX;
                }
            }

            if (profileImageBitmap.getWidth() <= x2) {
                m_x2 = profileImageBitmap.getWidth();
            } else {
                if (x2 + tempDistanceX >= profileImageBitmap.getWidth()) {
                    m_x2 = profileImageBitmap.getWidth();
                } else {
                    m_x2 = x2 + tempDistanceX;
                }
            }

            if (y1 <= 0) {
                m_y1 = 0;
            } else {
                if (y1 - tempDistanceY_top <= 0) {
                    m_y1 = 0;
                } else {
                    m_y1 = y1 - tempDistanceY_top;
                }
            }

            if (profileImageBitmap.getHeight() <= y2) {
                m_y2 = profileImageBitmap.getHeight();
            } else {
                if (y2 + tempDistanceY_bottom >= profileImageBitmap.getHeight()) {
                    m_y2 = profileImageBitmap.getHeight();
                } else {
                    m_y2 = y2 + tempDistanceY_bottom;
                }
            }

            Log.d(TAG, "New face coordinates are;"
                    + "\nx1= " + String.valueOf(m_x1)
                    + "\nx2= " + String.valueOf(m_x2)
                    + "\ny1= " + String.valueOf(m_y1)
                    + "\ny2= " + String.valueOf(m_y2));


            Bitmap croppedBitmap = Bitmap.createBitmap(profileImageBitmap, m_x1, m_y1, (m_x2 - m_x1), (m_y2 - m_y1));
            registeredImage = (AppCompatImageButton) findViewById(R.id.registerImageButton);
            registeredImage.setImageBitmap(croppedBitmap);

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            croppedBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            croppedProfileImage = stream.toByteArray();

            registerUserImage();
        }
    }

    @Override
    public void nullDataReturned() {
        Log.d(TAG, "NullDataReturned... No face detected.");
        registeredImageText = (TextView) findViewById(R.id.registerImageText);
        registeredImageText.setText(getResources().getString(R.string.error_reg_image_no_face));
    }

    public void registerUserImage() {
        FaceEnroll enroll = new FaceEnroll();
        enroll.delegate = this;
        enroll.execute(croppedProfileImage);
    }

    @Override
    public void processFaceEnroll(String imgId) {
        Log.d(TAG, "Face Enrolled: Image Id = " + imgId);

        UserInfo userInfo = new UserInfo();

        FormatStrings formatStrings = new FormatStrings();
        userInfo.setmPhone(formatStrings.formatPhone(registeredPhone.getText().toString()));
        userInfo.setmPassword(registeredPassword.getText().toString());
        userInfo.setmName(registeredName.getText().toString());
        userInfo.setmSurname(registeredSurname.getText().toString());
        if (registeredMiddleName.getText() != null) {
            userInfo.setmMiddleName(registeredMiddleName.getText().toString());
        } else {
            userInfo.setmMiddleName("");
        }
        userInfo.setmEmail(registeredEmail.getText().toString());
        userInfo.setmCompany(registeredCompany.getText().toString());
        userInfo.setmTitle(registeredTitle.getText().toString());
        userInfo.setmImageId(imgId);

        Register register = new Register();
        register.delegate = this;
        register.execute(userInfo);
    }

    @Override
    public void nullDataOnFaceEnroll() {
        //TODO face not enrolled mesajı döndürülecek...
        Log.d(TAG, "NullDataReturned... Face not enrolled.");

    }

    @Override
    public void processRegister(String registeredPhoneNumber) {
        //TODO onay mesajı gönderilecek...
    }

    @Override
    public void nullDataReturnedFromRegister() {
        //TODO kaydedilemedi hata bilgisi dönecek...
    }
}
