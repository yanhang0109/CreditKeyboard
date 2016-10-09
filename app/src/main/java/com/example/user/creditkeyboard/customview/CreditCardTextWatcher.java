package com.example.user.creditkeyboard.customview;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;

import java.util.Calendar;

/**
 * Created by user on 16/9/23.
 */

public class CreditCardTextWatcher implements TextWatcher {
    private static final String BACK_SLASH = "/";
    private static final String ZERO = "0";
    private static final String ONE = "1";
    private static final String TWO = "2";
    private static final int MAX_YEAR = 15;

    private String beforeTC;
    private String currentInputDesc;
    private String currentY;
    private String upYearLY;
    private String recordFirstYearNum;
    private int yearMinValue;
    private int yearMinParentValue;
    private int yearMaxValue;
    private int yearMaxChildValue;

    private EditText creditSystemNum;
    private OnCreditCardExpireDataFinishedListener cardExpireDataFinishedListener;
    private ExpireEntity expireEntity;
    private final int calendarMouth;

    public CreditCardTextWatcher(
            EditText editText,
            OnCreditCardExpireDataFinishedListener cardExpireDataFinishedListener) {
        this.cardExpireDataFinishedListener = cardExpireDataFinishedListener;
        this.creditSystemNum = editText;
        expireEntity = new ExpireEntity();

        Calendar calendar = Calendar.getInstance();
        // 计算信用卡年限逻辑
        int currentYear = calendar.get(Calendar.YEAR);
        calendarMouth = calendar.get(Calendar.MONTH) + 1;
        int upYearLimit = currentYear + MAX_YEAR;

        currentY = currentYear + "";
        upYearLY = upYearLimit + "";

        String yearMin = currentY.charAt(currentY.length() - 2) + ""
                + currentY.charAt(currentY.length() - 1);
        yearMinValue = Integer.parseInt(yearMin);
        yearMinParentValue = yearMinValue + 1;

        String yearMax = upYearLY.charAt(upYearLY.length() - 2) + ""
                + upYearLY.charAt(upYearLY.length() - 1);
        yearMaxValue = Integer.parseInt(yearMax);
        yearMaxChildValue = yearMaxValue - 1;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count,
                                  int after) {
        beforeTC = s.toString();
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        // 输入的时候进入逻辑判断
        if (count != 0) {
            String temp = s.toString();
            if (temp.length() >= 1) {
                currentInputDesc = temp.charAt(temp.length() - 1) + "";
            }

            if (ZERO.equals(beforeTC)) {
                if (ZERO.equals(currentInputDesc)) {
                    // 第一位是零,输入的数字也是零,则设置为0
                    creditSystemNum.setText(ZERO);
                    creditSystemNum.setSelection(ZERO.length());
                } else {
                    // 第一位是零,输入的数字不是零,补全斜杠
                    String aTemp = s.toString() + BACK_SLASH;
                    creditSystemNum.setText(aTemp);
                    creditSystemNum.setSelection(aTemp.length());
                }
            }

            if (TextUtils.isEmpty(beforeTC)) {
                if (!currentInputDesc.equals(ZERO)
                        && !currentInputDesc.equals(ONE)) {
                    // 直接输入数字
                    String aTemp = ZERO + s.toString() + BACK_SLASH;
                    creditSystemNum.setText(aTemp);
                    creditSystemNum.setSelection(aTemp.length());
                }
            }

            if (ONE.equals(beforeTC)) {
                // 之前等于1
                if (currentInputDesc.equals(ZERO)
                        || currentInputDesc.equals(ONE)
                        || currentInputDesc.equals(TWO)) {
                    // 并且输入的第二位数字满足 0,1,2月份要求
                    String aTemp = ONE + currentInputDesc + BACK_SLASH;
                    creditSystemNum.setText(aTemp);
                    creditSystemNum.setSelection(aTemp.length());
                } else {
                    creditSystemNum.setText(ONE);
                    creditSystemNum.setSelection(ONE.length());
                }
            }
            String creditViewText = creditSystemNum.getText().toString();
            Log.e("TAG", "--->>>onTextChanged-->>creditViewText-->>"
                    + creditViewText);

            // 输入的文字对年份第一位进行逻辑判断
            if (beforeTC.endsWith(BACK_SLASH)) {
                try {
                    // 计算年份第一位的上下限
                    int currentCY = Integer.parseInt(currentY.charAt(currentY
                            .length() - 2) + "");
                    int upCY = Integer.parseInt(upYearLY.charAt(upYearLY
                            .length() - 2) + "");
                    // 当前输入的数字
                    int currentInputNum = Integer.parseInt(currentInputDesc);
                    if (!(currentInputNum >= currentCY && currentInputNum <= upCY)) {
                        creditSystemNum.setText(beforeTC);
                        creditSystemNum.setSelection(creditSystemNum.getText()
                                .length());
                    } else {
                        recordFirstYearNum = currentInputDesc;
                    }
                } catch (Exception e) {
                    Log.e("TAG", e.toString());
                }
            }

            if (creditViewText.length() == 5) {
                int maxValueForCard;
                int minValueForCard;
                int mouth = Integer.parseInt(creditViewText.charAt(0) + "" + creditViewText.charAt(1));
                // 信用卡有效期和当前月份作比较,小于当前月份的,最小年限是今年的年份+1,大于等于当前月份的可以输入当年,但是最大年限都是上限15年
                if (mouth >= calendarMouth) {
                    maxValueForCard = yearMaxValue;
                    minValueForCard = yearMinValue;
                } else {
                    maxValueForCard = yearMaxValue;
                    minValueForCard = yearMinParentValue;
                }

                // 输入的文字对于年份的第二位进行逻辑判断
                Log.e("TAG", "--->>>onTextChanged-->>creditViewText-->>"
                        + creditViewText + "-->>beforeTC-->>" + beforeTC
                        + "-->>s.toString()-->>" + s.toString());
                String secondYearNum = creditViewText.charAt(creditViewText
                        .length() - 1) + "";
                Log.e("TAG", "设置第二位年份数字记录上一个recordFirstYearNum-->"
                        + recordFirstYearNum + "secondYearNum-->>"
                        + secondYearNum);
                String tempLast2Y = recordFirstYearNum + secondYearNum;
                int last2YearValue = Integer.parseInt(tempLast2Y);
                // 输入的年份第二位数字>=最小值 小于等于最大值
                if (!(last2YearValue >= minValueForCard && last2YearValue <= maxValueForCard)) {
                    creditSystemNum.setText(beforeTC);
                    creditSystemNum.setSelection(creditSystemNum.getText()
                            .length());
                }
            }

            if (creditViewText.length() > 5) {
                // 控制输出
                creditSystemNum.setText(beforeTC);
                creditSystemNum
                        .setSelection(creditSystemNum.getText().length());
            }
        } else {
            int selectionStart = creditSystemNum.getSelectionStart();
            int textLength = creditSystemNum.getText().length();
            // 判断光标有没有移动,移动的话不删除文字,
            if (selectionStart != textLength) {
                creditSystemNum.setText(beforeTC);
                creditSystemNum
                        .setSelection(creditSystemNum.getText().length());
            } else {
                // 如果在最后,则删除
                Log.e("TAG", "--->>selectionStart=" + selectionStart
                        + "--->>textLength" + textLength);
                creditSystemNum.setSelection(s.toString().length());
                if (s.length() == 2) {
                    // 当只剩下三个字符的时候一下删除两个字符
                    int index = creditSystemNum.getSelectionStart();
                    Editable editable = creditSystemNum.getText();
                    editable.delete(index - 1, index);
                }
            }
        }
    }

