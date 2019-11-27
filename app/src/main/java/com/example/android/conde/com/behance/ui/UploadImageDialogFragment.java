package com.example.android.conde.com.behance.ui;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.android.conde.com.behance.R;
import com.example.android.conde.com.behance.models.Image;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import static android.app.Activity.RESULT_OK;

public class UploadImageDialogFragment extends DialogFragment implements View.OnClickListener {
    private static final String TAG = "UploadImageDialogFragme";
    private Button mAddButton, mUploadButton;
    private EditText mImageName;
    private ImageView mImageToUpload;
    private ProgressBar mProgressBar;
    private static final int PICK_IMAGE_REQUEST = 999;
    private Uri mImageUri;
    private FirebaseFirestore mDb;
    private StorageReference mStorageReference;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dialog, container, false);
        mAddButton = view.findViewById(R.id.add);
        mUploadButton = view.findViewById(R.id.btn_upload);
        mProgressBar = view.findViewById(R.id.progress_bar);
        mImageToUpload = view.findViewById(R.id.iv_image_to_upload);
        mImageName = view.findViewById(R.id.et_image_name);
        mUploadButton.setOnClickListener(this);
        mUploadButton.setText("Cancel");
        mAddButton.setOnClickListener(this);
        mDb = FirebaseFirestore.getInstance();
        mStorageReference = FirebaseStorage.getInstance().getReference("uploads");
        return view;
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.add:
                mUploadButton.setText("Upload");
                pickImage();
                break;
            case R.id.btn_upload:
                if(mUploadButton.getText().toString().equals("Cancel"))
                    dismiss();
                upload();
                break;

        }
    }


    private void upload() {
        if (mImageUri != null) {
            mProgressBar.setVisibility(View.VISIBLE);
            mImageToUpload.setAlpha(0.5f);
            final StorageReference fileReference = mStorageReference.child(System.currentTimeMillis()
                    + "." + getFileExtension(mImageUri));

            final String uri;


            fileReference.putFile(mImageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(final UploadTask.TaskSnapshot taskSnapshot) {
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    mProgressBar.setProgress(0);
                                    dismiss();
                                }
                            }, 2000);

                            fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    Image image = new Image(mImageName.getText().toString().trim(),
                                            uri.toString());
                                    uploadImageMetaData(image);
                                }
                            });

                            Toast.makeText(getContext(), "Upload successful", Toast.LENGTH_SHORT).show();

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getContext(), "Upload Failed", Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred()
                                    / taskSnapshot.getTotalByteCount());

                            mProgressBar.setProgress((int) progress);
                        }
                    });

        }
    }





    private void uploadImageMetaData(Image image){

        mDb.collection("uploads")
                .add(image)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "onSuccess: UploadSuccessful");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "onFailure: "+e.getMessage());
                    }
                });
    }

    //jgg, png etc...
    private String getFileExtension(Uri uri) {
        if (getActivity() != null) {
            ContentResolver contentResolver = getActivity().getContentResolver();
            MimeTypeMap mime = MimeTypeMap.getSingleton();
            return mime.getExtensionFromMimeType(contentResolver.getType(uri));
        }
        return "";
    }


    private void pickImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            mImageUri = data.getData();
            if (getContext() != null)
                Glide.with(getContext())
                        .load(mImageUri)
                        .into(mImageToUpload);
        }
    }


}
