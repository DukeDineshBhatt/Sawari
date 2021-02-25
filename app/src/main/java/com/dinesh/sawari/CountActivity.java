package com.dinesh.sawari;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class CountActivity extends AppCompatActivity {
    ImageView close, plus_img, minus_img;
    TextView qnty_tv;
    int qnty = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_count);

        close = findViewById(R.id.close);
        plus_img = findViewById(R.id.plus_img);
        minus_img = findViewById(R.id.minus_img);
        qnty_tv = findViewById(R.id.qnty);

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                onBackPressed();

            }
        });


        qnty_tv.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {


            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if (qnty_tv.getText().toString().equals("8")) {

                    plus_img.setClickable(false);

                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

      /*  plus_img.setBackgroundResource(R.drawable.m_plus);
        plus_img.getLayoutParams().height = 170;
        plus_img.getLayoutParams().width = 170;
        plus_img.requestLayout();

        minus_img.setBackgroundResource(R.drawable.minus);
        minus_img.getLayoutParams().height = 170;
        minus_img.getLayoutParams().width = 170;
        minus_img.requestLayout();*/

        plus_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (qnty == 1) {

                    qnty++;
                    qnty_tv.setText(String.valueOf(qnty));
                    Log.d("DDD", "" + qnty);
                    return;

                }
                if (qnty > 1) {
                    qnty++;
                    Log.d("SSS", "" + qnty);

                    qnty_tv.setText(String.valueOf(qnty));

                    minus_img.setBackgroundResource(R.drawable.m_minus);
                    minus_img.getLayoutParams().height = 170;
                    minus_img.getLayoutParams().width = 170;
                    minus_img.requestLayout();
                    return;

                }

                if (qnty == 8) {

                    qnty_tv.setText(String.valueOf(qnty));

                    plus_img.setBackgroundResource(R.drawable.plus);
                    plus_img.getLayoutParams().height = 170;
                    plus_img.getLayoutParams().width = 170;
                    plus_img.requestLayout();


                }

            }
        });

        minus_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (qnty == 1) {

                    qnty_tv.setText(String.valueOf(qnty));

                    minus_img.setBackgroundResource(R.drawable.minus);
                    minus_img.getLayoutParams().height = 170;
                    minus_img.getLayoutParams().width = 170;
                    minus_img.requestLayout();

                }
                if (qnty > 1) {

                    qnty--;
                    qnty_tv.setText(String.valueOf(qnty));


                    minus_img.setBackgroundResource(R.drawable.m_minus);
                    minus_img.getLayoutParams().height = 170;
                    minus_img.getLayoutParams().width = 170;
                    minus_img.requestLayout();

                }

            }
        });


    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.nothing, R.anim.slide_out_up);
    }
}
