package app.uicomponents;

import android.content.Context;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatTextView;

public class UnderlineTextView extends AppCompatTextView {

	public UnderlineTextView(Context context) {
		super(context);
		init(null);
	}

	public UnderlineTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(attrs);
	}

	public UnderlineTextView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init(attrs);
	}

	private void init(AttributeSet attrs) {
		setPaintFlags(getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
	}
}
