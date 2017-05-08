package shine.com.doorscreen.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

/**
 * Created by Administrator on 2016/8/8.
 */
public class DoorInfoPagerAdapter extends FragmentPagerAdapter {
    private List<Fragment> mFragmentList;



    public DoorInfoPagerAdapter(FragmentManager fm,List<Fragment> mFragmentList) {
        super(fm);
        this.mFragmentList=mFragmentList;
    }

    @Override
    public Fragment getItem(int position) {
        return mFragmentList.get(position);
    }

    @Override
    public int getCount() {
        return null==mFragmentList?0:mFragmentList.size();
    }
}
