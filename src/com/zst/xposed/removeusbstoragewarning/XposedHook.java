package com.zst.xposed.removeusbstoragewarning;

import static de.robv.android.xposed.XposedHelpers.findClass;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

public class XposedHook implements IXposedHookLoadPackage{

	@Override
	public void handleLoadPackage(LoadPackageParam lpparam ) {
		if (!lpparam.packageName.equals("com.android.systemui")) return;
		onCreateDialog(lpparam);
	}

	private static void onCreateDialog(final LoadPackageParam lpparam) { 
		Class<?> hookClass = findClass("com.android.systemui.usb.UsbStorageActivity", lpparam.classLoader);
		XposedBridge.hookAllMethods(hookClass, "onCreateDialog", new XC_MethodHook(){ 
			@Override 
			protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
				final int DLG_CONFIRM_KILL_STORAGE_USERS = 1; // from source : id integer to show confirm warning.
				int id = (Integer)param.args[0];
				if (id == DLG_CONFIRM_KILL_STORAGE_USERS){ 
					param.args[0] = 100; 
					//Now change the id to a random number so no dialog id match(and no dialog will be returned)
				}
				//Finally call method to start usb storage
				XposedHelpers.callMethod(param.thisObject, "switchUsbMassStorage", Boolean.TRUE);
			}
		});
	}
}
