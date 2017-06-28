package rayan.avik.signinwithgoogle;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener,GoogleApiClient.OnConnectionFailedListener {

    private LinearLayout profileSection;
    private RelativeLayout mainLayout;
    private Button signOut;
    private SignInButton signinButton;
    private TextView name, email;
    private CircleImageView userImage;
    private GoogleApiClient googleApiClient;
    private static final int REQ_CODE = 9001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        profileSection = (LinearLayout) findViewById(R.id.user_details_pannel);
        mainLayout = (RelativeLayout) findViewById(R.id.main_layout);
        signOut = (Button) findViewById(R.id.btn_logout);
        signinButton = (SignInButton) findViewById(R.id.btn_signin);
        name = (TextView) findViewById(R.id.user_name);
        email = (TextView) findViewById(R.id.user_email_address);
        userImage = (CircleImageView) findViewById(R.id.profile_image);

        signinButton.setOnClickListener(this);
        signOut.setOnClickListener(this);

        profileSection.setVisibility(View.GONE);

        GoogleSignInOptions signInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        googleApiClient = new GoogleApiClient.Builder(this).enableAutoManage(this,this).addApi(Auth.GOOGLE_SIGN_IN_API,signInOptions).build();

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){

            case R.id.btn_signin :
                signIn();
                break;
            case R.id.btn_logout :
                signOut();
                break;
        }

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private void signIn(){

        Intent intent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
        startActivityForResult(intent,REQ_CODE);

    }

    private  void  signOut(){

        Auth.GoogleSignInApi.signOut(googleApiClient).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(@NonNull Status status) {
                signOut.setVisibility(View.GONE);
                Snackbar.make(mainLayout,"Logout Successfully",Snackbar.LENGTH_LONG).show();
                updateUI(false);
            }
        });

    }

    private void handleResult(GoogleSignInResult result){

        if (result.isSuccess()){
            GoogleSignInAccount account = result.getSignInAccount();
            String userName = account.getDisplayName();
            String emailAddress = account.getEmail();
            //String imgURL = account.getPhotoUrl().toString();

            if (account.getPhotoUrl()==null){
                userImage.setImageResource(R.drawable.ic_person);
            }else{
                Picasso.with(this).load(account.getPhotoUrl().toString())
                        .placeholder(R.drawable.ic_person)
                        .error(R.drawable.ic_person)
                        .into(userImage);
            }
            name.setText(userName);
            email.setText(emailAddress);

            updateUI(true);

        }else {

            updateUI(false);
        }
    }

    private void updateUI(Boolean isLogin){

        if (isLogin){

            profileSection.setVisibility(View.VISIBLE);
            signinButton.setVisibility(View.GONE);
            signOut.setVisibility(View.VISIBLE);
            Snackbar.make(mainLayout,"Login Successfully",Snackbar.LENGTH_LONG).show();


        }else{

            profileSection.setVisibility(View.GONE);
            signinButton.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==REQ_CODE){

            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleResult(result);
        }
    }
}