    @Override
    public void afterTextChanged(Editable s) {
        if (s.toString().length() == 5) {
            String exipireValue = s.toString();
            String[] mouthAndYearStrings = exipireValue.split("/");
            int expireMonth = Integer.parseInt(mouthAndYearStrings[0]);

            Calendar calendar = Calendar.getInstance();
            String cCurrentYear = calendar.get(Calendar.YEAR) + "";
            String yearSeconedString = cCurrentYear.substring(0, 2)
                    + mouthAndYearStrings[1];

            int expireYear = Integer.parseInt(yearSeconedString);
            String expireDate = String.valueOf(expireYear) + "年"
                    + String.valueOf(expireMonth) + "月";
            expireEntity.setExpireMonth(expireMonth);
            expireEntity.setExpireYear(expireYear);
            expireEntity.setExpireDate(expireDate);
            if (null != cardExpireDataFinishedListener) {
                cardExpireDataFinishedListener.dataFinished(expireEntity);
            }
        }
    }

    /**
     * 信用卡有效期entity
     *
     * @author user
     */
    public class ExpireEntity {
        private int expireMonth;
        private int expireYear;
        private String expireDate;

        public int getExpireMonth() {
            return expireMonth;
        }

        void setExpireMonth(int expireMonth) {
            this.expireMonth = expireMonth;
        }

        public int getExpireYear() {
            return expireYear;
        }

        void setExpireYear(int expireYear) {
            this.expireYear = expireYear;
        }

        public String getExpireDate() {
            return expireDate;
        }

        void setExpireDate(String expireDate) {
            this.expireDate = expireDate;
        }

        @Override
        public String toString() {
            return "ExpireEntity{" +
                    "expireMonth=" + expireMonth +
                    ", expireYear=" + expireYear +
                    ", expireDate='" + expireDate + '\'' +
                    '}';
        }
    }

    /**
     * 信用卡输入完毕的回调
     *
     * @author user
     */
    public interface OnCreditCardExpireDataFinishedListener {
        void dataFinished(ExpireEntity expireEntity);
    }

}
