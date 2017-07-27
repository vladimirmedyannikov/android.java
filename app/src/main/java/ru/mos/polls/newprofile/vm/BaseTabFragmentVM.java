package ru.mos.polls.newprofile.vm;

import android.content.Intent;
import android.databinding.ViewDataBinding;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;

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
import ru.mos.polls.newprofile.base.vm.FragmentViewModel;
import ru.mos.polls.newprofile.model.Achievement;
import ru.mos.polls.newprofile.service.Media;
import ru.mos.polls.newprofile.service.UploadMedia;
import ru.mos.polls.util.FileUtils;
import ru.mos.polls.util.ImagePickerController;

/**
 * Created by Trunks on 19.06.2017.
 */

public abstract class BaseTabFragmentVM<F extends JugglerFragment, B extends ViewDataBinding> extends FragmentViewModel<F, B> {
    protected RecyclerView recyclerView;
    protected AgUser changed, saved;
    protected CircleImageView circleImageView;
    protected Observable<Achievement> achievementList;

    public BaseTabFragmentVM(F fragment, B binding) {
        super(fragment, binding);
    }


    protected void initialize(B binding) {
        if (recyclerView != null)
            setRecyclerList(recyclerView);
    }

    @Override
    public void onResume() {
        super.onResume();
        saved = new AgUser(getActivity());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ImagePickerController.beginCrop(getFragment(), requestCode, resultCode, data);
    }

    @AfterPermissionGranted(ImagePickerController.MEDIA_PERMISSION_REQUEST_CODE)
    protected void showChooseMediaDialog() {
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
            sendAndUpdateNavigatorIcon(uri);
            e.onNext(uri);
            e.onComplete();
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
        disposables.add(observable.subscribe(this::setAvatar));
    }


    private void sendAndUpdateNavigatorIcon(@NonNull Uri uri) {
        Bitmap bitmap = FileUtils.getBitmap(getFragment().getContext(), uri);
        File file = FileUtils.getFileFromUri(getFragment().getContext(), uri, "aguser_avatar.jpg");
        BadgesSource.getInstance().setAvatar(file.getAbsolutePath(), bitmap);
        LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(new Intent(BadgeManager.ACTION_RELOAD_AVATAR_FROM_CACHE));
        sendAvatar(file);
    }

    protected void sendAvatar(File file) {
        Observable<Media> media = Observable.just(file)
                .subscribeOn(Schedulers.io())
                .flatMap(f -> { //конвертируем в base64
                    Media m = new Media(FileUtils.getFileExtension(f.getName()), FileUtils.getStringFile(f));
                    return Observable.just(m);
                });
        disposables.add(media.subscribe(
                media1 -> {
                    Observable<UploadMedia.Response> responseObservable = AGApplication.api.uploadFile(new UploadMedia.Request().setBase64(media1.getBase64()).setExtension(media1.getExtension()));
                    disposables.add(responseObservable.observeOn(Schedulers.io()).subscribe(response -> {
                        System.out.println("response = " + response.getResult().getId());
                        System.out.println("response = " + response.getResult().getUrl());
                    }, throwable -> throwable.printStackTrace()));
                },
                throwable -> throwable.printStackTrace()
        ));

    }
}
