package ru.mos.polls.support.vm;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.theartofdev.edmodo.cropper.CropImage;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import me.ilich.juggler.change.Remove;
import ru.mos.polls.AGApplication;
import ru.mos.polls.GoogleStatistics;
import ru.mos.polls.R;
import ru.mos.polls.base.component.ProgressableUIComponent;
import ru.mos.polls.base.component.UIComponentFragmentViewModel;
import ru.mos.polls.base.component.UIComponentHolder;
import ru.mos.polls.databinding.FragmentFeedbackBinding;
import ru.mos.polls.profile.model.AgUser;
import ru.mos.polls.profile.service.UploadMedia;
import ru.mos.polls.profile.service.model.Media;
import ru.mos.polls.rxhttp.rxapi.handle.response.HandlerApiResponseSubscriber;
import ru.mos.polls.rxhttp.session.Session;
import ru.mos.polls.support.model.Subject;
import ru.mos.polls.support.service.FeedbackSend;
import ru.mos.polls.support.service.SubjectsLoad;
import ru.mos.polls.support.ui.adapter.ImagesFilesAdapter;
import ru.mos.polls.support.ui.adapter.SubjectAdapter;
import ru.mos.polls.support.ui.fragment.FeedBackFragment;
import ru.mos.polls.util.FileUtils;
import ru.mos.polls.util.GuiUtils;
import ru.mos.polls.util.ImagePickerController;
import ru.mos.polls.util.NetworkUtils;
import ru.mos.polls.util.PermissionsUtils;

public class FeedBackFragmentVM extends UIComponentFragmentViewModel<FeedBackFragment, FragmentFeedbackBinding> implements ImageCrossListener {

    TextInputEditText emailET, subjectET, messageET, orderNumberET;
    TextInputLayout orderNumberLayout;
    RecyclerView recyclerView;
    private List<Subject> subjects;
    private ArrayAdapter subjectAdapter;
    Subject subject;
    List<Uri> uriList;
    ImagesFilesAdapter adapter;
    ArrayList<String> attachments;

    public FeedBackFragmentVM(FeedBackFragment fragment, FragmentFeedbackBinding binding) {
        super(fragment, binding);
    }

    @Override
    protected UIComponentHolder createComponentHolder() {
        return new UIComponentHolder.Builder()
                .with(new ProgressableUIComponent())
                .build();
    }

    @Override
    protected void initialize(FragmentFeedbackBinding binding) {
        emailET = binding.emailFeedback;
        subjectET = binding.subjectFeedback;
        messageET = binding.messageFeedback;
        orderNumberET = binding.orderNumberFeedback;
        orderNumberLayout = binding.orderNumberFeedbackWraper;
        recyclerView = binding.list;
        subjects = Subject.getDefault();
        subject = Subject.SUBJECT_NOT_SELECTED;
        subjectAdapter = new SubjectAdapter(getActivity(), subjects);
        uriList = new ArrayList<>();
        adapter = new ImagesFilesAdapter(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setAdapter(adapter);
        attachments = new ArrayList<>();
    }

    @Override
    public void onViewCreated() {
        super.onViewCreated();
        setEmailView();
        setSubjectListener();
        loadSubjects();
    }

    public void setSubjectListener() {
        subjectET.setOnClickListener(view -> showSubjectSelectDialog());
    }

    public void showSubjectSelectDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Выберите тему");
        builder.setAdapter(subjectAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                subject = subjects.get(i);
                if (subject != null) {
                    if (subject.getTitle().equals(Subject.WORD_SHOP_BONUS)) {
                        orderNumberLayout.setVisibility(View.VISIBLE);
                    } else if (orderNumberLayout.getVisibility() == View.VISIBLE) {
                        orderNumberLayout.setVisibility(View.GONE);
                        orderNumberET.setText("");
                    }
                    subjectET.setText(subject.getTitle());
                }
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void setEmailView() {
        String email = getActivity().getSharedPreferences(AgUser.PREFS, Activity.MODE_PRIVATE).getString(AgUser.EMAIL, "");
        if (email.length() > 0) {
            emailET.setText(email);
            messageET.requestFocus();
        }
    }

    private void loadSubjects() {
        HandlerApiResponseSubscriber<SubjectsLoad.Response.Result> handler
                = new HandlerApiResponseSubscriber<SubjectsLoad.Response.Result>(getFragment().getContext(), getProgressable()) {
            @Override
            protected void onResult(SubjectsLoad.Response.Result result) {
                subjects.clear();
                subjects.addAll(result.getSubjects());
                subjectAdapter.notifyDataSetChanged();
            }
        };
        if (NetworkUtils.hasInternetConnection(getActivity())) {
            Observable<SubjectsLoad.Response> responseObservabl = AGApplication
                    .api
                    .getFeedbackSubjects(new SubjectsLoad.Request())
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread());
            disposables.add(responseObservabl.subscribeWith(handler));
        } else {
            Toast.makeText(getActivity(), getActivity().getString(R.string.internet_failed_to_connect), Toast.LENGTH_SHORT).show();
            getProgressable().end();
        }
    }

    public void sendMessage() {
        System.out.println("sendMessage ");
        for (String attachment : attachments) {
            System.out.println("sendMessage attachment " + attachment);
        }
        HandlerApiResponseSubscriber<String> handler
                = new HandlerApiResponseSubscriber<String>(getFragment().getContext(), getProgressable()) {

            @Override
            protected void onResult(String response) {
                GoogleStatistics.Feedback.feedbackSanded(subject.getTitle());
                /**
                 * Очищаем поля при успещной отправке сообщения
                 */
                messageET.setText("");
                orderNumberET.setText("");
                subjectET.setText("");
                subject = Subject.SUBJECT_NOT_SELECTED;
                attachments.clear();
                adapter.clear();
                checkFilesCount();
                GuiUtils.displayOkMessage(getActivity(), R.string.succeeded_support, (dialog, which) -> {
                    if (getFragment().isStartWithNewActivity()) {
                        getFragment().navigateTo().state(Remove.closeCurrentActivity());
                    } else {
                        getFragment().navigateTo().state(Remove.last());
                    }
                });
            }

            @Override
            public void onError(Throwable throwable) {
                super.onError(throwable);
                sendError(throwable.getMessage());
            }

            @Override
            public void onErrorListener(int code, String message) {
                sendError(message);
            }

            private void sendError(String message) {
                GoogleStatistics.Feedback.errorOccurs(subject.getTitle(), message);
                GuiUtils.showKeyboard(emailET);
            }
        };

        Observable<FeedbackSend.Response> responseObservabl = AGApplication
                .api
                .sendFeedback(new FeedbackSend.Request(subject.getId(),
                        emailET.getText().toString(),
                        messageET.getText().toString(),
                        orderNumberET.getText().toString(),
                        Session.get().getSession(),
                        attachments))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());
        disposables.add(responseObservabl.subscribeWith(handler));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ImagePickerController.beginCrop(getFragment(), requestCode, resultCode, data, false);
        getCropedUri(requestCode, resultCode, data);
    }

