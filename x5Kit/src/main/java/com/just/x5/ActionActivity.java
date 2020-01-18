package com.just.x5;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.just.x5.helpClass.AgentWebX5Config;
import com.just.x5.permission.IPermissionRequestListener;
import com.just.x5.uploadFile.IFileDataListener;
import com.just.x5.util.AgentWebX5Utils;
import com.just.x5.util.LogUtils;
import com.permission.kit.PermissionActivity;

import java.io.File;

/**
 *
 */
public class ActionActivity extends PermissionActivity {
    private static final String TAG = "ActionActivity";
    /**
     * 保存拍照数据
     */
    private Uri mUri;

    /**
     * @param activity 上下文
     * @param action   类型
     */
    public static void start(Activity activity, int action, int fromIntention, String... arg) {
        Intent mIntent = new Intent(activity, ActionActivity.class);
        mIntent.putExtra(AgentWebX5Config.KEY_ACTION, action);
        mIntent.putExtra(AgentWebX5Config.KEY_FROM_INTENTION, fromIntention);
        mIntent.putExtra(AgentWebX5Config.STRING_ARR, arg);
        activity.startActivity(mIntent);
    }

    /**
     * 文件选择监听器
     */
    private static IFileDataListener mFileDataListener;

    public static void setFileDataListener(IFileDataListener fileDataListener) {
        mFileDataListener = fileDataListener;
    }

    /**
     * 权限请求监听器
     */
    private static IPermissionRequestListener mPermissionListener;

    public static void setPermissionListener(IPermissionRequestListener permissionListener) {
        mPermissionListener = permissionListener;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        int action = intent.getIntExtra(AgentWebX5Config.KEY_ACTION, 0);
        //请求权限
        if (action == AgentWebX5Config.ACTION_PERMISSION) {
            permission(intent.getStringArrayExtra(AgentWebX5Config.STRING_ARR));
        } else {
            if (mFileDataListener == null) {
                finish();
            } else {
                if (action == AgentWebX5Config.ACTION_CAMERA) {
                    //打开相机
                    openCamera();
                } else {
                    //选择文件
                    openFileChooser();
                }
            }
        }

    }

    /**
     * 打开相机
     */
    private void openCamera() {
        File mFile = AgentWebX5Utils.createImageFile(this);
        if (mFile == null) {
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.tip_create_file_fail), Toast.LENGTH_LONG).show();
            finish();
        } else {
            Intent intent = AgentWebX5Utils.getIntentCaptureCompat(this, mFile);
            LogUtils.getInstance().e(TAG, "openCamera file:" + mFile.getAbsolutePath());
            // 指定开启系统相机的Action
            mUri = intent.getParcelableExtra(MediaStore.EXTRA_OUTPUT);
            startActivityForResult(intent, AgentWebX5Config.REQUEST_CODE);
        }
    }

    /**
     * 选择文件
     */
    private void openFileChooser() {
        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
        i.addCategory(Intent.CATEGORY_OPENABLE);
        i.setType("*/*");
        startActivityForResult(Intent.createChooser(i, "选择文件"), AgentWebX5Config.REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        LogUtils.getInstance().e(TAG, "mFileDataListener:" + mFileDataListener + " " + mUri);
        if (resultCode == Activity.RESULT_CANCELED) {
            finish();
        } else {
            if (requestCode == AgentWebX5Config.REQUEST_CODE) {
                if (mFileDataListener != null) {
                    mFileDataListener.onFileDataResult(requestCode, resultCode, mUri != null ? new Intent().putExtra(AgentWebX5Config.KEY_URI, mUri) : data);
                }
                finish();
            }
        }

    }

    /**
     * 申请权限
     *
     * @param permissions 权限
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void permission(String[] permissions) {
        if (permissions == null) {
            mPermissionListener = null;
            finish();
        } else {
            LogUtils.getInstance().e(TAG, "requestPermissions:" + permissions[0]);
            if (mPermissionListener != null) {
                requestPermissions(permissions, 1);
            }
        }
    }

    /**
     * 申请权限授权结果
     *
     * @param requestCode  请求码
     * @param permissions  权限
     * @param grantResults 授权结果
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (mPermissionListener != null) {
            Bundle mBundle = new Bundle();
            mBundle.putInt(AgentWebX5Config.KEY_FROM_INTENTION, getIntent().getIntExtra(AgentWebX5Config.KEY_FROM_INTENTION, 0));
            mPermissionListener.onRequestPermissionsResult(permissions, grantResults, mBundle);
        }
        mPermissionListener = null;
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mFileDataListener = null;
        mPermissionListener = null;
    }
}
