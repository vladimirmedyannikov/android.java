package ru.mos.polls.newprofile.vm;

import android.content.Intent;
import android.databinding.ViewDataBinding;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.android.volley2.VolleyError;
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
import ru.mos.elk.BaseActivity;
import ru.mos.elk.profile.AgUser;
import ru.mos.elk.profile.ProfileManager;
import ru.mos.polls.AGApplication;
import ru.mos.polls.R;
import ru.mos.polls.badge.manager.BadgeManager;
import ru.mos.polls.badge.model.BadgesSource;
import ru.mos.polls.base.component.UIComponentFragmentViewModel;
import ru.mos.polls.base.rxjava.Events;
import ru.mos.polls.base.ui.rvdecoration.UIhelper;
import ru.mos.polls.newprofile.service.AvatarSet;
import ru.mos.polls.newprofile.service.model.Media;
import ru.mos.polls.newprofile.service.UploadMedia;
import ru.mos.polls.rxhttp.rxapi.handle.response.HandlerApiResponseSubscriber;
import ru.mos.polls.util.FileUtils;
import ru.mos.polls.util.ImagePickerController;

/**
 * Created by Trunks on 19.06.2017.
 */

public abstract class BaseProfileTabFragmentVM<F extends JugglerFragment, B extends ViewDataBinding> extends UIComponentFragmentViewModel<F, B> implements EasyPermissions.PermissionCallbacks {
    protected RecyclerView recyclerView;
    protected AgUser saved;
    protected CircleImageView circleImageView;
    public boolean isAvatarLoaded;

    public BaseProfileTabFragmentVM(F fragment, B binding) {
        super(fragment, binding);
    }


    protected void initialize(B binding) {
        if (recyclerView != null)
            UIhelper.setRecyclerList(recyclerView, getActivity());
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
        if (BadgesSource.getInstance().getAvatar() != null)
            circleImageView.setImageBitmap(BadgesSource.getInstance().getAvatar());
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
        HandlerApiResponseSubscriber<AvatarSet.Response.Result> handler
                = new HandlerApiResponseSubscriber<AvatarSet.Response.Result>(getActivity(), progressable) {
            @Override
            protected void onResult(AvatarSet.Response.Result result) {
                Toast.makeText(getActivity(), "Аватарка загружена", Toast.LENGTH_SHORT).show();
                isAvatarLoaded = true;
                AGApplication.bus().send(new Events.WizardEvents(Events.WizardEvents.WIZARD_AVATAR, result.getPercentFillProfile()));
            }
        };
        Observable<AvatarSet.Response> responseObservable = AGApplication.api
                .setAvatar(new AvatarSet.Request(id))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
        disposables.add(responseObservable.subscribeWith(handler));
    }

    public void refreshProfile() {
        ProfileManager.AgUserListener agUserListener = new ProfileManager.AgUserListener() {
            @Override
            public void onLoaded(AgUser loadedAgUser) {
                try {
                    saved = loadedAgUser;
                    progressable.end();
                    updateView();
                } catch (Exception ignored) {
                }
            }

            @Override
            public void onError(VolleyError error) {
                try {
                    Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();
                } catch (Exception ignored) {
                }
            }
        };
        ProfileManager.getProfile((BaseActivity) getActivity(), agUserListener);
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
}
