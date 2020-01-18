package com.webx5.controller.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.permission.kit.OnPermissionListener;
import com.permission.kit.PermissionKit;
import com.webx5.R;


public class MainActivity extends AppCompatActivity {

    public String[] datas = new String[]{"Activity使用AgentWebX5(反弹效果:音乐)", "Fragment 使用 AgentWebX5 ", "文件下载", "input标签文件上传", "Js 通信文件上传,兼用Android 4.4Kitkat", "Js 通信", "Video 视屏全屏播放", "自定义进度条", "自定义设置", "电话 ， 信息 ， 邮件"};
    private String TYPE_KEY;

    @SuppressLint("SetTextI18n")
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar mToolbar = findViewById(R.id.toolbar);
        mToolbar.setTitleTextColor(Color.WHITE);
        mToolbar.setTitle(getResources().getString(R.string.title_main));
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            // Enable the Up button
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        TYPE_KEY = getResources().getString(R.string.key_type);
        ListView mListView = findViewById(R.id.listView);
        mListView.setAdapter(new MainAdapter());
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                doClick(position);
            }
        });
        PermissionKit.getInstance().requestPermission(this, 200, new OnPermissionListener() {
            @Override
            public void onSuccess(int requestCode, String... permissions) {

            }

            @Override
            public void onFail(int requestCode, String... permissions) {

            }
        }, Manifest.permission.READ_PHONE_STATE, Manifest.permission.WRITE_EXTERNAL_STORAGE);
    }

    private void doClick(int position) {
        switch (position) {
            case 0:
                startActivity(new Intent(this, WebActivity.class).putExtra("url", "http://music.runker.net/"));
                break;
            case 1:
                startActivity(new Intent(this, CommonActivity.class).putExtra(TYPE_KEY, 0));
                break;
            case 2:
                startActivity(new Intent(this, CommonActivity.class).putExtra(TYPE_KEY, 1));
                break;
            case 3:
                startActivity(new Intent(this, CommonActivity.class).putExtra(TYPE_KEY, 2));
                break;
            case 4:
                startActivity(new Intent(this, CommonActivity.class).putExtra(TYPE_KEY, 3));
                break;
            case 5:
                startActivity(new Intent(this, CommonActivity.class).putExtra(TYPE_KEY, 4));
                break;
            case 6:
                startActivity(new Intent(this, CommonActivity.class).putExtra(TYPE_KEY, 5));
                break;
            case 7:
                startActivity(new Intent(this, CommonActivity.class).putExtra(TYPE_KEY, 6));
                break;

            case 8:
                startActivity(new Intent(this, CommonActivity.class).putExtra(TYPE_KEY, 7));
                break;

            case 9:
                startActivity(new Intent(this, CommonActivity.class).putExtra(TYPE_KEY, 8));
                break;
        }
    }


    public class MainAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return datas.length;
        }

        @Override
        public Object getItem(int position) {
            return datas[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder mViewHolder;
            if (convertView == null) {
                mViewHolder = new ViewHolder();
                View mView = MainActivity.this.getLayoutInflater().inflate(R.layout.listview_main, parent, false);
                mViewHolder.mTextView = mView.findViewById(R.id.content);
                mView.setTag(mViewHolder);
                convertView = mView;
            } else {
                mViewHolder = (ViewHolder) convertView.getTag();
            }
            mViewHolder.mTextView.setText(datas[position]);
            return convertView;
        }
    }

    class ViewHolder {
        TextView mTextView;
    }
}
