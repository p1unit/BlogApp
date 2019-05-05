package com.getactive.Interface;

import com.getactive.Model.PostModel;

public interface PostManageInteface {

    void deletePost(int position,String post_id);
    void update(PostModel model,String post_id);
}
