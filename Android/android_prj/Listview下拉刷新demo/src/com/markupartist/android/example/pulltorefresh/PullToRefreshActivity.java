package com.markupartist.android.example.pulltorefresh;

import java.util.Arrays;
import java.util.LinkedList;

import android.app.Activity;
import android.app.ListActivity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ArrayAdapter;

import com.markupartist.android.widget.PullToRefreshListView;
import com.markupartist.android.widget.PullToRefreshListView.OnRefreshListener;

public class PullToRefreshActivity extends Activity {    
    private LinkedList<String> mListItems;

    private PullToRefreshListView mlistview;
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pull_to_refresh);

        // Set a listener to be invoked when the list should be refreshed.
        mlistview =  ((PullToRefreshListView)PullToRefreshActivity.this.findViewById(R.id.usalist) );
        mlistview.setOnRefreshListener(new OnRefreshListener() {
			
			@Override
			public void onRefresh() {
				// TODO Auto-generated method stub
				 new GetDataTask().execute();
			}
		});/*() {
            @Override
            public void onRefresh() {
                // Do work to refresh the list here.
                new GetDataTask().execute();
            }
        });*/

        mListItems = new LinkedList<String>();
        mListItems.addAll(Arrays.asList(mStrings));

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, mListItems);

        mlistview.setAdapter(adapter);
    }

    //执行异步的操作
    private class GetDataTask extends AsyncTask<Void, Void, String[]> {

        @Override
        protected String[] doInBackground(Void... params) {
            // Simulates a background job.
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                ;
            }
            return mStrings;
        }

        @Override
        protected void onPostExecute(String[] result) {
            mListItems.addFirst("Added after refresh...");

            // Call onRefreshComplete when the list has been refreshed.
            mlistview.onRefreshComplete();

            super.onPostExecute(result);
        }
    }

    private String[] mStrings = {
            "Abbaye de Belloc", "Abbaye du Mont des Cats", "Abertam",
            "Abondance", "Ackawi", "Acorn", "Adelost", "Affidelice au Chablis",
            "Afuega'l Pitu", "Airag", "Airedale", "Aisy Cendre",
            "Allgauer Emmentaler"};
}

