package lapisnet.it;

import java.util.Date;

import lapisnet.it.FilterCategoryDialogFragment.OnFilterCategoryListener;
import lapisnet.it.FilterDateDialogFragment.OnFilterDateListener;
import lapisnet.it.utility.ConnectionDetector;
import lapisnet.it.adapter.DrawerAdapter;
import lapisnet.it.adapter.ListNavigationItemModel;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

public class MainActivity extends ActionBarActivity implements OnFilterDateListener, OnFilterCategoryListener {

	static final String ACTION = "android.net.conn.CONNECTIVITY_CHANGE";
	private ConnectionDetector mCd;
	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;
	private ActionBarDrawerToggle mDrawerToggle;
	private CharSequence mDrawerTitle;
	private CharSequence mTitle;
	private String[] mList1, mList2;
	private MyBroadcastReceiver mConnectionReceiver;
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.activity_main);
		
		setProgressBarIndeterminateVisibility(false);
		mCd = new ConnectionDetector(MainActivity.this);
		mConnectionReceiver = new MyBroadcastReceiver();
		IntentFilter filter = new IntentFilter(ACTION);
		this.registerReceiver(mConnectionReceiver, filter);
		mTitle = mDrawerTitle = getTitle();
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);

		DrawerAdapter mAdapter = new DrawerAdapter(this);        
				
		mList1 = getResources().getStringArray(R.array.list_1); 
		String[] menuOneIcons = getResources().getStringArray(R.array.icon_list_1);
		int oneIcons = 0;
		for (String item : mList1) {
			int id_menu_one = this.getResources().getIdentifier(item, "string", this.getPackageName());
			int id_menu_one_icons = getResources().getIdentifier(menuOneIcons[oneIcons], "drawable", this.getPackageName());
			ListNavigationItemModel mItem = new ListNavigationItemModel(id_menu_one, id_menu_one_icons);
			mAdapter.addItem(mItem);
			oneIcons++;
		}
		
		mAdapter.addHeader(R.string.title1);
		mList2 = getResources().getStringArray(R.array.list_2); 
		String[] menuTwoIcons = getResources().getStringArray(R.array.icon_list_2);
		int twoIcons = 0;
		for (String item : mList2) {
			int id_menu_one = this.getResources().getIdentifier(item, "string", this.getPackageName());
			int id_menu_one_icons = getResources().getIdentifier(menuTwoIcons[twoIcons], "drawable", this.getPackageName());
			ListNavigationItemModel mItem = new ListNavigationItemModel(id_menu_one, id_menu_one_icons);
			mAdapter.addItem(mItem);
			twoIcons++;
		}

		mDrawerList = (ListView) findViewById(R.id.left_drawer);
		if (mDrawerList != null)
			mDrawerList.setAdapter(mAdapter);	

		mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setHomeButtonEnabled(true);
		mDrawerToggle = new ActionBarDrawerToggle(
				this,
				mDrawerLayout,
				R.drawable.ic_drawer,
				R.string.drawer_open,
				R.string.drawer_close
				) {
			public void onDrawerClosed(View view) {		
				getSupportActionBar().setTitle(mTitle);
				supportInvalidateOptionsMenu();
			}

			public void onDrawerOpened(View drawerView) {				
				getSupportActionBar().setTitle(mDrawerTitle);
				supportInvalidateOptionsMenu();
			}
		};
		mDrawerLayout.setDrawerListener(mDrawerToggle);

		if (savedInstanceState == null) {
			selectItem(0);
		}
		
	}
	
	@Override
	protected void onResume() {
		IntentFilter filter = new IntentFilter(ACTION);
		this.registerReceiver(mConnectionReceiver, filter);
		super.onResume();
	}

	@Override
	protected void onStop() {
		unregisterReceiver(mConnectionReceiver);
		super.onStop();
	}


	/*** COMMENTATA LA ZONA DEL MENU PER IL WEB SEARCH ***/
	
   @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //MenuInflater inflater = getMenuInflater();
        //inflater.inflate(R.menu.main, menu);        
        return super.onCreateOptionsMenu(menu);
    }
   
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        //boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
        //menu.findItem(R.id.action_websearch).setVisible(!drawerOpen);
        return super.onPrepareOptionsMenu(menu);
    }

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (mDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}
		switch(item.getItemId()) {
//		case R.id.action_websearch:
//			Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
//			intent.putExtra(SearchManager.QUERY, getActionBar().getTitle());
//			if (intent.resolveActivity(getPackageManager()) != null) {
//				startActivity(intent);
//			} else {
//				Toast.makeText(this, R.string.app_not_available, Toast.LENGTH_LONG).show();
//			}
//			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private class DrawerItemClickListener implements ListView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			selectItem(position);
		}
	}

	private void selectItem(int position) {
		// update the main content by replacing fragments
		//Fragment fragment = new PlanetFragment();
		//Bundle args = new Bundle();
		//args.putInt(PlanetFragment.ARG_PLANET_NUMBER, position);
		//fragment.setArguments(args);
		
		/*Fragment fragment = new EventListFragment();
		Bundle tipo_home = new Bundle();
		tipo_home.putString("titolo", getString(R.string.tipo_home));
		fragment.setArguments(tipo_home);*/
		
		Fragment fragment = new HomeEventListFragment();

		if (!mCd.isConnectingToInternet()) { 
			fragment = new NoConnectionFragment();
		}
		else {
			
			
			switch(position) {
			case 0:
				fragment = new HomeEventListFragment();
				Bundle tipo0 = new Bundle();
				tipo0.putString("titolo", getString(R.string.a_list_2));
				fragment.setArguments(tipo0);
				break;
			case 1:
				fragment = new CategoryListFragment();
				Bundle tipo1 = new Bundle();
				tipo1.putString("titolo", getString(R.string.b_list_2));
				fragment.setArguments(tipo1);
				break;	
			case 3:
				fragment = new EventListFragment();
				Bundle tipo3 = new Bundle();
				tipo3.putString("titolo", getString(R.string.a_list_1));
				tipo3.putString("provincia", "1");
				fragment.setArguments(tipo3);
				break;
			case 4:
				fragment = new EventListFragment();
				Bundle tipo4 = new Bundle();
				tipo4.putString("titolo", getString(R.string.b_list_1));
				tipo4.putString("provincia", "2");
				fragment.setArguments(tipo4);
				break;
			case 5:
				fragment = new EventListFragment();
				Bundle tipo5 = new Bundle();
				tipo5.putString("titolo", getString(R.string.c_list_1));
				tipo5.putString("provincia", "3");
				fragment.setArguments(tipo5);
				break;
			case 6:
				fragment = new EventListFragment();
				Bundle tipo6 = new Bundle();
				tipo6.putString("titolo", getString(R.string.d_list_1));
				tipo6.putString("provincia", "4");
				fragment.setArguments(tipo6);
				break;
			case 7:
				fragment = new EventListFragment();
				Bundle tipo7 = new Bundle();
				tipo7.putString("titolo", getString(R.string.e_list_1));
				tipo7.putString("provincia", "5");
				fragment.setArguments(tipo7);
				break;
			case 8:
				fragment = new EventListFragment();
				Bundle tipo8 = new Bundle();
				tipo8.putString("titolo", getString(R.string.f_list_1));
				tipo8.putString("provincia", "6");
				fragment.setArguments(tipo8);
				break;
			case 9:
				fragment = new EventListFragment();
				Bundle tipo9 = new Bundle();
				tipo9.putString("titolo", getString(R.string.g_list_1));
				tipo9.putString("provincia", "7");
				fragment.setArguments(tipo9);
				break;
			case 10:
				fragment = new EventListFragment();
				Bundle tipo10 = new Bundle();
				tipo10.putString("titolo", getString(R.string.h_list_1));
				tipo10.putString("provincia", "8");
				fragment.setArguments(tipo10);
				break;			
			case 11:
				fragment = new EventListFragment();
				Bundle tipo11 = new Bundle();
				tipo11.putString("titolo", getString(R.string.i_list_1));
				tipo11.putString("provincia", "9");
				fragment.setArguments(tipo11);
				break;	
			
			}	
		}

		FragmentManager fragmentManager = getSupportFragmentManager();
		fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();

		mDrawerList.setItemChecked(position, true);
		mDrawerLayout.closeDrawer(mDrawerList);
	}

	@SuppressLint("NewApi")
	@Override
	public void setTitle(CharSequence title) {
		mTitle = title;
		getActionBar().setTitle(mTitle);
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		mDrawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		mDrawerToggle.onConfigurationChanged(newConfig);
	}

	public void showStatusConnection(boolean a) {
		((TextView)findViewById(R.id.statusConnection)).setVisibility(!a ? View.GONE : View.VISIBLE);
	}


	private class MyBroadcastReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			boolean a = intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY,false);
			showStatusConnection(a);
		}
	};
	
	
	public void onCompleteDate(Date date) {}
	public void onCompleteCategory(String idCat, String tCat) {}

}