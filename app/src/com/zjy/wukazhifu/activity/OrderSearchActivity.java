package com.zjy.wukazhifu.activity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.feezu.liuli.timeselector.TimeSelector;
import org.feezu.liuli.timeselector.TimeSelector.MODE;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.zjy.wukazhifu.R;
import com.zjy.wukazhifu.entity.Order;
import com.zjy.wukazhifu.util.Constants;
import com.zjy.wukazhifu.util.MyVolley;
import com.zjy.wukazhifu.util.PostJsonObjectRequest;
import com.zjy.wukazhifu.util.PreferenceHelper;
import com.zjy.wukazhifu.util.ToastHelper;
import com.zjy.wukazhifu.util.ToolUtil;

public class OrderSearchActivity extends Activity implements OnClickListener {

	private ImageView backImg;
	private TextView confirmTxt;
	private TextView titleTxt;

	private TextView time1Txt;
	private TextView time2Txt;
	private TextView statusTxt;

	private PullToRefreshListView listView;
	private MsgAdapter adapter;
	private List<Order> dataList = new ArrayList<Order>();

	private int currentPages = 1;
	private int pageSize = 10;

	private String sdate = "";
	private String edate = "";
	private String busType;

	private String ordstatus = "";
	private List<String> statusNameList = new ArrayList<String>();
	private List<String> statusValueList = new ArrayList<String>();

