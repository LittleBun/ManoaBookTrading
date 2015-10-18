package com.uhmanoa.booktrade.activity;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.uhmanoa.booktrade.R;
import com.uhmanoa.booktrade.entity.Post;
import com.uhmanoa.booktrade.entity.User;
import com.uhmanoa.booktrade.utils.LogUtils;
import com.uhmanoa.booktrade.utils.ToastUtils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UploadFileListener;

public class NewPostActivity extends BaseActivity implements View.OnClickListener {

    private static final int REQUEST_CODE_ALBUM = 1;
    private static final int REQUEST_CODE_CAMERA = 2;

    EditText content;

    LinearLayout openLayout;
    LinearLayout takeLayout;

    ImageView albumPic;
    ImageView takePic;

    String dateTime;
    String targeturl = null;

    ImageLoader imageLoader;
    DisplayImageOptions options;

    String mCurrentPhotoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_post);
        getActionBar().setTitle(R.string.title_activity_new_post);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        content = (EditText) findViewById(R.id.new_post_content);
        openLayout = (LinearLayout) findViewById(R.id.open_layout);
        takeLayout = (LinearLayout) findViewById(R.id.take_layout);

        albumPic = (ImageView) findViewById(R.id.open_pic);
        takePic = (ImageView) findViewById(R.id.take_pic);

        openLayout.setOnClickListener(this);
        takeLayout.setOnClickListener(this);

        imageLoader = ImageLoader.getInstance();
        setImageLoaderOptions();
    }

    private void setImageLoaderOptions() {
        options = new DisplayImageOptions.Builder()
                .showImageForEmptyUri(R.drawable.ic_launcher)//setting pic for empty or errors
                .showImageForEmptyUri(R.drawable.ic_launcher)
                .showImageOnFail(R.drawable.ic_launcher)  //setting pic for loading/unzip errors
                .cacheInMemory(true)//setting if cache downloaded pics in memory
                .cacheOnDisc(true)//setting if cache downloaded pics in SD card
                .considerExifParams(true)
                .imageScaleType(ImageScaleType.EXACTLY_STRETCHED)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        super.onBackPressed();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.open_layout:
                Date date1 = new Date(System.currentTimeMillis());
                dateTime = date1.getTime() + "";
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setDataAndType(MediaStore.Images.Media.INTERNAL_CONTENT_URI, "image/*");
                startActivityForResult(intent, REQUEST_CODE_ALBUM);
                break;
            case R.id.take_layout:
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    // Create the File where the photo should go
                    File photoFile = null;
                    try {
                        photoFile = createImageFile();
                    } catch (IOException ex) {
                        // Error occurred while creating the File
                        ToastUtils.showToast(mContext, "fail to create pictures！", Toast.LENGTH_SHORT);
                    }
                    // Continue only if the File was successfully created
                    if (photoFile != null) {
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                                Uri.fromFile(photoFile));
                        startActivityForResult(takePictureIntent, REQUEST_CODE_CAMERA);
                    }
                }
                break;
            default:
                break;
        }
    }


    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        LogUtils.d(TAG, mCurrentPhotoPath);
        return image;
    }

    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CODE_ALBUM:
                    String fileName = null;
                    if (data != null) {
                        Uri originalUri = data.getData();
                        ContentResolver cr = getContentResolver();
                        Cursor cursor = cr.query(originalUri, null, null, null, null);
                        if (cursor.moveToFirst()) {
                            do {
                                fileName = cursor.getString(cursor.getColumnIndex("_data"));
                            } while (cursor.moveToNext());
                        }
                        LogUtils.d(TAG, fileName);
                        targeturl = fileName;
                        imageLoader.displayImage("file://" + fileName, albumPic);
                        //takeLayout.setVisibility(View.GONE);
                    }
                    break;
                case REQUEST_CODE_CAMERA:
                    galleryAddPic();
                    LogUtils.d(TAG, mCurrentPhotoPath);
                    targeturl = mCurrentPhotoPath;
                    imageLoader.displayImage("file://" + mCurrentPhotoPath, takePic);
                    break;
                default:
                    break;
            }
        }
    }


     /*
         * 发表带图片
         */
    private void publish(final String commitContent) {

        final BmobFile figureFile = new BmobFile(new File(targeturl));
        figureFile.upload(mContext, new UploadFileListener() {

            @Override
            public void onSuccess() {
                // TODO Auto-generated method stub
                LogUtils.i(TAG, "Upload Successfully!" + figureFile.getFileUrl(mContext));
                publishWithoutFigure(commitContent, figureFile);

            }

            @Override
            public void onProgress(Integer arg0) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onFailure(int arg0, String arg1) {
                // TODO Auto-generated method stub
                LogUtils.i(TAG, "Fail to upload!" + arg1);
            }
        });

    }

    //不带图片
    private void publishWithoutFigure(final String commitContent,
                                      final BmobFile figureFile) {
        User user = BmobUser.getCurrentUser(mContext, User.class);

        final Post post = new Post();
        post.setAuthor(user);
        post.setContent(commitContent);
        if (figureFile != null) {
            post.setContentfigureurl(figureFile);
        }
        post.setLove(0);
        post.setHate(0);
        post.setShare(0);
        post.setComment(0);
        post.setPass(true);
        post.save(mContext, new SaveListener() {

            @Override
            public void onSuccess() {
                // TODO Auto-generated method stub
                ToastUtils.showToast(mContext, "Post Successfully!", Toast.LENGTH_SHORT);
                LogUtils.i(TAG, "Create Successfully");
                setResult(RESULT_OK);
                onBackPressed();
            }

            @Override
            public void onFailure(int arg0, String arg1) {
                // TODO Auto-generated method stub
                ToastUtils.showToast(mContext, "Fail to Post!" + arg1, Toast.LENGTH_SHORT);
                LogUtils.i(TAG, "Fail to Create!" + arg1);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_new_post, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id) {
            case R.id.action_send_post:
                String commitContent = content.getText().toString().trim();
                if (commitContent.isEmpty()) {
                    ToastUtils.showToast(mContext, "Content can't be empty", Toast.LENGTH_SHORT);
                    return true;
                }
                if (targeturl == null) {
                    publishWithoutFigure(commitContent, null);
                } else {
                    publish(commitContent);
                }
                ToastUtils.showToast(mContext, "Posting...", Toast.LENGTH_SHORT);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
