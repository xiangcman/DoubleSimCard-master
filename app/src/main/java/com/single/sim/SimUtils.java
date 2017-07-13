package com.single.sim;

import android.Manifest;
import android.content.Context;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.lang.reflect.Method;

import static android.telephony.TelephonyManager.NETWORK_TYPE_1xRTT;
import static android.telephony.TelephonyManager.NETWORK_TYPE_CDMA;
import static android.telephony.TelephonyManager.NETWORK_TYPE_EDGE;
import static android.telephony.TelephonyManager.NETWORK_TYPE_EHRPD;
import static android.telephony.TelephonyManager.NETWORK_TYPE_EVDO_0;
import static android.telephony.TelephonyManager.NETWORK_TYPE_EVDO_A;
import static android.telephony.TelephonyManager.NETWORK_TYPE_EVDO_B;
import static android.telephony.TelephonyManager.NETWORK_TYPE_GPRS;
import static android.telephony.TelephonyManager.NETWORK_TYPE_HSDPA;
import static android.telephony.TelephonyManager.NETWORK_TYPE_HSPA;
import static android.telephony.TelephonyManager.NETWORK_TYPE_HSPAP;
import static android.telephony.TelephonyManager.NETWORK_TYPE_HSUPA;
import static android.telephony.TelephonyManager.NETWORK_TYPE_IDEN;
import static android.telephony.TelephonyManager.NETWORK_TYPE_LTE;
import static android.telephony.TelephonyManager.NETWORK_TYPE_UMTS;
import static com.single.sim.PermissionUtil.hasSelfPermission;

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
        if (hasSelfPermission(context, Manifest.permission.READ_PHONE_STATE) ||
                hasSelfPermission(context, "android.permission.READ_PRIVILEGED_PHONE_STATE")) {
            Log.d(TAG, "READ_PHONE_STATE permission has BEEN granted to getSimPhonenumber().");
            if (getSimStateBySlotIdx(context, slotIdx)) {
                //sim1
                if (slotIdx == 0) {
                    return (String) getSimByMethod(context, SIM_LINE_NUMBER, 1);
                } else if (slotIdx == 1) {
                    if (getSimStateBySlotIdx(context, 0)) {
                        return (String) getSimByMethod(context, SIM_LINE_NUMBER, 2);
                    } else {
                        return (String) getSimByMethod(context, SIM_LINE_NUMBER, 1);
                    }
                }
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
            if (getSimStateBySlotIdx(context, slotIdx)) {
                //sim1
                if (slotIdx == 0) {
                    return (String) getSimByMethod(context, SIM_IMEI, 0);
                } else if (slotIdx == 1) {
                    return (String) getSimByMethod(context, SIM_IMEI, 1);
                }
            }
            return null;
        } else {
            Log.d(TAG, "READ_PHONE_STATE permission has NOT been granted to getSimImei().");
            return null;
        }
    }

    public static String getSimNetworkType(Context context, int slotIdx) {
        if (getSimStateBySlotIdx(context, slotIdx)) {
            //sim1
            if (slotIdx == 0) {
                int type = (int) getSimByMethod(context, SIM_NETWORK_TYPE, 1);
                Log.d(TAG, "type:" + type);
                return getNetworkClass(type);
            } else if (slotIdx == 1) {
                if (getSimStateBySlotIdx(context, 0)) {
                    return getNetworkClass((int) getSimByMethod(context, SIM_NETWORK_TYPE, 2));
                } else {
                    return getNetworkClass((int) getSimByMethod(context, SIM_NETWORK_TYPE, 1));
                }
            }
        }
        return "UNKNOWN";
    }

    public static String getNetworkClass(int networkType) {
        switch (networkType) {
            case NETWORK_TYPE_GPRS:
            case NETWORK_TYPE_EDGE:
            case NETWORK_TYPE_CDMA:
            case NETWORK_TYPE_1xRTT:
            case NETWORK_TYPE_IDEN:
                return "2G";
            case NETWORK_TYPE_UMTS:
            case NETWORK_TYPE_EVDO_0:
            case NETWORK_TYPE_EVDO_A:
            case NETWORK_TYPE_HSDPA:
            case NETWORK_TYPE_HSUPA:
            case NETWORK_TYPE_HSPA:
            case NETWORK_TYPE_EVDO_B:
            case NETWORK_TYPE_EHRPD:
            case NETWORK_TYPE_HSPAP:
                return "3G";
            case NETWORK_TYPE_LTE:
                return "4G";
            default:
                return "UNKNOWN";
        }
    }

    public static String getSimOperatorName(Context context, int slotIdx) {
        if (getSimStateBySlotIdx(context, slotIdx)) {
            //sim1
            if (slotIdx == 0) {
                return (String) getSimByMethod(context, SIM_OPERATOR_NAME, 1);
            } else if (slotIdx == 1) {
                if (getSimStateBySlotIdx(context, 0)) {
                    return (String) getSimByMethod(context, SIM_OPERATOR_NAME, 2);
                } else {
                    return (String) getSimByMethod(context, SIM_OPERATOR_NAME, 1);
                }
            }
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

}
