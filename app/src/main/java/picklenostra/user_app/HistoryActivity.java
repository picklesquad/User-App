package picklenostra.user_app;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import picklenostra.user_app.adapter.HistoryPagerAdapter;
import picklenostra.user_app.fragment.TransaksiFragment;
import picklenostra.user_app.fragment.WithdrawalFragment;

public class HistoryActivity extends AppCompatActivity {
    HistoryPagerAdapter pagerAdapter;
    ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("History");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setElevation(0);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.history_tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText("Transaksi"));
        tabLayout.addTab(tabLayout.newTab().setText("Withdrawal"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        viewPager = (ViewPager) findViewById(R.id.history_pager);
        pagerAdapter = new HistoryPagerAdapter(getSupportFragmentManager(),
                tabLayout.getTabCount());
        viewPager.setAdapter(pagerAdapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                //TODO: Implement your custom code
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                //TODO: Implement your custom code
            }
        });

        String type = getIntent().getExtras().getString("type");
        if (type.equals("1")) {
            viewPager.setCurrentItem(0);
        } else {
            viewPager.setCurrentItem(1);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.history_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        switch (itemId){
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_refresh:
                int active = viewPager.getCurrentItem();
                if (active == 0) {
                    Fragment f = pagerAdapter.getItem(0);
                    if (f instanceof TransaksiFragment) {
                        getTransaksiRefreshListener().onRefresh();
                    } else {
                        Toast.makeText(HistoryActivity.this, "fragmen gatau", Toast.LENGTH_SHORT).show();
                    }
                } else if (active == 1) {
                    Fragment f = pagerAdapter.getItem(1);
                    if (f instanceof WithdrawalFragment) {
                        getWithdrawalRefreshListener().onRefresh();
                    } else {
                        Toast.makeText(HistoryActivity.this, "fragmen gatau", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(HistoryActivity.this, "fragmen gatau", Toast.LENGTH_SHORT).show();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        finish();
        startActivity(new Intent(this, DashboardActivity.class));
    }

    /**
     * buat refresh transaksi history
     */
    public interface TransaksiRefreshListener{
        void onRefresh();
    }

    private TransaksiRefreshListener tRefreshListener;

    public TransaksiRefreshListener getTransaksiRefreshListener() {
        return tRefreshListener;
    }

    public void setTransaksiRefreshListener(TransaksiRefreshListener refreshListener) {
        this.tRefreshListener = refreshListener;
    }

    /**
     * buat refresh withdraw history
     */
    private WithdrawalRefreshListener wRefreshListener;

    public interface WithdrawalRefreshListener{
        void onRefresh();
    }

    public WithdrawalRefreshListener getWithdrawalRefreshListener() {
        return wRefreshListener;
    }

    public void setWithdrawalRefreshListener(WithdrawalRefreshListener refreshListener) {
        this.wRefreshListener = refreshListener;
    }
}