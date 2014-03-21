package crucian.gui.menu;

import mulavito.utils.ClassScanner;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author LiRuiNet
 *         14-2-28 下午3:22
 */
public class DynamicClassMenu extends JMenu {
    public DynamicClassMenu(String name, String packageName) {
        super(name);

        for (Class<? extends Action> actionClass : listClasses(packageName, Action.class)) {
            add(newInstance(actionClass));
        }
    }

    protected static <T> T newInstance(Class<? extends T> type) {
        try {
            return type.newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        return null;
    }

    protected static <T> List<Class<? extends T>> listClasses(String packageName, Class<T> type) {
        try {
            List<Class<? extends T>> types = new ArrayList<Class<? extends T>>();
            for (Class<? extends T> clazz : ClassScanner.getDerivates(DynamicClassMenu.class, packageName, type)) {
                types.add(clazz);
            }
            return types;
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
            return Collections.emptyList();
        }
    }
}
