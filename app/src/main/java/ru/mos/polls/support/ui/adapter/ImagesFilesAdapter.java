package ru.mos.polls.support.ui.adapter;

import android.net.Uri;

import java.util.ArrayList;
import java.util.List;

import ru.mos.polls.base.BaseRecyclerAdapter;
import ru.mos.polls.base.RecyclerBaseViewModel;
import ru.mos.polls.support.vm.ImageCrossListener;
import ru.mos.polls.support.vm.ImageFilesVM;

public class ImagesFilesAdapter extends BaseRecyclerAdapter<ImageFilesVM> {
    ImageCrossListener listener;

    public ImagesFilesAdapter(ImageCrossListener listener) {
        this.listener = listener;
    }

    public void add(List<Uri> uriList) {
        List<ImageFilesVM> content = new ArrayList<>();
        for (Uri uri : uriList) {
            content.add(new ImageFilesVM(uri, listener));
        }
        addData(content);
    }

    public void add(Uri uri) {
        add(new ImageFilesVM(uri, listener));
    }

    public void removeItem(Uri uri) {
        for (RecyclerBaseViewModel recyclerBaseViewModel : list) {
            if (uri.equals(recyclerBaseViewModel.getModel())) {
                list.remove(recyclerBaseViewModel);
                break;
            }
        }
        notifyDataSetChanged();
    }
}
