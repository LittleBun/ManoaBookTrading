package com.uhmanoa.booktrade.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.uhmanoa.booktrade.MyApplication;
import com.uhmanoa.booktrade.R;
import com.uhmanoa.booktrade.activity.CommentActivity;
import com.uhmanoa.booktrade.activity.LoginActivity;
import com.uhmanoa.booktrade.activity.PersonalHomeActivity;
import com.uhmanoa.booktrade.db.DatabaseUtil;
import com.uhmanoa.booktrade.entity.Post;
import com.uhmanoa.booktrade.entity.User;
import com.uhmanoa.booktrade.utils.Constant;
import com.uhmanoa.booktrade.utils.LogUtils;
import com.uhmanoa.booktrade.utils.ToastUtils;

import java.util.List;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobRelation;
import cn.bmob.v3.listener.UpdateListener;

public class CardsAdapter extends BaseAdapter {

    private final String TAG = "CardsAdapter";
    private final Context context;
    private List<Post> dataList;

    public CardsAdapter(Context context, List<Post> dataList) {
        this.context = context;
        this.dataList = dataList;
    }

    @Override
    public int getCount() {
        return dataList.size();
    }

    @Override
    public Post getItem(int position) {
        return dataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final ViewHolder viewHolder;

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.list_item_card, null);
            viewHolder = new ViewHolder();
            viewHolder.userAvatar = (ImageView) convertView.findViewById(R.id.user_avatar_post);
            viewHolder.userName = (TextView) convertView.findViewById(R.id.user_name_post);
            viewHolder.timeStamp = (TextView) convertView.findViewById(R.id.timestamp_post);
            viewHolder.favorite = (ImageView) convertView.findViewById(R.id.favorite_post);
            viewHolder.favoriteLayout = (RelativeLayout) convertView.findViewById(R.id.favorite_post_layout);
            viewHolder.title = (TextView) convertView.findViewById(R.id.post_title);
            viewHolder.postPics = (RelativeLayout) convertView.findViewById(R.id.pics_post);
            viewHolder.pic1 = (ImageView) convertView.findViewById(R.id.pic1);
            viewHolder.pic2 = (ImageView) convertView.findViewById(R.id.pic2);
            viewHolder.pic3 = (ImageView) convertView.findViewById(R.id.pic3);
            viewHolder.shareCount = (TextView) convertView.findViewById(R.id.count_post_share);
            viewHolder.commentCount = (TextView) convertView.findViewById(R.id.count_post_comment);
            viewHolder.thumb = (ImageView) convertView.findViewById(R.id.post_thumb);
            viewHolder.likeCount = (TextView) convertView.findViewById(R.id.count_post_like);
            viewHolder.likePost = (LinearLayout) convertView.findViewById(R.id.post_like);
            viewHolder.sharePost = (LinearLayout) convertView.findViewById(R.id.post_share);
            viewHolder.commentPost = (LinearLayout) convertView.findViewById(R.id.post_comment);
            convertView.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        final Post post = dataList.get(position);
        User user = post.getAuthor();

        if (user == null) {
            LogUtils.i(TAG, "USER IS NULL");
        }
        if (user.getAvatar() == null) {
            LogUtils.i(TAG, "USER avatar IS NULL");
        }

        //
        if (user.getAvatar() != null) {
            String avatarUrl = user.getAvatar().getFileUrl(context);
            int defaultAvatar = user.getSex().equals(Constant.SEX_MALE) ? R.drawable.avatar_default_m : R.drawable.avatar_default_f;
            Glide.clear(viewHolder.userAvatar);
            Glide.with(context)
                    .load(Uri.parse(avatarUrl))
                    .centerCrop()
                    .placeholder(defaultAvatar)
                    .diskCacheStrategy(DiskCacheStrategy.RESULT)
                    .into(viewHolder.userAvatar);
        }

