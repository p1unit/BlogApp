package com.getactive.Activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;

import com.getactive.Adapter.PostRecyclerAdapter;
import com.getactive.Interface.PostManageInteface;
import com.getactive.Migration.UserMigration;
import com.getactive.Model.PostModel;
import com.getactive.R;
import com.getactive.Utils.ApplicationContextProvider;
import com.getactive.Utils.Config;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class AllPostsActivity extends AppCompatActivity implements View.OnClickListener, PostManageInteface {

    private RecyclerView post_list_view;
    private List<PostModel> post_list;
    private FirebaseFirestore firebaseFirestore;
    private PostRecyclerAdapter postRecyclerAdapter;
    private DocumentSnapshot lastVisible;
    private Boolean isFirstPageFirstLoad = true;
    private boolean fabExpanded = false;
    private FloatingActionButton fab;
    private LinearLayout layoutFabEdit;
    private LinearLayout layoutFabPhoto;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_posts);

        fab=findViewById(R.id.fab);
        layoutFabEdit = (LinearLayout) this.findViewById(R.id.layoutFabEdit);
        layoutFabPhoto = (LinearLayout) this.findViewById(R.id.layoutFabPhoto);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (fabExpanded == true){
                    closeSubMenusFab();
                } else {
                    openSubMenusFab();
                }
            }
        });

        layoutFabEdit.setOnClickListener(this);
        layoutFabPhoto.setOnClickListener(this);



        closeSubMenusFab();

        post_list = new ArrayList<>();
        post_list_view = this.findViewById(R.id.post_recycler);

        postRecyclerAdapter = new PostRecyclerAdapter(post_list,this);
        post_list_view.setLayoutManager(new LinearLayoutManager(this));
        post_list_view.setAdapter(postRecyclerAdapter);
        post_list_view.setHasFixedSize(true);

        firebaseFirestore = FirebaseFirestore.getInstance();

        post_list_view.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                Boolean reachedBottom = !recyclerView.canScrollVertically(1);

                if(reachedBottom){

                    loadMorePost();

                }

            }
        });

        Query firstQuery = firebaseFirestore.collection("Posts").orderBy("timestamp", Query.Direction.DESCENDING).limit(5);
        firstQuery.addSnapshotListener(this, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {

                if (!documentSnapshots.isEmpty()) {
                    if (isFirstPageFirstLoad) {

                        lastVisible = documentSnapshots.getDocuments().get(documentSnapshots.size() - 1);
                        post_list.clear();
                    }

                    for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {

                        if (doc.getType() == DocumentChange.Type.ADDED) {

                            String blogPostId = doc.getDocument().getId();
                            PostModel blogPost = doc.getDocument().toObject(PostModel.class).withId(blogPostId);

                            if (isFirstPageFirstLoad) {
                                post_list.add(blogPost);

                            } else {
                                post_list.add(0, blogPost);
                            }

                            post_list_view.getAdapter().notifyDataSetChanged();
                        }
                    }
                    isFirstPageFirstLoad = false;
                }
            }

        });
    }

    private void closeSubMenusFab(){
        layoutFabEdit.setVisibility(View.INVISIBLE);
        layoutFabPhoto.setVisibility(View.INVISIBLE);
        fab.setImageResource(R.drawable.ic_add_white_24dp);
        fabExpanded = false;
    }

    //Opens FAB submenus
    private void openSubMenusFab(){
        layoutFabEdit.setVisibility(View.VISIBLE);
        layoutFabPhoto.setVisibility(View.VISIBLE);
        fab.setImageResource(R.drawable.ic_clear_white_24dp);
        fabExpanded = true;
    }

    public void loadMorePost(){


        Query nextQuery = firebaseFirestore.collection("Posts")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .startAfter(lastVisible)
                .limit(5);

        nextQuery.addSnapshotListener(this, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {

                if (!documentSnapshots.isEmpty()) {

                    lastVisible = documentSnapshots.getDocuments().get(documentSnapshots.size() - 1);
                    for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {

                        if (doc.getType() == DocumentChange.Type.ADDED) {

                            String blogPostId = doc.getDocument().getId();
                            PostModel blogPost = doc.getDocument().toObject(PostModel.class).withId(blogPostId);
                            post_list.add(blogPost);

                            post_list_view.getAdapter().notifyDataSetChanged();
                        }

                    }
                }

            }


        });

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.layoutFabEdit:
                startActivity(new Intent(this,CreateTextPost.class));
                finish();
                break;
            case R.id.layoutFabPhoto:
                startActivity(new Intent(this,CreatePhotoPost.class));
                finish();
                break;
        }
    }

    @Override
    public void deletePost(final int position, String post_id) {

        // Todo Fix the Bug

//        firebaseFirestore.collection("Posts").document(post_id).delete()
//                .addOnSuccessListener(new OnSuccessListener<Void>() {
//                    @Override
//                    public void onSuccess(Void aVoid) {
//                        Config.toastShort(ApplicationContextProvider.getContext(), "Post successfully deleted!");
//                        // ToDO - Due to error
//                        startActivity(new Intent(ApplicationContextProvider.getContext(),AllPostsActivity.class));
//                        finish();
//                    }
//                })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        Config.toastShort(ApplicationContextProvider.getContext(), "Error deleting Post");
//                    }
//                });


    }

    @Override
    public void update(PostModel model, String post_id) {

        if(model.getImage_url().equals("")){

            Intent intent=new Intent(ApplicationContextProvider.getContext(), UpdateTextPost.class);
            intent.putExtra("model",model);
            intent.putExtra("postid",post_id);
            startActivity(intent);
            finish();

        }else {

            Intent intent=new Intent(ApplicationContextProvider.getContext(), UpdatePhotoPost.class);
            intent.putExtra("model",model);
            intent.putExtra("postid",post_id);
            startActivity(intent);
            finish();

        }
    }

    public void backbtn(View view) {

        UserMigration userMigration=new UserMigration();
        userMigration.removeUserId();
        startActivity(new Intent(this,MainActivity.class));
        finish();
    }
}
