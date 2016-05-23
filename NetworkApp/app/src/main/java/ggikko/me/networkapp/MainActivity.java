package ggikko.me.networkapp;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import butterknife.OnClick;
import ggikko.me.networkapp.check.ReactiveNetwork;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "ggikko";
    private ReactiveNetwork reactiveNetwork;

    private boolean NETWORKING_FLAG = true;

    @OnClick(R.id.btn_first)
    void callNext() {
//        startActivity(new Intent(MainActivity.this, TwoActivity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkNetworkStatus();
    }

    private void checkNetworkStatus() {
        reactiveNetwork = new ReactiveNetwork();

        //TODO : 비효율적인 구조 개선필요
        new ReactiveNetwork().observeInternetConnectivity()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Boolean>() {
                    @Override
                    public void call(Boolean isConnectedToInternet) {
                        if (isConnectedToInternet) {
                            NETWORKING_FLAG = false;
                            callNextStep();
                        } else {
                            /** networking dialog */
                            new MaterialDialog.Builder(MainActivity.this).title("네트워크 연결").content("이 서비스는 네트워크 연결이 필요합니다.").positiveText("연결하기").negativeText("취소")
                                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                                        @Override
                                        public void onClick(MaterialDialog dialog, DialogAction which) {
                                            startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                                        }
                                    })
                                    .onNegative(new MaterialDialog.SingleButtonCallback() {
                                        @Override
                                        public void onClick(MaterialDialog dialog, DialogAction which) {
                                            dialog.dismiss();
                                            checkNetworkStatus();
                                        }
                                    }).show();
                        }
                    }
                });
    }

    void callNextStep() {
        startActivity(new Intent(MainActivity.this, TwoActivity.class));
        NETWORKING_FLAG = true;
        finish();
    }


}
