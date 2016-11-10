package com.example.android.movieratingapp;

import android.content.Context;
import android.support.v4.app.FragmentManager;

/**
 * Created by Gabe on 2016-11-06.
 */
public class FragmentPagerAdapter extends android.support.v4.app.FragmentPagerAdapter{

    public FragmentPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        mContext = context;
    }

    @Override
    public android.support.v4.app.Fragment getItem(int position) {
        if (position == 0) {
            return new PosterFragment();
        } else {
            return new FavoriteFragment();
        }
    }

    @Override
    public int getCount() {
        return 2;
    }

    private Context mContext;

    @Override
    public CharSequence getPageTitle(int position) {
        if (position == 0) {
            return mContext.getString(R.string.fragment_movies);
        } else {
            return mContext.getString(R.string.fragment_favorite);
        }
    }
}