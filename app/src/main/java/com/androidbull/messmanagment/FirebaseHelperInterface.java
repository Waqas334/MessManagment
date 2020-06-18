package com.androidbull.messmanagment;

import org.json.JSONObject;

public interface FirebaseHelperInterface {
    void menuDownloadComplete(JSONObject menuJSONObject);
    void menuDownloadFailed(String errorMessage);

    void currentUserMealState(JSONObject userMealState);
}
