package com.softtech.apps.autocallrecorder;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.NotificationManager;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SearchView;

import com.softtech.apps.constant.Constant;

public class MainActivity extends Activity implements onKeyBoardEvent{

	private static final int CATEGORY_DETAIL = 1;
	private static final int NO_MEMORY_CARD = 2;
	private static final int TERMS = 3;

	public static final int MEDIA_MOUNTED = 0;
	public static final int MEDIA_MOUNTED_READ_ONLY = 1;
	public static final int NO_MEDIA = 2;

	private Context context;

	private Fragment fragment = null;
	public static boolean isLogin = false;
	public static int isLoginDB = 0;
	public static String password;
	DatabaseHandler db;
	private Config cfgEnablePinCode,cfgPinCode;
	/**
	 * Navication menu
	 * */
	String[] menutitles;
	TypedArray menuIcons;

	// nav drawer title
	private CharSequence mDrawerTitle;
	private CharSequence mTitle;

	private DrawerLayout drawer;
	private ListView drawerList;
	private ActionBarDrawerToggle drawerToggle;

	private List<RowItem> rowItems;
	private CustomMenuAdapter adapter;

	static int mDelayTime = 10000;

	// - END navication menu

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		// enabling action bar app icon and behaving it as toggle button
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);

		// register for dropbox account
		// put my code here
		context = this.getBaseContext();

		// Get TabHost Refference

		setSharedPreferences(true);

		// Get all config and store it to STATIC variable
		db = new DatabaseHandler(this);
		cfgEnablePinCode = db.getConfig(6);
		cfgPinCode = db.getConfig(7);
		/**
		 * Navication menu here
		 * */
		mTitle = mDrawerTitle = getTitle();

		menutitles = getResources().getStringArray(R.array.titles);
		menuIcons = getResources().obtainTypedArray(R.array.icons);

		drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
		drawerList = (ListView) findViewById(R.id.drawer);

		// Get value and store to list Item
		rowItems = new ArrayList<RowItem>();
		for (int i = 0; i < menutitles.length; i++) {
			RowItem items = new RowItem(menutitles[i], menuIcons.getResourceId(
					i, -1));
			rowItems.add(items);
		}
		
		adapter = new CustomMenuAdapter(getApplicationContext(), rowItems);

		drawerList.setAdapter(adapter);

		drawerList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// Display fragment
				updateDisplay(position);

			}
		});

		drawerToggle = new ActionBarDrawerToggle(this, drawer,
				R.drawable.ic_drawer, R.string.app_name, R.string.app_name) {
			@SuppressLint("NewApi")
			public void onDrawerClosed(View view) {
				// getActionBar().setTitle(mTitle);
				// calling onPrepareOptionsMenu() to show action bar icons
				invalidateOptionsMenu();
			}

			@SuppressLint("NewApi")
			public void onDrawerOpened(View drawerView) {
				// getActionBar().setTitle(mDrawerTitle);
				// calling onPrepareOptionsMenu() to hide action bar icons
				invalidateOptionsMenu();
			}
		};
		drawer.setDrawerListener(drawerToggle);
		
		Log.d("LOGIN", "Enable pincode = "+cfgEnablePinCode.get_value());
		if (savedInstanceState == null) {
			// On first time, show Home Fragment
			updateDisplay(0);
		}
		// END - navication menu here
		
		Intent intent = getIntent();
		String notify = intent.getStringExtra("me");
		if(notify != null){
			NotificationManager nMgr = (NotificationManager) getBaseContext()
					.getSystemService(Context.NOTIFICATION_SERVICE);
			nMgr.cancel(100);	
		}
	}

	@SuppressLint("SimpleDateFormat")
	private Date stringToDate(String aDate) {

		if (aDate == null)
			return null;
		ParsePosition pos = new ParsePosition(0);
		SimpleDateFormat simpledateformat = new SimpleDateFormat(
				"yyyy-MM-dd'T'HH:mm:ss'Z'");
		Date stringDate = simpledateformat.parse(aDate, pos);
		return stringDate;

	}

	@SuppressLint("NewApi")
	private void updateDisplay(int position) {
		switch (position) {
		case 0:
			if((cfgEnablePinCode.get_value()==1 && isLoginDB !=0) || cfgEnablePinCode.get_value()==0){
				isLogin = true;
			}else{
				isLogin = false;
			}
			if(isLogin){
				fragment = new optionFramentHome(context);
				getActionBar().setTitle("Home");
				getActionBar().setDisplayHomeAsUpEnabled(true);
				drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED, drawerList);
			}else{
				password =String.valueOf(cfgPinCode.get_value());
				drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_OPEN, drawerList);
				fragment = KeyBoardFragment.newInstance();
				getActionBar().setTitle("Login");
				getActionBar().setHomeButtonEnabled(false);
			}
			
			break;
		case 1:
			getActionBar().setTitle("General setting");
			fragment = new GeneralSetting(context);
			break;
		case 2:
			getActionBar().setTitle("About us");
			fragment = new optionFrament3();
			break;
		case 3:
			// Share app to social network
			Intent shareIntent = new Intent();
			shareIntent.setAction(Intent.ACTION_SEND);
			shareIntent.putExtra(Intent.EXTRA_TEXT, "https://play.google.com/store/apps/details?id=" + getPackageName());// change link to app store
			shareIntent.setType("text/plain"); // set lai cai type
			shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
			startActivity(Intent.createChooser(shareIntent, "Share with"));
			break;
		default:
			getActionBar().setTitle("General setting");
			fragment = new GeneralSetting(context);
			break;
		}

		if (fragment != null) {
			FragmentManager fragmentManager = getFragmentManager();

			fragmentManager.beginTransaction()
					.replace(R.id.frame_container, fragment).commit();
			// update selected item and title, then close the drawer
			setTitle(menutitles[position]);
			drawerList.setItemChecked(position, true);
			drawerList.setSelection(position);
			drawer.closeDrawer(drawerList);
		} else {
			// error in creating fragment
			Log.e("MainActivity", "Error in creating fragment");
		}
	}

	/**
	 * checks if an external memory card is available
	 * 
	 * @return
	 */
	public static int updateExternalStorageState() {
		String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(state)) {
			return MEDIA_MOUNTED;
		} else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
			return MEDIA_MOUNTED_READ_ONLY;
		} else {
			return NO_MEDIA;
		}

	}

	private void setSharedPreferences(boolean settingsValue) {
		SharedPreferences settings = this.getSharedPreferences(
				Constant.LISTEN_ENABLED, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putBoolean("silentMode", settingsValue);
		editor.commit();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		
		//Log.d("QUERY", "Khoi tao menu o Home Fragment ###");
		
		 SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
         SearchView searchView = (SearchView) menu.findItem(R.id.menu_search).getActionView();

         searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
         searchView.setIconifiedByDefault(true);  

         SearchView.OnQueryTextListener textChangeListener = new SearchView.OnQueryTextListener()
         {
             @Override
             public boolean onQueryTextChange(String newText)
             {
            	 if(fragment == null)
            		 return true;
            	 if(fragment.getClass().equals(GeneralSetting.class)){
            		 Log.d("Fragment", "Fragment General setting");
            		 Message msg=new Message();
                	 Bundle b=new Bundle();
                	 b.putString("text", newText);
                	 msg.setData(b);
                	 GeneralSetting.handle.sendMessage(msg);
            	 }
            	 if(fragment.getClass().equals(optionFramentHome.class)){
                	 Message msg=new Message();
                	 Bundle b=new Bundle();
                	 b.putString("text", newText);
                	 msg.setData(b);
            		 optionFramentHome.handle.sendMessage(msg);
            	 }
                 return true;
             }
             @Override
             public boolean onQueryTextSubmit(String query)
             {
                 // this is your adapter that will be filtered
            	 //Log.d("QUERY", "Submit query ="+query);
                 return true;
             }
         };
         searchView.setOnQueryTextListener(textChangeListener);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// toggle nav drawer on selecting action bar app icon/title
		if (drawerToggle.onOptionsItemSelected(item)) {
			return true;
		}
		// Handle action bar actions click
		switch (item.getItemId()) {
		
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	/***
	 * Called when invalidateOptionsMenu() is triggered
	 */
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		// if nav drawer is opened, hide the action items
		boolean drawerOpen = drawer.isDrawerOpen(drawerList);
//		menu.findItem(R.id.action_settings).setVisible(!drawerOpen);
		
		// If the nav drawer is open, hide action items related to the content
		// view

		return super.onPrepareOptionsMenu(menu);
	}

	/**
	 * When using the ActionBarDrawerToggle, you must call it during
	 * onPostCreate() and onConfigurationChanged()...
	 */

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred.
		drawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		// Pass any configuration change to the drawer toggles
		drawerToggle.onConfigurationChanged(newConfig);
	}

	// @Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
	}

	public boolean hasConnections() {
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo ni = cm.getActiveNetworkInfo();
		if (null == ni)
			return false;
		return ni.isConnectedOrConnecting();
	}

	@Override
	public void numberIsPressed(String total) {
		// TODO Auto-generated method stub
		Log.d("KEYBOARD", "Keyboard event");
	}

	@Override
	public void doneButtonPressed(String total) {
		// TODO Auto-generated method stub
		Log.d("KEYBOARD", "Keyboard event");
	}

	@Override
	public void backLongPressed() {
		// TODO Auto-generated method stub
		Log.d("KEYBOARD", "Keyboard event");
	}

	@Override
	public void backButtonPressed(String total) {
		// TODO Auto-generated method stub
		Log.d("KEYBOARD", "Keyboard event");
	}
	
}
