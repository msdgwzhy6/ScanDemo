package man.scandemo;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;


/**
 * Created by kingman on 2017/7/25.
 */

public class TitleView extends RelativeLayout{
    Drawable backIconDrawable;
    int backIconWidth=LayoutParams.WRAP_CONTENT;
    int backIconHeight=LayoutParams.WRAP_CONTENT;
    String leftText="";
    String titleText="";
    int leftColor=Color.WHITE;
    int titleColor=Color.WHITE;
    Drawable rightIconDrawable;
    int rightIconWidth=LayoutParams.WRAP_CONTENT;
    int rightIconHeight=LayoutParams.WRAP_CONTENT;
    public TitleView(Context context) {
        super(context);
        init(context);
    }

    public TitleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        getAttrs(context,attrs);
        init(context);
    }

    public TitleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        getAttrs(context,attrs);
        init(context);
    }
    @SuppressWarnings("ResourceType")
    private void getAttrs(Context context, AttributeSet attrs) {
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.titleView);
        backIconDrawable=ta.getDrawable(R.styleable.titleView_paysdk_backIcon);
        backIconWidth=ta.getDimensionPixelSize(R.styleable.titleView_paysdk_backIconWidth,LayoutParams.WRAP_CONTENT);
        backIconHeight=ta.getDimensionPixelSize(R.styleable.titleView_paysdk_backIconHeight,LayoutParams.WRAP_CONTENT);

        leftText = ta.getString(R.styleable.titleView_paysdk_leftText);
        titleText = ta.getString(R.styleable.titleView_paysdk_titleText);
        leftColor=ta.getColor(R.styleable.titleView_paysdk_leftColor,Color.WHITE);
        titleColor=ta.getColor(R.styleable.titleView_paysdk_titleColor,Color.WHITE);
        rightIconDrawable=ta.getDrawable(R.styleable.titleView_paysdk_rightIcon);
        rightIconWidth=ta.getDimensionPixelSize(R.styleable.titleView_paysdk_rightIconWidth,LayoutParams.WRAP_CONTENT);
        rightIconHeight=ta.getDimensionPixelSize(R.styleable.titleView_paysdk_rightIconHeight,LayoutParams.WRAP_CONTENT);
        ta.recycle();
    }
    private void init(final Context context){
        LayoutInflater.from(context).inflate(R.layout.title_layout,this);


        ImageView backIcon=(ImageView) findViewById(R.id.backIcon);
        backIcon.getLayoutParams().width=backIconWidth;
        backIcon.getLayoutParams().height=backIconHeight;
        backIcon.setImageDrawable(backIconDrawable);

        TextView leftTextView=(TextView)findViewById(R.id.leftText);
        leftTextView.setText(leftText);
        leftTextView.setTextColor(leftColor);

        LinearLayout leftWrap=(LinearLayout)findViewById(R.id.leftWrap);
        leftWrap.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(context instanceof Activity){
                    ((Activity) context).finish();
                }
            }
        });

        TextView titleTextView=(TextView)findViewById(R.id.titleText);
        titleTextView.setText(titleText);
        titleTextView.setTextColor(titleColor);
        if(rightIconDrawable!=null){
            ImageView rightIcon=(ImageView)findViewById(R.id.rightIcon);
            rightIcon.getLayoutParams().width=rightIconWidth;
            rightIcon.getLayoutParams().height=rightIconHeight;
            rightIcon.setImageDrawable(rightIconDrawable);
        }
    }


}
