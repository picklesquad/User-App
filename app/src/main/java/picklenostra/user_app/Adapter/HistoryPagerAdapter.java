package picklenostra.user_app.Adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import picklenostra.user_app.Fragment.TransaksiFragment;
import picklenostra.user_app.Fragment.WithdrawalFragment;

/**
 * Created by Edwin on 5/10/2016.
 */
public class HistoryPagerAdapter extends FragmentStatePagerAdapter {

    int numOfTabs;

    public HistoryPagerAdapter(FragmentManager fm, int numOfTabs) {
        super(fm);
        this.numOfTabs = numOfTabs;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                TransaksiFragment transaksiFragment = new TransaksiFragment();
                return transaksiFragment;
            case 1:
                WithdrawalFragment withdrawalFragment = new WithdrawalFragment();
                return withdrawalFragment;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return numOfTabs;
    }
}
