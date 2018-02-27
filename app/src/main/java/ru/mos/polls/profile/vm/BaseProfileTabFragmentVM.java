package ru.mos.polls.profile.vm;

import android.content.Intent;
import android.databinding.ViewDataBinding;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.theartofdev.edmodo.cropper.CropImage;

import java.io.File;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.schedulers.Schedulers;
import me.ilich.juggler.gui.JugglerFragment;
import pub.devrel.easypermissions.EasyPermissions;
import ru.mos.polls.profile.ProfileManagerRX;
import ru.mos.polls.profile.model.AgUser;
import ru.mos.polls.AGApplication;
import ru.mos.polls.R;
import ru.mos.polls.badge.manager.BadgeManager;
import ru.mos.polls.badge.model.BadgesSource;
import ru.mos.polls.base.component.UIComponentFragmentViewModel;
import ru.mos.polls.base.rxjava.Events;
import ru.mos.polls.base.ui.rvdecoration.UIhelper;
import ru.mos.polls.friend.ui.utils.FriendGuiUtils;
import ru.mos.polls.profile.service.AvatarSet;
import ru.mos.polls.profile.service.UploadMedia;
import ru.mos.polls.profile.service.model.Media;
import ru.mos.polls.rxhttp.rxapi.config.AgApiBuilder;
import ru.mos.polls.rxhttp.rxapi.handle.response.HandlerApiResponseSubscriber;
import ru.mos.polls.rxhttp.rxapi.progreessable.Progressable;
import ru.mos.polls.util.FileUtils;
import ru.mos.polls.util.ImagePickerController;
import ru.mos.polls.util.NetworkUtils;

/**
 * Created by Trunks on 19.06.2017.
 */

public abstract class BaseProfileTabFragmentVM<F extends JugglerFragment, B extends ViewDataBinding> extends UIComponentFragmentViewModel<F, B> implements EasyPermissions.PermissionCallbacks {
    protected RecyclerView recyclerView;
    protected AgUser saved;
    protected CircleImageView circleImageView;
    protected ProgressBar avatarProgress;
    protected FrameLayout avatarContainer;
    private Handler handler;
    private Progressable avatarProgressable = new Progressable() {
        @Override
        public void begin() {
            progressAvatar(true);
        }

        @Override
        public void end() {
            progressAvatar(false);
        }
    };
    public boolean isAvatarLoaded;

    public BaseProfileTabFragmentVM(F fragment, B binding) {
        super(fragment, binding);
    }


    protected void initialize(B binding) {
        if (recyclerView != null)
            UIhelper.setRecyclerList(recyclerView, getActivity());
    }

    @Override
    public void onViewCreated() {
        super.onViewCreated();
        handler = new Handler();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ImagePickerController.beginCrop(getFragment(), requestCode, resultCode, data);
    }

    public void showChooseMediaDialog() {
        if (EasyPermissions.hasPermissions(getFragment().getContext(), ImagePickerController.MEDIA_PERMS)) {
            ImagePickerController.showDialog(getFragment());
        } else {
            EasyPermissions.requestPermissions(getFragment(),
                    getFragment().getResources().getString(R.string.permission_camera_gallery),
                    ImagePickerController.MEDIA_PERMISSION_REQUEST_CODE, ImagePickerController.MEDIA_PERMS);
        }
    }

