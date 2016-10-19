package com.example.mg_win.facebusiness.Activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.renderscript.ScriptGroup;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.app.LoaderManager.LoaderCallbacks;

import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.text.Editable;
import android.text.Html;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mg_win.facebusiness.R;
import com.example.mg_win.facebusiness.Services.Login;
import com.example.mg_win.facebusiness.Services.LoginResponse;
import com.example.mg_win.facebusiness.Utils.FormatStrings;
import com.example.mg_win.facebusiness.Utils.UserInfo;

import java.util.ArrayList;
import java.util.List;

import static android.Manifest.permission.READ_CONTACTS;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity implements LoaderCallbacks<Cursor>, LoginResponse {

    /**
     * Id to identity READ_CONTACTS permission request.
     */
    private static final int REQUEST_READ_CONTACTS = 0;

    private static String TAG = "LoginActivity";

    /**
     * A dummy authentication store containing known user names and passwords.
     * TODO: remove after connecting to a real authentication system.
     */
    private static final String[] DUMMY_CREDENTIALS = new String[]{
            "foo@example.com:hello", "bar@example.com:world"
    };
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */


    private EditText mLoginPhone;
    private EditText mLoginPassword;
    private ImageView mLoginPasswordButton;
    private Button mLoginButton;
    private TextView mSignUpText;
    private TextView mLoginErrorText;


    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        mContext = this.getApplicationContext();
        mSignUpText = (TextView) findViewById(R.id.link_signup);
        mSignUpText.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, RegisterActivity.class);
                startActivity(intent);
            }
        });


        mLoginPasswordButton = (ImageView) findViewById(R.id.loginPasswordImage);
        mLoginPassword = (EditText) findViewById(R.id.loginPassword);
        mLoginPasswordButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        mLoginPassword.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
                        break;
                    case MotionEvent.ACTION_UP:
                        mLoginPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                        break;
                    default:
                        break;
                }
                return true;
            }
        });

        // EditText Phone Number Format
        mLoginPhone = (EditText) findViewById(R.id.loginPhone);
        mLoginPhone.addTextChangedListener(new PhoneNumberFormattingTextWatcher() {

            // User is erasing or input new char
            private boolean backSpacingFlag = false;
            // Block :afterTextChanges
            private boolean editedFlag = false;
            // Mark cursor position and restore it after edit
            private int cursorComplement;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                cursorComplement = s.length() - mLoginPhone.getSelectionStart();

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
                        mLoginPhone.setText(ans);
                        mLoginPhone.setSelection(mLoginPhone.getText().length() - cursorComplement);

                    } else if (phoneNumber.length() >= 6 && !backSpacingFlag) {
                        editedFlag = true;

                        String ans = "(" + phoneNumber.substring(0, 3) + ") " + phoneNumber.substring(3, 6) + " " + phoneNumber.substring(6);
                        mLoginPhone.setText(ans);
                        mLoginPhone.setSelection(mLoginPhone.getText().length() - cursorComplement);

                    } else if (phoneNumber.length() >= 3 && !backSpacingFlag) {
                        editedFlag = true;

                        String ans = "(" + phoneNumber.substring(0, 3) + ") " + phoneNumber.substring(3);
                        mLoginPhone.setText(ans);
                        mLoginPhone.setSelection(mLoginPhone.getText().length() - cursorComplement);

                    }
                } else {
                    editedFlag = false;
                }

            }
        });


    }

    public void loginClickedEvent(View view) {
        mLoginPhone = (EditText) findViewById(R.id.loginPhone);
        mLoginPassword = (EditText) findViewById(R.id.loginPassword);

        mLoginErrorText = (TextView) findViewById(R.id.loginErrorResult);
        mLoginErrorText.setText("");


        if (mLoginPhone.getText().toString() != null && mLoginPassword.getText().toString() != null ) {
            if (loginAttempt(mLoginPhone.getText().toString(), mLoginPassword.getText().toString())) {

                FormatStrings format = new FormatStrings();
                String phone = format.formatPhone(mLoginPhone.getText().toString());

                Login login = new Login();
                login.delegate = this;
                login.execute(phone, mLoginPassword.getText().toString());
            }
        }
    }


    private boolean loginAttempt(String phone, String password) {

        if (isPhoneValid() && isPasswordValid()) {
            return true;
        } else {
            return false;
        }

    }

    private boolean isPhoneValid() {

        boolean flag = false;

        mLoginPhone = (EditText) findViewById(R.id.loginPhone);
        mLoginPhone.setError(null);

        if (mLoginPhone.getText().length() != 15) {
            Log.d(TAG, "Login Field is Invalid: " + "Phone");
            mLoginPhone.setError(Html.fromHtml(getResources().getString(R.string.error_login_phone)));

            flag = false;
        } else {
            flag = true;
        }
        return flag;
    }

    private boolean isPasswordValid() {
        boolean flag = false;

        mLoginPassword = (EditText) findViewById(R.id.loginPassword);
        mLoginPassword.setError(null);

        if (mLoginPassword.getText().length() < 4) {
            Log.d(TAG, "Login Field is Invalid: " + "Password");
            mLoginPassword.setError(Html.fromHtml(getResources().getString(R.string.error_login_password)));

            flag = false;
        } else {
            flag = true;
        }
        return flag;
    }


    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }


    // Login Service Override Methods
    @Override
    public void processLogin(UserInfo userInfo) {

        Log.d(TAG, "Login: userName=" + userInfo.getmName() + " userSurname=" + userInfo.getmSurname());
/*
        Intent intent = new Intent(mContext, MainMenu.class);
        startActivity(intent);
        */
    }

    // Login Service Override Methods
    @Override
    public void nullDataReturned() {
        Log.d(TAG, "Login Service : Null Data Returned");
        mLoginErrorText = (TextView) findViewById(R.id.loginErrorResult);
        mLoginErrorText.setText("*Username or password is incorrect!");

    }
}