    public void showChooseMediaDialog() {
        if (PermissionsUtils.CAMERA_MEDIA.isGranted(getFragment().getContext())) {
            ImagePickerController.showDialog(getFragment());
        } else {
            PermissionsUtils.CAMERA_MEDIA.request(getFragment(), ImagePickerController.MEDIA_PERMISSION_REQUEST_CODE);
        }
    }

    public void getCropedUri(int requestCode, int resultCode, Intent data) {
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == AppCompatActivity.RESULT_OK) {
            Uri uri = ImagePickerController.getCropedUri(data);
            uriList.add(uri);
            adapter.add(uri);
            checkFilesCount();
        }
    }

    public void checkFilesCount() {
        if (uriList.size() >= 4) {
            getFragment().hideMenuItem(R.id.action_attach_files);
        } else {
            getFragment().showMenuItem(R.id.action_attach_files);
        }
    }

    public void uploadImagefiles(List<Uri> uriList) {
        Flowable<Media> mediaList = Flowable.fromIterable(uriList)
                .subscribeOn(Schedulers.io())
                .map(uri -> {
                    File file = FileUtils.getFileFromUri(getFragment().getContext(), uri, FileUtils.getFileName(uri, getActivity()));
                    Media m = new Media(FileUtils.getFileExtension(file.getName()), FileUtils.getStringFile(file));
                    return m;
                })
                .observeOn(AndroidSchedulers.mainThread());
        disposables.add(mediaList.subscribe(
                media -> {
                    HandlerApiResponseSubscriber<UploadMedia.Response.Result> handler
                            = new HandlerApiResponseSubscriber<UploadMedia.Response.Result>(getActivity(), getProgressable()) {
                        @Override
                        protected void onResult(UploadMedia.Response.Result result) {
                            attachments.add(result.getId());
                        }

                        @Override
                        public void onComplete() {
                        }
                    };
                    Observable<UploadMedia.Response> responseObservable = AGApplication.api
                            .uploadFile(new UploadMedia.Request()
                                    .setContent(media.getBase64())
                                    .setExtension(media.getExtension()))
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread());
                    disposables.add(responseObservable.subscribeWith(handler));
                }
                , throwable -> throwable.printStackTrace(), this::sendMessage));
    }

    public void sendFeedBack() {
        GuiUtils.hideKeyboard(getFragment().getView());
        if (uriList.size() == 0) {
            sendMessage();
        } else {
            uploadImagefiles(uriList);
        }
    }

    @Override
    public void onOptionsItemSelected(int menuItemId) {
        switch (menuItemId) {
            case R.id.action_confirm:
                sendFeedBack();
                break;
            case R.id.action_attach_files:
                showChooseMediaDialog();
                break;
        }
    }

    @Override
    public void onCrossClicked(Uri uri) {
        uriList.remove(uri);
        adapter.removeItem(uri);
        checkFilesCount();
    }
}
