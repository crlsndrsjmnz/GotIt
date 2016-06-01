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

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import co.carlosandresjimenez.android.gotit.beans.Answer;
import co.carlosandresjimenez.android.gotit.beans.Question;
import co.carlosandresjimenez.android.gotit.cloud.AnswerManager;
import co.carlosandresjimenez.android.gotit.cloud.ApplicationState;
import co.carlosandresjimenez.android.gotit.cloud.CloudFactory;
import co.carlosandresjimenez.android.gotit.cloud.CloudManager;
import co.carlosandresjimenez.android.gotit.connection.ConnectionState;
import co.carlosandresjimenez.android.gotit.connection.GoogleConnection;

/**
 * Created by carlosjimenez on 10/5/15.
 */
public class AddCheckInFragment extends DialogFragment implements Observer, TimePickerFragment.Listener {

    private static final String LOG_TAG = AddCheckInFragment.class.getSimpleName();

    @Bind(R.id.question)
    TextView mTvQuestion;
    @Bind(R.id.answer)
    TextView mTvAnswer;
    @Bind(R.id.answer_checkbox)
    CheckBox mCbAnswer;

    Button mBtNextQuestion;

    ProgressDialog progressDialog;

    CheckInListActivity checkInListActivity;
    Question currentQuestion;
    CloudManager mCloudManager;

    boolean checkinStarted = false;
    int questionPosition = 0;
    int questionNumber = 0;
    int currentRetryNumber = 0;

    String userEmail;

    Listener mListener = null;

    private ArrayList<Question> questions;
    private ArrayList<Answer> answers;

    /* Connection used to interact with Google APIs. */
    private GoogleConnection mGoogleConnection;

    public void setListener(Listener l) {
        mListener = l;
    }

    @Override
    public void onPause() {
        super.onPause();

        if (mCloudManager != null)
            mCloudManager.deleteObserver(this);
    }

