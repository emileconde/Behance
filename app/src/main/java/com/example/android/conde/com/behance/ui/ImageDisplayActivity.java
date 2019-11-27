package com.example.android.conde.com.behance.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.example.android.conde.com.behance.R;
import com.example.android.conde.com.behance.adapter.ImageRecyclerAdapter;
import com.example.android.conde.com.behance.models.Image;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ImageDisplayActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "ImageDisplayActivity";
    private RecyclerView mImageReyclerView;
    private FloatingActionButton mFabAddImageButton;
    private FirebaseAuth mAuth;
    private FirebaseFirestore mDb;
    private List<Image> mImageList;
    private ImageRecyclerAdapter mImageRecyclerAdapter;
    private ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_display);
        mImageReyclerView = findViewById(R.id.recyclerView);
        mFabAddImageButton = findViewById(R.id.fab_add);
        mAuth = FirebaseAuth.getInstance();
        mFabAddImageButton.setOnClickListener(this);
        mProgressBar = findViewById(R.id.img_display_progress_bar);
        mImageList = new ArrayList<>();
        mDb = FirebaseFirestore.getInstance();
        showProgressBar();
        CollectionReference uploadsCollection = mDb.collection("uploads");

                uploadsCollection.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            hideProgressBar();
                            if(mImageList.size() > 0)
                                mImageList.clear();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                //Log.d(TAG, document.getId() + " => " + document.getData());
                                String url = (String) document.get("imageUrl");
                                String name = (String) document.get("name");
                                Log.d(TAG, "onComplete: "+url+" Name"+name);
                                mImageList.add(new Image(name, url));
                                mImageRecyclerAdapter.notifyDataSetChanged();
                            }
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });

                Log.i(TAG, "onCreate: List size "+mImageList.size());

                initRecyclerView();

    }



    private void showProgressBar(){
        mProgressBar.setVisibility(View.VISIBLE);
        mImageReyclerView.setVisibility(View.INVISIBLE);
    }


    private void hideProgressBar(){
        mProgressBar.setVisibility(View.INVISIBLE);
        mImageReyclerView.setVisibility(View.VISIBLE);
    }


    private void initRecyclerView() {
        mImageRecyclerAdapter = new ImageRecyclerAdapter(mImageList, this);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        mImageReyclerView.setAdapter(mImageRecyclerAdapter);
        mImageReyclerView.setLayoutManager(manager);
    }


    @Override
    public void onClick(View view) {
        displayUploadDialog();
    }


    private void displayUploadDialog() {
        UploadImageDialogFragment dialogFragment = new UploadImageDialogFragment();
        dialogFragment.show(getSupportFragmentManager(), "uploadDialog");
    }
}
