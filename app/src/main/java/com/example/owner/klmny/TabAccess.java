package com.example.owner.klmny;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class TabAccess extends FragmentPagerAdapter {

    public TabAccess(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int i) {

        switch (i) {

            case 0:
                ChatsFrag chatsFrag = new ChatsFrag();
                return chatsFrag;
            case 1:
                GroupsFrag groupsFrag = new GroupsFrag();
                return groupsFrag;
            case 2:
                ContactsFrag contactsFrag = new ContactsFrag();
                return contactsFrag;
            default:
                return null;
        }

    }

    @Override
    public int getCount() {
        return 3;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {

            case 0:
               return "Chats";
            case 1:
                return "Groups";
            case 2:
                return "Contacts";
            default:
                return null;
        }
    }
}
