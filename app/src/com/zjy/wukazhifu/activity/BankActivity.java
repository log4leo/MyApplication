package com.zjy.wukazhifu.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zjy.wukazhifu.R;
import com.zjy.wukazhifu.entity.Bank;
import com.zjy.wukazhifu.util.Constants;
import com.zjy.wukazhifu.util.MyVolley;
import com.zjy.wukazhifu.util.PostJsonObjectRequest;
import com.zjy.wukazhifu.util.PreferenceHelper;
import com.zjy.wukazhifu.util.ToastHelper;

public class BankActivity extends Activity implements OnClickListener {

	private ImageView backImg;
	private EditText searchEdt;
	private ListView listView;
	private BankAdapter adapter;

	private List<Bank> dataList = new ArrayList<Bank>();

	private String bank;
	private String sheng;
	private String shi;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_bank);
		Intent intent = getIntent();
		bank = intent.getStringExtra("bank");
		sheng = intent.getStringExtra("sheng");
		shi = intent.getStringExtra("shi");
		initView();
		bank();
	}

	private void initView() {
		backImg = (ImageView) findViewById(R.id.backImg);
		backImg.setOnClickListener(this);

		listView = (ListView) findViewById(R.id.listView);
		adapter = new BankAdapter(this);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Bank value = adapter.getDataList().get(position);
				Intent intent = new Intent();
				intent.putExtra("cnapsCode", value.getCnapsCode());
				intent.putExtra("subBranch", value.getSubBranch());
				BankActivity.this.setResult(RESULT_OK, intent);
				BankActivity.this.finish();
			}
		});

		searchEdt = (EditText) findViewById(R.id.searchEdt);
		searchEdt.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// 当输入框里面的值为空，更新为原来的列表，否则为过滤数据列表
				filterData(s.toString());
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {
			}
		});
	}

	private void filterData(String filterStr) {
		List<Bank> filterDateList = new ArrayList<Bank>();

		if (TextUtils.isEmpty(filterStr)) {
			filterDateList = dataList;
		} else {
			filterDateList.clear();
			for (Bank b : dataList) {
				String name = b.getSubBranch();
				if (name.indexOf(filterStr.toString()) != -1) {
					filterDateList.add(b);
				}
			}
		}
		adapter.updateListView(filterDateList);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.backImg:
			finish();
			break;
		default:
			break;
		}
	}

	private void bank() {
		String url = Constants.ADDR_bank2;
		String cookie = PreferenceHelper.getCookie();
		Map<String, String> param = new HashMap<String, String>();
		param.put("bankPro", sheng);
		param.put("bankCity", shi);
		param.put("bankName", bank);
		PostJsonObjectRequest request = new PostJsonObjectRequest(url, cookie, param, new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				try {
					JSONObject REP_BODY = response.optJSONObject("REP_BODY");
					String RSPCOD = REP_BODY.optString("RSPCOD");
					if ("000000".equals(RSPCOD)) {
						JSONArray bankCardList = REP_BODY.optJSONArray("bankCardList");
						Gson gson = new Gson();
						dataList = gson.fromJson(bankCardList.toString(), new TypeToken<List<Bank>>() {
						}.getType());
						adapter.updateListView(dataList);
					} else {
						String RSPMSG = REP_BODY.optString("RSPMSG");
						ToastHelper.toast(BankActivity.this,RSPMSG);
					}
				} catch (Exception e) {
					ToastHelper.toast(BankActivity.this,"服务器错误");
				}
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError arg0) {
				ToastHelper.toast(BankActivity.this,"无法连接服务器");
			}
		});
		MyVolley.addRequest(request);
	}

	public class BankAdapter extends BaseAdapter {
		private LayoutInflater mInflater = null;
		private List<Bank> dataList = new ArrayList<Bank>();

		public List<Bank> getDataList() {
			return dataList;
		}

		private BankAdapter(Context context) {
			this.mInflater = LayoutInflater.from(context);
		}

		public void updateListView(List<Bank> list) {
			this.dataList = list;
			notifyDataSetChanged();
		}

		@Override
		public int getCount() {
			return dataList.size();
		}

		@Override
		public Object getItem(int position) {
			return position;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = mInflater.inflate(R.layout.listitem_bank, null);
				holder.txt = (TextView) convertView.findViewById(R.id.txt);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			Bank data = dataList.get(position);
			holder.txt.setText(data.getSubBranch());
			return convertView;
		}

		class ViewHolder {
			public TextView txt;
		}
	}
}