package jeti.io.typefaces;

import android.content.Context;
import android.content.res.Resources.NotFoundException;
import android.graphics.Typeface;
import android.util.Log;
import android.util.SparseArray;
import android.widget.TextView;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

/**
 * This is a wrapper class that helps to efficiently handle different fonts in
 * Android applications. To use a non-default font, you should place the .ttf file
 * in your project's res/raw folder, and programmatically retrieve
 * the {@link Typeface} with a call to {@link #get(Context, int)}. For example,
 * if I place <code>aliens.ttf</code> in the <code>res/raw</code>, then I can retrieve that
 * {@link Typeface} and use it in a {@link TextView} with either:
 * <pre>
 * <code>
 * Typeface typeface = Typefaces.get(context, R.raw.aliens);
 * textView.setTypeface( typeface );
 * </code>
 * </pre>
 * or
 * <pre>
 * <code>
 * Typefaces.set(textView, R.raw.aliens);
 * </code>
 * </pre>
 */
public class Typefaces {

    /**
     * The Collection of loaded fonts. We keep a static reference because
     * loading fonts is expensive and we don't want to be doing that a lot.
     */
    private static final SparseArray<Typeface> map = new SparseArray<Typeface>();

    /**
     * Load one of the predefined {@link Typeface}s that is saved as one of your
     * project's resources. For example, if I place <code>aliens.ttf</code> in
     * the <code>res/raw</code> folder,
     * then I can retrieve that {@link Typeface} and use it in a
     * {@link TextView} with the call:
     * <pre>
     * <code>
     * Typeface typeface = Typefaces.get(context, R.raw.aliens);
     * textView.setTypeface( typeface );
     * </code>
     * </pre>
     * Note, however, that it is more concise to use the {@link #set(TextView, int)} method:
     * <pre>
     * <code>
     * Typefaces.set(textView, R.raw.aliens);
     * </code>
     * </pre>
     */
    synchronized public static Typeface get(Context context, int typefaceID) {

        /*
         * Check to see if we loaded the typeface already, and return
         * immediately if we did.
         */
        Typeface tf = map.get(typefaceID);
        if (tf != null) {
            return tf;
        }
        if (typefaceID != 0) {
            tf = typefaceFromRes(context, typefaceID);
            map.put(typefaceID, tf);
            return tf;
        } else {
            Log.d(Typefaces.class.getSimpleName(), "Invalid font ID.");
            return Typeface.defaultFromStyle(Typeface.NORMAL);
        }
    }

    /**
     * Set the typeface of the specified {@link TextView} using the resource ID (such as
     * R.raw.aliens if I have aliens.ttf in the res/raw folder). The {@link TextView}
     * is then returned to promote chained commands.
     */
    synchronized public static TextView set(TextView textView, int typefaceID) {
        Typeface typeface = Typefaces.get(textView.getContext(), typefaceID);
        textView.setTypeface(typeface);
        return textView;
    }

    /**
     * Get a {@link Typeface} from a resource file. This is primarily needed so
     * that we can save typefaces in the library project, and use them in other
     * projects. Typically you would save the ttf file in the assets folder of a
     * project, but that folder will not be copied to projects which use the
     * library. In this case, the only solution is to put it in the res folder.
     * Note that we keep this method private since it is an expensive operation.
     * Instead, we cache the results of previous calls in the Typefaces class so
     * that we don't have to repeatedly call this function.
     */
    private static Typeface typefaceFromRes(Context context, int resource) {
        Typeface tf = Typeface.defaultFromStyle(Typeface.NORMAL);
        InputStream is = null;
        try {
            is = context.getResources().openRawResource(resource);
        } catch (NotFoundException e) {
            return tf;
        }

        File base = context.getCacheDir();
        String outPath = base + "/tmp" + System.nanoTime() + ".ttf";
        try {
            byte[] buffer = new byte[is.available()];
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(outPath));

            int l = 0;
            while ((l = is.read(buffer)) > 0) {
                bos.write(buffer, 0, l);
            }
            bos.close();
            tf = Typeface.createFromFile(outPath);
            new File(outPath).delete();
        } catch (Exception e) {
            Log.e("Typefaces", "Could not use custom font. Reverting to default.");
        }
        return tf;
    }

}
