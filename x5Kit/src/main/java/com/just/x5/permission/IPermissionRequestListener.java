package com.just.x5.permission;

import android.os.Bundle;

import androidx.annotation.NonNull;

public interface IPermissionRequestListener {
    void onRequestPermissionsResult(@NonNull String[] permissions, @NonNull int[] grantResults, Bundle extras);
}
