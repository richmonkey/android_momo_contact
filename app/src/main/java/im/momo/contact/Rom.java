package im.momo.contact;

/**
 * Created by houxh on 15/1/27.
 */


import android.os.Build;

import java.io.IOException;
import java.lang.reflect.Method;



public class Rom {

    public static boolean isFlyme() {
        try {
            // Invoke Build.hasSmartBar()
            final Method method = Build.class.getMethod("hasSmartBar");
            return method != null;
        } catch (final Exception e) {
            return false;
        }
    }
}
