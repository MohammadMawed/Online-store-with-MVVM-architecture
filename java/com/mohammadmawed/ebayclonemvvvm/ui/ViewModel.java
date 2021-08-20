package com.mohammadmawed.ebayclonemvvvm.ui;

import android.app.Application;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseUser;
import com.mohammadmawed.ebayclonemvvvm.data.AppRepo;

public class ViewModel extends AndroidViewModel {

    private final AppRepo appRepo;
    private final MutableLiveData<FirebaseUser> userMutableLiveData;
    private final MutableLiveData<Boolean> loggedOutMutableLiveData;
    private final MutableLiveData<String> documentSnapshotMutableLiveData;
    private final MutableLiveData<Uri> uriProfilePocMutableLiveData;


    public ViewModel(@NonNull Application application) {
        super(application);

        appRepo = new AppRepo(application);
        userMutableLiveData = appRepo.getUserMutableLiveData();
        loggedOutMutableLiveData = appRepo.getLoggedOutMutableLiveData();
        documentSnapshotMutableLiveData = appRepo.getUsernameMutableLiveData();
        uriProfilePocMutableLiveData = appRepo.getUriProfilePicMutableLiveData();
    }

    public MutableLiveData<Boolean> getLoggedOutMutableLiveData() {
        return loggedOutMutableLiveData;
    }

    public void login(String email, String password){
        appRepo.loginUsers(email, password);
    }


    public MutableLiveData<FirebaseUser> getUserMutableLiveData() {
        return userMutableLiveData;
    }

    public MutableLiveData<String> getUsernameMutableLiveData() {
        return documentSnapshotMutableLiveData;
    }

    public MutableLiveData<Uri> getUriProfilePocMutableLiveData() {
        return uriProfilePocMutableLiveData;
    }
}
