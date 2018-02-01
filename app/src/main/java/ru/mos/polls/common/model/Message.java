package ru.mos.polls.common.model;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;

import org.json.JSONObject;

import me.ilich.juggler.change.Add;
import me.ilich.juggler.change.Remove;
import me.ilich.juggler.gui.JugglerActivity;
import ru.mos.elk.profile.AgUser;
import ru.mos.polls.MainActivity;
import ru.mos.polls.R;
import ru.mos.polls.base.ui.BaseActivity;
import ru.mos.polls.common.controller.UrlSchemeController;
import ru.mos.polls.social.model.AppPostValue;
import ru.mos.polls.wizardprofile.state.WizardProfileState;
import ru.mos.polls.wizardprofile.ui.fragment.WizardProfileFragment;

/**
 * Структура данных для хранения информации для кастомных сообщений после отправки опроса и выполенния чекина
 * Имеет возможность создания диалога для отображения кастомного сообщения пользователю, запуска экрана по url scheme
 *
 * @since 1.8
 */
public class Message {
    private String title, body, urlScheme;

    public Message(JSONObject jsonObject) {
        if (jsonObject != null) {
            title = jsonObject.optString("title");
            body = jsonObject.optString("body");
            JSONObject urlSchemeJson = jsonObject.optJSONObject("url_schemes");
            urlScheme = "";
            if (urlSchemeJson != null) {
                urlScheme = urlSchemeJson.optString("android");
            }
        }
    }

    public Message(String title, String body) {
        this.title = title;
        this.body = body;
    }

    public String getTitle() {
        return title;
    }

    public String getBody() {
        return body;
    }

    public String getUrlScheme() {
        return urlScheme;
    }

    public boolean isEmpty() {
        return isEmpty(body);
    }

    /**
     * Показываем кастомный диалог после чекина/отправки опроса
     * Подразумевает возможность запуска другиъ экранов приложения, использую функционал url scheme {@link UrlSchemeController}
     *
     * @param context              текущий контекст
     * @param questResultPostValue - данные для постинга результатов в соцсеть
     * @param postType             - тип совершаемого поста, требуется для метода flurry
     * @param id                   - идентификатор опроса или мероприятия,также исопльзуется для метода flurry
     */
    public void showCustomMessage(final JugglerActivity context,
                                  final AppPostValue questResultPostValue,
                                  final AppPostValue.Type postType,
                                  final long id,
                                  final Runnable onClose) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        if (!TextUtils.isEmpty(title) && !"null".equalsIgnoreCase(title)) {
            builder.setTitle(title);
        }
        builder.setMessage(body);
        builder.setPositiveButton(R.string.survey_done_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                /**
                 * Если urlScheme не пуст, то посылаем неявный интент
                 */
                if (!isEmpty(urlScheme)) {
                    /**
                     * если у url scheme требуется для открытия списков событий, опросов, новостей,
                     * то закрываем активти
                     */
                    Uri uri = Uri.parse(urlScheme);
                    String host = uri.getHost();
                    if (host.equalsIgnoreCase("task") && UrlSchemeController.PERSONAL_WIZARD.equalsIgnoreCase(uri.getQueryParameter("task_id"))) {
                        context.navigateTo().state(Add.newActivityForResult(new WizardProfileState(ru.mos.polls.newquests.controller.QuestStateController.getInstance().getIdsList(), AgUser.getPercentFillProfile(context)), BaseActivity.class, WizardProfileFragment.RESULT_CODE_START_PROFILE_FOR_INFO_PAGE));
                        onClose.run();
                        return;
                    }
                    if (UrlSchemeController.EVENTS_HOST.equalsIgnoreCase(host)
                            || UrlSchemeController.POLLTASKS_HOST.equalsIgnoreCase(host)
                            || UrlSchemeController.NEWS_HOST.equalsIgnoreCase(host)
                            || UrlSchemeController.NOVELTIES_HOST.equalsIgnoreCase(host)
                            || UrlSchemeController.TASK_HOST.equalsIgnoreCase(host)) {
                        MainActivity.close();
                    }
                    /**
                     * запуск экрана
                     */
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(urlScheme));
                    context.startActivity(intent);
                    onClose.run();
                }
                if (postType == AppPostValue.Type.POLL) {
                    onClose.run();
                }
            }
        });

        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                onClose.run();
            }
        });
        builder.show();

    }

    /**
     * Отображение кастомного прстого диалога
     *
     * @param context текущий контекст
     */
    public void showSimpleDialog(final Context context) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        if (!TextUtils.isEmpty(title) && !"null".equalsIgnoreCase(title)) {
            builder.setTitle(title);
        }
        builder.setMessage(body);
        builder.setPositiveButton(R.string.survey_done_ok, null);
        builder.show();
    }

    public void showDialog(final Context context) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        if (!TextUtils.isEmpty(title) && !"null".equalsIgnoreCase(title)) {
            builder.setTitle(title);
        }
        builder.setMessage(body);
        builder.setPositiveButton(R.string.survey_done_ok, null);
        builder.show();
    }

    private boolean isEmpty(String target) {
        return TextUtils.isEmpty(target) || "null".equalsIgnoreCase(target);
    }
}
