package x.homework.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.List;

/**
 * @author michael sanchez
 */
public class Item implements ParentItemPostProcessor.PostProcessable {

    @SerializedName("children")
    @Expose
    private List<Item> children;
    @SerializedName("element")
    @Expose
    private String element;
    @SerializedName("image")
    @Expose
    private String imageUrl;
    @SerializedName("key")
    @Expose
    private String key;
    private Item parent;
    @SerializedName("subtext")
    @Expose
    private String subText;
    @SerializedName("text")
    @Expose
    private String text;
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("URL")
    @Expose
    private String url;

    public List<Item> getChildren() {
        return children;
    }

    public String getElement() {
        return element;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getKey() {
        return key;
    }

    public Item getParent() {
        return parent;
    }

    public String getSubText() {
        return subText;
    }

    public String getText() {
        return text;
    }

    public String getType() {
        return type;
    }

    public String getUrl() {
        return url;
    }

    public boolean isChild() {
        return children == null && parent != null;
    }

    // display as section header only
    public boolean isParent() {
        return children != null;
    }

    @Override
    public void postProcess() {
        if (children != null) {
            for (Item child : children) {
                child.parent = this;
            }
        }
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}