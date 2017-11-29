package com.single.sim;

import android.Manifest;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.widget.TextView;

import de.greenrobot.event.EventBus;

public class MainActivity extends AppCompatActivity {
    public static final int PERMISSION_REQUEST_CODE_BASIC_INFORMATION = 1;
    private SimConnectReceive mSimConnectReceive;
    private SimStateReceive mSimStateReceive;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (!PermissionUtil.hasSelfPermission(this,
                Manifest.permission.READ_PHONE_STATE)) {
            requestPermissions(
                    new String[]{Manifest.permission.READ_PHONE_STATE},
                    PERMISSION_REQUEST_CODE_BASIC_INFORMATION);
        } else {
            initView();
        }
    }

    private void initView() {
        if (mSimConnectReceive == null) {
            mSimConnectReceive = new SimConnectReceive();
            IntentFilter filter = new IntentFilter();
            filter.addAction(SimConnectReceive.ACTION_SIM_STATE_CHANGED);
            registerReceiver(mSimConnectReceive, filter);
        }
        if (mSimStateReceive == null) {
            mSimStateReceive = new SimStateReceive();
            IntentFilter filter1 = new IntentFilter();
            filter1.addAction(SimStateReceive.ACTION_SIM_STATE_CHANGED);
            registerReceiver(mSimStateReceive, filter1);
        }
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        String number1 = SimUtils.getSimPhonenumber(this, 0);
        if (!TextUtils.isEmpty(number1)) {
            ((TextView) findViewById(R.id.sim1_number)).setText(number1);
        } else {
            ((TextView) findViewById(R.id.sim1_number)).setText("未检测到卡1");
        }

        String number2 = SimUtils.getSimPhonenumber(this, 1);
        if (!TextUtils.isEmpty(number2)) {
            ((TextView) findViewById(R.id.sim2_number)).setText(number2);
        } else {
            ((TextView) findViewById(R.id.sim2_number)).setText("未检测到卡2");
        }

        String imei1 = SimUtils.getSimImei(this, 0);
        if (!TextUtils.isEmpty(imei1)) {
            ((TextView) findViewById(R.id.sim1_imei)).setText(imei1);
        } else {
            ((TextView) findViewById(R.id.sim1_imei)).setText("未检测到卡1");
        }

        String imei2 = SimUtils.getSimImei(this, 1);
        if (!TextUtils.isEmpty(number2)) {
            ((TextView) findViewById(R.id.sim2_imei)).setText(imei2);
        } else {
            ((TextView) findViewById(R.id.sim2_imei)).setText("未检测到卡2");
        }

        String operator1 = SimUtils.getSimOperatorName(this, 0);
        if (!TextUtils.isEmpty(operator1)) {
            ((TextView) findViewById(R.id.sim1_operatorname)).setText(operator1);
        } else {
            ((TextView) findViewById(R.id.sim1_operatorname)).setText("未检测到卡1");
        }

        String operator2 = SimUtils.getSimOperatorName(this, 1);
        if (!TextUtils.isEmpty(operator2)) {
            ((TextView) findViewById(R.id.sim2_operatorname)).setText(operator2);
        } else {
            ((TextView) findViewById(R.id.sim2_operatorname)).setText("未检测到卡2");
        }

        String net1 = SimUtils.getSimNetworkName(this, 0);
        if (!TextUtils.isEmpty(net1)) {
            ((TextView) findViewById(R.id.sim1_networkType)).setText(net1);
        } else {
            ((TextView) findViewById(R.id.sim1_networkType)).setText("未检测到卡1");
        }

        String net2 = SimUtils.getSimNetworkName(this, 1);
        if (!TextUtils.isEmpty(net2)) {
            ((TextView) findViewById(R.id.sim2_networkType)).setText(net2);
        } else {
            ((TextView) findViewById(R.id.sim2_networkType)).setText("未检测到卡2");
        }

        SimUtils.CurrentNetwork currentNetwork = SimUtils.getCurrentNetwork(this);
        TextView tv = (TextView) findViewById(R.id.current_network);
        if (TextUtils.isEmpty(currentNetwork.whichSim)) {
            tv.setText("当前网络不是sim卡数据流量");
        } else {
            tv.setText("当前哪张卡在使用网络:" + currentNetwork.whichSim + "\n" +
                    "当前网络状态:" + currentNetwork.netWorkName + "\n" +
                    "当前卡的生产厂商:" + currentNetwork.operateName);
        }
    }

    public void onEventMainThread(SimStateChange event) {
        initView();
    }

    public void onEventMainThread(SimConnectChange event) {
        initView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
        if (mSimConnectReceive != null) {
            unregisterReceiver(mSimConnectReceive);
            mSimConnectReceive = null;
        }
        if (mSimStateReceive != null) {
            unregisterReceiver(mSimStateReceive);
            mSimStateReceive = null;
        }
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE_BASIC_INFORMATION) {
            if (grantResults.length > 0) {
                boolean grant = true;
                for (int granted : grantResults) {
                    if (granted != PackageManager.PERMISSION_GRANTED) {
                        grant = false;
                        break;
                    }
                }
                if (grant == true) {
                    initView();
                } else {
                    finish();
                }
            }
        }
    }
}
