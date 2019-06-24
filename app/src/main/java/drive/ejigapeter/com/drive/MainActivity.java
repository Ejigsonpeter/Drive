package drive.ejigapeter.com.drive;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
//import android.support.design.widget.Snackbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.rengwuxian.materialedittext.MaterialEditText;

import dmax.dialog.SpotsDialog;
import drive.ejigapeter.com.drive.model.user;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class MainActivity extends AppCompatActivity {
Button btnRegister,btnSignIn;
FirebaseAuth auth;
FirebaseDatabase db;
DatabaseReference users;
RelativeLayout rootLayout;
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //calll calligraphy
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/arkship.tff")
                .setFontAttrId(R.attr.fontPath)
                 .build());
        setContentView(R.layout.activity_main);

        //initialize Firebase
        auth = FirebaseAuth.getInstance();
        db = FirebaseDatabase.getInstance();
        users = db.getReference("users");

        //call the layout
        rootLayout = (RelativeLayout)findViewById(R.id.rootLayout);
        //initializing the View
        btnRegister = (Button)findViewById(R.id.btnRegister);
        btnSignIn = (Button)findViewById(R.id.btnSignIn);

        //create events and set onclick listiners

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showRegisterDialog();
            }
        });

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showLoginDialog();

            }
        });

    }

    private void showLoginDialog(){
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("SIGN IN");
        dialog.setMessage("Please use a valid email to sign");
        LayoutInflater inflater = LayoutInflater.from(this);
        View login_layout = inflater.inflate(R.layout.layout_login,null);


        final MaterialEditText edtEmail = login_layout.findViewById(R.id.editEmail);
        final MaterialEditText edtPassword = login_layout.findViewById(R.id.editPassword);

        dialog.setView(login_layout);
        dialog.setPositiveButton("SIGN IN", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                btnSignIn.setEnabled(false);



                //check validation
                if (TextUtils.isEmpty(edtEmail.getText().toString())) {
                    Snackbar.make(rootLayout, "Please enter email address", Snackbar.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(edtPassword.getText().toString())) {
                    Snackbar.make(rootLayout, "Please enter password", Snackbar.LENGTH_SHORT).show();
                    return;
                }
                if (edtPassword.getText().toString().length() < 6) {
                    Snackbar.make(rootLayout, "Password cannot be less than characters", Snackbar.LENGTH_SHORT).show();
                    return;
                }
                final SpotsDialog waitingDialog = new SpotsDialog(MainActivity.this);
                waitingDialog.show();
                //login
                auth.signInWithEmailAndPassword(edtEmail.getText().toString(),edtPassword.getText().toString())
                        .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) {
                                waitingDialog.dismiss();
                                startActivity(new Intent(MainActivity.this,Welcome.class));
                                finish();


                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Snackbar.make(rootLayout,"Error !!"+e.getMessage(),Snackbar.LENGTH_SHORT)
                                .show();
                    }
                });

            }
            });
        dialog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });


        dialog.show();

    }


    private void showRegisterDialog(){
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("REGISTER");
        dialog.setMessage("Please use a valid email to register");
        LayoutInflater inflater = LayoutInflater.from(this);
        View register_layout = inflater.inflate(R.layout.layout_register,null);

         final MaterialEditText edtEmail = register_layout.findViewById(R.id.editEmail);
         final MaterialEditText edtName = register_layout.findViewById(R.id.editName);
         final MaterialEditText edtPhone = register_layout.findViewById(R.id.editPhone);
         final MaterialEditText edtPassword = register_layout.findViewById(R.id.editPassword);

         dialog.setView(register_layout);
         dialog.setPositiveButton("REGISTER", new DialogInterface.OnClickListener() {
             @Override
             public void onClick(DialogInterface dialogInterface, int i) {
                 dialogInterface.dismiss();
                 //check validation
                 if (TextUtils.isEmpty(edtEmail.getText().toString())){
                    Snackbar.make(rootLayout,"Please enter email address",Snackbar.LENGTH_SHORT).show();
                    return;
                 }
                 if (TextUtils.isEmpty(edtPassword.getText().toString())){
                     Snackbar.make(rootLayout,"Please enter password",Snackbar.LENGTH_SHORT).show();
                     return;
                 }
                 if (edtPassword.getText().toString().length() < 6){
                     Snackbar.make(rootLayout,"Password cannot be less than characters",Snackbar.LENGTH_SHORT).show();
                     return;

                 }
                 if (TextUtils.isEmpty(edtName.getText().toString())){
                     Snackbar.make(rootLayout,"Please enter you name ",Snackbar.LENGTH_SHORT).show();
                     return;
                 }
                 if (TextUtils.isEmpty(edtPhone.getText().toString())){
                     Snackbar.make(rootLayout,"Please enter phone number",Snackbar.LENGTH_SHORT).show();
                     return;
                 }
                 auth.createUserWithEmailAndPassword(edtEmail.getText().toString(),edtPassword.getText().toString())
                                                        .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                                            @Override
                                                            public void onSuccess(AuthResult authResult) {
                                                                //save to database
                                                                user User = new user();
                                                                User.setEmail(edtEmail.getText().toString());
                                                                User.setName(edtName.getText().toString());
                                                                User.setPassword(edtPassword.getText().toString());
                                                                User.setPhone(edtPhone.getText().toString());


                                                                //use email to key
                                                                users.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                                        .setValue(User)
                                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                            @Override
                                                                            public void onSuccess(Void aVoid) {
                                                                                Snackbar.make(rootLayout,"Registration Successful !!",Snackbar.LENGTH_SHORT)
                                                                                        .show();
                                                                            }
                                                                        })
                                                                .addOnFailureListener(new OnFailureListener() {
                                                                    @Override
                                                                    public void onFailure(@NonNull Exception e) {
                                                                        Snackbar.make(rootLayout,"Failed!!" + e.getMessage(),Snackbar.LENGTH_SHORT)
                                                                                .show();
                                                                    }
                                                                });

                                                            }
                                                        })
                                                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<AuthResult> task) {
                                                                    Snackbar.make(rootLayout,"Failed",Snackbar.LENGTH_SHORT)
                                                                            .show();
                                                                }
                                                            });

             }
         });
         dialog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
             @Override
             public void onClick(DialogInterface dialogInterface, int i) {
                 dialogInterface.dismiss();
             }
         });
         dialog.show();


    }
}
