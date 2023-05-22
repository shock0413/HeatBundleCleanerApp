package com.example.myapplication;

import android.content.Context;
import android.database.DataSetObservable;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.HashMap;

public class SettingAdapter extends BaseExpandableListAdapter {
    private Context context;
    private ArrayList<String> arrayGroup;
    private HashMap<String, ArrayList<String>> arrayChild;
    private float density;
    boolean m_IsTouch = false;
    private int n = 0;
    private long pauseOffset = 0;
    private int sleep = 1000;
    private String preText = "";
    private ArrayList<EditText> editTextList;
    private DataSetObservable dataSetObservable = new DataSetObservable();

    private DataSetObservable getDataSetObservable() {
        return dataSetObservable;
    }

    public void notifyDataSetChanged() {
        this.getDataSetObservable().notifyChanged();
    }

    public void notifyDataSetInvalidated() {
        this.getDataSetObservable().notifyInvalidated();
    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer) {
        this.getDataSetObservable().registerObserver(observer);
    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {
        this.getDataSetObservable().unregisterObserver(observer);
    }

    public SettingAdapter(Context context, ArrayList<String> arrayGroup, HashMap<String, ArrayList<String>> arrayChild, float density) {
        super();

        this.context = context;
        this.arrayGroup = arrayGroup;
        this.arrayChild = arrayChild;
        this.density = density;
        editTextList = new ArrayList<>();
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
        textGroup.setTextSize(28);

        return v;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        String groupName = arrayGroup.get(groupPosition);
        String childName = arrayChild.get(arrayGroup.get(groupPosition)).get(childPosition);
        View v = convertView;

        if (v == null) {
            LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = (LinearLayout)inflater.inflate(R.layout.setting_child, null);
        }

        TextView textChild = (TextView)v.findViewById(R.id.textChild);
        textChild.setText(childName);
        textChild.setTextColor(Color.BLACK);
        textChild.setTextSize(24);

        EditText editChild = (EditText)v.findViewById(R.id.editChild);
        editChild.setVisibility(View.VISIBLE);
        editChild.setTextColor(Color.BLACK);
        editChild.setTextSize(24);

        return v;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    public void setEditChild(int groupPosition, int childPosition, String text) {
        /*
        String childName = arrayChild.get(arrayGroup.get(groupPosition)).get(childPosition);
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout v = (LinearLayout)inflater.inflate(R.layout.setting_child, null);
        EditText editText = (EditText)v.getChildAt(1);
        editText.setText(text);
        */
        // editTextList.get(childPosition).setText(text);
    }
}
