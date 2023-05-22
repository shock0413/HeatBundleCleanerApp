package com.example.myapplication;

import android.content.Context;
import android.graphics.Color;
import android.os.SystemClock;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethod;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.HashMap;

public class AdptMain extends BaseExpandableListAdapter {
    private Context context;
    private ArrayList<String> arrayGroup;
    private HashMap<String, ArrayList<String>> arrayChild;
    private float density;
    boolean m_IsTouch = false;
    private int n = 0;
    private long pauseOffset = 0;
    private int sleep = 1000;
    private String preText = "";

    public AdptMain(Context context, ArrayList<String> arrayGroup, HashMap<String, ArrayList<String>> arrayChild, float density) {
        super();

        this.context = context;
        this.arrayGroup = arrayGroup;
        this.arrayChild = arrayChild;
        this.density = density;
    }

    @Override
    public int getGroupCount() {
        return arrayGroup.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return arrayChild.get(arrayGroup.get(groupPosition)).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return arrayGroup.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return arrayChild.get(groupPosition).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        String groupName = arrayGroup.get(groupPosition);
        View v = convertView;

        if (v == null) {
            LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = (RelativeLayout)inflater.inflate(R.layout.listview_group, null);
        }

        TextView textGroup = (TextView)v.findViewById(R.id.textGroup);
        textGroup.setBackgroundColor(ContextCompat.getColor(context, R.color.steelblue));
        textGroup.setText(groupName);
        textGroup.setTextColor(Color.WHITE);
        // textGroup.setTextSize(28);
        textGroup.setTextAppearance(context, android.R.style.TextAppearance_Large_Inverse);

        return v;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        String groupName = arrayGroup.get(groupPosition);
        String childName = arrayChild.get(arrayGroup.get(groupPosition)).get(childPosition);
        View v = convertView;

        if (v == null) {
            LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = (LinearLayout)inflater.inflate(R.layout.listview_child, null);
        }

        TextView textChild = (TextView)v.findViewById(R.id.textChild);
        textChild.setText(childName);
        textChild.setTextColor(Color.BLACK);
        // textChild.setTextSize(24);
        textChild.setTextAppearance(context, android.R.style.TextAppearance_Large);

        if (childName.equals("공사 번호")) {
            RelativeLayout relativeLayoutChild = (RelativeLayout)v.findViewById(R.id.relativeLayoutChild);
            relativeLayoutChild.setVisibility(View.GONE);
            EditText editChild = (EditText)v.findViewById(R.id.editChild);
            editChild.setVisibility(View.VISIBLE);
            editChild.setTextColor(Color.BLACK);
            editChild.setTextSize(24);
            InputMethodManager imm = (InputMethodManager)context.getSystemService(context.INPUT_METHOD_SERVICE);
        }
        else {
            RelativeLayout relativeLayoutChild = (RelativeLayout)v.findViewById(R.id.relativeLayoutChild);
            relativeLayoutChild.setVisibility(View.VISIBLE);
            EditText editChild = (EditText)v.findViewById(R.id.editChild);
            editChild.setVisibility(View.GONE);
            EditText numericChild = (EditText)v.findViewById(R.id.numericChild);
            numericChild.setText("0");
            Button numericUpButtonChild = (Button)v.findViewById(R.id.numericUpButtonChild);
            Button numericDownButtonChild = (Button)v.findViewById(R.id.numericDownButtonChild);
            numericChild.setPadding((int)(numericChild.getWidth() / 2 * density + 0.5f), 0, 0, 0);
            // numericChild.setTextSize(24);
            numericChild.setTextAppearance(context, android.R.style.TextAppearance_Large);
            InputMethodManager imm = (InputMethodManager)context.getSystemService(context.INPUT_METHOD_SERVICE);

            numericChild.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (hasFocus) {
                        Log.i("확인", "포커스 get");
                        imm.showSoftInput(numericChild, 0);
                    } else {
                        Log.i("확인", "포커스 lost");
                        imm.hideSoftInputFromWindow(numericChild.getWindowToken(), 0);
                    }
                }
            });

            numericChild.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    preText = s.toString();
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (preText.equals(s.toString())) {
                        return;
                    }
                    else {
                        if (s.toString().equals("")) {
                            numericChild.setText(0 + "");
                            numericChild.setSelection(numericChild.getText().length());
                        }
                        else {
                            int n = Integer.parseInt(s.toString());
                            numericChild.setText(n + "");
                            numericChild.setSelection(numericChild.getText().length());
                        }
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });

            numericUpButtonChild.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String text = numericChild.getText().toString();
                    int n = Integer.parseInt(text);
                    n++;
                    numericChild.setText(String.valueOf(n));
                }
            });

            numericDownButtonChild.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String text = numericChild.getText().toString();
                    int n = Integer.parseInt(text);
                    n--;
                    numericChild.setText(String.valueOf(n));
                }
            });
        }

        return v;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