    @Override
    public void onResume() {
        super.onResume();

        if (mGoogleConnection != null)
            mGoogleConnection.addObserver(this);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Log.i(LOG_TAG, "AddCheckInFragment:onActivityCreated");

        answers = Lists.newArrayList();

        mGoogleConnection = GoogleConnection.getInstance(getActivity());

        checkInListActivity = (CheckInListActivity) getActivity();

        userEmail = Utility.getUserEmail(getActivity());

        startCheckin();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_checkin, null);
        ButterKnife.bind(this, dialogView);

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setTitle(R.string.title_dialog_checkin)
                .setView(dialogView)
                        // Add action buttons
                .setPositiveButton(R.string.action_next, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                })
                .setNegativeButton(R.string.action_cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        AddCheckInFragment.this.getDialog().cancel();
                    }
                });

        AlertDialog alertDialog = builder.create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();

        mBtNextQuestion = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
        mBtNextQuestion.setOnClickListener(new NextQuestionListener(alertDialog));

        return alertDialog;
    }

    private class NextQuestionListener implements View.OnClickListener {
        private final Dialog dialog;

        public NextQuestionListener(Dialog dialog) {
            this.dialog = dialog;
        }

        @Override
        public void onClick(View v) {
            onClickNextQuestion();
        }
    }

    public void onClickNextQuestion() {

        if (questionNumber > questionPosition) {
            addAnswer(
                    new Answer(
                            null,
                            null,
                            currentQuestion.getQuestionId(),
                            userEmail,
                            mTvAnswer.getText().toString(),
                            currentQuestion.getType(),
                            Utility.getDatetime()));
        } else {
            addAnswer(
                    new Answer(
                            null,
                            null,
                            null,
                            userEmail,
                            String.valueOf(mCbAnswer.isChecked()),
                            "checkbox",
                            Utility.getDatetime()));
        }

        changeQuestion();
    }

    public void changeQuestion() {
        if (questionNumber > questionPosition) {
            currentQuestion = getQuestionAt(questionPosition);
            questionPosition++;

            if (currentQuestion == null) {
                return;
            }

            mTvAnswer.setText("");
            mTvAnswer.setFocusableInTouchMode(true);

            if (currentQuestion.getAnswerType().equals("checkbox")) {
                mTvAnswer.setVisibility(View.GONE);
                mCbAnswer.setVisibility(View.VISIBLE);
            } else {
                mTvAnswer.setVisibility(View.VISIBLE);
                mCbAnswer.setVisibility(View.GONE);

                switch (currentQuestion.getAnswerType()) {
                    case "time":
                        mTvAnswer.setFocusable(false);
                        Utility.hideSoftKeyboard(getActivity(), mTvAnswer);
                        break;
                    case "numeric":
                        mTvAnswer.setInputType(InputType.TYPE_CLASS_NUMBER);
                        break;
                    default:
                        mTvAnswer.setInputType(InputType.TYPE_CLASS_TEXT);
                }
            }

            mTvQuestion.setText(currentQuestion.getValue());

            if (questionNumber == questionPosition)
                mBtNextQuestion.setText(getString(R.string.action_save));
        } else {
            saveAnswers();
            showProgressDialog(getString(R.string.progressdialog_saving));
        }
    }

    @OnClick(R.id.answer)
    void onClickTextView() {

        if (currentQuestion == null)
            return;

        if (currentQuestion.getAnswerType().equals("time")) {
            TimePickerFragment timePickerFragment = new TimePickerFragment();
            timePickerFragment.setListener(this);
            timePickerFragment.show(getActivity().getSupportFragmentManager(), "timePicker");
        }

    }

    @Override
    public void onTimeSet(String time) {
        mTvAnswer.setText(time);
    }

    public void startCheckin() {
        if (!checkinStarted) {
            questionNumber = getQuestionsSize();

            if (questionNumber > 0) {
                checkinStarted = true;

                changeQuestion();
            } else {
                Log.e(LOG_TAG, "AddCheckInFragment:startCheckin - Check-in questions empty");
            }
        } else {
            Log.e(LOG_TAG, "Check-in previusly started");
        }
    }

    public void setQuestions(ArrayList<Question> questions) {
        this.questions = questions;
    }

    public int getQuestionsSize() {
        return questions != null ? questions.size() : 0;
    }

    public boolean isQuestionsEmpty() {
        return questions == null || questions.size() == 0;
    }

    public Question getQuestionAt(int position) {
        return questions != null ? questions.get(position) : null;
    }

    public void addAnswer(Answer answer) {
        answers.add(answer);
    }

    @Override
    public void update(Observable observable, Object data) {
        if (data instanceof ConnectionState) {
            checkConnectionState((ConnectionState) data);
        } else if (data instanceof ApplicationState) {
            checkApplicationState((ApplicationState) data, (AnswerManager) observable);
        }
    }

    public void checkConnectionState(ConnectionState connectionState) {

        switch (connectionState) {
            case CREATED:
                break;
            case OPENING:
                break;
            case OPENED:
                break;
            case CLOSED:
                break;
            case TOKEN_OK:
                //searchUsername();

                break;
            case TOKEN_EXPIRED:
                mGoogleConnection.renewToken();

                break;
        }
    }

    public void checkApplicationState(ApplicationState applicationState, AnswerManager answerManager) {
        String currentAccount = "";

        mCloudManager.deleteObserver(this);
        progressDialog.dismiss();

        switch (applicationState) {
            case ANSWERS_SAVED:

                mListener.onCheckinSaved();
                this.dismiss();

                break;
            case ANSWERS_NOT_SAVED:
                Toast.makeText(getActivity(), "Error: Answers not saved", Toast.LENGTH_SHORT).show();

                break;
            case ACCESS_UNAUTHORIZED:
                Toast.makeText(getActivity(), "Access not available", Toast.LENGTH_SHORT).show();

                break;
            case BAD_REQUEST:

                currentRetryNumber++;
                if (currentRetryNumber <= CloudManager.BADREQUEST_MAX_RETRIES) {
                    Toast.makeText(getActivity(), "Error saving the answers, retrying...", Toast.LENGTH_SHORT).show();

                    saveAnswers();
                } else {
                    Toast.makeText(getActivity(), "Error saving the answers, please try again later.", Toast.LENGTH_SHORT).show();

                    this.dismiss();
                }
                break;
            case NO_INTERNET:
                Toast.makeText(getActivity(), "Network not available", Toast.LENGTH_SHORT).show();

                break;
            default:
                Toast.makeText(getActivity(), "Unknown error " + applicationState.name(), Toast.LENGTH_SHORT).show();

                break;
        }
    }

    public void saveAnswers() {
        mCloudManager = CloudFactory.getManager(getActivity(), answers);
        mCloudManager.addObserver(this);
        mCloudManager.save();
    }

    public interface Listener {
        void onCheckinSaved();
    }

    public void showProgressDialog(String message) {
        progressDialog = ProgressDialog.show(getActivity(), null, message, true, false);
    }
}
