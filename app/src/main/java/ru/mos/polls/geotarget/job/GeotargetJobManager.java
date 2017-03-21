package ru.mos.polls.geotarget.job;

import android.content.Context;

import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.RetryStrategy;
import com.firebase.jobdispatcher.Trigger;

/**
 * Created by Sergey Elizarov (sergey.elizarov@altarix.ru)
 * on 21.03.17 12:09.
 */

public class GeotargetJobManager {
    private Context context;
    private FirebaseJobDispatcher dispatcher;
    private Job currentJob;

    public GeotargetJobManager(Context context) {
        this.context = context;
        dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(context));
        currentJob = dispatcher.newJobBuilder()
                .setService(GeotargetJobService.class)
                .setRecurring(true)
                .setTrigger(Trigger.executionWindow(5, 10))
                .setRetryStrategy(RetryStrategy.DEFAULT_LINEAR)
                .setTag("location-update-job")
                .setLifetime(Lifetime.FOREVER)
                .setConstraints(Constraint.ON_ANY_NETWORK)
                .build();
    }

    public void start() {
        dispatcher.mustSchedule(currentJob);
    }

    public void cancel() {
        dispatcher.cancelAll();
    }
}
