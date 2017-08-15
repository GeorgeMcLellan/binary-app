package com.development.georgemcl.binaryapp;


import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


public class ConvertFragment extends Fragment {

    EditText toConvertTf;
    Button convertBtn;
    TextView answerTxt;
    Spinner toolbarModeSel;
    FloatingActionButton copyBtn;
    FloatingActionButton clearBtn;
    InputFilter binaryFilter;
    InputFilter hexFilter;


    public ConvertFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_convert, container, false);
        toConvertTf = (EditText) view.findViewById(R.id.toConvertTf);
        convertBtn = (Button) view.findViewById(R.id.convertBtn);
        answerTxt = (TextView) view.findViewById(R.id.answerTxt);
        copyBtn = (FloatingActionButton) view.findViewById(R.id.copyFab);
        clearBtn = (FloatingActionButton) view.findViewById(R.id.clearFab);

        //remove toolbar title
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("");

        initialiseInputFilters();
        convertBtnListener();
        editTextButtonListener();
        copyBtnListener();
        clearBtnListener();
        return view;

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_convert, menu);

        MenuItem item = menu.findItem(R.id.action_modeSpin);
        toolbarModeSel = (Spinner) MenuItemCompat.getActionView(item);

        String[] modes = {"Decimal to Binary",
                    "Decimal to Hexadecimal",
                    "Binary to Decimal",
                    "Binary to Hexadecimal",
                    "Hexadecimal to Decimal",
                    "Hexadecimal to Binary",
                    "Text to Ascii Numbers",
                    "Ascii Number to Text"};
        ArrayAdapter adapter = new ArrayAdapter(getContext(), android.R.layout.simple_spinner_item, modes);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        toolbarModeSel.setAdapter(adapter);
        SharedPreferences sharedPref = getActivity().getSharedPreferences("ConvertSpinner", Context.MODE_PRIVATE);
        int spinnerValue = sharedPref.getInt("ConvertSpinnerVal",-1);
        if(spinnerValue != -1) {
            // set the selected value of the spinner
            toolbarModeSel.setSelection(spinnerValue);
        }

        //listener for spinner
        toolbarModeSel.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ((TextView) parent.getChildAt(0)).setTextColor(Color.WHITE);
                answerTxt.setText("");
                toConvertTf.setText("");

                if (position == 0 || position == 1) {
                    toConvertTf.setInputType(InputType.TYPE_CLASS_NUMBER);
                    toConvertTf.setHint("enter decimal number");
                    toConvertTf.setFilters(new InputFilter[]{});
                }
                else if (position == 2 || position == 3){
                    toConvertTf.setInputType(InputType.TYPE_CLASS_NUMBER);
                    toConvertTf.setHint("enter binary number");
                    toConvertTf.setFilters(new InputFilter[]{binaryFilter});
                }
                else if (position == 4 || position == 5){
                    toConvertTf.setInputType(InputType.TYPE_CLASS_TEXT);
                    toConvertTf.setHint("enter hexadecimal number");
                    toConvertTf.setFilters(new InputFilter[]{hexFilter});
                }
                else if (position == 6){
                    toConvertTf.setInputType(InputType.TYPE_CLASS_TEXT);
                    toConvertTf.setHint("enter text");
                    toConvertTf.setFilters(new InputFilter[]{});
                }
                else if (position == 7){
                    toConvertTf.setInputType(InputType.TYPE_CLASS_NUMBER);
                    toConvertTf.setHint("enter ascii number");
                    toConvertTf.setFilters(new InputFilter[]{});
                }

                SharedPreferences sharedPreferences = getActivity().getSharedPreferences("ConvertSpinner",0);
                SharedPreferences.Editor prefEditor = sharedPreferences.edit();
                prefEditor.putInt("ConvertSpinnerVal",position);
                prefEditor.apply();
            }
            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void checkAnswer() {
        String mode = toolbarModeSel.getSelectedItem().toString();
        if (mode.equals("Decimal to Binary")) {
            decimalToBinary();
        }else if (mode.equals("Binary to Decimal")) {
            binaryToDecimal();
        }else if (mode.equals("Text to Ascii Numbers")){
            textToAscii();
        }else if (mode.equals("Ascii Number to Text")){
            asciiToText();
        }else if (mode.equals("Decimal to Hexadecimal")){
            decimalToHex();
        }else if (mode.equals("Hexadecimal to Decimal")){
            hexToDecimal();
        }else if (mode.equals("Hexadecimal to Binary")){
            hexToBinary();
        }else if (mode.equals("Binary to Hexadecimal")){
            binaryToHex();
        }
    }

    private void binaryToHex() {
        try{
            long decimal = Long.parseLong(toConvertTf.getText().toString(),2);
            String hex = Long.toHexString(decimal);
            answerTxt.setText(hex);
            answerTxt.setTextColor(Color.BLACK);
        }
        catch(Exception e){
            answerTxt.setText(R.string.errorTxt);
            answerTxt.setTextColor(Color.RED);
        }
    }

    private void hexToBinary() {
        try {
            long decimal = Long.parseLong(toConvertTf.getText().toString(), 16);
            String binary = Long.toBinaryString(decimal);
            answerTxt.setText(separateBinaryBytes(binary));
            answerTxt.setTextColor(Color.BLACK);
        }
        catch(Exception e){
            answerTxt.setText(R.string.errorTxt);
            answerTxt.setTextColor(Color.RED);
        }
    }

    private void hexToDecimal() {
        try {
            String hexString = toConvertTf.getText().toString();
            long decimal = Long.parseLong(hexString,16);
            String decimalStr = decimal+"";
            answerTxt.setText(decimalStr);
            answerTxt.setTextColor(Color.BLACK);
        }
        catch (Exception e){
            answerTxt.setText(R.string.errorTxt);
            answerTxt.setTextColor(Color.RED);
        }
    }

    private void decimalToHex() {
        try {
            long decimal = Long.parseLong(toConvertTf.getText().toString());
            String hex = Long.toHexString(decimal);
            answerTxt.setText(hex);
            answerTxt.setTextColor(Color.BLACK);
        }
        catch (Exception e){
            answerTxt.setText(R.string.errorTxt);
            answerTxt.setTextColor(Color.RED);
        }

    }

    private void asciiToText() {
        try{
            int asciiNumber = Integer.parseInt(toConvertTf.getText().toString());
            //Convert the integer to its Ascii Character equivalent
            String outputTxt = Character.toString((char) asciiNumber)+"";
            answerTxt.setText(outputTxt);
            answerTxt.setTextColor(Color.BLACK);
        }
        catch (Exception e){
            answerTxt.setText(R.string.errorTxt);
            answerTxt.setTextColor(Color.RED);
        }
    }

    private void textToAscii() {
        try {

            String userString = toConvertTf.getText().toString();
            String outputText = "";
            for(int i = 0; i < userString.length(); i++){
                outputText+= (int) userString.charAt(i) + " ";
            }
            answerTxt.setText(outputText);
            answerTxt.setTextColor(Color.BLACK);
        }
        catch (Exception e){
            answerTxt.setText(R.string.errorTxt);
            answerTxt.setTextColor(Color.RED);
        }
    }

    private void decimalToBinary() {
        try{
            long userDecimal = Long.parseLong(toConvertTf.getText().toString());
            String binary =  Long.toBinaryString(userDecimal);
            answerTxt.setText(separateBinaryBytes(binary));
            answerTxt.setTextColor(Color.BLACK);
        }
        catch (Exception e){
            answerTxt.setText(R.string.errorTxt);
            answerTxt.setTextColor(Color.RED);
        }
    }

    private void binaryToDecimal(){
        try {
            String userBinary = toConvertTf.getText().toString();
            long decimal = Long.parseLong(userBinary,2);
            String decimalStr = decimal +"";
            answerTxt.setText(decimalStr);
            answerTxt.setTextColor(Color.BLACK);
        }
        catch (Exception e){
            answerTxt.setText(R.string.errorTxt);
            answerTxt.setTextColor(Color.RED);
        }

    }

    private String separateBinaryBytes(String original){
        StringBuilder sb = new StringBuilder(original);
        int count = 0;
        for (int i = sb.length(); i > 0; i--) {
            if (count % 4 == 0) {
                sb.insert(i, ' ');
            }
            count++;
        }
        return sb.toString();
    }


    private void initialiseInputFilters() {
        binaryFilter = new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                for (int i = start; i < end; i++){
                    char current = source.charAt(i);
                    if (current != '0' && current != '1')
                    {return "";}
                }
                return null;
            }
        };

        hexFilter = new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                for (int i = start; i < end; i++){
                    //convert the current character into ascii format
                    int current = (int) source.charAt(i);
                    //check if it does not fall between 0-9 or a-f
                    if (current < 47 || (current >57 && current <97) || current > 102) {
                        return source.subSequence(0, source.length() -1);
                    }
                }
                return null;
            }
        };
    }


    private void editTextButtonListener(){
        toConvertTf.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_GO){
                    checkAnswer();
                    return true;
                }
                return false;
            }
        });
    }

    private void convertBtnListener(){
        convertBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAnswer();
            }
        });
    }

    private void copyBtnListener() {
        copyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager clipboard = (ClipboardManager)
                        getActivity().getSystemService(getContext().CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("result",answerTxt.getText().toString());
                clipboard.setPrimaryClip(clip);
                Toast.makeText(getContext(),"Copied result to clipboard",Toast.LENGTH_SHORT).show();
            }
        });
        copyBtn.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(getContext(),"Click to copy result to clipboard", Toast.LENGTH_SHORT).show();
                return true;
            }
        });
    }

    private void clearBtnListener() {
        clearBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toConvertTf.setText("");
                answerTxt.setText("");
            }
        });

        clearBtn.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(getContext(),"Click to clear input", Toast.LENGTH_SHORT).show();
                return true;
            }
        });
    }


}
