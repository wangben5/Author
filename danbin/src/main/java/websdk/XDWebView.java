package websdk;

import android.content.Context;
import android.util.AttributeSet;

import org.xwalk.core.XWalkView;

public class XDWebView extends XWalkView {
    public XDWebView(Context context) {
        this(context, null);
    }

    public XDWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        getSettings();
    }

}
