package com.sypir;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

/**
 * Fragment Adapter for different views
 */
class GuestlistViewPagerAdapter extends FragmentStatePagerAdapter
{

	public GuestlistViewPagerAdapter(FragmentManager fm)
	{
		super(fm);
	}

	@Override
	public Fragment getItem(int position) 
	{
		Fragment fragment = null;
		switch(position)
        {
            case 0:
            	fragment = SelectionActivity.newInstance();
            	break;
            case 1:
            	fragment = GuestlistFragment.newInstance();
            	break;
        }
        return fragment;
	}

	@Override
	public int getCount() 
	{
		return 2;
	}
	
	@Override
    public CharSequence getPageTitle(int position) 
	{
		String tabname = "";
        return tabname;
    }
	
}
