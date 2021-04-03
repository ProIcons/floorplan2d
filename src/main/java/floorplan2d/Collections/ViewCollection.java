package floorplan2d.Collections;

import floorplan2d.View;

import java.util.ArrayList;

/**
 *
 * @author Nikolas
 */
public class ViewCollection extends ArrayList<View> implements java.io.Serializable {

    private View activeView = null;

    public View getActiveView() {
        return activeView;
    }

    public boolean setActiveView(int index) {
        if (size() > index - 1) {
            activeView = get(index);
            return true;
        }
        return false;
    }

    public boolean setActiveView(View view) {
        if (contains(view)) {
            activeView = view;
            return true;
        }
        return false;
    }
}
