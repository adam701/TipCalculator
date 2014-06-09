package com.example.tipcalculator;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.commons.io.FileUtils;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

public class MainActivity extends Activity {

	private EditText subtotal;
	private EditText tax;
	private EditText tipRate;
	private Spinner split;
	private RadioButton preTaxBasedButton;
	private RadioButton afterTaxBasedButton;
	private EditText result;
	private RadioGroup rdGrp;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		getActionBar().hide();
		init();
		setUpListeners();
	}

	private void init(){
		this.subtotal = (EditText) findViewById(R.id.etSubtotal);
		this.subtotal.setText("0.0");
		this.tax = (EditText) findViewById(R.id.etTax);
		this.tax.setText("0.0");
		this.tipRate = (EditText) findViewById(R.id.edTip);
		this.tipRate.setText("0.0");
		this.split = (Spinner) findViewById(R.id.spSplit);
		this.split.setId(1);
		;
		this.preTaxBasedButton = (RadioButton) findViewById(R.id.rbPreTax);
		this.afterTaxBasedButton = (RadioButton) findViewById(R.id.rbPreTax);
		// this.preTaxBasedButton.setChecked(true);
		// this.afterTaxBasedButton.setChecked(false);
		this.result = (EditText) findViewById(R.id.edResult);
		this.rdGrp = (RadioGroup) findViewById(R.id.rgPolicy);
		try {
			loadSetting();
		} catch (IOException e) {
			throw new RuntimeException("Fail to read data from File",e);
		}
	}

	private void resetResult() {
		double subtotal;
		double tax;
		double tipRate;
		double split;
		String res;
		try {
			subtotal = Double.valueOf(this.subtotal.getText().toString());
			tax = Double.valueOf(this.tax.getText().toString());
			tipRate = Double.valueOf(this.tipRate.getText().toString()) / 100.0;
			split = Double.valueOf(this.split.getSelectedItem().toString());
			double tempRes;
			if (this.preTaxBasedButton.isChecked()) {
				tempRes = (subtotal * (1 + tipRate) + tax) / split;
			} else {
				tempRes = (subtotal + tax) * (1 + tipRate) / split;
			}
			res = String.format("%.2f", tempRes);
		} catch (NumberFormatException e) {
			res = this.result.getText().toString();
		}
		this.result.setText(res);
	}

	private void setUpListeners() {
		this.subtotal.addTextChangedListener(new EditTextWatcher());
		this.tax.addTextChangedListener(new EditTextWatcher());
		this.split.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				resetResult();
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {

			}
		});
		this.tipRate.addTextChangedListener(new EditTextWatcher());
		this.rdGrp.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				resetResult();
				try {
					writeSettingToFile();
				} catch (IOException e) {
					throw new RuntimeException("Fail to write data to File",e);
				}
			}
		});

	}

	private class EditTextWatcher implements TextWatcher {

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
			// TODO Auto-generated method stub
		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			// TODO Auto-generated method stub
		}

		@Override
		public void afterTextChanged(Editable s) {
			resetResult();
			try {
				writeSettingToFile();
			} catch (IOException e) {
				throw new RuntimeException("Fail to write data to File",e);
			}
		}

	}

	private void loadSetting() throws IOException{
		File dirFile = this.getFilesDir();
		File itemFile = new File(dirFile, "settings.txt");
		if(!itemFile.exists()){
			return;
		}
		String setting = FileUtils.readFileToString(itemFile);
		String[] settings = setting.split(",");
		for(String oneSet : settings){
			String[] keyValuePair = oneSet.split(":");
			if("tipRate".equalsIgnoreCase(keyValuePair[0].trim())){
				this.tipRate.setText(keyValuePair[1]);
			} else if("policy".equalsIgnoreCase(keyValuePair[0].trim())){
				this.rdGrp.check(Integer.valueOf(keyValuePair[1]));
			}
		}
	}

	private void writeSettingToFile() throws IOException {
		File dirFile = this.getFilesDir();
		File itemFile = new File(dirFile, "settings.txt");
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("tipRate:")
				.append(this.tipRate.getText().toString()).append(",policy:")
				.append(this.rdGrp.getCheckedRadioButtonId());
		FileUtils.writeStringToFile(itemFile, stringBuilder.toString(), false);
	}
}
