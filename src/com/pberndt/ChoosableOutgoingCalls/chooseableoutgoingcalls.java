package com.pberndt.ChoosableOutgoingCalls;

import android.content.Intent;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;
import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;
import android.net.Uri;
import android.os.Build;
import android.telecom.PhoneAccountHandle;
import android.telecom.PhoneAccount;
import android.telecom.PhoneAccountHandle;
import android.telecom.TelecomManager;
import android.telecom.VideoProfile;

public class chooseableoutgoingcalls implements IXposedHookLoadPackage {
	public void handleLoadPackage(final LoadPackageParam lpparam) throws Throwable {
		if (!lpparam.packageName.equals("com.android.contacts") && !lpparam.packageName.equals("com.google.android.contacts")) {
			return;
		}

		if(Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
			// Lollipop only, Marshmallow doesn't need this
			findAndHookMethod("com.android.contacts.common.CallUtil", lpparam.classLoader, "getCallIntent", Uri.class, String.class, PhoneAccountHandle.class, int.class, new XC_MethodReplacement() {
				@Override
				protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
					Uri uri = (Uri)param.args[0];
					String callOrigin = (String)param.args[1];
					PhoneAccountHandle accountHandle = (PhoneAccountHandle)param.args[2];
					int videoState = (int)param.args[3];

					final Intent intent = new Intent(Intent.ACTION_VIEW, uri);
					intent.putExtra(TelecomManager.EXTRA_START_CALL_WITH_VIDEO_STATE, videoState);
					if (callOrigin != null) {
						intent.putExtra("com.android.phone.CALL_ORIGIN", callOrigin);
					}
					if (accountHandle != null) {
						intent.putExtra(TelecomManager.EXTRA_PHONE_ACCOUNT_HANDLE, accountHandle);
					}
					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					return intent;
				}
			});
		}
		else {
			findAndHookMethod("com.android.contacts.common.CallUtil", lpparam.classLoader, "getCallIntent", Uri.class, String.class, new XC_MethodReplacement() {
				@Override
				protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
					final Intent intent = new Intent("android.intent.action.VIEW", (Uri) param.args[0]);
					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					String callOrigin = (String) param.args[1];
					if (callOrigin != null) {
						// From com.android.phone.common.PhoneConstants
						intent.putExtra("com.android.phone.CALL_ORIGIN", callOrigin);
					}
					return intent;
				}
			});
		}
	}

}
