package ru.mos.polls.quests;

import ru.mos.polls.social.model.SocialPostValue;


public class QuestsListenerStub implements QuestsFragment.Listener {

    @Override
    public void onSurvey(long id) {
    }

    @Override
    public void onAllSurveys() {
    }

    @Override
    public void onUpdatePersonal() {
    }

    @Override
    public void onUpdateLocation() {
    }

    @Override
    public void onUpdateSocial() {
    }

    @Override
    public void onUpdateEmail() {
    }

    @Override
    public void onUpdateExtraInfo() {
    }

    @Override
    public void onUpdateFamilyInfo() {
    }

    @Override
    public void onBindToPgu() {
    }

    @Override
    public void onRateThisApplication(String appId) {
    }

    @Override
    public void onInviteFriends(boolean isTask) {
    }

    @Override
    public void onSocialPost(SocialPostValue socialPostValue) {
    }

    @Override
    public void onNews(String title, String linkUrl) {
    }

    @Override
    public void onEvent(long eventId) {

    }

    @Override
    public void onOther(String title, String linkUrl) {

    }

    @Override
    public void onResults(String title, String linkUrl) {

    }
}
