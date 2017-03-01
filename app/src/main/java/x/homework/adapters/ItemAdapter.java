package x.homework.adapters;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;

import x.homework.R;
import x.homework.model.Item;
import x.homework.presenters.Presenter;

/**
 * @author michael sanchez
 */
public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ViewHolder> {
    // in a live impl this adapter might have an abstract generify-ed parent to provide potential reuse 
    // for methods like setItems(), parseItems() addItems() etc.

    private final static int CATEGORY = 0;
    private final static int DETAIL = 1;
    private final static int SECTION_HEADER = 2;
    private final MediaPlayer mediaPlayer = new MediaPlayer();
    private final Presenter presenter;
    private List<Item> items;

    public ItemAdapter(Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    @Nullable
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Integer vhLayout = null;
        switch (viewType) {
            case SECTION_HEADER: {
                vhLayout = R.layout.sectionheader_viewholder;
                break;
            }
            case CATEGORY: {
                vhLayout = R.layout.browse_viewholder;
                break;
            }
            case DETAIL: {
                vhLayout = R.layout.detail_viewholder;
            }
        }
        if (vhLayout == null) return null;
        View view = LayoutInflater.from(parent.getContext()).inflate(vhLayout, parent, false);
        ViewHolder vh = new ViewHolder(view);
        view.setOnClickListener(vh);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (holder == null) return;
        Item item = items.get(position);
        holder.bindData(item, getItemViewType(position));
    }

    @Override
    @ViewHolderType
    public int getItemViewType(int position) {
        Item item = items.get(position);
        if (item.isParent()) return SECTION_HEADER;
        if (item.isChild()) return DETAIL;
        return CATEGORY;
    }

    @Override
    public int getItemCount() {
        return items == null ? 0 : items.size();
    }

    private void parseItems(@Nullable List<Item> items) {
        List<Item> allItems = new ArrayList<>();
        if (items != null) {
            // flattens child items
            for (Item item : items) {
                allItems.add(item);
                List<Item> children = item.getChildren();
                if (children != null) {
                    allItems.addAll(children);
                }
            }
        }
        this.items = allItems;
    }

    public void setItems(@Nullable List<Item> items) {
        parseItems(items);
        notifyDataSetChanged();
    }

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({CATEGORY, DETAIL, SECTION_HEADER})
    @interface ViewHolderType {
    }

    class ViewHolder extends RecyclerView.ViewHolder implements RecyclerView.OnClickListener {

        private Item item;
        private String url;

        ViewHolder(View itemView) {
            super(itemView);
        }

        void bindData(Item item, int viewType) {
            this.item = item;
            switch (viewType) {
                case SECTION_HEADER: {
                    TextView sectionText = (TextView) itemView.findViewById(R.id.sectionheader_text);
                    sectionText.setText(item.getText());
                    break;
                }
                case DETAIL: {
                    TextView subText = (TextView) itemView.findViewById(R.id.item_subtext);
                    subText.setText(item.getSubText());
                    // fall through - detail is a superset of browse
                }
                case CATEGORY: {
                    TextView text = (TextView) itemView.findViewById(R.id.item_text);
                    text.setText(item.getText());
                    url = item.getUrl();
                    String imageUrl = item.getImageUrl();
                    if (StringUtils.isNotBlank(imageUrl)) {
                        ImageView icon = (ImageView) itemView.findViewById(R.id.item_icon);
                        Picasso.with(itemView.getContext()).load(imageUrl).into(icon);
                    }
                }
            }
        }

        // in person interview...turn this into a service so that media keeps playing if user
        // navigates away from view
        @Override
        public void onClick(View v) {
            // hard ref to the adapter should be fine as long as long as we reuse a single instance 
            // of the adapter
            // (passing the adapter into the viewholder to facilitate onclick actions feels gross...)
            String type = item.getType();
            if (StringUtils.isNotBlank(type) && "audio".equalsIgnoreCase(type)) {
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                try {
                    mediaPlayer.setDataSource("http://apnews.streamguys1.com/apnews");
                    mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                        public void onPrepared(MediaPlayer mp) {
                            mediaPlayer.start();
                        }
                    });
                    mediaPlayer.prepareAsync();
                    return;
                } catch (IOException e) {
                    e.printStackTrace();
                    return;
                }
            }

            if (StringUtils.isNotBlank(url)) {
                ItemAdapter.this.presenter.showPage(url);
            }
        }
    }
}