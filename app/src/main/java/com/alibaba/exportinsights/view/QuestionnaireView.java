package com.alibaba.exportinsights.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.alibaba.exportinsights.R;
import com.alibaba.exportinsights.bean.Option;
import com.alibaba.exportinsights.bean.Questionnaire;
import com.alibaba.exportinsights.databinding.LayoutQuestionnaireBinding;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

public class QuestionnaireView extends LinearLayout {

    private final Context mContext;
    private OptionCheckedListener mOptionCheckedListener;
    private LayoutQuestionnaireBinding mBinding;

    public QuestionnaireView(Context context) {
        this(context, null, 0);
    }

    public QuestionnaireView(Context context,
            @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public QuestionnaireView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        initView(context);
    }

    private void initView(Context context) {
        mBinding = LayoutQuestionnaireBinding.inflate(LayoutInflater.from(context), this, true);
    }

    public void initByData(Questionnaire questionnaire) {
        TextView question = mBinding.tvQuestion;
        question.setText(questionnaire.enName);
        RadioGroup options = mBinding.rgOptions;
        options.removeAllViews();
        for (int i = 0; i < questionnaire.values.size(); i++) {
            Option option = questionnaire.values.get(i);
            RadioButton atomicButton = new RadioButton(mContext);
            RadioGroup.LayoutParams lp = new RadioGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            lp.topMargin = (int) getContext().getResources().getDimension(R.dimen.dimen_standard_s2);
            atomicButton.setPadding((int) getContext().getResources().getDimension(R.dimen.dimen_standard_s5), 0, 0, 0);
            atomicButton.setButtonDrawable(null);
            Drawable drawable = ContextCompat.getDrawable(mContext, R.drawable.selector_bg_option_item);
            atomicButton.setBackground(drawable);

            atomicButton.setMinHeight((int) getContext().getResources().getDimension(R.dimen.questionnaire_option_min_height));
            atomicButton.setText(option.enValue);
            atomicButton.setTag(option.value);
            atomicButton.setTextSize(13);
            atomicButton.setTextColor(getResources().getColor(R.color.dark));
            options.addView(atomicButton, lp);
        }
        options.setOnCheckedChangeListener((radioGroup, checkedId) -> {
            RadioButton checkedButton = findViewById(checkedId);
            if (mOptionCheckedListener != null) {
                mOptionCheckedListener.onChecked(questionnaire.name,
                        checkedButton.getText().toString(),
                        String.valueOf(checkedButton.getTag()));
            }
        });
    }

    public void setOptionCheckedListener(
            OptionCheckedListener optionCheckedListener) {
        mOptionCheckedListener = optionCheckedListener;
    }

    public interface OptionCheckedListener {
        void onChecked(String title, String enValue, String zhValue);
    }
}
