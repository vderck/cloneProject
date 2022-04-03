package com.example.newlogin;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.newlogin.Utills.Comment;
import com.example.newlogin.Utills.Posts;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    Toolbar toolbar;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    FirebaseAuth mAuth;
    FirebaseUser mUser;
    DatabaseReference mUserRef, postRef, likeRef,commentRef;
    String profileImageUriV, userNameV;
    CircleImageView profileImageHeader;
    TextView usernameHeader;
    ImageView addImagePost, sendImagePost;
    EditText inputPostDescription;
    private static final int REQUEST_CODE = 101;
    Uri imageUri;
    ProgressDialog mLoadingBar;
    StorageReference postImageRef;
    FirebaseRecyclerAdapter<Posts,MyViewHolder>adapter;
    FirebaseRecyclerOptions<Posts>options;
    RecyclerView recyclerView;
    FirebaseRecyclerOptions<Comment> commentOption;
    FirebaseRecyclerAdapter<Comment,CommentViewHolder> CommentAdapter;

    //Toast Message 기존 메시지 삭제 / 현재 메시지만 보이도록 하기
    public static Toast mToast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        toolbar = findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Chat App");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_baseline_menu_24);

        addImagePost = findViewById(R.id.iv_addImagePost);
        sendImagePost = findViewById(R.id.iv_send_post_imageView);
        inputPostDescription = findViewById(R.id.et_addPost);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        mUserRef = FirebaseDatabase.getInstance().getReference().child("Users");
        postRef = FirebaseDatabase.getInstance().getReference().child("Posts");
        likeRef = FirebaseDatabase.getInstance().getReference().child("Likes");
        commentRef = FirebaseDatabase.getInstance().getReference().child("Comments");
        postImageRef = FirebaseStorage.getInstance().getReference().child("PostImages");
        mLoadingBar = new ProgressDialog(this);
        recyclerView = findViewById(R.id.rv_recyclerView);

        // 기존 순서의 코드
        // recyclerView.setLayoutManager(new LinearLayoutManager(this));
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.nv_navigationView);
        sendImagePost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                AddPost();
            }
        });

        addImagePost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent , REQUEST_CODE);
            }
        });
        LoadPost();

        View view = navigationView.inflateHeaderView(R.layout.drawer_header);
        profileImageHeader = view.findViewById(R.id.profile_image_header);
        usernameHeader = view.findViewById(R.id.tv_userNameHeader);

        // Toast Message 기존 메시지 삭제 / 현재 메시지만 보이도록 하기
        mToast = Toast.makeText(this, "null", Toast.LENGTH_SHORT);
        navigationView.setNavigationItemSelectedListener(this);
    }


    private void LoadPost() {
        options = new FirebaseRecyclerOptions.Builder<Posts>().setQuery(postRef, Posts.class).build();
        adapter = new FirebaseRecyclerAdapter<Posts, MyViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull MyViewHolder holder, int position, @NonNull Posts model) {
                String postKey = getRef(position).getKey();
                holder.postDescription.setText(model.getPostDescription());
                String timeAgo = calculateTimeAgo(model.getDataPost());
                holder.timeAgo.setText(timeAgo);
                holder.username.setText(model.getUsername());
                Picasso.get().load(model.getPostImageUri()).into(holder.postImage);
                Picasso.get().load(model.getUserprofileImage()).into(holder.profileImage);
                holder.countLikes(postKey, mUser.getUid(), likeRef);
                holder.countComments(postKey, mUser.getUid(), commentRef);


                holder.likeImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        likeRef.child(postKey).child(mUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                              if (snapshot.exists())
                              {
                                  likeRef.child(postKey).child(mUser.getUid()).removeValue();
                                  holder.likeImage.setColorFilter(Color.GRAY);
                                  notifyDataSetChanged();
                              }
                              else
                              {
                                  likeRef.child(postKey).child(mUser.getUid()).setValue("like");
                                  holder.likeImage.setColorFilter(Color.GREEN);
                                  notifyDataSetChanged();
                              }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                    Toast.makeText(HomeActivity.this, ""+error.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
                holder.commentSend.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String comment = holder.inputComments.getText().toString();
                        if (comment.isEmpty())
                        {
                            Toast.makeText(HomeActivity.this, "Please write your contents",Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            AddComment(holder,postKey,commentRef, mUser.getUid(),comment);
                        }
                    }
                });
                LoadComment(postKey);
                holder.postImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(HomeActivity.this , Image_View_Activity.class);
                        intent.putExtra("url" , model.getPostImageUri());
                        startActivity(intent);
                    }
                });
            }

            @NonNull
            @Override
            public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_view_post,parent,false);
                return new MyViewHolder(view);
            }
        };
        adapter.startListening();
        recyclerView.setAdapter(adapter);
    }

    private void LoadComment(String postKey) {
        MyViewHolder.recyclerView.setLayoutManager(new LinearLayoutManager(HomeActivity.this));
        commentOption = new FirebaseRecyclerOptions.Builder<Comment>().setQuery(commentRef.child(postKey),Comment.class).build();
        CommentAdapter = new FirebaseRecyclerAdapter<Comment, CommentViewHolder>(commentOption) {
            @Override
            protected void onBindViewHolder(@NonNull CommentViewHolder holder, int position, @NonNull Comment model) {
                Picasso.get().load(model.getProfileImageUri()).into(holder.profileImage);
                holder.username.setText(model.getUsername());
                holder.comment.setText(model.getComments());
            }

            @NonNull
            @Override
            public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_view_comment, parent, false);
                return new CommentViewHolder(view);
            }
        };
        CommentAdapter.startListening();
        MyViewHolder.recyclerView.setAdapter(CommentAdapter);
    }

    private void AddComment(MyViewHolder holder, String postKey, DatabaseReference commentRef, String uid, String comment) {

        HashMap hashMap = new HashMap();
        hashMap.put("username",userNameV);
        hashMap.put("profileImageUri", profileImageUriV);
        hashMap.put("comments" , comment);

        String key = mUserRef.child(postKey).child(uid).push().getKey();

//        database.child("/name/"+key).setValue(editText.text.toString())
//        commentRef = FirebaseDatabase.getInstance().getReference().child("Comments").child(postKey).child(uid);
//        Map<String, Object> extraComment = new HashMap<String,Object>();
//        extraComment.put("username", userNameV);
//        extraComment.put("profileImageUri", profileImageUriV);
//        extraComment.put("comments", comment);

        commentRef.child(postKey).child(uid + key).updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if (task.isSuccessful())
                {
                    Toast.makeText(HomeActivity.this, "New comment added!", Toast.LENGTH_SHORT).show();
                    adapter.notifyDataSetChanged();
                    holder.inputComments.setText(null);
                }
                else
                {
                    Toast.makeText(HomeActivity.this, ""+task.getException().toString(), Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    private String calculateTimeAgo(String dataPost) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-M-yyyy hh:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
        try {
            long time = sdf.parse(dataPost).getTime();
            long now = System.currentTimeMillis();
            CharSequence ago =
                    DateUtils.getRelativeTimeSpanString(time, now, DateUtils.MINUTE_IN_MILLIS);
            return ago+"";
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode ==RESULT_OK && data!=null)
        {
            imageUri=data.getData();
            addImagePost.setImageURI(imageUri);
        }
    }

    private void AddPost()
    {
            String postDesc = inputPostDescription.getText().toString();
            if (imageUri == null)
            {
                Toast.makeText(this , "please select your image" , Toast.LENGTH_SHORT).show();
            }
            else if (postDesc.isEmpty() || postDesc.length() < 2)
            {
                inputPostDescription.setError("please fill in your content");
            }
            else
            {
                mLoadingBar.setTitle("Adding Post");
                mLoadingBar.setCanceledOnTouchOutside(false);
                mLoadingBar.show();

                Date date = new Date();
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd-M-yyyy hh:mm:ss");
                String strDate = dateFormat.format(date);

                postImageRef.child(mUser.getUid()+strDate).putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task)
                    {
                        if (task.isSuccessful())
                        {
                            postImageRef.child(mUser.getUid()+strDate).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>()
                            {
                                @Override
                                public void onSuccess(Uri uri)
                                {

                                    HashMap hashMap = new HashMap();
                                    hashMap.put("dataPost" , strDate);
                                    hashMap.put("postImageUri" , uri.toString());
                                    hashMap.put("postDescription" , postDesc);
                                    hashMap.put("userprofileImage" , profileImageUriV);
                                    hashMap.put("username" , userNameV);

                                    postRef.child(mUser.getUid()+strDate).updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener() {
                                        @Override
                                        public void onComplete(@NonNull Task task)
                                        {
                                            if (task.isSuccessful())
                                            {
                                                mLoadingBar.dismiss();
                                                Toast.makeText(HomeActivity.this, "post added !" , Toast.LENGTH_SHORT).show();
                                                addImagePost.setImageResource(R.drawable.ic_baseline_image_24);
                                                // 초기에러 구문
                                                // addImagePost.setImageURI(null);
                                                inputPostDescription.setText("");
                                            }

                                            else
                                            {
                                                mLoadingBar.dismiss();
                                                Toast.makeText(HomeActivity.this , ""+task.getException().toString(), Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                }
                            });


                        }

                        else
                        {
                            mLoadingBar.dismiss();
                            Toast.makeText(HomeActivity.this, ""+task.getException().toString(), Toast.LENGTH_SHORT).show();
                        }



                    }
                });
            }

    }


    @Override
    protected void onStart()
    {
        super.onStart();
        if (mUser==null)
        {
            sendUserToLoginActivity();
        }
        else
        {
            mUserRef.child(mUser.getUid()).addValueEventListener(new ValueEventListener()
            {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot)
                {
                    if (snapshot.exists())
                    {
                        profileImageUriV = snapshot.child("profileImage").getValue().toString();
                        userNameV = snapshot.child("username").getValue().toString();
                        Picasso.get().load(profileImageUriV).into(profileImageHeader);
                        usernameHeader.setText(userNameV);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error)
                {
                    Toast.makeText(HomeActivity.this, "sorry, server connect failure", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void sendUserToLoginActivity()
    {
        Intent intent = new Intent(HomeActivity.this , MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.it_home:
                if(mToast != null) mToast.cancel();
                mToast.cancel();
                mToast.setText("Home");
                mToast.show();

                //Toast.makeText(this, "Home" , Toast.LENGTH_SHORT).show();
                break;

            case R.id.it_profile:
                if(mToast != null) mToast.cancel();
                mToast.cancel();
                mToast.setText("Profile");
                mToast.show();
                startActivity(new Intent(HomeActivity.this , ProfileActivity.class));
                //Toast.makeText(this, "Profile" , Toast.LENGTH_SHORT).show();
                break;

            case R.id.it_friend:
                if(mToast != null) mToast.cancel();
//                mToast.cancel();
//                mToast.setText("Friend");
//                mToast.show();
                startActivity(new Intent(HomeActivity.this, FriendActivity.class));
                finish();

                // Toast.makeText(this, "Friend" , Toast.LENGTH_SHORT).show();
                break;

            case R.id.it_findFriends:
                if(mToast != null) mToast.cancel();
                //mToast.cancel();
                //mToast.setText("Find Friends");
                //mToast.show();
                startActivity(new Intent(HomeActivity.this, FindFriendActivity.class));
                // Toast.makeText(this, "Find Friends", Toast.LENGTH_SHORT).show();

                break;

            case R.id.it_chat:
                if(mToast != null) mToast.cancel();
                mToast.cancel();
                mToast.setText("Chat");
                mToast.show();
                // Toast.makeText(this, "Chat" , Toast.LENGTH_SHORT).show();
                break;

            case R.id.it_logout:
                if(mToast != null) mToast.cancel();
                mAuth.signOut();
                Intent intent = new Intent(HomeActivity.this , MainActivity.class);
                startActivity(intent);
                // finish를 쓰는 이유는 기존 다른 불필요한 기능을 꺼서 메모리 효율을 높임 -> 속도가빨라짐
                finish();
                // Toast.makeText(this, "Logout" , Toast.LENGTH_SHORT).show();
                break;
        }
        return true;
    }

}