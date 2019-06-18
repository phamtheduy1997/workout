package com.example.a7_minutes_workout;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.ArrayList;

public class BMI extends AppCompatActivity {
    private EditText edtWeight, edtHeight;
    private TextView tvBMI, tvEvaluate;
    private Button btnBMI,btnInsert,btnShow;
    private LineChart lineChart;
    private MyHelper myHelper;
    private SQLiteDatabase sqLiteDatabase;
    private LineDataSet lineDataSet = new LineDataSet(null,null);
    private ArrayList<ILineDataSet> dataSets = new ArrayList<>();
    private LineData lineData;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bmi);
        edtWeight = findViewById(R.id.edt_weight);
        edtHeight = findViewById(R.id.edt_height);
        tvBMI = findViewById(R.id.tv_kq);
        tvEvaluate = findViewById(R.id.tv_nx);
        btnBMI = findViewById(R.id.btn_bmi);
        lineChart = findViewById(R.id.chart);
        btnInsert = findViewById(R.id.btn_insert);
        btnShow = findViewById(R.id.btn_show);
        myHelper = new MyHelper(this);
        sqLiteDatabase = myHelper.getWritableDatabase();
        btnBMI.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String weight = edtWeight.getText().toString();
                String height = edtHeight.getText().toString();
                if (weight.isEmpty()||height.isEmpty()){
                    Toast.makeText(BMI.this, "Vui lòng nhập cân nặng, chiều cao!", Toast.LENGTH_SHORT).show();
                }
                float weightValue = Float.parseFloat(weight);
                float heightValue = Float.parseFloat(height)/100;
                float bmiValue = weightValue/(heightValue*heightValue);

                tvBMI.setText(String.valueOf(bmiValue));
                if (bmiValue<15){
                    tvEvaluate.setText("Thiếu cân rất nặng");
                }else if (bmiValue>=15&&bmiValue<16){
                    tvEvaluate.setText("Thiếu cân nặng");
                }else if (bmiValue>=16&&bmiValue<18.5){
                    tvEvaluate.setText("Thiếu cân");
                }else if (bmiValue>=18.5&&bmiValue<23){
                    tvEvaluate.setText("Bình thường");
                }else if (bmiValue>=25&&bmiValue<30){
                    tvEvaluate.setText("Thừa cân độ 1");
                }else if (bmiValue>=30&&bmiValue<40){
                    tvEvaluate.setText("Thừa cân độ 2");
                }else if (bmiValue>=40){
                    tvEvaluate.setText("Thừa cân độ 3");
                }
            }
        });
        InsertBtn();
        ShowBtn();
        lineDataSet.setLineWidth(4);
    }

    private void ShowBtn() {
        btnShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lineDataSet.setValues(getDataValues());
                lineDataSet.setLabel("Cân nặng - Chiều cao");
                dataSets.clear();
                dataSets.add(lineDataSet);
                lineData = new LineData(dataSets);
                lineChart.clear();
                lineChart.setData(lineData);
                lineChart.invalidate();
            }
        });
    }

    private void InsertBtn() {
        btnInsert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                float xVal = (float) Float.parseFloat(String.valueOf(edtWeight.getText()));
                float yVal = (float) Float.parseFloat(String.valueOf(edtHeight.getText()));
                myHelper.insertData(xVal,yVal);
            }
        });
    }
    private ArrayList<Entry> getDataValues(){
        ArrayList<Entry> dataVals = new ArrayList<>();
        String[] columns = {"xValues","yValues"};
        Cursor cursor = sqLiteDatabase.query("myTable",columns,null,null,null,null,null);
        for (int i = 0; i <cursor.getCount() ; i++) {
            cursor.moveToNext();
            dataVals.add(new Entry(cursor.getFloat(0),cursor.getFloat(1)));
        }
        return dataVals;
    }
}
