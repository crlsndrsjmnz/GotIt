/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Carlos Andres Jimenez <apps@carlosandresjimenez.co>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package co.carlosandresjimenez.android.gotit;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

import co.carlosandresjimenez.android.gotit.beans.Checkin;
import co.carlosandresjimenez.android.gotit.cloud.CheckinManager;

/**
 * Created by carlosjimenez on 10/4/15.
 */
public class DataFeedActivity extends BaseActivity {

    private static final String LOG_TAG = DataFeedActivity.class.getSimpleName();

    ListView listView;
    ArrayList<Checkin> mCheckins;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);

        mCurrentActivity = ACTIVITY_FEED_KEY;

        // Get ListView object from xml
        listView = (ListView) findViewById(R.id.feed_list);

        // ListView Item Click Listener
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Checkin checkin = mCheckins.get(position);
                openCheckinDetails(checkin.getCheckinId(), CheckinDetailsActivity.SHARING_FORBIDDEN);
            }

        });

        getCheckinFeedList();
    }

    @Override
    public void onCheckinsNotFound() {
        super.onCheckinsNotFound();
    }

    @Override
    public void onCheckinsFound(CheckinManager checkinManager) {
        super.onCheckinsFound(checkinManager);

        mCheckins = checkinManager.getCheckins();
        refreshCheckinAdapter();
    }

    private void refreshCheckinAdapter() {
        CheckinAdapter adapter = new CheckinAdapter(this, mCheckins);
        listView.setAdapter(adapter);
    }

}
