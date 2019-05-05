package com.getactive.Adapter;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.getactive.Activities.UpdatePhotoPost;
import com.getactive.Activities.UpdateTextPost;
import com.getactive.Interface.PostManageInteface;
import com.getactive.Migration.UserMigration;
import com.getactive.Model.PostModel;
import com.getactive.R;
import com.getactive.Utils.ApplicationContextProvider;
import com.getactive.Utils.Config;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;
import java.util.List;

public class PostRecyclerAdapter  extends RecyclerView.Adapter<PostRecyclerAdapter.ViewHolder> {

    public List<PostModel> post_list;
    private FirebaseFirestore firebaseFirestore;
    UserMigration userMigration=new UserMigration();
    private PostManageInteface iface;

    public PostRecyclerAdapter(List<PostModel> post_list, PostManageInteface iface) {
        this.post_list = post_list;
        this.iface=iface;
    }

    @NonNull
    @Override
    public PostRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.post_row, viewGroup, false);
        firebaseFirestore = FirebaseFirestore.getInstance();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull  PostRecyclerAdapter.ViewHolder viewHolder, int i) {

        viewHolder.setIsRecyclable(false);

        final PostRecyclerAdapter.ViewHolder holder=viewHolder;
        final String blogPostId = post_list.get(i).PostModelId;
        final int pos=i;

        viewHolder.buttonViewOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                PopupMenu popup = new PopupMenu(ApplicationContextProvider.getContext(), holder.buttonViewOption);

                popup.inflate(R.menu.menu_item);

                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.delete:
                                holder.deletePost(blogPostId,pos);
                                holder.deletePost(blogPostId,pos);
                                break;
                            case R.id.update:
                                holder.update(blogPostId);
                                break;
                        }
                        return false;
                    }
                });
                popup.show();
            }
        });

        String desc_data = post_list.get(i).getDesc();
        if(!desc_data.equals(""))
            viewHolder.setDescText(desc_data);

        String image_url = post_list.get(i).getImage_url();
        if(!image_url.equals(""))
            viewHolder.setBlogImage(image_url);

        viewHolder.setUserData(post_list.get(i).getUser_id(),post_list.get(i).getTitle());

        try {
            long millisecond = post_list.get(i).getTimestamp().getTime();
            String dateString = DateFormat.format("MM/dd/yyyy", new Date(millisecond)).toString();
            viewHolder.setTime(dateString);
        } catch (Exception e) {

            Config.toastShort(ApplicationContextProvider.getContext(),"Exception :"+e.getMessage());

        }
    }

    @Override
    public int getItemCount() {
        return post_list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private View mView;

        private TextView descView;
        private ImageView blogImageView;
        private TextView blogDate;
        private TextView title;
        private TextView blogUserName;
        public TextView buttonViewOption;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.mView=itemView;
            descView = itemView.findViewById(R.id.blog_desc);
            blogImageView = itemView.findViewById(R.id.blog_image);
            buttonViewOption = itemView.findViewById(R.id.textViewOptions);
        }


        public void setDescText(String descText){

            descView.setVisibility(View.VISIBLE);
            descView.setText(descText);

        }

        public void setBlogImage(String downloadUri){


            blogImageView.setVisibility(View.VISIBLE);

            RequestOptions requestOptions = new RequestOptions();
            requestOptions.placeholder(R.drawable.temp);
            requestOptions.error(R.drawable.temp);

            Glide.with(ApplicationContextProvider.getContext()).load(downloadUri)
                    .apply(requestOptions).thumbnail(0.5f).into(blogImageView);

        }

        public void setTime(String date) {

            blogDate = mView.findViewById(R.id.blog_date);
            blogDate.setText(date);

        }

        public void setUserData(String name, String ti){

            blogUserName = mView.findViewById(R.id.blog_user_name);
            blogUserName.setText(name);

            title=mView.findViewById(R.id.blog_title);
            title.setText(ti);

        }

        public void update(String blogPostId){

            if(!post_list.get(getAdapterPosition()).getUser_id().equals(userMigration.getUserId())){
                Config.toastShort(ApplicationContextProvider.getContext(),"You can not Update other's Post");
                return;
            }

            iface.update(post_list.get(getAdapterPosition()),blogPostId);
        }

        // Todo Bug Here : need to fix

        public void deletePost( String post_id,final int pos){

            if(!post_list.get(getAdapterPosition()).getUser_id().equals(userMigration.getUserId())){
                Config.toastShort(ApplicationContextProvider.getContext(),"You can not delete other's Post");
                return;
            }

            firebaseFirestore.collection("Posts").document(post_id).delete()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            post_list.remove(pos);
//                            notifyItemRemoved(pos);
                            notifyDataSetChanged();
//                            notifyItemRangeChanged(pos, post_list.size());
                            mView.setVisibility(View.GONE);
                            Config.toastShort(ApplicationContextProvider.getContext(), "Post successfully deleted!");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Config.toastShort(ApplicationContextProvider.getContext(), "Error deleting Post");
                        }
                    });

//            iface.deletePost(getAdapterPosition(),post_id);
        }

    }


}
