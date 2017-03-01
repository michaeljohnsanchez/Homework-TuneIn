package x.homework.model;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

/**
 * @author michael sanchez
 */
// using this class to create a parent/child association between items in order to facilitate grouping
// of child items for pages that have grouped items

// in real life I'd spend more time to avoid adding this complexity
public class ParentItemPostProcessor implements TypeAdapterFactory {

    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
        final TypeAdapter<T> delegate = gson.getDelegateAdapter(this, type);

        return new TypeAdapter<T>() {
            public void write(JsonWriter out, T value) throws IOException {
                delegate.write(out, value);
            }

            public T read(JsonReader in) throws IOException {
                T obj = delegate.read(in);
                if (obj instanceof PostProcessable) {
                    ((PostProcessable) obj).postProcess();
                }
                return obj;
            }
        };
    }

    interface PostProcessable {
        void postProcess();
    }
}