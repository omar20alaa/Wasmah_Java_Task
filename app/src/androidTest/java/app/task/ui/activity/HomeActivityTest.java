package app.task.ui.activity;

import android.app.Activity;
import android.app.Instrumentation;
import android.view.View;

import androidx.test.rule.ActivityTestRule;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import app.task.R;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;


public class HomeActivityTest {

    @Rule
    public ActivityTestRule<HomeActivity> homeActivityTestTestRule = new ActivityTestRule<HomeActivity>(HomeActivity.class);

    private HomeActivity homeActivity = null;
    Instrumentation.ActivityMonitor monitor =
            getInstrumentation().addMonitor(FavActivity.class.getName(), null, false);

    @Before
    public void setUp() throws Exception {
        homeActivity = homeActivityTestTestRule.getActivity();
    }

    @Test
    public void testIntentNewActivity() {
       assert  (homeActivity.findViewById(R.id.fab) != null);
        onView(withId(R.id.fab)).perform(click());
        Activity second_activity = getInstrumentation().waitForMonitorWithTimeout(monitor, 5000);
        assert (second_activity != null);
        second_activity.finish();
    }

    @Test
    public void testFindIdLaunch() {
        View view = homeActivity.findViewById(R.id.title);
        assert view != null;
    }


    @After
    public void tearDown() throws Exception {
        homeActivity = null;
    }
}