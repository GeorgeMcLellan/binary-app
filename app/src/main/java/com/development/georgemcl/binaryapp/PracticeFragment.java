package com.development.georgemcl.binaryapp;


import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
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

import java.util.Random;


/**
 * A simple {@link Fragment} subclass.
 */
public class PracticeFragment extends Fragment {

    TextView numberTxt;
    EditText answerTf;
    TextView isCorrectTxt;
    FloatingActionButton newNumFab;
    Spinner rangeSpin;
    Spinner modeSpin;
    Button checkBtn;
    InputFilter binaryFilter;
    InputFilter hexFilter;

    public PracticeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_practice, container, false);
        numberTxt = (TextView) view.findViewById(R.id.numberTxt);
        answerTf = (EditText) view.findViewById(R.id.answerTxt);
        isCorrectTxt = (TextView) view.findViewById(R.id.isCorrectTxt);
        newNumFab = (FloatingActionButton) view.findViewById(R.id.newNumFab);
        checkBtn = (Button) view.findViewById(R.id.checkBtn);

        initialiseInputFilters();
        newNumberClick();
        checkAnswerClick();
        editTextButtonClick();

        return view;
    }

    //Toolbar

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_practice, menu);

        //Spinner Initialisation
        MenuItem item = menu.findItem(R.id.action_rangeSpin);
        rangeSpin = (Spinner) MenuItemCompat.getActionView(item);

        MenuItem modeItem = menu.findItem(R.id.action_modeSpin);
        modeSpin = (Spinner) MenuItemCompat.getActionView(modeItem);

        String[] modes = {"Decimal to Binary",
                        "Decimal to Hexadecimal",
                        "Binary to Decimal",
                        "Binary to Hexadecimal",
                        "Hexadecimal to Decimal",
                        "Hexadecimal to Binary"};
        ArrayAdapter modeAdapter = new ArrayAdapter(getContext(), android.R.layout.simple_spinner_item, modes);
        modeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        modeSpin.setAdapter(modeAdapter);

        String[] numRanges = {"1-15","16-63", "64-127", "128-512"};
        ArrayAdapter adapter = new ArrayAdapter(getContext(), android.R.layout.simple_spinner_item, numRanges);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        rangeSpin.setAdapter(adapter);

        SharedPreferences sharedPref = getActivity().getSharedPreferences("PracticeSpinners", Context.MODE_PRIVATE);
        int modeSpinnerVal = sharedPref.getInt("ModeSpinnerVal",-1);
        int rangeSpinnerVal = sharedPref.getInt("RangeSpinnerVal",-1);
        if(modeSpinnerVal != -1 && rangeSpinnerVal != -1) {
            // set the selected value of the spinner
            modeSpin.setSelection(modeSpinnerVal);
            rangeSpin.setSelection(rangeSpinnerVal);
        }

        //listeners for the spinners
        modeSpin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //make toolbar text white
                ((TextView) parent.getChildAt(0)).setTextColor(Color.WHITE);

                if (position == 2 || position == 4){
                    answerTf.setInputType(InputType.TYPE_CLASS_NUMBER);
                    answerTf.setFilters(new InputFilter[]{});
                    answerTf.setHint("enter decimal answer");
                }
                else if (position == 0 || position == 5){
                    answerTf.setInputType(InputType.TYPE_CLASS_NUMBER);
                    answerTf.setFilters(new InputFilter[]{binaryFilter});
                    answerTf.setHint("enter binary answer");
                }
                else if (position == 1 || position == 3){
                    answerTf.setInputType(InputType.TYPE_CLASS_TEXT);
                    answerTf.setFilters(new InputFilter[]{hexFilter});
                    answerTf.setHint("enter hexadecimal answer");
                }

                SharedPreferences sharedPreferences = getActivity().getSharedPreferences("PracticeSpinners",0);
                SharedPreferences.Editor prefEditor = sharedPreferences.edit();
                prefEditor.putInt("ModeSpinnerVal",position);
                prefEditor.apply();

                setNewQuestion();
            }
            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });
        rangeSpin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ((TextView) parent.getChildAt(0)).setTextColor(Color.WHITE);

                SharedPreferences sharedPreferences = getActivity().getSharedPreferences("PracticeSpinners",0);
                SharedPreferences.Editor prefEditor = sharedPreferences.edit();
                prefEditor.putInt("RangeSpinnerVal",position);
                prefEditor.apply();

                setNewQuestion();
            }
            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });

    }

    //Procedures

    private void setNewQuestion(){
        answerTf.setText("");
        isCorrectTxt.setText("");

        Random questionRan = new Random();

        if (rangeSpin == null)
            return;
        String range = rangeSpin.getSelectedItem().toString();
        int num;
        //generate a random number within the range selected
        if (range.equals("1-15")) {
            num = questionRan.nextInt(14) + 1;
        }else
        if (range.equals("16-63")){
            num = questionRan.nextInt(47) + 16;
        }else
        if (range.equals("64-127")){
            num = questionRan.nextInt(63)+64;
        }else
        if (range.equals("128-512")) {
            num = questionRan.nextInt(384)+128;
        }
        else num = 0;

        String mode = modeSpin.getSelectedItem().toString();

        //Convert the random decimal to the appropriate base if converting from binary/hex
        if (mode.equals("Binary to Decimal") || mode.equals("Binary to Hexadecimal")){
            numberTxt.setText(separateBinaryBytes(Integer.toBinaryString(num)));
        }
        else
        if (mode.equals("Hexadecimal to Decimal") || mode.equals("Hexadecimal to Binary")){
            numberTxt.setText(Integer.toHexString(num));
        }
        else{
            String numStr = num+"";
            numberTxt.setText(numStr);
        }
    }

    private void checkAnswer(){

        String answer = "";
        String userAnswer;
        String question = numberTxt.getText().toString().replace(" ","");
        userAnswer = answerTf.getText().toString();

        String mode = modeSpin.getSelectedItem().toString();

        if (mode.equals("Decimal to Binary")) {
            //convert the decimal question into its binary answer
            answer = Integer.toBinaryString(Integer.parseInt(question));
        }
        else if (mode.equals("Decimal to Hexadecimal")){
            answer = Integer.toHexString(Integer.parseInt(question));
        }
        else if (mode.equals("Binary to Decimal")){
            answer = Integer.parseInt(question, 2) +"";
        }
        else if (mode.equals("Binary to Hexadecimal")){
            int decimal = Integer.parseInt(question, 2);
            answer = Integer.toHexString(decimal);
        }
        else if (mode.equals("Hexadecimal to Decimal")){
            answer = Integer.parseInt(question,16) +"";
        }
        else if (mode.equals("Hexadecimal to Binary")){
            int decimal = Integer.parseInt(question, 16);
            answer = Integer.toBinaryString(decimal);

        }
        userAnswer = deleteLeadingZeros(userAnswer);


        if (answer.equals(userAnswer)){
            isCorrectTxt.setText(R.string.correctTxt);
            isCorrectTxt.setTextColor(Color.parseColor("#08a534"));
        }else{
            isCorrectTxt.setText(R.string.incorrectTxt);
            isCorrectTxt.setTextColor(Color.parseColor("#ce0606"));
        }
    }

    private String deleteLeadingZeros(String userAnswer){
        StringBuilder sb = new StringBuilder(userAnswer);
        while (sb.length() > 0 && sb.charAt(0) == '0')
        {
            sb.deleteCharAt(0);
        }
        return sb.toString();
    }

    //ISSUE
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
                    //check if it doesnt fall between 0-9 or a-f
                    if (current < 47 || (current >57 && current <97) || current > 102) {
                        return source.subSequence(0, source.length() -1);
                    }
                }
                return null;
            }
        };
    }

    //BUTTON HANDLERS

    private void checkAnswerClick(){
        checkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAnswer();
            }
        });
    }

    private void editTextButtonClick() {
        answerTf.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_GO) {
                    checkAnswer();
                    return true;
                }
                return false;
            }
        });
    }

    private void newNumberClick(){
        newNumFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setNewQuestion();
            }
        });
        newNumFab.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(getContext(), "Click for a new number", Toast.LENGTH_SHORT).show();
                return true;
            }
        });
    }

}
