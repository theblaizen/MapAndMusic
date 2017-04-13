package owdienko.jaroslaw.testapplication2.Data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Jaroslaw Owdienko on 4/12/2017. All rights reserved TestApplication2!
 */

public class ArrayData {

    private static ArrayData data;
    private static List<GMapMarkersObject> array = Collections.synchronizedList(new ArrayList<GMapMarkersObject>());

    public static ArrayData getInstance() {
        if (data == null)
            data = new ArrayData();
        return data;
    }

    private ArrayData() {
    }

    public GMapMarkersObject getItemByPosition(int position) {
        return array.get(position);
    }

    public int getArraySize() {
        return array.size();
    }

    public void addItemToArray(GMapMarkersObject task) {
        array.add(task);
    }

    public void updateItemInArray(int position, GMapMarkersObject newCollection) {
        array.set(position, newCollection);
    }

    public void removeItemFromArray(int position) {
        array.remove(position);
    }

    public void clearAllData() {
        array.clear();
    }

}