    public void getCropedUri(int requestCode, int resultCode, Intent data) {
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == AppCompatActivity.RESULT_OK) {
            Uri uri = ImagePickerController.getCropedUri(data);
            setAvatar(uri);
            updateAvatar(uri);
        }
    }

    void setAvatar(Uri uri) {
        circleImageView.setImageURI(uri);
    }

    public void setAvatar() {
        if (NetworkUtils.hasInternetConnection(getActivity())) {
            FriendGuiUtils.loadAvatar(circleImageView, AgApiBuilder.resourceURL(saved.getAvatar()));
        } else if (BadgesSource.getInstance().getAvatar() != null) {
            setAvatarFromBadges();
        }
    }

    public void setAvatarFromBadges() {
        circleImageView.setImageBitmap(BadgesSource.getInstance().getAvatar());
        circleImageView.invalidate();
    }

    void updateAvatar(Uri uri) {
        Observable<Uri> observable = Observable.create((ObservableOnSubscribe<Uri>) e -> {
            sendAndUpdateNavigatorAvatar(uri);
            e.onNext(uri);
            e.onComplete();
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
        disposables.add(observable.subscribe(this::setAvatar));
    }


    private void sendAndUpdateNavigatorAvatar(@NonNull Uri uri) {
        Bitmap bitmap = FileUtils.getBitmap(getFragment().getContext(), uri);
        File file = FileUtils.getFileFromUri(getFragment().getContext(), uri, "aguser_avatar.jpg");
        BadgesSource.getInstance().setAvatar(file.getAbsolutePath(), bitmap);
        LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(new Intent(BadgeManager.ACTION_RELOAD_AVATAR_FROM_CACHE));
        uploadAvatarRequest(file);
    }

    public void uploadAvatarRequest(File file) {
        progressAvatar(true);
        Observable<Media> media = Observable.just(file)
                .subscribeOn(Schedulers.io())
                .flatMap(f -> { //конвертируем в base64
                    Media m = new Media(FileUtils.getFileExtension(f.getName()), FileUtils.getStringFile(f));
                    return Observable.just(m);
                }).observeOn(AndroidSchedulers.mainThread());

        disposables.add(media.subscribe(
                media1 -> {
                    HandlerApiResponseSubscriber<UploadMedia.Response.Result> handler
                            = new HandlerApiResponseSubscriber<UploadMedia.Response.Result>(getActivity(), progressable) {
                        @Override
                        protected void onResult(UploadMedia.Response.Result result) {
                            setAvatarRequest(result.getId());
                        }
                    };
                    Observable<UploadMedia.Response> responseObservable = AGApplication.api
                            .uploadFile(new UploadMedia.Request()
                                    .setContent(media1.getBase64())
                                    .setExtension(media1.getExtension()))
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread());
                    disposables.add(responseObservable.subscribeWith(handler));
                },
                throwable -> throwable.printStackTrace()
        ));
    }

    public void setAvatarRequest(String id) {
        /**
         * для избежания морганий при загрузке фотографии на сервер при использовании прогрессейбла на весь экран
         */
        progressable = avatarProgressable;
        HandlerApiResponseSubscriber<AvatarSet.Response.Result> handler
                = new HandlerApiResponseSubscriber<AvatarSet.Response.Result>(getActivity(), progressable) {
            @Override
            protected void onResult(AvatarSet.Response.Result result) {
                Toast.makeText(getActivity(), "Аватарка загружена", Toast.LENGTH_SHORT).show();
                isAvatarLoaded = true;
                saved.setPercentFillProfile(result.getPercentFillProfile());
                saved.save(getActivity());
                AGApplication.bus().send(new Events.WizardEvents(Events.WizardEvents.WIZARD_AVATAR, result.getPercentFillProfile()));
                setAvatarFromBadges();
                refreshProfile();
            }
        };
        Observable<AvatarSet.Response> responseObservable = AGApplication.api
                .setAvatar(new AvatarSet.Request(id))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
        disposables.add(responseObservable.subscribeWith(handler));
    }

    public void refreshProfile() {
        ProfileManagerRX.AgUserListener listener = new ProfileManagerRX.AgUserListener() {
            @Override
            public void onLoaded(AgUser agUser) {
                saved = agUser;
                progressable.end();
                updateView();
                AGApplication.bus().send(new Events.ProfileEvents(Events.ProfileEvents.PROFILE_LOADED));
            }

            @Override
            public void onError(String message, int code) {
                saved = new AgUser(getActivity());
                progressable.end();
                updateView();
                AGApplication.bus().send(new Events.ProfileEvents(Events.ProfileEvents.PROFILE_LOADED));
                try {
                    Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                } catch (Exception ignored) {
                }
            }
        };
        if (NetworkUtils.hasInternetConnection(getActivity())) {
            ProfileManagerRX.getProfile(disposables, getActivity(), listener);
        } else {
            progressable.end();
            Toast.makeText(getActivity(), getActivity().getString(R.string.internet_failed_to_connect), Toast.LENGTH_SHORT).show();
            AGApplication.bus().send(new Events.ProfileEvents(Events.ProfileEvents.PROFILE_LOADED));
            updateView();
        }
    }

    public void updateView() {
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        ImagePickerController.showDialog(getFragment());
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @android.support.annotation.NonNull String[] permissions, @android.support.annotation.NonNull int[] grantResults) {
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    private void progressAvatar(boolean progress) {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (avatarProgress != null && circleImageView != null) {
                    avatarProgress.setVisibility(progress ? View.VISIBLE : View.GONE);
                    circleImageView.setVisibility(progress ? View.GONE : View.VISIBLE);
                    circleImageView.clearAnimation();
                }
            }
        }, progress ? 0 : 500);
    }
}
