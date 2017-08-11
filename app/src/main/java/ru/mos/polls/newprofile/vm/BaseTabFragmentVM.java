package ru.mos.polls.newprofile.vm;

import android.content.Intent;
import android.databinding.ViewDataBinding;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.theartofdev.edmodo.cropper.CropImage;


import java.io.File;

import de.hdodenhof.circleimageview.CircleImageView;
import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.schedulers.Schedulers;
import me.ilich.juggler.gui.JugglerFragment;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;
import ru.mos.elk.profile.AgUser;
import ru.mos.polls.AGApplication;
import ru.mos.polls.R;
import ru.mos.polls.badge.manager.BadgeManager;
import ru.mos.polls.badge.model.BadgesSource;
import ru.mos.polls.base.component.UIComponentFragmentViewModel;
import ru.mos.polls.newprofile.base.rxjava.Events;
import ru.mos.polls.newprofile.base.ui.rvdecoration.UIhelper;
import ru.mos.polls.newprofile.base.vm.FragmentViewModel;
import ru.mos.polls.newprofile.service.AvatarSet;
import ru.mos.polls.newprofile.service.EmptyResponse;
import ru.mos.polls.newprofile.service.model.EmptyResult;
import ru.mos.polls.newprofile.service.model.Media;
import ru.mos.polls.newprofile.service.UploadMedia;
import ru.mos.polls.rxhttp.rxapi.handle.response.HandlerApiResponseSubscriber;
import ru.mos.polls.util.FileUtils;
import ru.mos.polls.util.ImagePickerController;

/**
 * Created by Trunks on 19.06.2017.
 */

public abstract class BaseTabFragmentVM<F extends JugglerFragment, B extends ViewDataBinding> extends FragmentViewModel<F, B> {
    protected RecyclerView recyclerView;
    protected AgUser saved;
    protected CircleImageView circleImageView;
    public boolean isAvatarLoaded;

    public BaseTabFragmentVM(F fragment, B binding) {
        super(fragment, binding);
    }


    protected void initialize(B binding) {
        if (recyclerView != null)
            UIhelper.setRecyclerList(recyclerView,getActivity());
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

    @AfterPermissionGranted(ImagePickerController.MEDIA_PERMISSION_REQUEST_CODE)
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

    void setAvatar() {
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
        HandlerApiResponseSubscriber<EmptyResult[]> handler
                = new HandlerApiResponseSubscriber<EmptyResult[]>(getActivity(), progressable) {
            @Override
            protected void onResult(EmptyResult[] result) {
                Toast.makeText(getActivity(), "Аватарка загружена", Toast.LENGTH_SHORT).show();
                isAvatarLoaded = true;
                AGApplication.bus().send(new Events.WizardEvents(Events.WizardEvents.WIZARD_AVATAR, 3));
            }
        };
        Observable<EmptyResponse> responseObservable = AGApplication.api
                .setAvatar(new AvatarSet.Request(id))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
        disposables.add(responseObservable.subscribeWith(handler));
    }
}
