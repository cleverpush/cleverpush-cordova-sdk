package com.cleverpush.cordova;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.google.gson.Gson;

import java.util.Iterator;
import java.util.ArrayList;
import java.util.Collection;

import com.cleverpush.CleverPush;
import com.cleverpush.NotificationOpenedResult;
import com.cleverpush.listener.NotificationOpenedListener;
import com.cleverpush.listener.SubscribedListener;

public class CleverPushPlugin extends CordovaPlugin {
  public static final String TAG = "CleverPushPlugin";

  private static void callbackSuccess(CallbackContext callbackContext, JSONObject jsonObject) {
    if (jsonObject == null) {
      jsonObject = new JSONObject();
    }

    PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, jsonObject);
    pluginResult.setKeepCallback(true);
    callbackContext.sendPluginResult(pluginResult);
  }

  private static void callbackSuccess(CallbackContext callbackContext, String resultString) {
    PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, resultString);
    pluginResult.setKeepCallback(true);
    callbackContext.sendPluginResult(pluginResult);
  }

  private static void callbackError(CallbackContext callbackContext, JSONObject jsonObject) {
    if (jsonObject == null) {
      jsonObject = new JSONObject();
    }

    PluginResult pluginResult = new PluginResult(PluginResult.Status.ERROR, jsonObject);
    pluginResult.setKeepCallback(true);
    callbackContext.sendPluginResult(pluginResult);
  }

  private static void callbackError(CallbackContext callbackContext, String str) {
    PluginResult pluginResult = new PluginResult(PluginResult.Status.ERROR, str);
    pluginResult.setKeepCallback(true);
    callbackContext.sendPluginResult(pluginResult);
  }

  @Override
  public boolean execute(String action, JSONArray data, CallbackContext openedContext, CallbackContext subscribedContext) {
    if (action.equals("init")) {
      try {
        String channelId = data.getString(0);

        CleverPush.init(this.cordova.getActivity(), channelId, new CordovaNotificationOpenedHandler(openedContext), new CordovaSubscribedHandler(subscribedContext));
        return true;
      } catch (JSONException e) {
        Log.e(TAG, "execute: Got JSON Exception " + e.getMessage());
        return false;
      }
    } else {
      Log.e(TAG, "Invalid action: " + action);
      callbackError(callbackContext, "Invalid action : " + action);
      return false;
    }
  }

  private class CordovaNotificationOpenedHandler implements NotificationOpenedListener {
    private CallbackContext callbackContext;

    public CordovaNotificationOpenedHandler(CallbackContext callbackContext) {
      this.callbackContext = callbackContext;
    }

    @Override
    public void notificationOpened(NotificationOpenedResult result) {
      try {
        Gson gson = new Gson();

        JSONObject resultObj = new JSONObject();
        resultObj.put("notification", new JSONObject(gson.toJson(result.getNotification())));
        resultObj.put("subscription", new JSONObject(gson.toJson(result.getSubscription())));

        callbackSuccess(callbackContext, resultObj);
      } catch (Throwable t) {
        t.printStackTrace();
      }
    }
  }

  private class CordovaSubscribedHandler implements SubscribedListener {
    private CallbackContext callbackContext;

    public CordovaSubscribedHandler(CallbackContext callbackContext) {
      this.callbackContext = callbackContext;
    }

    @Override
    public void subscribed(String subscriptionId) {
      try {
        callbackSuccess(callbackContext, subscriptionId);
      } catch (Throwable t) {
        t.printStackTrace();
      }
    }
  }
}
