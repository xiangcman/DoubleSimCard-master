package com.single.sim;

import android.Manifest;
import android.content.Context;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.lang.reflect.Method;

/**
 * Created by xiangcheng on 17/7/13.
 */

public class SimUtils {
    private static final String TAG = SimUtils.class.getSimpleName();
    private static final String SIM_STATE = "getSimState";
    private static final String SIM_OPERATOR_NAME = "getNetworkOperatorName";
    private static final String SIM_NETWORK_TYPE = "getNetworkType";
    private static final String SIM_IMEI = "getImei";
    private static final String SIM_LINE_NUMBER = "getLine1Number";

    public static String getSimPhonenumber(Context context, int slotIdx) {
        if (PermissionUtil.hasSelfPermission(context, Manifest.permission.READ_PHONE_STATE) ||
                PermissionUtil.hasSelfPermission(context, "android.permission.READ_PRIVILEGED_PHONE_STATE")) {
            Log.d(TAG, "READ_PHONE_STATE permission has BEEN granted to getSimPhonenumber().");
            if (getSimStateBySlotIdx(context, slotIdx)) {
                return (String) getSimByMethod(context, SIM_LINE_NUMBER, getSubidBySlotId(context, slotIdx));
            }
            return null;
        } else {
            Log.d(TAG, "READ_PHONE_STATE permission has NOT been granted to getSimPhonenumber().");
            return null;
        }
    }

    public static String getSimImei(Context context, int slotIdx) {
        if (PermissionUtil.hasSelfPermission(context, Manifest.permission.READ_PHONE_STATE) ||
                PermissionUtil.hasSelfPermission(context, "android.permission.READ_PRIVILEGED_PHONE_STATE")) {
            Log.d(TAG, "READ_PHONE_STATE permission has BEEN granted to getSimImei().");
            return (String) getSimByMethod(context, SIM_IMEI, slotIdx);;
        } else {
            Log.d(TAG, "READ_PHONE_STATE permission has NOT been granted to getSimImei().");
            return null;
        }
    }

    public static String getSimNetworkName(Context context, int slotIdx) {
        if (getSimStateBySlotIdx(context, slotIdx)) {
            return getNetworkName((int)
                    getSimByMethod(context, SIM_NETWORK_TYPE, getSubidBySlotId(context, slotIdx)));
        }
        return "UNKNOWN";
    }

    public static String getSimOperatorName(Context context, int slotIdx) {
        if (getSimStateBySlotIdx(context, slotIdx)) {
            return (String) getSimByMethod(context, SIM_OPERATOR_NAME, getSubidBySlotId(context, slotIdx));
        }
        return null;
    }

    /**
     * @param context
     * @param slotIdx:0(sim1),1(sim2)
     * @return
     */
    public static boolean getSimStateBySlotIdx(Context context, int slotIdx) {
        boolean isReady = false;
        Object getSimState = getSimByMethod(context, SIM_STATE, slotIdx);
        if (getSimState != null) {
            int simState = Integer.parseInt(getSimState.toString());
            if ((simState != TelephonyManager.SIM_STATE_ABSENT) && (simState != TelephonyManager.SIM_STATE_UNKNOWN)) {
                isReady = true;
            }
        }
        return isReady;
    }

    public static Object getSimByMethod(Context context, String method, int param) {
        TelephonyManager telephony = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        try {
            Class<?> telephonyClass = Class.forName(telephony.getClass().getName());
            Class<?>[] parameter = new Class[1];
            parameter[0] = int.class;
            Method getSimState = telephonyClass.getMethod(method, parameter);
            Object[] obParameter = new Object[1];
            obParameter[0] = param;
            Object ob_phone = getSimState.invoke(telephony, obParameter);

            if (ob_phone != null) {
                return ob_phone;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static String getNetworkName(int networkType) {
        switch (networkType) {
            case TelephonyManager.NETWORK_TYPE_GPRS:
            case TelephonyManager.NETWORK_TYPE_EDGE:
            case TelephonyManager.NETWORK_TYPE_CDMA:
            case TelephonyManager.NETWORK_TYPE_1xRTT:
            case TelephonyManager.NETWORK_TYPE_IDEN:
                return "2G";
            case TelephonyManager.NETWORK_TYPE_UMTS:
            case TelephonyManager.NETWORK_TYPE_EVDO_0:
            case TelephonyManager.NETWORK_TYPE_EVDO_A:
            case TelephonyManager.NETWORK_TYPE_HSDPA:
            case TelephonyManager.NETWORK_TYPE_HSUPA:
            case TelephonyManager.NETWORK_TYPE_HSPA:
            case TelephonyManager.NETWORK_TYPE_EVDO_B:
            case TelephonyManager.NETWORK_TYPE_EHRPD:
            case TelephonyManager.NETWORK_TYPE_HSPAP:
                return "3G";
            case TelephonyManager.NETWORK_TYPE_LTE:
                return "4G";
            default:
                return "UNKNOWN";
        }
    }

    /**
     * to
     * @param context
     * @param slotId
     * @return
     */
    public static int getSubidBySlotId(Context context, int slotId) {
        SubscriptionManager subscriptionManager = (SubscriptionManager) context.getSystemService(
                Context.TELEPHONY_SUBSCRIPTION_SERVICE);
        try {
            Class<?> telephonyClass = Class.forName(subscriptionManager.getClass().getName());
            Class<?>[] parameter = new Class[1];
            parameter[0] = int.class;
            Method getSimState = telephonyClass.getMethod("getSubId", parameter);
            Object[] obParameter = new Object[1];
            obParameter[0] = slotId;
            Object ob_phone = getSimState.invoke(subscriptionManager, obParameter);

            if (ob_phone != null) {
                Log.d(TAG, "slotId:" + slotId + ";" + ((int[]) ob_phone)[0]);
                return ((int[]) ob_phone)[0];
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;

    }

}