	protected PopupWindow statusPopWin;
	protected ListView statusList;
	protected StatusAdapter statusAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ordersearch);
		busType = getIntent().getStringExtra("busType");
		initView();
		// getData(currentPages);
		statusNameList.add("-未处理-");
		statusNameList.add("-处理中-");
		statusNameList.add("-成功-");
		statusNameList.add("-失败-");
		statusNameList.add("-全部-");

		statusValueList.add("00");
		statusValueList.add("03");
		statusValueList.add("01");
		statusValueList.add("02");
		statusValueList.add("");
	}

	private void initView() {
		backImg = (ImageView) findViewById(R.id.backImg);
		backImg.setOnClickListener(this);
		confirmTxt = (TextView) findViewById(R.id.confirmTxt);
		confirmTxt.setOnClickListener(this);

		titleTxt = (TextView) findViewById(R.id.titleTxt);
		if ("01".equals(busType)) {
			titleTxt.setText("收款明细搜索");
		} else if ("03".equals(busType)) {
			titleTxt.setText("提现明细搜索");
		}

		time1Txt = (TextView) findViewById(R.id.time1Txt);
		time2Txt = (TextView) findViewById(R.id.time2Txt);
		time1Txt.setOnClickListener(this);
		time2Txt.setOnClickListener(this);

		statusTxt = (TextView) findViewById(R.id.statusTxt);
		statusTxt.setOnClickListener(this);

		listView = (PullToRefreshListView) findViewById(R.id.listView);
		listView.setMode(Mode.BOTH);
		listView.setOnRefreshListener(new OnRefreshListener2<ListView>() {
			@Override
			public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
				currentPages = 1;
				getData(currentPages);
			}

			@Override
			public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
				currentPages++;
				getData(currentPages);
			}
		});
		adapter = new MsgAdapter(this);
		listView.setAdapter(adapter);

		TextView emptyTxt = new TextView(this);
		emptyTxt.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		emptyTxt.setGravity(Gravity.CENTER);
		emptyTxt.setText("暂无数据");
		emptyTxt.setTextSize(20);
		emptyTxt.setVisibility(View.GONE);
		((ViewGroup) listView.getParent()).addView(emptyTxt);
		listView.setEmptyView(emptyTxt);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.backImg:
			finish();
			break;
		case R.id.confirmTxt:
			currentPages = 1;
			dataList.clear();
			getData(currentPages);
			break;
		case R.id.time1Txt:
			TimeSelector timeSelector1 = new TimeSelector(OrderSearchActivity.this, new TimeSelector.ResultHandler() {
				@Override
				public void handle(String time) {
					sdate = time.subSequence(0, 10).toString();
					time1Txt.setText(sdate);
				}
			}, "2000-01-01 00:00", "2020-12-31 00:00");
			timeSelector1.setMode(MODE.YMD);
			timeSelector1.show(Calendar.getInstance().getTime());
			break;
		case R.id.time2Txt:
			TimeSelector timeSelector2 = new TimeSelector(OrderSearchActivity.this, new TimeSelector.ResultHandler() {
				@Override
				public void handle(String time) {
					edate = time.subSequence(0, 10).toString();
					time2Txt.setText(edate);
				}
			}, "2000-01-01 00:00", "2020-12-31 00:00");
			timeSelector2.setMode(MODE.YMD);
			timeSelector2.show(Calendar.getInstance().getTime());
			break;
		case R.id.statusTxt:
			showStatusPopupWindow();
			break;
		default:
			break;
		}
	}

	protected void showStatusPopupWindow() {
		if (statusPopWin != null && statusPopWin.isShowing()) {
			return;
		}
		DisplayMetrics dm = getResources().getDisplayMetrics();
		LayoutInflater inflater = getLayoutInflater();
		View view = inflater.inflate(R.layout.pop_status, null);
		// 实例化并且设置PopupWindow显示的视图
		statusPopWin = new PopupWindow(view, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 120, dm), LayoutParams.WRAP_CONTENT);
		statusList = (ListView) view.findViewById(R.id.listView);
		statusList.setAdapter(new StatusAdapter(this));
		statusList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				ordstatus = statusValueList.get(arg2);
				statusTxt.setText(statusNameList.get(arg2).replaceAll("-", ""));
				statusPopWin.dismiss();
			}
		});
		// 最多显示5行
		setListHeight(statusList, 5);
		statusPopWin.setFocusable(true);
		statusPopWin.setOutsideTouchable(true);
		statusPopWin.setBackgroundDrawable(new BitmapDrawable());
		view.measure(0, 0);
		statusPopWin.showAsDropDown(statusTxt, 0, 0);
	}

	protected void setListHeight(ListView listview, int num) {
		int totalHeight = 0;
		for (int i = 0; i < listview.getAdapter().getCount(); i++) {
			View listItem = listview.getAdapter().getView(i, null, listview);
			listItem.measure(0, 0);
			totalHeight += listItem.getMeasuredHeight();
			if (i == (num - 1)) {
				break;
			}
		}
		ViewGroup.LayoutParams params = listview.getLayoutParams();
		params.height = totalHeight + (listview.getDividerHeight() * (listview.getCount() - 1));
		listview.setLayoutParams(params);
	}

	private void getData(int page) {
		String url = Constants.ADDR_order;
		String cookie = PreferenceHelper.getCookie();
		Map<String, String> param = new HashMap<String, String>();
		param.put("busType", busType);
		param.put("pageSize", "" + pageSize);
		param.put("currentPages", "" + page);
		param.put("sdate", sdate);
		param.put("edate", edate);
		if (!"".equals(ordstatus)) {
			param.put("ordstatus", ordstatus);
		}
		PostJsonObjectRequest request = new PostJsonObjectRequest(url, cookie, param, new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				listView.onRefreshComplete();
				try {
					JSONObject REP_BODY = response.optJSONObject("REP_BODY");
					String RSPCOD = REP_BODY.optString("RSPCOD");
					if ("000000".equals(RSPCOD)) {
						JSONArray tranList = REP_BODY.optJSONArray("tranList");
						Gson gson = new Gson();
						List<Order> list = gson.fromJson(tranList.toString(), new TypeToken<List<Order>>() {
						}.getType());
						if (currentPages == 1) {
							dataList.clear();
						}
						dataList.addAll(list);
						adapter.notifyDataSetChanged();
					} else {
						String RSPMSG = REP_BODY.optString("RSPMSG");
						ToastHelper.toast(OrderSearchActivity.this, RSPMSG);
					}
				} catch (Exception e) {
					ToastHelper.toast(OrderSearchActivity.this, "服务器错误");
				}
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError arg0) {
				listView.onRefreshComplete();
				ToastHelper.toast(OrderSearchActivity.this, "无法连接服务器");
			}
		});
		MyVolley.addRequest(request);
	}

	public class MsgAdapter extends BaseAdapter {
		private LayoutInflater mInflater = null;

		private MsgAdapter(Context context) {
			this.mInflater = LayoutInflater.from(context);
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
				convertView = mInflater.inflate(R.layout.listitem_order, null);
				holder.typeTxt = (TextView) convertView.findViewById(R.id.typeTxt);
				holder.statusTxt = (TextView) convertView.findViewById(R.id.statusTxt);
				holder.timeTxt = (TextView) convertView.findViewById(R.id.timeTxt);
				holder.noTxt = (TextView) convertView.findViewById(R.id.noTxt);
				holder.amtTxt = (TextView) convertView.findViewById(R.id.amtTxt);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			Order item = dataList.get(position);
			holder.typeTxt.setText(getType(item.getPAYTYPE()));
			if ("03".equals(busType)) {
				holder.typeTxt.setText("提现T+0");
			}
			holder.statusTxt.setText(getStatus(item.getOrdstatus()));
			holder.timeTxt.setText(ToolUtil.formatTime(item.getOrdtime()));
			holder.noTxt.setText(item.getPrdordno());
			holder.amtTxt.setText(ToolUtil.getMoney(item.getOrdamt(), true));
			return convertView;
		}

		private String getType(String code) {
			String type = "";
			if ("03".equals(code)) {
				type = "快捷支付";
			} else if ("04".equals(code)) {
				type = " 微信支付";
			} else if ("05".equals(code)) {
				type = "支付宝";
			}
			return type;
		}

		private String getStatus(String code) {
			// 00||06 未处理。01 成功。02 失败。03||04||05||07||08 处理中。
			String status = "";
			if ("00".equals(code) || "06".equals(code)) {
				status = "未处理";
			} else if ("01".equals(code)) {
				status = "成功";
			} else if ("02".equals(code)) {
				status = " 失败";
			} else if ("03".equals(code) || "04".equals(code) || "05".equals(code) || "07".equals(code) || "08".equals(code)) {
				status = " 处理中";
			}
			return status;
		}

		class ViewHolder {
			public TextView typeTxt;
			public TextView statusTxt;
			public TextView timeTxt;
			public TextView noTxt;
			public TextView amtTxt;
		}
	}

	protected class StatusAdapter extends BaseAdapter {
		private LayoutInflater mInflater;

		public StatusAdapter(Context context) {
			this.mInflater = LayoutInflater.from(context);
		}

		@Override
		public int getCount() {
			return statusNameList.size();
		}

		@Override
		public Object getItem(int arg0) {
			return statusNameList.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			return arg0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			convertView = mInflater.inflate(R.layout.listitem_status, null);
			TextView txt = (TextView) convertView.findViewById(R.id.txt);
			txt.setText(statusNameList.get(position));
			return convertView;
		}
	}
}
