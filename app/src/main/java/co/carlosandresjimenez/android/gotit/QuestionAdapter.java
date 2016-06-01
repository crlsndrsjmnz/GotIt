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

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import co.carlosandresjimenez.android.gotit.beans.Answer;
import co.carlosandresjimenez.android.gotit.beans.Question;

/**
 * Created by carlosjimenez on 10/26/15.
 */
public class QuestionAdapter extends ArrayAdapter<Question> {

    private final Context context;
    private final ArrayList<Question> questions;

    public QuestionAdapter(Context context, ArrayList<Question> questions) {
        super(context, -1, questions);
        this.context = context;
        this.questions = questions;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = LayoutInflater.from(context).inflate(R.layout.list_item_question, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);

        Question question = questions.get(position);
        Answer answer = question != null ? question.getAnswer() : null;
        if (question != null && answer != null) {

            viewHolder.tvQuestion.setText(question.getValue());
            viewHolder.tvAnswer.setText(answer.getValue());
        }

        return view;
    }

    /**
     * Cache of the children views for a forecast list item.
     */
    public static class ViewHolder {
        public final TextView tvQuestion;
        public final TextView tvAnswer;

        public ViewHolder(View view) {
            tvQuestion = (TextView) view.findViewById(R.id.question);
            tvAnswer = (TextView) view.findViewById(R.id.answer);
        }
    }

}