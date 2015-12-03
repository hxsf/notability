package me.hxsf.notability.draw;

import java.io.Serializable;

/**
 * Created by hxsf on 15－12－01.
 */
public class BaseLine implements Serializable {
    private static final long serialVersionUID = 1L;
    public float x1, y1, x2, y2;
    public int isstart = 0;

    public BaseLine(int isstart, float x1, float y1, float x2, float y2) {
        this.isstart = isstart;
        this.x1 = x1;
        this.x2 = x2;
        this.y1 = y1;
        this.y2 = y2;
    }

    /**
     * Returns a string containing a concise, human-readable description of this
     * object. Subclasses are encouraged to override this method and provide an
     * implementation that takes into account the object's type and data. The
     * default implementation is equivalent to the following expression:
     * <pre>
     *   getClass().getName() + '@' + Integer.toHexString(hashCode())</pre>
     * <p>See <a href="{@docRoot}reference/java/lang/Object.html#writing_toString">Writing a useful
     * {@code toString} method</a>
     * if you intend implementing your own {@code toString} method.
     *
     * @return a printable representation of this object.
     */
    @Override
    public String toString() {
        return "{" + isstart + ",\tx1='" + x1 + ",\ty1='" + y1 + ",\tx2='" + x2 + ",\ty2='" + y2 + "}";
    }
}