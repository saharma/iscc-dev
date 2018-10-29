package com.applozic.phonegap;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.applozic.mobicomkit.Applozic;
import com.applozic.mobicomkit.api.account.register.RegisterUserClientService;
import com.applozic.mobicomkit.api.account.register.RegistrationResponse;
import com.applozic.mobicomkit.api.account.user.MobiComUserPreference;
import com.applozic.mobicomkit.api.account.user.PushNotificationTask;
import com.applozic.mobicomkit.api.account.user.User;
import com.applozic.mobicomkit.api.account.user.UserClientService;
import com.applozic.mobicomkit.api.account.user.UserDetail;
import com.applozic.mobicomkit.api.account.user.UserLoginTask;
import com.applozic.mobicomkit.api.account.user.UserService;
import com.applozic.mobicomkit.api.conversation.database.MessageDatabaseService;
import com.applozic.mobicomkit.api.notification.MobiComPushReceiver;
import com.applozic.mobicomkit.api.people.ChannelInfo;
import com.applozic.mobicomkit.channel.service.ChannelService;
import com.applozic.mobicomkit.contact.AppContactService;
import com.applozic.mobicomkit.uiwidgets.async.ApplozicChannelAddMemberTask;
import com.applozic.mobicomkit.uiwidgets.async.ApplozicChannelRemoveMemberTask;
import com.applozic.mobicomkit.uiwidgets.async.ApplozicConversationCreateTask;
import com.applozic.mobicomkit.uiwidgets.conversation.ConversationUIService;
import com.applozic.mobicomkit.uiwidgets.conversation.activity.ConversationActivity;
import com.applozic.mobicomkit.uiwidgets.people.activity.MobiComKitPeopleActivity;
import com.applozic.mobicommons.json.AnnotationExclusionStrategy;
import com.applozic.mobicommons.json.GsonUtils;
import com.applozic.mobicommons.people.channel.Channel;
import com.applozic.mobicommons.people.channel.Conversation;
import com.applozic.mobicommons.people.contact.Contact;
import com.applozic.mobicomkit.uiwidgets.ApplozicSetting;
//import com.applozic.audiovideo.activity.AudioCallActivityV2;
//import com.applozic.audiovideo.activity.VideoActivity;
import com.applozic.mobicomkit.ApplozicClient;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class ApplozicCordovaPlugin extends CordovaPlugin {

    @Override
    public boolean execute(String action, JSONArray data, CallbackContext callbackContext) throws JSONException {
        Context context = cordova.getActivity().getApplicationContext();
        String response = "success";

        final CallbackContext callback = callbackContext;

        if (action.equals("login")) {
            String userJson = data.getString(0);
            User user = (User) GsonUtils.getObjectFromJson(userJson, User.class);

            Applozic.init(context, user.getApplicationId());

            /*List<String> featureList =  new ArrayList<>();
            featureList.add(User.Features.IP_AUDIO_CALL.getValue());// FOR AUDIO
            featureList.add(User.Features.IP_VIDEO_CALL.getValue());// FOR VIDEO
            user.setFeatures(featureList); // ADD FEATURES*/

            UserLoginTask.TaskListener listener = new UserLoginTask.TaskListener() {

                @Override
                public void onSuccess(RegistrationResponse registrationResponse, Context context) {
                    //After successful registration with Applozic server the callback will come her

                    ApplozicClient.getInstance(context).setHandleDial(true).setIPCallEnabled(true);
                    Map<ApplozicSetting.RequestCode, String> activityCallbacks = new HashMap<ApplozicSetting.RequestCode, String>();
                   /* activityCallbacks.put(ApplozicSetting.RequestCode.AUDIO_CALL, AudioCallActivityV2.class.getName());
                    activityCallbacks.put(ApplozicSetting.RequestCode.VIDEO_CALL, VideoActivity.class.getName());*/
                    ApplozicSetting.getInstance(context).setActivityCallbacks(activityCallbacks);


                    callback.success(GsonUtils.getJsonFromObject(registrationResponse, RegistrationResponse.class));
                }

                @Override
                public void onFailure(RegistrationResponse registrationResponse, Exception exception) {
                    //If any failure in registration the callback  will come here
                    callback.error(GsonUtils.getJsonFromObject(registrationResponse, RegistrationResponse.class));
                }};

            new UserLoginTask(user, listener, context).execute((Void) null);
        } else if (action.equals("registerPushNotification")) {
            PushNotificationTask pushNotificationTask = null;
            PushNotificationTask.TaskListener listener=  new PushNotificationTask.TaskListener() {
                @Override
                public void onSuccess(RegistrationResponse registrationResponse) {
                    callback.success(GsonUtils.getJsonFromObject(registrationResponse, RegistrationResponse.class));
                }
                @Override
                public void onFailure(RegistrationResponse registrationResponse, Exception exception) {
                    callback.error(GsonUtils.getJsonFromObject(registrationResponse, RegistrationResponse.class));
                }
            };
            pushNotificationTask = new PushNotificationTask(Applozic.getInstance(context).getDeviceRegistrationId(), listener, context);
            pushNotificationTask.execute((Void)null);
        } else if (action.equals("isLoggedIn")) {
            callbackContext.success(String.valueOf(MobiComUserPreference.getInstance(context).isLoggedIn()));
        } else if (action.equals("getUnreadCount")) {
            callback.success(String.valueOf(new MessageDatabaseService(context).getTotalUnreadCount()));
        } else if (action.equals("updatePushNotificationToken")) {
            if (MobiComUserPreference.getInstance(context).isRegistered()) {
                try {
                    new RegisterUserClientService(context).updatePushNotificationId(data.getString(0));
                    callback.success(response);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else if (action.equals("launchChat")) {
            Intent intent = new Intent(context, ConversationActivity.class);
            if (ApplozicClient.getInstance(context).isContextBasedChat()) {
                intent.putExtra(ConversationUIService.CONTEXT_BASED_CHAT,true);
            }
            cordova.getActivity().startActivity(intent);
        } else if (action.equals("launchChatWithUserId")) {
            Intent intent = new Intent(context, ConversationActivity.class);
            intent.putExtra(ConversationUIService.USER_ID, data.getString(0));
            cordova.getActivity().startActivity(intent);
        } else if (action.equals("launchChatWithGroupId")) {
            Intent intent = new Intent(context, ConversationActivity.class);
            intent.putExtra(ConversationUIService.GROUP_ID, data.getString(0));
            cordova.getActivity().startActivity(intent);
        } else if (action.equals("launchChatWithClientGroupId")) {
            Intent intent = new Intent(context, ConversationActivity.class);
            intent.putExtra(ConversationUIService.CLIENT_GROUP_ID, data.getString(0));
            cordova.getActivity().startActivity(intent);
        }  else if (action.equals("startNew")) {
            Intent intent = new Intent(context, MobiComKitPeopleActivity.class);
            cordova.getActivity().startActivity(intent);
        } else if (action.equals("showAllRegisteredUsers")) {
            if ("true".equals(data.getString(0))) {
                ApplozicSetting.getInstance(context).enableRegisteredUsersContactCall();
            }
        } else if (action.equals("addContact")) {
            String contactJson = data.getString(0);
            UserDetail userDetail = (UserDetail) GsonUtils.getObjectFromJson(contactJson, UserDetail.class);
            UserService.getInstance(context).processUser(userDetail);
            callback.success(response);
        } else if (action.equals("updateContact")) {
            String contactJson = data.getString(0);
            Contact contact = (Contact) GsonUtils.getObjectFromJson(contactJson, Contact.class);
            AppContactService appContactService = new AppContactService(context);
            appContactService.updateContact(contact);
            callback.success(response);
        } else if (action.equals("removeContact")) {
            String contactJson = data.getString(0);
            Contact contact = (Contact) GsonUtils.getObjectFromJson(contactJson, Contact.class);
            AppContactService appContactService = new AppContactService(context);
            appContactService.deleteContact(contact);
            callback.success(response);
        } else if (action.equals("addContacts")) {
            String contactJson = data.getString(0);
            Gson gson = new GsonBuilder().setExclusionStrategies(new AnnotationExclusionStrategy()).create();
            UserDetail[] userDetails = (UserDetail[]) gson.fromJson(contactJson, UserDetail[].class);
            for (UserDetail userDetail: userDetails) {
                UserService.getInstance(context).processUser(userDetail);
            }
            callback.success(response);
        } else if (action.equals("processPushNotification")) {
            Map<String, String> pushData = new HashMap<String, String>();
            //convert data to pushData
            System.out.println(data);
            if (MobiComPushReceiver.isMobiComPushNotification(pushData)) {
                MobiComPushReceiver.processMessageAsync(context, pushData);
            }
            callback.success(response);
        } else if (action.equals("createGroup")) {
            ChannelInfo channelInfo = (ChannelInfo) GsonUtils.getObjectFromJson(data.getString(0), ChannelInfo.class);
            Channel channel = ChannelService.getInstance(context).createChannel(channelInfo);
            callback.success(GsonUtils.getJsonFromObject(channel, Channel.class));
        }  else if (action.equals("addGroupMember")) {
            Gson gson = new Gson();
            Type type = new TypeToken<Map<String, String>>(){}.getType();
            Map<String, String> channelDetails = gson.fromJson(data.getString(0), type);

            ApplozicChannelAddMemberTask.ChannelAddMemberListener channelAddMemberListener =  new ApplozicChannelAddMemberTask.ChannelAddMemberListener() {
                @Override
                public void onSuccess(String response, Context context) {
                    //Response will be "success" if user is added successfully
                    Log.i("ApplozicChannelMember","Add Response:" + response);
                    callback.success(response);
                }

                @Override
                public void onFailure(String response, Exception e, Context context) {
                    callback.success(response);
                }
            };

            Integer channelKey = getChannelKey(context, channelDetails);

            ApplozicChannelAddMemberTask applozicChannelAddMemberTask =  new ApplozicChannelAddMemberTask(context, channelKey, channelDetails.get("userId"), channelAddMemberListener);//pass channel key and userId whom you want to add to channel
            applozicChannelAddMemberTask.execute((Void)null);
        }  else if (action.equals("removeGroupMember")) {
            Gson gson = new Gson();
            Type type = new TypeToken<Map<String, String>>(){}.getType();
            Map<String, String> channelDetails = gson.fromJson(data.getString(0), type);

            ApplozicChannelRemoveMemberTask.ChannelRemoveMemberListener channelRemoveMemberListener = new ApplozicChannelRemoveMemberTask.ChannelRemoveMemberListener() {
                @Override
                public void onSuccess(String response, Context context) {
                    //Response will be "success" if user is removed successfully
                    Log.i("ApplozicChannel","remove member response:"+response);
                    callback.success(response);
                }

                @Override
                public void onFailure(String response, Exception e, Context context) {
                    callback.success(response);
                }
            };

            Integer channelKey = getChannelKey(context, channelDetails);

            ApplozicChannelRemoveMemberTask applozicChannelRemoveMemberTask =  new ApplozicChannelRemoveMemberTask(context,channelKey, channelDetails.get("userId"),channelRemoveMemberListener);//pass channelKey and userId whom you want to remove from channel
            applozicChannelRemoveMemberTask.execute((Void)null);
        } else if(action.equals("enableTopicBasedChat")) {
            ApplozicClient.getInstance(context).setContextBasedChat( data.getBoolean(0));
        } else if (action.equals("startTopicBasedChat")) {
            final Conversation conversation = (Conversation) GsonUtils.getObjectFromJson(data.getString(0), Conversation.class);

            ApplozicConversationCreateTask applozicConversationCreateTask = null;

            ApplozicConversationCreateTask.ConversationCreateListener conversationCreateListener =  new ApplozicConversationCreateTask.ConversationCreateListener() {
                @Override
                public void onSuccess(Integer conversationId, Context context) {
                    Intent intent = new Intent(context, ConversationActivity.class);
                    intent.putExtra("takeOrder", true);
                    if (!TextUtils.isEmpty(conversation.getUserId())) {
                        intent.putExtra(ConversationUIService.USER_ID, conversation.getUserId());//RECEIVER USERID
                    } else {
                        intent.putExtra(ConversationUIService.GROUP_ID, conversation.getGroupId());//RECEIVER USERID
                    }
                    intent.putExtra(ConversationUIService.CONTEXT_BASED_CHAT, true);
                    intent.putExtra(ConversationUIService.CONVERSATION_ID,conversationId);
                    cordova.getActivity().startActivity(intent);
                }

                @Override
                public void onFailure(Exception e, Context context) {

                }
            };
            applozicConversationCreateTask = new ApplozicConversationCreateTask(context, conversationCreateListener, conversation);
            applozicConversationCreateTask.execute((Void)null);
        } else if (action.equals("logout")) {
            new UserClientService(cordova.getActivity()).logout();
            callbackContext.success(response);
        } else {
            return false;
        }

        return true;
    }

    private Integer getChannelKey(Context context, Map<String, String> channelDetails) {
        Integer channelKey;
        if (channelDetails.containsKey("groupId")) {
            channelKey = Integer.parseInt(channelDetails.get("groupId"));
        } else {
            String clientGroupId = channelDetails.get("clientGroupId");
            Channel channel = ChannelService.getInstance(context).getChannelByClientGroupId(clientGroupId);
            channelKey = channel.getKey();
        }
        return channelKey;
    }

}