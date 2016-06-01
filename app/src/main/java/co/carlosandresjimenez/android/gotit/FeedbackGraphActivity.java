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

import android.animation.PropertyValuesHolder;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import com.db.chart.Tools;
import com.db.chart.model.LineSet;
import com.db.chart.view.AxisController;
import com.db.chart.view.ChartView;
import com.db.chart.view.LineChartView;
import com.db.chart.view.Tooltip;
import com.db.chart.view.animation.Animation;

import java.util.ArrayList;

import co.carlosandresjimenez.android.gotit.beans.Answer;
import co.carlosandresjimenez.android.gotit.cloud.AnswerManager;

/**
 * Created by carlosjimenez on 11/11/15.
 */
public class FeedbackGraphActivity extends BaseActivity {

    private static final String LOG_TAG = FeedbackGraphActivity.class.getSimpleName();

    ProgressDialog mProgressDialog;

    LineChartView mLineChartView;

    private ArrayList<Answer> mAnswers;

    private int maxValue = 0;
    private String[] mLabels = {""};
    private float[] mValues = {0f};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback_graph);

        mCurrentActivity = ACTIVITY_GRAPH_KEY;

        mLineChartView = (LineChartView) findViewById(R.id.linechart);

        getGraphData(Utility.getUserEmail(this));

        showProgressDialog(getString(R.string.progressdialog_loading));
    }

    @Override
    public void onAnswersNotFound() {
        super.onAnswersNotFound();

        mProgressDialog.dismiss();
    }

    @Override
    public void onAnswersFound(AnswerManager answerManager) {
        super.onAnswersFound(answerManager);

        if (answerManager.getAnswers() == null || answerManager.getAnswers().isEmpty())
            return;

        mAnswers = answerManager.getAnswers();

        createDataAndLabel();

        createChart(new Runnable() {
            @Override
            public void run() {
                new Handler().postDelayed(new Runnable() {
                    public void run() {
                    }
                }, 500);
            }
        });

        mProgressDialog.dismiss();
    }

    public void createDataAndLabel() {

        mLabels = new String[mAnswers.size()];
        mValues = new float[mAnswers.size()];
        int i = 0;

        for (Answer answer : mAnswers) {
            mLabels[i] = answer.getDatetime().substring(2, 10);
            mValues[i] = Float.parseFloat(answer.getValue() + "f");

            if (mValues[i] > maxValue)
                maxValue = (int) mValues[i] + 20;

            i++;
        }

        maxValue = maxValue + (100 - (maxValue % 100));
    }

    public void showProgressDialog(String message) {
        mProgressDialog = ProgressDialog.show(this, null, message, true, false);
    }

    public void createChart(Runnable action) {

        Tooltip tip = new Tooltip(this, R.layout.linechart_tooltip, R.id.value);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {

            tip.setEnterAnimation(PropertyValuesHolder.ofFloat(View.ALPHA, 1),
                    PropertyValuesHolder.ofFloat(View.SCALE_X, 1f),
                    PropertyValuesHolder.ofFloat(View.SCALE_Y, 1f));

            tip.setExitAnimation(PropertyValuesHolder.ofFloat(View.ALPHA, 0),
                    PropertyValuesHolder.ofFloat(View.SCALE_X, 0f),
                    PropertyValuesHolder.ofFloat(View.SCALE_Y, 0f));
        }

        mLineChartView.setTooltips(tip);

        LineSet dataset = new LineSet(mLabels, mValues);
        dataset.setColor(Color.parseColor("#FF58C674"))
                .setDotsStrokeThickness(Tools.fromDpToPx(2))
                .setDotsStrokeColor(Color.parseColor("#FF58C674"))
                .setDotsColor(Color.parseColor("#eef1f6"));
        mLineChartView.addData(dataset);

        Paint gridPaint = new Paint();
        gridPaint.setColor(Color.parseColor("#308E9196"));
        gridPaint.setStyle(Paint.Style.STROKE);
        gridPaint.setAntiAlias(true);
        gridPaint.setStrokeWidth(Tools.fromDpToPx(1f));

        mLineChartView.setBorderSpacing(1)
                .setAxisBorderValues(0, maxValue, maxValue / 10)
                .setXLabels(AxisController.LabelPosition.OUTSIDE)
                .setYLabels(AxisController.LabelPosition.OUTSIDE)
                .setLabelsColor(Color.parseColor("#FF8E9196"))
                .setFontSize(25)
                .setAxisLabelsSpacing(1)
                .setXAxis(false)
                .setYAxis(false)
                .setBorderSpacing(Tools.fromDpToPx(15))
                .setGrid(ChartView.GridType.VERTICAL, gridPaint);

        Animation anim = new Animation().setEndAction(action);

        mLineChartView.show(anim);
    }

    public void updateChart() {
        mLineChartView.dismissAllTooltips();
        mLineChartView.updateValues(0, mValues);
        mLineChartView.notifyDataUpdate();
    }
}