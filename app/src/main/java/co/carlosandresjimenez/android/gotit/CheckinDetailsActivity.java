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

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;

import java.util.ArrayList;
import java.util.Observer;

import co.carlosandresjimenez.android.gotit.beans.Question;
import co.carlosandresjimenez.android.gotit.cloud.QuestionManager;

/**
 * Created by carlosjimenez on 10/26/15.
 */
public class CheckinDetailsActivity extends BaseActivity implements Observer {

    private static final String LOG_TAG = CheckinDetailsActivity.class.getSimpleName();

    public static final boolean SHARING_ALLOWED = true;
    public static final boolean SHARING_FORBIDDEN = false;

    ArrayList<Question> mQuestions;

    ProgressBar mPbCheckinDetails;
    ListView mLvQuestions;

    boolean isAllowSharing = false;

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkin_details);

        mPbCheckinDetails = (ProgressBar) findViewById(R.id.checkinDetailsLoading);
        mLvQuestions = (ListView) findViewById(R.id.checkin_details_list);

        if (getIntent().hasExtra(CHECKIN_ALLOW_SHARING))
            isAllowSharing = getIntent().getBooleanExtra(CHECKIN_ALLOW_SHARING, SHARING_FORBIDDEN);

        getCheckinDetails();

        updateUI(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        if (isAllowSharing) {
            getMenuInflater().inflate(R.menu.checkin_details, menu);
            finishCreatingMenu(menu);
        }
        return true;
    }

    private String getCheckinDataTxt() {

        if (mQuestions == null)
            return "";

        String data = "";
        for (Question question : mQuestions) {
            data = data + "\n" + question.getValue() + " " + question.getAnswer().getValue();
        }
        return data;
    }

    private void finishCreatingMenu(Menu menu) {
        // Retrieve the share menu item
        MenuItem menuItem = menu.findItem(R.id.action_share);

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT,
                getCheckinDataTxt());

        menuItem.setIntent(shareIntent);
    }

    public void updateUI(boolean isLoading) {

        if (isLoading)
            mPbCheckinDetails.setVisibility(View.VISIBLE);
        else
            mPbCheckinDetails.setVisibility(View.GONE);
    }

    @Override
    public void onQuestionsNotFound() {
        super.onQuestionsNotFound();
    }

    @Override
    public void onQuestionsFound(QuestionManager questionManager) {
        super.onQuestionsFound(questionManager);

        updateUI(false);
        mQuestions = questionManager.getQuestions();

        refreshQuestionAdapter();

        if (isAllowSharing) {
            Toolbar toolbarView = (Toolbar) findViewById(R.id.toolbar);

            if (null != toolbarView) {
                Menu menu = toolbarView.getMenu();
                if (null != menu) menu.clear();
                toolbarView.inflateMenu(R.menu.checkin_details);
                finishCreatingMenu(toolbarView.getMenu());
            }
        }

    }

    private void refreshQuestionAdapter() {
        QuestionAdapter adapter = new QuestionAdapter(this, mQuestions);
        mLvQuestions.setAdapter(adapter);
    }

}