        //
        viewHolder.userAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isLogin()) {
                    ToastUtils.showToast(context, "Please Login First", Toast.LENGTH_SHORT);
                    Intent loginIntent = new Intent(context, LoginActivity.class);
                    context.startActivity(loginIntent);
                    return;
                }

                MyApplication.getMyApplication().setCurrentPost(post);
                Intent personalPageIntent = new Intent(context, PersonalHomeActivity.class);
                context.startActivity(personalPageIntent);
            }
        });

        //
        viewHolder.userName.setText(user.getUsername());
        //
        viewHolder.timeStamp.setText(post.getCreatedAt());
        //
        viewHolder.title.setText(post.getContent());

        //
        if (post.getContentfigureurl() == null) {
            viewHolder.postPics.setVisibility(View.GONE);
        } else {
            viewHolder.postPics.setVisibility(View.VISIBLE);
            String picUrl = post.getContentfigureurl().getFileUrl(context) == null ?
                    "" : post.getContentfigureurl().getFileUrl(context);
            Glide.clear(viewHolder.pic1);
            Glide.with(context)
                    .load(Uri.parse(picUrl))
                    .centerCrop()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(viewHolder.pic1);
            viewHolder.pic1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ToastUtils.showToast(context, "pic1", Toast.LENGTH_SHORT);
                }
            });
        }

        //
        if (post.getMyFav()) {
            viewHolder.favorite.setImageResource(R.drawable.ic_favorite);
        } else {
            viewHolder.favorite.setImageResource(R.drawable.ic_favorite_outline);
        }
        //
        viewHolder.favoriteLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickFav(viewHolder.favorite, post);
            }
        });


        //
        viewHolder.likeCount.setText(post.getLove() + "");
        if (post.getMyLove()) {
            viewHolder.thumb.setImageResource(R.drawable.ic_post_my_like);
            viewHolder.likeCount.setTextColor(R.color.google_red);
        } else {
            viewHolder.thumb.setImageResource(R.drawable.ic_post_like);
            viewHolder.likeCount.setTextColor(R.color.button_material_dark);
        }

        //
        viewHolder.likePost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isLogin()) {
                    ToastUtils.showToast(context, "Please Login First", Toast.LENGTH_SHORT);
                    Intent loginIntent = new Intent(context, LoginActivity.class);
                    context.startActivity(loginIntent);
                    return;
                }

                if (post.getMyLove()) {
                    viewHolder.thumb.setImageResource(R.drawable.ic_post_like);
                    viewHolder.likeCount.setTextColor(R.color.button_material_dark);
                    if (post.getLove() > 0) {
                        viewHolder.likeCount.setText((post.getLove() - 1) + "");
                    }
                    if (post.getLove() > 0) {
                        post.setLove(post.getLove() - 1);
                    }
                    post.increment("love", -1);
                    post.setMyLove(false);
                    post.update(context, new UpdateListener() {
                        @Override
                        public void onSuccess() {
                            //DatabaseUtil.getInstance(context).insertFav(post);
                            LogUtils.i(TAG, post.getLove());
                        }

                        @Override
                        public void onFailure(int i, String s) {
                            post.setMyLove(true);
                            post.setLove(post.getLove() + 1);
                            LogUtils.e(TAG, s);
                        }
                    });
                } else {
                    viewHolder.thumb.setImageResource(R.drawable.ic_post_my_like);
                    viewHolder.likeCount.setTextColor(R.color.google_red);
                    viewHolder.likeCount.setText((post.getLove() + 1) + "");
                    post.setMyLove(true);
                    post.setLove(post.getLove() + 1);
                    post.increment("love", 1);
                    post.update(context, new UpdateListener() {
                        @Override
                        public void onSuccess() {
                            //DatabaseUtil.getInstance(context).insertFav(post);
                            LogUtils.d(TAG, post.getLove());
                        }

                        @Override
                        public void onFailure(int i, String s) {
                            if (post.getLove() > 0) {
                                post.setMyLove(false);
                                post.setLove(post.getLove() - 1);
                            }
                            LogUtils.e(TAG, s);
                        }
                    });
                }
            }
        });


        //
        viewHolder.sharePost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToastUtils.showToast(context, "To be shared", Toast.LENGTH_SHORT);
            }
        });

        //
        viewHolder.commentCount.setText(post.getComment() + "");
        //
        viewHolder.commentPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isLogin()) {
                    ToastUtils.showToast(context, "Please Login First", Toast.LENGTH_SHORT);
                    Intent loginIntent = new Intent(context, LoginActivity.class);
                    context.startActivity(loginIntent);
                    return;
                }
                Intent commentIntent = new Intent(context, CommentActivity.class);
                commentIntent.putExtra("data", post);
                context.startActivity(commentIntent);
            }
        });
        return convertView;
    }

    /**
     * 
     *
     * @return
     */
    private boolean isLogin() {
        BmobUser user = BmobUser.getCurrentUser(context, User.class);
        if (user != null) {
            return true;
        }
        return false;
    }
    private static class ViewHolder {
        private ImageView userAvatar;
        private TextView userName;
        private TextView timeStamp;
        private ImageView favorite;
        private TextView title;
        private ImageView pic1;
        private ImageView pic2;
        private ImageView pic3;
        private TextView shareCount;
        private TextView commentCount;
        private ImageView thumb;
        private TextView likeCount;
        private RelativeLayout postPics;
        private LinearLayout likePost;
        private LinearLayout sharePost;
        private LinearLayout commentPost;
        private RelativeLayout favoriteLayout;
    }


    //
    private void onClickFav(View v, final Post post) {
        // TODO Auto-generated method stub
        final User user = BmobUser.getCurrentUser(context, User.class);
        if (isLogin() && user != null && user.getSessionToken() != null) {
            final BmobRelation favRelation = new BmobRelation();
            if (!post.getMyFav()) {
                ((ImageView) v).setImageResource(R.drawable.ic_favorite);
                post.setMyFav(true);
                favRelation.add(post);
                user.setFavorite(favRelation);
                user.update(context, new UpdateListener() {
                    @Override
                    public void onSuccess() {
                        // TODO Auto-generated method stub
                        ToastUtils.showToast(context, "Save successfully", Toast.LENGTH_SHORT);
                        LogUtils.i(TAG, "Save successfully");
                        //try get fav to see if fav success
//						getMyFavourite();
                    }

                    @Override
                    public void onFailure(int arg0, String arg1) {
                        // TODO Auto-generated method stub
                        post.setMyFav(false);
                        favRelation.remove(post);
                        user.setFavorite(favRelation);
                        LogUtils.i(TAG, arg1);
                        ToastUtils.showToast(context, "Save failed:" + arg0, Toast.LENGTH_SHORT);
                    }
                });

                post.update(context, new UpdateListener() {
                    @Override
                    public void onSuccess() {
                        DatabaseUtil.getInstance(context).insertFav(post);
                    }

                    @Override
                    public void onFailure(int i, String s) {
                        post.setMyFav(false);
                    }
                });
            } else {
                ((ImageView) v).setImageResource(R.drawable.ic_favorite_outline);
                post.setMyFav(false);
                favRelation.remove(post);
                user.setFavorite(favRelation);
                user.update(context, new UpdateListener() {
                    @Override
                    public void onSuccess() {
                        // TODO Auto-generated method stub
                        ToastUtils.showToast(context, "Cancel saving successfully", Toast.LENGTH_SHORT);
                        LogUtils.i(TAG, "Cancel saving");
                        //try get fav to see if fav success
//						getMyFavourite();
                    }

                    @Override
                    public void onFailure(int arg0, String arg1) {
                        // TODO Auto-generated method stub
                        post.setMyFav(true);
                        favRelation.add(post);
                        user.setFavorite(favRelation);
                        LogUtils.i(TAG, arg1);
                        ToastUtils.showToast(context, "Cancel saving failed:" + arg0, Toast.LENGTH_SHORT);
                    }
                });

                post.update(context, new UpdateListener() {
                    @Override
                    public void onSuccess() {
                        DatabaseUtil.getInstance(context).deleteFav(post);
                    }

                    @Override
                    public void onFailure(int i, String s) {
                        post.setMyFav(true);
                    }
                });
            }
        } else {
            //
            ToastUtils.showToast(context, "Please Login First", Toast.LENGTH_SHORT);
            Intent intent = new Intent(context, LoginActivity.class);
            context.startActivity(intent);
        }
    }
}
