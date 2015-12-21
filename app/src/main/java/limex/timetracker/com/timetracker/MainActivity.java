package limex.timetracker.com.timetracker;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextClock;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import io.github.codefalling.recyclerviewswipedismiss.SwipeDismissRecyclerViewTouchListener;
import limex.timetracker.com.adapters.TrackerAdapter;
import limex.timetracker.com.helpers.DatabaseHelper;
import limex.timetracker.com.models.Tracker;
import xyz.danoz.recyclerviewfastscroller.vertical.VerticalRecyclerViewFastScroller;

public class MainActivity extends AppCompatActivity {

    private Context context;
    private int id;
    private DatabaseHelper mDbHelper;
    private TrackerAdapter mAdapter;

    // Views
    private TextClock clock;
    private Button mBtnLogin;
    private Button mBtnLogout;
    private RecyclerView mRecyclerView;
    private VerticalRecyclerViewFastScroller fastScroller;
    private SwipeDismissRecyclerViewTouchListener mSwipeDismiss;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initObject();

        if (mDbHelper.checkIfNewDay()) {
            in();
        } else {
            id = mDbHelper.getId();
            mBtnLogin.setEnabled(false);

            if (mDbHelper.checkIfAlreadyLoggedOut()) {
                mBtnLogout.setEnabled(false);
            }
        }

        mBtnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mDbHelper.checkIfAlreadyLoggedIn()) {
                    Toast.makeText(context, "Thank You!", Toast.LENGTH_SHORT).show();
                    mDbHelper.loggedIn();
                    out();
                    getData();
                }
            }
        });

        mBtnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mDbHelper.checkIfAlreadyLoggedOut()) {
                    Toast.makeText(context, "Thank You!", Toast.LENGTH_SHORT).show();
                    id = mDbHelper.getId();
                    mDbHelper.loggedOut(id);
                    mBtnLogout.setEnabled(false);
                    getData();
                }
            }
        });
    }

    public void getData() {
        final List<Tracker> list = new ArrayList<>();
        list.addAll(mDbHelper.getTrackerList());

        mAdapter = new TrackerAdapter(this, list);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        fastScroller = (VerticalRecyclerViewFastScroller) findViewById(R.id.fast_scroller);
        fastScroller.setRecyclerView(mRecyclerView);
        mRecyclerView.setOnScrollListener(fastScroller.getOnScrollListener());
        mSwipeDismiss= new SwipeDismissRecyclerViewTouchListener.Builder(
                mRecyclerView,
                new SwipeDismissRecyclerViewTouchListener.DismissCallbacks() {
                    @Override
                    public boolean canDismiss(int position) {
                        return true;
                    }

                    @Override
                    public void onDismiss(View view) {
                        // Do what you want when dismiss
                        int positionId = mRecyclerView.getChildPosition(view);
                        Tracker tracker = list.get(positionId);
                        mDbHelper.removeRecord(tracker.date);
                        mAdapter.list.remove(positionId);
                        mAdapter.notifyDataSetChanged();
                        getData();

                        Toast.makeText(context, "Delete " + tracker.date + " record!",Toast.LENGTH_LONG).show();
                    }
                })
                .setIsVertical(false)
                .setItemTouchCallback(
                        new SwipeDismissRecyclerViewTouchListener.OnItemTouchCallBack() {
                            @Override
                            public void onTouch(int index) {
                                // Do what you want when item be touched
                            }
                        })
                .create();
        mRecyclerView.setOnTouchListener(mSwipeDismiss);
    }

    private void initObject() {
        context = this;
        mDbHelper = new DatabaseHelper(this);
        clock = (TextClock) findViewById(R.id.clock);
        mBtnLogin = (Button) findViewById(R.id.button_login);
        mBtnLogout = (Button) findViewById(R.id.button_logout);
        mRecyclerView = (RecyclerView) findViewById(R.id.tracker_list);
        getData();
    }

    private void in() {
        mBtnLogin.setEnabled(true);
        mBtnLogout.setEnabled(false);
    }

    private void out() {
        mBtnLogin.setEnabled(false);
        mBtnLogout.setEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Clear all data?")
                    .setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            mDbHelper.clear();
                            getData();
                            in();
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
