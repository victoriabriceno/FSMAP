package com.example.FSMap;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.auth.FirebaseUser;

//This screen handles registering the user

public class RegisterUser extends AppCompatActivity implements View.OnClickListener {
    private FirebaseAuth mAuth;
    private Button registerUser;
    private TextInputLayout editEmail, editPassword, confirmEmail, confirmPassword, editUser;
    private ProgressBar progressBar;
    private Button registerBack;
    ImageView registerBackButton;
    CheckBox checkBox;
    RelativeLayout loadingRegister;
    MaterialAlertDialogBuilder materialAlertDialogBuilder;

    //Register Screen
    @SuppressLint({"WrongViewCast", "MissingInflatedId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_user);

        mAuth = FirebaseAuth.getInstance();

        registerUser = findViewById(R.id.RegisterUser);
        registerUser.setOnClickListener(this);

        registerBackButton = findViewById(R.id.backBTN);
        registerBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent i = new Intent(RegisterUser.this, MainActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                SharedPreferences preferences = getSharedPreferences("checkBox", MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("remember", "false");
                editor.apply();
                startActivity(i);
                finish();
            }
        });
        editEmail = findViewById(R.id.registerEmailAddress);
        editPassword = findViewById(R.id.registerPassword);
        confirmPassword = findViewById(R.id.ConfirmPassword);
        editUser = findViewById(R.id.Username);

        progressBar = (ProgressBar) findViewById(R.id.ProgressBar);
        loadingRegister = (RelativeLayout) findViewById(R.id.LoadingDesignRegister);

        checkBox = findViewById(R.id.checkboxTerms);
        materialAlertDialogBuilder = new MaterialAlertDialogBuilder(this);

        registerUser.setEnabled(false);
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            //Prompt the user with TOS, they must accept it before their account can be created.
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {
                    materialAlertDialogBuilder.setTitle("Terms and Conditions");
                    materialAlertDialogBuilder.setMessage("Privacy Policy\n\n" +
                            "Last updated: January 09, 2023\n\n" +
                            "This Privacy Policy describes Our policies and procedures on the collection, use and disclosure of Your information when You use the Service and tells You about Your privacy rights and how the law protects You.\n" +
                            "We use Your Personal data to provide and improve the Service. By using the Service, You agree to the collection and use of information in accordance with this Privacy Policy. This Privacy Policy has been created with the help of the TermsFeed Privacy Policy Generator.\n" +
                            "Interpretation and Definitions\n\n" +
                            "Interpretation\n\n" +
                            "The words of which the initial letter is capitalized have meanings defined under the following conditions. The following definitions shall have the same meaning regardless of whether they appear in singular or in plural.\n\n" +
                            "Definitions\n\n" +
                            "For the purposes of this Privacy Policy:\n\n" +
                            "Account means a unique account created for You to access our Service or parts of our Service.\n" +
                            "Affiliate means an entity that controls, is controlled by or is under common control with a party, where \"control\" means ownership of 50% or more of the shares, equity interest or other securities entitled to vote for election of directors or other managing authority.\n" +
                            "Application refers to FS Map, the software program provided by the Company.\n" +
                            "Company (referred to as either \"the Company\", \"We\", \"Us\" or \"Our\" in this Agreement) refers to FS Map.\n" +
                            "Country refers to: Florida, United States\n" +
                            "Device means any device that can access the Service such as a computer, a cellphone or a digital tablet.\n" +
                            "Personal Data is any information that relates to an identified or identifiable individual.\n" +
                            "Service refers to the Application.\n" +
                            "Service Provider means any natural or legal person who processes the data on behalf of the Company. It refers to third-party companies or individuals employed by the Company to facilitate the Service, to provide the Service on behalf of the Company, to perform services related to the Service or to assist the Company in analyzing how the Service is used.\n" +
                            "Third-party Social Media Service refers to any website or any social network website through which a User can log in or create an account to use the Service.\n" +
                            "Usage Data refers to data collected automatically, either generated by the use of the Service or from the Service infrastructure itself (for example, the duration of a page visit).\n" +
                            "You means the individual accessing or using the Service, or the company, or other legal entity on behalf of which such individual is accessing or using the Service, as applicable.\n\n" +
                            "Collecting and Using Your Personal Data\n\n" +
                            "Types of Data Collected\n" +
                            "Personal Data\n" +
                            "While using Our Service, We may ask You to provide Us with certain personally identifiable information that can be used to contact or identify You. Personally identifiable information may include, but is not limited to:\n" +
                            "\tEmail address\n" +
                            "\tUsage Data\n\n" +
                            "Usage Data\n" +
                            "Usage Data is collected automatically when using the Service.\n" +
                            "Usage Data may include information such as Your Device's Internet Protocol address (e.g. IP address), browser type, browser version, the pages of our Service that You visit, the time and date of Your visit, the time spent on those pages, unique device identifiers and other diagnostic data.\n" +
                            "When You access the Service by or through a mobile device, We may collect certain information automatically, including, but not limited to, the type of mobile device You use, Your mobile device unique ID, the IP address of Your mobile device, Your mobile operating system, the type of mobile Internet browser You use, unique device identifiers and other diagnostic data.\n" +
                            "We may also collect information that Your browser sends whenever You visit our Service or when You access the Service by or through a mobile device.\n\n" +
                            "Information from Third-Party Social Media Services\n\n" +
                            "The Company allows You to create an account and log in to use the Service through the following Third-party Social Media Services:\n" +
                            "•\tGoogle\n" +
                            "•\tFacebook\n" +
                            "•\tTwitter\n" +
                            "•\tLinkedIn\n" +
                            "If You decide to register through or otherwise grant us access to a Third-Party Social Media Service, We may collect Personal data that is already associated with Your Third-Party Social Media Service's account, such as Your name, Your email address, Your activities or Your contact list associated with that account.\n\n" +
                            "You may also have the option of sharing additional information with the Company through Your Third-Party Social Media Service's account. If You choose to provide such information and Personal Data, during registration or otherwise, You are giving the Company permission to use, share, and store it in a manner consistent with this Privacy Policy.\n\n" +
                            "Information Collected while Using the Application\n\n" +
                            "While using Our Application, in order to provide features of Our Application, We may collect, with Your prior permission:\n" +
                            "\tInformation regarding your location\n" +
                            "\tPictures and other information from your Device's camera and photo library\n" +
                            "We use this information to provide features of Our Service, to improve and customize Our Service. The information may be uploaded to the Company's servers and/or a Service Provider's server or it may be simply stored on Your device.\n" +
                            "You can enable or disable access to this information at any time, through Your Device settings.\n\n" +
                            "Use of Your Personal Data\n\n" +
                            "The Company may use Personal Data for the following purposes:\n\n" +
                            "To provide and maintain our Service, including to monitor the usage of our Service.\n" +
                            "To manage Your Account: to manage Your registration as a user of the Service. The Personal Data You provide can give You access to different functionalities of the Service that are available to You as a registered user.\n" +
                            "For the performance of a contract: the development, compliance and undertaking of the purchase contract for the products, items or services You have purchased or of any other contract with Us through the Service.\n" +
                            "To contact You: To contact You by email, telephone calls, SMS, or other equivalent forms of electronic communication, such as a mobile application's push notifications regarding updates or informative communications related to the functionalities, products or contracted services, including the security updates, when necessary or reasonable for their implementation.\n" +
                            "To provide You with news, special offers and general information about other goods, services and events which we offer that are similar to those that you have already purchased or enquired about unless You have opted not to receive such information.\n" +
                            "To manage Your requests: To attend and manage Your requests to Us.\n" +
                            "For business transfers: We may use Your information to evaluate or conduct a merger, divestiture, restructuring, reorganization, dissolution, or other sale or transfer of some or all of Our assets, whether as a going concern or as part of bankruptcy, liquidation, or similar proceeding, in which Personal Data held by Us about our Service users is among the assets transferred.\n" +
                            "For other purposes: We may use Your information for other purposes, such as data analysis, identifying usage trends, determining the effectiveness of our promotional campaigns and to evaluate and improve our Service, products, services, marketing and your experience.\n" +
                            "We may share Your personal information in the following situations:\n" +
                            "•\tWith Service Providers: We may share Your personal information with Service Providers to monitor and analyze the use of our Service, to contact You.\n" +
                            "•\tFor business transfers: We may share or transfer Your personal information in connection with, or during negotiations of, any merger, sale of Company assets, financing, or acquisition of all or a portion of Our business to another company.\n" +
                            "•\tWith Affiliates: We may share Your information with Our affiliates, in which case we will require those affiliates to honor this Privacy Policy. Affiliates include Our parent company and any other subsidiaries, joint venture partners or other companies that We control or that are under common control with Us.\n" +
                            "•\tWith business partners: We may share Your information with Our business partners to offer You certain products, services or promotions.\n" +
                            "•\tWith other users: when You share personal information or otherwise interact in the public areas with other users, such information may be viewed by all users and may be publicly distributed outside. If You interact with other users or register through a Third-Party Social Media Service, Your contacts on the Third-Party Social Media Service may see Your name, profile, pictures and description of Your activity. Similarly, other users will be able to view descriptions of Your activity, communicate with You and view Your profile.\n" +
                            "•\tWith Your consent: We may disclose Your personal information for any other purpose with Your consent.\n\n" +
                            "Retention of Your Personal Data\n\n" +
                            "The Company will retain Your Personal Data only for as long as is necessary for the purposes set out in this Privacy Policy. We will retain and use Your Personal Data to the extent necessary to comply with our legal obligations (for example, if we are required to retain your data to comply with applicable laws), resolve disputes, and enforce our legal agreements and policies.\n" +
                            "The Company will also retain Usage Data for internal analysis purposes. Usage Data is generally retained for a shorter period of time, except when this data is used to strengthen the security or to improve the functionality of Our Service, or We are legally obligated to retain this data for longer time periods.\n\n" +
                            "Transfer of Your Personal Data\n\n" +
                            "Your information, including Personal Data, is processed at the Company's operating offices and in any other places where the parties involved in the processing are located. It means that this information may be transferred to — and maintained on — computers located outside of Your state, province, country or other governmental jurisdiction where the data protection laws may differ than those from Your jurisdiction.\n" +
                            "Your consent to this Privacy Policy followed by Your submission of such information represents Your agreement to that transfer.\n" +
                            "The Company will take all steps reasonably necessary to ensure that Your data is treated securely and in accordance with this Privacy Policy and no transfer of Your Personal Data will take place to an organization or a country unless there are adequate controls in place including the security of Your data and other personal information.\n\n" +
                            "Delete Your Personal Data\n\n" +
                            "You have the right to delete or request that We assist in deleting the Personal Data that We have collected about You.\n" +
                            "Our Service may give You the ability to delete certain information about You from within the Service.\n" +
                            "You may update, amend, or delete Your information at any time by signing in to Your Account, if you have one, and visiting the account settings section that allows you to manage Your personal information. You may also contact Us to request access to, correct, or delete any personal information that You have provided to Us.\n" +
                            "Please note, however, that We may need to retain certain information when we have a legal obligation or lawful basis to do so.\n\n" +
                            "Disclosure of Your Personal Data\n\n" +
                            "Business Transactions\n" +
                            "If the Company is involved in a merger, acquisition or asset sale, Your Personal Data may be transferred. We will provide notice before Your Personal Data is transferred and becomes subject to a different Privacy Policy.\n" +
                            "Law enforcement\n" +
                            "Under certain circumstances, the Company may be required to disclose Your Personal Data if required to do so by law or in response to valid requests by public authorities (e.g. a court or a government agency).\n" +
                            "Other legal requirements\n" +
                            "The Company may disclose Your Personal Data in the good faith belief that such action is necessary to:\n" +
                            "•\tComply with a legal obligation\n" +
                            "•\tProtect and defend the rights or property of the Company\n" +
                            "•\tPrevent or investigate possible wrongdoing in connection with the Service\n" +
                            "•\tProtect the personal safety of Users of the Service or the public\n" +
                            "•\tProtect against legal liability\n" +
                            "Security of Your Personal Data\n" +
                            "The security of Your Personal Data is important to Us, but remember that no method of transmission over the Internet, or method of electronic storage is 100% secure. While We strive to use commercially acceptable means to protect Your Personal Data, We cannot guarantee its absolute security.\n\n" +
                            "Children's Privacy\n\n" +
                            "Our Service does not address anyone under the age of 13. We do not knowingly collect personally identifiable information from anyone under the age of 13. If You are a parent or guardian and You are aware that Your child has provided Us with Personal Data, please contact Us. If We become aware that We have collected Personal Data from anyone under the age of 13 without verification of parental consent, We take steps to remove that information from Our servers.\n" +
                            "If We need to rely on consent as a legal basis for processing Your information and Your country requires consent from a parent, We may require Your parent's consent before We collect and use that information.\n\n" +
                            "Links to Other Websites\n\n" +
                            "Our Service may contain links to other websites that are not operated by Us. If You click on a third party link, You will be directed to that third party's site. We strongly advise You to review the Privacy Policy of every site You visit.\n" +
                            "We have no control over and assume no responsibility for the content, privacy policies or practices of any third party sites or services.\n\n" +
                            "Changes to this Privacy Policy\n\n" +
                            "We may update Our Privacy Policy from time to time. We will notify You of any changes by posting the new Privacy Policy on this page.\n" +
                            "We will let You know via email and/or a prominent notice on Our Service, prior to the change becoming effective and update the \"Last updated\" date at the top of this Privacy Policy.\n" +
                            "You are advised to review this Privacy Policy periodically for any changes. Changes to this Privacy Policy are effective when they are posted on this page.\n\n" +
                            "Contact Us\n\n" +
                            "If you have any questions about this Privacy Policy, You can contact us:\n" +
                            "•\tBy email: swiftkingspp@gmail.com\n");

                    materialAlertDialogBuilder.setPositiveButton("Accept", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            registerUser.setEnabled(true);
                            dialog.dismiss();
                        }
                    });

