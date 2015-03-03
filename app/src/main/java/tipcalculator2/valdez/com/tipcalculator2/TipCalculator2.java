package tipcalculator2.valdez.com.tipcalculator2;

import android.content.Context;
import android.view.inputmethod.EditorInfo;
import android.view.View.OnKeyListener;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Spinner;
import android.widget.RadioButton;
import android.widget.ArrayAdapter;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.os.Bundle;
import java.text.NumberFormat;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView.OnEditorActionListener;
import android.app.Activity;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;


public class TipCalculator2 extends Activity implements OnEditorActionListener, OnSeekBarChangeListener,
        OnCheckedChangeListener, OnItemSelectedListener, OnKeyListener
{

    //define the SharedPrefrences object
    private SharedPreferences savedValues;

    // define instance variables for the widgets
    private EditText billAmount;
    private TextView percentTextView;
    private SeekBar seekBar;
    private TextView tipDollarAmount;
    private TextView totalDollarAmount;
    private Spinner splitSpinner;
    private RadioGroup roundingRadioGroup;
    private RadioButton roundNoneRadioButton;
    private RadioButton roundTipRadioButton;
    private RadioButton roundTotalRadioButton;
    private TextView perPersonLabel;
    private TextView perPersonTextView;

    //define rounding constants
    private final int ROUND_NONE = 0;
    private final int ROUND_TIP = 1;
    private final int ROUND_TOTAL = 2;

    // define an instance variable for tip percent
    private float tipPercent = .15f;
    private String billAmountString = "";
    private int rounding = ROUND_NONE;
    private int split = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tip_calculator2);

        //Reference for the widget
        billAmount = (EditText) findViewById(R.id.billAmount);
        percentTextView = (TextView) findViewById(R.id.percentTextView);
        seekBar = (SeekBar) findViewById(R.id.seekBar);
        roundingRadioGroup = (RadioGroup) findViewById(R.id.roundingRadioGroup);
        roundNoneRadioButton = (RadioButton) findViewById(R.id.roundNoneRadioButton);
        roundTipRadioButton = (RadioButton) findViewById(R.id.roundTipRadioButton);
        roundTotalRadioButton = (RadioButton) findViewById(R.id.roundTotalRadioButton);
        splitSpinner = (Spinner) findViewById(R.id.splitSpinner);
        tipDollarAmount = (TextView) findViewById(R.id.tipDollarAmount);
        totalDollarAmount = (TextView) findViewById(R.id.totalDollarAmount);
        perPersonLabel = (TextView) findViewById(R.id.perPersonLabel);
        perPersonTextView = (TextView) findViewById(R.id.perPersonTextView);

        //set array adapter for spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.split_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        splitSpinner.setAdapter(adapter);


        //set the listeners
        billAmount.setOnEditorActionListener(this);
        billAmount.setOnKeyListener(this);
        seekBar.setOnKeyListener(this);
        seekBar.setOnSeekBarChangeListener(this);
        roundingRadioGroup.setOnCheckedChangeListener(this);
        roundingRadioGroup.setOnKeyListener(this);
        splitSpinner.setOnItemSelectedListener(this);

        //gets SharedPreference object
        savedValues = getSharedPreferences("SavedValues", MODE_PRIVATE);
    }

    @Override
    protected void onPause() {
        //save the instance variables
        Editor editor = savedValues.edit();
        editor.putString("billAmountString", billAmountString);
        editor.putFloat("tipPercent", tipPercent);
        editor.putInt("rounding", rounding);
        editor.putInt("split", split);
        editor.commit();

        super.onPause();
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        //get the instance variable
        billAmountString = savedValues.getString("billAmountString", "");
        tipPercent = savedValues.getFloat("tipPercent", 0.15f);
        rounding = savedValues.getInt("rounding", ROUND_NONE);
        split = savedValues.getInt("split", 1);

        //set bill amount
        billAmount.setText(billAmountString);

        //set the tip percent on its widget
        int progress = Math.round(tipPercent * 100);
        seekBar.setProgress(progress);

        if (rounding == ROUND_NONE)
        {
            roundNoneRadioButton.setChecked(true);
        }

        else if (rounding == ROUND_TIP)
        {
            roundTipRadioButton.setChecked(true);
        }

        else if (rounding == ROUND_TOTAL)
        {
            roundTotalRadioButton.setChecked(true);
        }


        int position = split - 1;
        splitSpinner.setSelection(position);

    }

    public void calculateAndDisplay()
    {
        //get user bill amount
        billAmountString = billAmount.getText().toString();
        float userAmount;

        if (billAmountString.equals(""))
        {
            userAmount = 0;
        } else
        {
            userAmount = Float.parseFloat(billAmountString);
        }

        //get Tip Percent
        int progress = seekBar.getProgress();
        tipPercent = (float) progress / 100;

        float tipAmount = 0;
        float totalAmount = 0;

        if (rounding == ROUND_NONE)
        {
            tipAmount = userAmount * tipPercent;
            totalAmount = userAmount + tipAmount;
        }
        else if (rounding == ROUND_TIP)
        {
            tipAmount = StrictMath.round(userAmount * tipPercent);
            totalAmount = userAmount + tipAmount;
            tipPercent = tipAmount / userAmount;
        }
        else if (rounding == ROUND_TOTAL)
        {
            float tipNotRounded = userAmount * tipPercent;
            totalAmount = StrictMath.round(userAmount + tipNotRounded);
            tipAmount = totalAmount - userAmount;
            tipPercent = tipAmount / userAmount;
        }

        float splitAmount = 0;

        //If there is no split then hide
        if (split == 1)
        {
            perPersonLabel.setVisibility(View.GONE);
            perPersonTextView.setVisibility(View.GONE);
        }
        else //if there is a split, show widgets
        {
            splitAmount = totalAmount / split;
            perPersonLabel.setVisibility(View.VISIBLE);
            perPersonTextView.setVisibility(View.VISIBLE);
        }

        //Display the results with formatting(percent/decimal)
        NumberFormat currency = NumberFormat.getCurrencyInstance();
        tipDollarAmount.setText(currency.format(tipAmount));
        totalDollarAmount.setText(currency.format(totalAmount));
        perPersonTextView.setText(currency.format(splitAmount));

        NumberFormat percent = NumberFormat.getPercentInstance();
        percentTextView.setText(percent.format(tipPercent));
    }


    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        //Allow use of both "hard" and "soft" keyboards
        if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_UNSPECIFIED) {
            calculateAndDisplay();
        }
        return false;
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        //TODO Auto=generated method stub
    }

    //links percent amount to seekbar
    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        percentTextView.setText(progress + "%");
    }

    //calculate amount from seekbar percent
    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        calculateAndDisplay();
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.roundNoneRadioButton:
                rounding = ROUND_NONE;
                break;
            case R.id.roundTipRadioButton:
                rounding = ROUND_TIP;
                break;
            case R.id.roundTotalRadioButton:
                rounding = ROUND_TOTAL;
                break;
        }
        calculateAndDisplay();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {
        split = position + 1;
        calculateAndDisplay();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent)
    {
        //Do nothing
    }


    @Override
    public boolean onKey(View view, int keyCode, KeyEvent event)
    {
        switch (keyCode)
        {
            case KeyEvent.KEYCODE_ENTER:
            case KeyEvent.KEYCODE_DPAD_CENTER:

                calculateAndDisplay();

                //hide the soft keyboard
                InputMethodManager imm = (InputMethodManager)
                        getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(billAmount.getWindowToken(), 0);

                //consume the event
                return true;
            case KeyEvent.KEYCODE_DPAD_RIGHT:
            case KeyEvent.KEYCODE_DPAD_LEFT:
                if (view.getId() == R.id.seekBar)
                {
                    calculateAndDisplay();
                }
                break;
        }
        return false;
    }
}