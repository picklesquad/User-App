package picklenostra.user_app;

import android.app.Application;
import android.support.test.espresso.ViewAssertion;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.ActivityInstrumentationTestCase2;
import android.test.ApplicationTestCase;
import android.test.suitebuilder.annotation.SmallTest;
import android.widget.TextView;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.assertThat;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.core.IsNull.notNullValue;

/**
 * Created by Edwin on 5/10/2016.
 */

public class DashboardTest{

    @Rule
    public ActivityTestRule<DashboardActivity> dashboardActivityTestRule = new
            ActivityTestRule<DashboardActivity>(DashboardActivity.class);

    @Test
    public void dashboardContent_isCorrect(){
//
//        onView(withId(R.id.dashboard_profile_name)).check(matches(notNullValue()));
//        onView(withId(R.id.dashboard_member_since)).check(matches(notNullValue()));
//        onView(withId(R.id.balance_content)).check(matches(notNullValue()));
//        onView(withId(R.id.trash_content)).check(matches(notNullValue()));
//        onView(withId(R.id.level_content)).check(matches(notNullValue()));

//        TextView tvProfileName = (TextView) getActivity().findViewById(R.id.dashboard_profile_name);
//        TextView tvMemberSince = (TextView) getActivity().findViewById(R.id.dashboard_member_since);
//        TextView tvUserContent = (TextView) getActivity().findViewById(R.id.balance_content);
//        TextView tvTrashContent = (TextView) getActivity().findViewById(R.id.trash_content);
//        TextView tvLevelContent = (TextView) getActivity().findViewById(R.id.level_content);
//
//        assertNotNull(tvProfileName);
//        assertNotNull(tvMemberSince);
//        assertNotNull(tvUserContent);
//        assertNotNull(tvTrashContent);
//        assertNotNull(tvLevelContent);


    }
}
