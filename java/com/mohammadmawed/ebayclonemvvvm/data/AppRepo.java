package com.mohammadmawed.ebayclonemvvvm.data;

import android.annotation.SuppressLint;
import android.app.Application;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.lifecycle.MutableLiveData;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.mohammadmawed.ebayclonemvvvm.R;
import com.mohammadmawed.ebayclonemvvvm.ui.OffersAdapter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;


public class AppRepo {

    private final Application application;

    private final MutableLiveData<FirebaseUser> userMutableLiveData;
    private final MutableLiveData<Boolean> loggedOutMutableLiveData;
    private final MutableLiveData<String> usernameMutableLiveData;
    private final MutableLiveData<Uri> uriProfilePicMutableLiveData;


    private final FirebaseAuth firebaseAuth;
    private final DatabaseReference databaseReference;
    private final StorageReference storageReference;


    @RequiresApi(api = Build.VERSION_CODES.P)
    public AppRepo(Application application){

        this.application = application;

        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("offers");
        storageReference = FirebaseStorage.getInstance().getReference();

        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();


        userMutableLiveData = new MutableLiveData<>();
        loggedOutMutableLiveData = new MutableLiveData<>();
        usernameMutableLiveData = new MutableLiveData<>();
        uriProfilePicMutableLiveData = new MutableLiveData<>();

        if (firebaseAuth.getCurrentUser() != null){

            String userID = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

            userMutableLiveData.postValue(firebaseAuth.getCurrentUser());
            loggedOutMutableLiveData.postValue(false);

            DocumentReference documentReference = firebaseFirestore.collection("users").document(userID);
            documentReference.addSnapshotListener( application.getMainExecutor(), new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                    assert value != null;
                    usernameMutableLiveData.postValue(value.getString("username"));
                }
            });

            StorageReference fileRef = storageReference.child("users/" + firebaseAuth.getCurrentUser().getUid() + "/profile.jpg");
            fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    uriProfilePicMutableLiveData.postValue(uri);
                }
            });

        }
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    public void loginUsers(String email, String password){

        firebaseAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(application.getMainExecutor(), new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    userMutableLiveData.postValue(firebaseAuth.getCurrentUser());
                }else {
                    Toast.makeText(application, "Logging in failed: " + task.getException(), Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    public void logOutUsers(){
        firebaseAuth.signOut();
        loggedOutMutableLiveData.postValue(true);
    }



    public MutableLiveData<List<FirebaseRecyclerAdapter<OffersModelClass, OffersAdapter>>> getOfferDataMutableLiveData(){

        MutableLiveData<List<FirebaseRecyclerAdapter<OffersModelClass, OffersAdapter>>> data = new MutableLiveData<>();
        //data.postValue();
        return data;
    }

    public MutableLiveData<FirebaseUser> getUserMutableLiveData() {
        return userMutableLiveData;
    }

    public MutableLiveData<Boolean> getLoggedOutMutableLiveData() {
        return loggedOutMutableLiveData;
    }

    public MutableLiveData<String> getUsernameMutableLiveData() {
        return usernameMutableLiveData;
    }

    public MutableLiveData<Uri> getUriProfilePicMutableLiveData() {
        return uriProfilePicMutableLiveData;
    }
}
