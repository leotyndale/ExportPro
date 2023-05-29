package com.alibaba.imagesearch;

public interface OnPermissionResultListener {
    void onSucceed(String[] permission);
    void onFailed(String[] permission);
    void onNotAskAgain(String[] permission);
}