                    materialAlertDialogBuilder.setNegativeButton("Decline", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            checkBox.setChecked(false);

                        }
                    });

                    materialAlertDialogBuilder.show();
                } else {
                    registerUser.setEnabled(false);
                }
            }
        });

    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.RegisterUser:
                registerUser();
                break;
        }
    }

    //Performs all the checks required to create an account before attempting to create the account
    private void registerUser() {

        //Grabbing all the inputs
        String userName = editUser.getEditText().getText().toString();
        String email = editEmail.getEditText().getText().toString();
        String password = editPassword.getEditText().getText().toString();
        String confirmP = confirmPassword.getEditText().getText().toString();

        //Check for empty usernam field
        if (userName.isEmpty()) {
            Toast.makeText(this, "An username is required!", Toast.LENGTH_SHORT).show();
            editUser.requestFocus();
            return;
        }
        //Check for empty email field
        if (email.isEmpty()) {

            Toast.makeText(this, "An email is required!", Toast.LENGTH_SHORT).show();
            editEmail.requestFocus();
            return;
        }
        //Check for valid email
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {

            Toast.makeText(this, "Please provide a valid email", Toast.LENGTH_SHORT).show();
            editEmail.requestFocus();
            return;
        }
        //Check for empty password field
        if (password.isEmpty()) {
            Toast.makeText(this, "A password is require!", Toast.LENGTH_SHORT).show();
            editPassword.requestFocus();
            return;
        }
        //Check for empty confirm password field
        if (confirmP.isEmpty()) {
            Toast.makeText(this, "You need to confirm the password", Toast.LENGTH_SHORT).show();
            confirmPassword.requestFocus();
            return;
        }
        //Check for a password longer than 6 characters
        if (password.length() < 6) {
            Toast.makeText(this, "The password should be at least 6 characters", Toast.LENGTH_SHORT).show();
            editPassword.requestFocus();
            return;
        }
        //Check for a password longer than 6 characters
        if (confirmP.length() < 6) {
            Toast.makeText(this, "The password should be at least 6 characters", Toast.LENGTH_SHORT).show();
            confirmPassword.requestFocus();
            return;
        }
        //Check both passwords match
        if (!password.equals(confirmP)) {
            Toast.makeText(this, "Password doesn't match", Toast.LENGTH_SHORT).show();
            confirmPassword.requestFocus();
            return;
        } else {
            //Toast.makeText(this, "The password match", Toast.LENGTH_SHORT).show();
        }

        //Attempt account creation with the provided username and password.
        loadingRegister.setVisibility(View.VISIBLE);
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            User user = new User(email, userName);
                            FirebaseDatabase.getInstance().getReference("Users")
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                            if (task.isSuccessful()) {
                                                user.sendEmailVerification();
                                                FirebaseAuth.getInstance().signOut();
                                                Toast.makeText(RegisterUser.this, "Check your email box to verify the email!", Toast.LENGTH_LONG).show();
                                                Intent i = new Intent(RegisterUser.this, MainActivity.class);
                                                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                SharedPreferences preferences = getSharedPreferences("checkBox", MODE_PRIVATE);
                                                SharedPreferences.Editor editor = preferences.edit();
                                                editor.putString("remember", "false");
                                                editor.apply();
                                                startActivity(i);
                                                finish();


                                            } else {
                                                Toast.makeText(RegisterUser.this, "Failed to register user or account already exist!", Toast.LENGTH_LONG).show();
                                            }
                                            loadingRegister.setVisibility(View.GONE);

                                        }
                                    });
                        } else {
                            Toast.makeText(RegisterUser.this, "Failed to register user or account already exist!", Toast.LENGTH_LONG).show();
                            loadingRegister.setVisibility(View.GONE);
                        }
                    }
                });

    }
}