package com.pberndt.ChoosableOutgoingCalls;

import android.content.Intent;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;
import de.robv.android.xposed.XposedHelpers;


public class chooseableoutgoingcalls implements IXposedHookLoadPackage {
	public void handleLoadPackage(final LoadPackageParam lpparam) throws Throwable {
		if (!lpparam.packageName.equals("com.android.contacts") && !lpparam.packageName.equals("com.google.android.contacts")) {
			return;
		}

		XposedBridge.hookAllMethods(XposedHelpers.findClass("com.android.contacts.common.CallUtil", lpparam.classLoader), "getCallIntent", new XC_MethodHook() {
			@Override
			protected void afterHookedMethod(MethodHookParam param) throws Throwable {
				super.afterHookedMethod(param);
				if (param.hasThrowable()) {
					return;
				}
				Intent result = (Intent) param.getResult();
				result.setAction(Intent.ACTION_VIEW);
				result.setComponent(null);
				param.setResult(result);
			}
		});
	}
}
