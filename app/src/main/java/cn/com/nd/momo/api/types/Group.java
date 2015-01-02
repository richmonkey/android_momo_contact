
package cn.com.nd.momo.api.types;

import java.util.ArrayList;
import java.util.Collection;

public class Group<T extends MomoType> extends ArrayList<T> implements MomoType {
    private static final long serialVersionUID = 6628875144902990459L;

    public Group() {
        super();
    }

    public Group(Collection<T> collection) {
        super(collection);
    }
}
