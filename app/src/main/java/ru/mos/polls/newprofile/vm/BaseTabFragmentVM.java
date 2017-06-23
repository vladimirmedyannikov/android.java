package ru.mos.polls.newprofile.vm;

import android.content.Intent;
import android.databinding.ViewDataBinding;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

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
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;
import ru.mos.elk.profile.AgUser;
import ru.mos.polls.R;
import ru.mos.polls.badge.manager.BadgeManager;
import ru.mos.polls.badge.model.BadgesSource;
import ru.mos.polls.newprofile.base.vm.FragmentViewModel;
import ru.mos.polls.profile.model.Achievement;
import ru.mos.polls.util.FileUtils;
import ru.mos.polls.util.ImagePickerController;

/**
 * Created by Trunks on 19.06.2017.
 */

public abstract class BaseTabFragmentVM<F extends JugglerFragment, B extends ViewDataBinding> extends FragmentViewModel<F, B> {
    protected RecyclerView recyclerView;
    protected AgUser changed, saved;
    protected CircleImageView circleImageView;
    protected Observable<List<Achievement>> achievementList;

    public BaseTabFragmentVM(F fragment, B binding) {
        super(fragment, binding);
    }


    protected void initialize(B binding) {
        saved = new AgUser(getActivity());
        setRecyclerList();
    }

    protected void setRecyclerList() {
        recyclerView.setLayoutManager(new LinearLayoutManager(getFragment().getContext()));
//        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(false);
        Drawable dividerDrawable = ContextCompat.getDrawable(getFragment().getContext(), R.drawable.divider);
        DividerItemDecoration did = new DividerItemDecoration(getFragment().getContext(), DividerItemDecoration.VERTICAL);
        did.setDrawable(dividerDrawable);
        recyclerView.addItemDecoration(did);
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
        circleImageView.setImageBitmap(BadgesSource.getInstance().getAvatar());
    }

    void updateAvatar(Uri uri) {
        Observable<Uri> observable = Observable.create((ObservableOnSubscribe<Uri>) e -> {
            sendAndUpdateNavigatorIcon(uri);
            e.onNext(uri);
            e.onComplete();
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
        disposables.add(observable.subscribe(this::setAvatar, throwable -> System.out.println(throwable.getMessage())));
    }


    private void sendAndUpdateNavigatorIcon(@NonNull Uri uri) {
        Bitmap bitmap = FileUtils.getBitmap(getFragment().getContext(), uri);
        File file = FileUtils.getFileFromUri(getFragment().getContext(), uri, "aguser_avatar.jpg");
        BadgesSource.getInstance().setAvatar(file.getAbsolutePath(), bitmap);
        LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(new Intent(BadgeManager.ACTION_RELOAD_AVATAR_FROM_CACHE));
        sendAvatar(file);
    }

    protected void sendAvatar(File file) {
        System.out.println("File send to server = " + file.getName());
        System.out.println("File send to server = " + file.getAbsolutePath());
    }
}
