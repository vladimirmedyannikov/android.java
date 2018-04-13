package ru.mos.polls.support.vm;

import android.net.Uri;

import ru.mos.polls.BR;
import ru.mos.polls.R;
import ru.mos.polls.base.RecyclerBaseViewModel;
import ru.mos.polls.databinding.ItemFeedbackImagefilesBinding;
import ru.mos.polls.util.FileUtils;

public class ImageFilesVM extends RecyclerBaseViewModel<Uri, ItemFeedbackImagefilesBinding> {

    ImageCrossListener listener;

    public ImageFilesVM(Uri model, ImageCrossListener listener) {
        super(model);
        this.listener = listener;
    }

    @Override
    public int getViewType() {
        return 0;
    }

    @Override
    public int getLayoutId() {
        return R.layout.item_feedback_imagefiles;
    }

    @Override
    public int getVariableId() {
        return BR.viewModel;
    }

    @Override
    public void onBind(ItemFeedbackImagefilesBinding viewDataBinding) {
        super.onBind(viewDataBinding);
        viewDataBinding.filesName.setText(FileUtils.getFileName(model, viewDataBinding.filesName.getContext()));
        viewDataBinding.filesImage.setImageURI(model);
        viewDataBinding.filesDelete.setOnClickListener(view -> listener.onCrossClicked(model));
    }
}
