package com.pberndt.ChoosableOutgoingCalls;

import android.content.Intent;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;
import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;
import android.net.Uri;


public class chooseableoutgoingcalls implements IXposedHookLoadPackage {
	public void handleLoadPackage(final LoadPackageParam lpparam) throws Throwable {
		if (!lpparam.packageName.equals("com.android.contacts")) {			
			return;
		}
		
		findAndHookMethod("com.android.contacts.common.CallUtil", lpparam.classLoader, "getCallIntent", Uri.class, String.class, new XC_MethodReplacement() {
			@Override
			protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
				final Intent intent = new Intent("android.intent.action.VIEW", (Uri)param.args[0]);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				String callOrigin = (String)param.args[1];
				if (callOrigin != null) {
					// From com.android.phone.common.PhoneConstants
					intent.putExtra("com.android.phone.CALL_ORIGIN", callOrigin);
				}
				return intent;
			}
		});
	}

}
