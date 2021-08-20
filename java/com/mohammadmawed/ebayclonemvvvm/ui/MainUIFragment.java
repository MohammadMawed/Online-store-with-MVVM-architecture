package com.mohammadmawed.ebayclonemvvvm.ui;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.mohammadmawed.ebayclonemvvvm.R;
import com.mohammadmawed.ebayclonemvvvm.data.OffersModelClass;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class MainUIFragment extends Fragment {

    ImageView profilePic;
    ImageButton settingButton, chattingButton;
    TextView usernameTextview;
    EditText searchEditText;
    RecyclerView recyclerView;

    public DatabaseReference databaseReference;
    public  StorageReference storageReference;

    FirebaseRecyclerOptions<OffersModelClass> options;
    FirebaseRecyclerAdapter<OffersModelClass, OffersAdapter> adapter;

    public ViewModel viewModel;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        databaseReference = FirebaseDatabase.getInstance().getReference().child("offers");
        storageReference = FirebaseStorage.getInstance().getReference();

        ViewModel viewModel = ViewModelProviders.of(this).get(ViewModel.class);
        viewModel.getUserMutableLiveData().observe(this, new Observer<FirebaseUser>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onChanged(FirebaseUser firebaseUser) {
                if (firebaseUser != null) {

                }
            }
        });
        viewModel.getUsernameMutableLiveData().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                usernameTextview.setText(s);
            }
        });
        viewModel.getLoggedOutMutableLiveData().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (aBoolean) {
                    Navigation.findNavController(requireView()).navigate(R.id.action_mainUIFragment_to_loginFragment);
                }
            }
        });
        viewModel.getUriProfilePocMutableLiveData().observe(this, new Observer<Uri>() {
            @Override
            public void onChanged(Uri uri) {
                Picasso.get().load(uri).into(profilePic);
            }
        });
    }

    public void loadData(String Data) {
        Query searchQuery = databaseReference.orderByChild("category").startAt(Data).endAt(Data + "\uf8ff");
        options = new FirebaseRecyclerOptions.Builder<OffersModelClass>().setQuery(searchQuery, OffersModelClass.class).build();
        adapter = new FirebaseRecyclerAdapter<OffersModelClass, OffersAdapter>(options) {
            @NonNull
            @Override
            public OffersAdapter onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_view, parent, false);
                return new OffersAdapter(view);
            }

            @SuppressLint("SetTextI18n")
            @Override
            protected void onBindViewHolder(@NonNull OffersAdapter holder, int position, @NonNull OffersModelClass model) {

                String timeAgo = calculateTimeAge(model.getTime());
                holder.titleTextView.setText(model.getTitle());
                holder.timeTextView.setText(timeAgo);
                holder.priceTextview.setText(model.getPrice() + "€");
                holder.locationTextView.setText(model.getCity());
                holder.timeTextView.setText(timeAgo);
                String UserID = model.getUserID();
                String ImageID = model.getImageID();
                StorageReference fileRef11 = FirebaseStorage.getInstance().getReference().child("offers/" + ImageID + ".jpg");
                fileRef11.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Glide.with(requireActivity().getApplicationContext()).load(uri).centerCrop().into(holder.imageView);
                    }
                });
                holder.itemViewView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        databaseReference.child("views").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()){
                                    String viewNumberText = snapshot.child("views").child("views").getValue().toString();
                                    int views = Integer.parseInt(viewNumberText);
                                    views = views + 1;
                                    String viewNumberText1 = String.valueOf(views);
                                    HashMap<String, String> hashMap = new HashMap<>();
                                    hashMap.put("views", viewNumberText1);
                                    databaseReference.child("views").removeValue();
                                    databaseReference.child(ImageID).child("views").setValue(hashMap);
                                    /*startActivity(new Intent(MainUI.this, ViewSingleItem.class).putExtra("postID", ImageID).putExtra("views", viewNumberText1)
                                            .putExtra("time", time).putExtra("providerUserID", UserID).putExtra("data", getRef(position).getKey()));*/
                                }else{
                                    int views = 0;
                                    views +=1;
                                    String viewNumberText = String.valueOf(views);
                                    HashMap<String, String> hashMap = new HashMap<>();
                                    hashMap.put("views", viewNumberText);
                                    databaseReference.child(ImageID).child("views").setValue(hashMap);
                                    /*startActivity(new Intent(MainUI.this, ViewSingleItem.class).putExtra("postID", ImageID).putExtra("views", viewNumberText)
                                            .putExtra("time", time).putExtra("providerUserID", UserID).putExtra("data", getRef(position).getKey()));*/
                                }

                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                        //The image ID is the same like post ID

                    }
                });
            }
        };
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    private String calculateTimeAge(String time) {
        String convTime = null;
        String prefix = "";
        String suffix = "ago";

        try {
            @SuppressLint("SimpleDateFormat")
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm a");
            Date pasTime = dateFormat.parse(time);
            Date nowTime = new Date();
            long dateDiff = nowTime.getTime() - pasTime.getTime();
            long second = TimeUnit.MILLISECONDS.toSeconds(dateDiff);
            long minute = TimeUnit.MILLISECONDS.toMinutes(dateDiff);
            long hour = TimeUnit.MILLISECONDS.toHours(dateDiff);
            long day = TimeUnit.MILLISECONDS.toDays(dateDiff);
            if (second < 60) {
                convTime = second + " Seconds " + suffix;
            } else if (minute < 60) {
                convTime = minute + " Minutes " + suffix;
            } else if (hour < 24) {
                convTime = hour + " Hours " + suffix;
            } else if (day >= 7) {
                if (day > 360) {
                    convTime = (day / 360) + " Years " + suffix;
                } else if (day > 30) {
                    convTime = (day / 30) + " Months " + suffix;
                } else {
                    convTime = (day / 7) + " Week " + suffix;
                }
            } else if (day < 7) {
                convTime = day + " Days " + suffix;
            }

        } catch (ParseException e) {
            e.printStackTrace();
            Log.e("ConvTimeE", e.getMessage());
        }

        return convTime;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main_ui, container, false);

        profilePic = view.findViewById(R.id.profilePic);
        settingButton = view.findViewById(R.id.settingButton);
        chattingButton = view.findViewById(R.id.chattingButton);
        usernameTextview = view.findViewById(R.id.usernameTextView);
        searchEditText = view.findViewById(R.id.searchEditText);
        recyclerView = view.findViewById(R.id.recyclerView);

        SpacingItemInRecyclerView spacingItemInRecyclerView = new SpacingItemInRecyclerView(20);
        recyclerView.addItemDecoration(spacingItemInRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);

        loadData("");

        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString() != null) {
                    loadData(s.toString());
                } else {
                    loadData("");
                }
            }
        });

        return view;
    }
}
