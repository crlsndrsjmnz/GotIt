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

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

import co.carlosandresjimenez.android.gotit.beans.Checkin;
import co.carlosandresjimenez.android.gotit.beans.Question;
import co.carlosandresjimenez.android.gotit.cloud.CheckinManager;
import co.carlosandresjimenez.android.gotit.cloud.QuestionManager;

/**
 * Created by carlosjimenez on 10/4/15.
 */
public class CheckInListActivity extends BaseActivity implements AddCheckInFragment.Listener {

    private static final String LOG_TAG = CheckInListActivity.class.getSimpleName();

    ListView listView;

    ProgressDialog progressDialog;

    ArrayList<Question> mQuestions;
    ArrayList<Checkin> mCheckins;

    boolean openingCheckinDialog = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkin);

        mCurrentActivity = ACTIVITY_CHECKIN_KEY;

        // Get ListView object from xml
        listView = (ListView) findViewById(R.id.checkin_list);

        // ListView Item Click Listener
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // ListView Clicked item value
                Checkin checkin = mCheckins.get(position);
                openCheckinDetails(checkin.getCheckinId(), CheckinDetailsActivity.SHARING_ALLOWED);
            }

        });

        getCheckinList();
    }

    @Override
    public void onFabClicked() {
        super.onFabClicked();

        if (mQuestions != null && !mQuestions.isEmpty())
            openCheckinDialog();
        else {
            showProgressDialog(getString(R.string.progressdialog_loading));
            openingCheckinDialog = true;
        }

    }

    @Override
    public void onQuestionsFound(QuestionManager questionManager) {
        super.onQuestionsFound(questionManager);

        mQuestions = questionManager.getQuestions();

        if (openingCheckinDialog) {
            openCheckinDialog();
            progressDialog.dismiss();
            openingCheckinDialog = false;
        }

    }

    @Override
    public void onQuestionsNotFound() {
        super.onQuestionsNotFound();

        Toast.makeText(this, "Questions not found", Toast.LENGTH_SHORT).show();

        if (openingCheckinDialog) {
            progressDialog.dismiss();
            openingCheckinDialog = false;
        }
    }

    @Override
    public void onCheckinsNotFound() {
        super.onCheckinsNotFound();

        getQuestionList();
    }

    @Override
    public void onCheckinsFound(CheckinManager checkinManager) {
        super.onCheckinsFound(checkinManager);

        mCheckins = checkinManager.getCheckins();
        refreshCheckinAdapter();

        getQuestionList();
    }

    private void refreshCheckinAdapter() {
        CheckinAdapter adapter = new CheckinAdapter(this, mCheckins);
        listView.setAdapter(adapter);
    }

    @Override
    public void onCheckinSaved() {
        getCheckinList();
    }

    public void openCheckinDialog() {
        AddCheckInFragment fragment = new AddCheckInFragment();

        if (mQuestions != null && !mQuestions.isEmpty())
            fragment.setQuestions(mQuestions);

        fragment.setMenuVisibility(true);

        fragment.setListener(CheckInListActivity.this);
        fragment.show(getSupportFragmentManager(), CHECKIN_FRAGMENT_TAG);
    }

    public void showProgressDialog(String message) {
        progressDialog = ProgressDialog.show(this, null, message, true, true, new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                openingCheckinDialog = false;
            }
        });
    }
}
