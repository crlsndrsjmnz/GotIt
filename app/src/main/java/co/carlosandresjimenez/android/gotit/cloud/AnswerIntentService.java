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

package co.carlosandresjimenez.android.gotit.cloud;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;

import java.util.ArrayList;

import co.carlosandresjimenez.android.gotit.api.GotItSvc;
import co.carlosandresjimenez.android.gotit.api.GotItSvcApi;
import co.carlosandresjimenez.android.gotit.beans.Answer;
import retrofit.RetrofitError;

/**
 * Created by carlosjimenez on 10/24/15.
 */
public class AnswerIntentService extends IntentService {

    private final static String LOG_TAG = UserIntentService.class.getCanonicalName();

    /**
     * The default constructor for this service. Simply forwards construction to
     * IntentService, passing in a name for the Thread that the service runs in.
     */
    public AnswerIntentService() {
        super("IntentService Worker Thread");
    }

    /**
     * Optionally allow the instantiator to specify the name of the thread this
     * service runs in.
     */
    public AnswerIntentService(String name) {
        super(name);
    }

    /**
     * Hook method called when a component calls startService() with the proper
     * intent. This method serves as the Executor in the Command Processor
     * Pattern. It receives an Intent, which serves as the Command, and executes
     * some action based on that intent in the context of this service.
     */
    @Override
    protected void onHandleIntent(Intent intent) {

        int requestCode = intent.getIntExtra(CloudManager.REQUEST_CODE_KEY, 0);

        switch (requestCode) {
            case CloudManager.REQUEST_TYPE_SAVE:
                handleSaveIntent(intent);
                break;
            case CloudManager.REQUEST_TYPE_FIND_ONE:
                handleFindOneIntent(intent);
                break;
            case CloudManager.REQUEST_TYPE_FIND_ALL:
                handleFindAllIntent(intent);
                break;
            case CloudManager.REQUEST_TYPE_DELETE:
                handleDeleteIntent(intent);
                break;
        }
    }

    public void handleSaveIntent(Intent intent) {
        GotItSvcApi gotItSvcApi = GotItSvc.connect(intent.getStringExtra(CloudManager.AUTH_TOKEN_KEY));
        Messenger messenger = (Messenger) intent.getExtras().get(CloudManager.MESSENGER_KEY);
        ArrayList<Answer> answers = (ArrayList<Answer>) intent.getExtras().get(CloudManager.ANSWER_ENTITIES_KEY);
        int responseCode;

        Message msg = Message.obtain();
        Bundle data = new Bundle();
        data.putInt(CloudManager.REQUEST_CODE_KEY, intent.getIntExtra(CloudManager.REQUEST_CODE_KEY, 0));

        try {
            responseCode = gotItSvcApi.saveAnswers(answers);

            if (responseCode == CloudManager.RESPONSE_STATUS_OK) {
                data.putInt(CloudManager.RESPONSE_CODE_KEY, CloudManager.RESPONSE_STATUS_OK);
            } else {
                data.putInt(CloudManager.RESPONSE_CODE_KEY, CloudManager.RESPONSE_STATUS_NOT_SAVED);
            }
        } catch (RetrofitError re) {
            data.putInt(CloudManager.RESPONSE_CODE_KEY, re.getResponse() != null ? re.getResponse().getStatus() : CloudManager.RESPONSE_STATUS_ERROR);
        } catch (Exception e) {
            data.putInt(CloudManager.RESPONSE_CODE_KEY, CloudManager.RESPONSE_STATUS_ERROR);
        }

        // Make the Bundle the "data" of the Message.
        msg.setData(data);

        try {
            // Send the Message back to the client Activity.
            messenger.send(msg);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void handleFindOneIntent(Intent intent) {

    }

    public void handleFindAllIntent(Intent intent) {
        GotItSvcApi gotItSvcApi = GotItSvc.connect(intent.getStringExtra(CloudManager.AUTH_TOKEN_KEY));
        Messenger messenger = (Messenger) intent.getExtras().get(CloudManager.MESSENGER_KEY);
        ArrayList<Answer> answers;

        Message msg = Message.obtain();
        Bundle data = new Bundle();
        data.putInt(CloudManager.REQUEST_CODE_KEY, intent.getIntExtra(CloudManager.REQUEST_CODE_KEY, 0));

        try {
            answers = gotItSvcApi.getGraphData(intent.getStringExtra(CloudManager.ANSWER_EMAIL_KEY));

            if (answers != null && !answers.isEmpty()) {
                data.putInt(CloudManager.RESPONSE_CODE_KEY, CloudManager.RESPONSE_STATUS_OK);

                data.putParcelableArrayList(CloudManager.ANSWER_ENTITIES_KEY, answers);
            } else {
                data.putInt(CloudManager.RESPONSE_CODE_KEY, CloudManager.RESPONSE_STATUS_NO_CONTENT);
            }

        } catch (RetrofitError re) {
            data.putInt(CloudManager.RESPONSE_CODE_KEY, re.getResponse() != null ? re.getResponse().getStatus() : CloudManager.RESPONSE_STATUS_ERROR);
        } catch (Exception e) {
            data.putInt(CloudManager.RESPONSE_CODE_KEY, CloudManager.RESPONSE_STATUS_ERROR);
        }

        // Make the Bundle the "data" of the Message.
        msg.setData(data);

        try {
            // Send the Message back to the client Activity.
            messenger.send(msg);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void handleDeleteIntent(Intent intent) {

    }
}