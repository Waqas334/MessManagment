package com.waqasyounis.phonenumberinput;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class PhoneNumberEditText extends RelativeLayout {

    private EditText mEtNumber, mEtNumberDummy, mEtCode, mEtCodeDummy;
    private TextView mTvDash;
    private OnDoneListener onDoneListener;

    //Constructors start

    public PhoneNumberEditText(Context context) {
        super(context);
        init(null);

    }

    public PhoneNumberEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);

    }

    public PhoneNumberEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }


    public PhoneNumberEditText(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs);
    }
    //Constructors end


    public interface OnDoneListener {
        //Interface to call when done key is pressed on keyboard
        void onDone(String number);
    }

    public enum FOCUS {
        NUMBER, CODE;
    }

    private void init(AttributeSet attributeSet) {
        //Init function to initialize all views
        inflate(getContext(), R.layout.input_mobile_number_2, this);

        mEtNumber = findViewById(R.id.et_number);
        mEtCode = findViewById(R.id.et_code);
        mTvDash = findViewById(R.id.tv_dash);

        mEtNumberDummy = findViewById(R.id.et_number_dummy);
        mEtCodeDummy = findViewById(R.id.et_code_dummy);

        mEtNumber.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mEtNumber.requestFocus();
                Toast.makeText(getContext(), "Et number clicked", Toast.LENGTH_SHORT).show();
            }
        });

        if (attributeSet != null) {
            TypedArray a = getContext().obtainStyledAttributes(attributeSet, R.styleable.PhoneNumberEditText);

            int textSize = a.getDimensionPixelSize(R.styleable.PhoneNumberEditText_textSize, (int) getResources().getDimension(R.dimen.defaultTextSize));
//            setTextSize(textSize);

            int color = a.getColor(R.styleable.PhoneNumberEditText_textColor, getResources().getColor(R.color.defaultTextColor));
            setTextColor(color);


        }

        mEtCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                if (s.length() == 4) {
                    //Code limit reached, set focus to number text field
                    mEtNumber.requestFocus();
                }

            }
        });

        mEtNumber.setOnKeyListener(new OnKeyListener() {
            //If user press the backspace key on Number EditText and there is not more number in that
            //EditText then it will take it back to Code EditText
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_DEL && mEtNumber.getText().length() == 0) {
                    mEtCode.requestFocus();
                    return true;
                }
                return false;
            }
        });


        mEtCode.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                return true;
            }
        });

        mEtNumber.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            //Listen to done key from keyboard
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                    //Done key pressed
                    if (mEtNumber.getText().length() != 7||mEtCode.getText().length() != 4) {
                        //Number is not correct

                        Toast.makeText(getContext(), "Please enter valid number", Toast.LENGTH_SHORT).show();
                    } else {
                        //Everything is correct, converting the local number to international standard
                        onDoneListener.onDone(mEtCode.getText().toString().replace("0", "+92") + mEtNumber.getText().toString());
                        //Hide Keyboard
//                        hideKeyboard();


                    }


                    return true;
                }
                return false;
            }
        });


    }


    public void requestFocus(FOCUS focus) {
        if (focus == FOCUS.CODE) {
            mEtCode.requestFocus();
        } else if (focus == FOCUS.NUMBER) {
            mEtNumber.requestFocus();
        }
    }

    public void setOnDoneListener(OnDoneListener onDoneListener) {
        this.onDoneListener = onDoneListener;
    }

    private void setTextSize(int textSize) {
        mEtNumber.setTextSize(textSize);
        mTvDash.setTextSize(textSize);
        mEtCode.setTextSize(textSize);
        mEtCodeDummy.setTextSize(textSize);
        mEtNumberDummy.setTextSize(textSize);


    }

    public String getJustPhoneNumber() throws InvalidPhoneNumberException {
        String code = mEtCode.getText().toString();
        String number = mEtNumber.getText().toString();
        checkNumberAndCode(code, number);
        return code + number;

    }

    public String getPhoneNumberWithCountryCode() throws InvalidPhoneNumberException {
        String code = mEtCode.getText().toString();
        String number = mEtNumber.getText().toString();
        checkNumberAndCode(code, number);
        code.replace("0", "");
        return "+92" + code + number;

    }

    public void setTextColor(int color) {
        mEtCode.setTextColor(color);
        mEtNumber.setTextColor(color);
        mTvDash.setTextColor(color);
    }

    private void checkNumberAndCode(String code, String number) throws InvalidPhoneNumberException {
        if (code.isEmpty()) {
            throw new InvalidPhoneNumberException("Code is empty");
        }

        if (code.length() < 4) {
            throw new InvalidPhoneNumberException("Code is less than 4 digits");
        }

        if (number.isEmpty()) {
            throw new InvalidPhoneNumberException("Number is empty");
        }

        if (number.length() < 7) {
            throw new InvalidPhoneNumberException("Number is less than 7 digits");

        }
    }


    @Override
    public void setEnabled(boolean enabled) {
        mEtCode.setEnabled(enabled);
        mEtNumber.setEnabled(enabled);
        super.setEnabled(enabled);
    }
}
