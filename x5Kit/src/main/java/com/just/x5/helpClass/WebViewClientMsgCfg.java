package com.just.x5.helpClass;

import android.os.Parcel;
import android.os.Parcelable;

public class WebViewClientMsgCfg implements Parcelable {
    private String leaveApp = "您需要离开%s前往其他应用吗？";
    private String confirm = "离开";
    private String cancel = "取消";
    private String title = "提示";

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    private WebViewClientMsgCfg(Parcel in) {
        leaveApp = in.readString();
        confirm = in.readString();
        cancel = in.readString();
        title = in.readString();
    }

    public String getLeaveApp() {
        return leaveApp;
    }

    public void setLeaveApp(String leaveApp) {
        this.leaveApp = leaveApp;
    }

    public String getConfirm() {
        return confirm;
    }

    public void setConfirm(String confirm) {
        this.confirm = confirm;
    }

    public String getCancel() {
        return cancel;
    }

    public void setCancel(String cancel) {
        this.cancel = cancel;
    }

    public static final Creator<WebViewClientMsgCfg> CREATOR = new Creator<WebViewClientMsgCfg>() {
        @Override
        public WebViewClientMsgCfg createFromParcel(Parcel in) {
            return new WebViewClientMsgCfg(in);
        }

        @Override
        public WebViewClientMsgCfg[] newArray(int size) {
            return new WebViewClientMsgCfg[size];
        }
    };

    public WebViewClientMsgCfg() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(leaveApp);
        dest.writeString(confirm);
        dest.writeString(cancel);
        dest.writeString(title);
    }
}
