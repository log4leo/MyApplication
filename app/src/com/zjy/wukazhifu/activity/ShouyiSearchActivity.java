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
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
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
import com.zjy.wukazhifu.entity.Shouyi;
import com.zjy.wukazhifu.util.Constants;
import com.zjy.wukazhifu.util.MyVolley;
import com.zjy.wukazhifu.util.PostJsonObjectRequest;
import com.zjy.wukazhifu.util.PreferenceHelper;
import com.zjy.wukazhifu.util.ToastHelper;
import com.zjy.wukazhifu.util.ToolUtil;

public class ShouyiSearchActivity extends Activity implements OnClickListener {

	private ImageView backImg;
	private TextView confirmTxt;

	private TextView time1Txt;
	private TextView time2Txt;

	private PullToRefreshListView listView;
	private MsgAdapter adapter;
	private List<Shouyi> dataList = new ArrayList<Shouyi>();

	private int currentPages = 1;
	private int pageSize = 10;

	private String sdate = "";
	private String edate = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_shouyisearch);

		initView();
		// getData(currentPages);
	}

	private void initView() {
		backImg = (ImageView) findViewById(R.id.backImg);
		backImg.setOnClickListener(this);
		confirmTxt = (TextView) findViewById(R.id.confirmTxt);
		confirmTxt.setOnClickListener(this);

		time1Txt = (TextView) findViewById(R.id.time1Txt);
		time2Txt = (TextView) findViewById(R.id.time2Txt);
		time1Txt.setOnClickListener(this);
		time2Txt.setOnClickListener(this);

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
			TimeSelector timeSelector1 = new TimeSelector(ShouyiSearchActivity.this, new TimeSelector.ResultHandler() {
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
			TimeSelector timeSelector2 = new TimeSelector(ShouyiSearchActivity.this, new TimeSelector.ResultHandler() {
				@Override
				public void handle(String time) {
					edate = time.subSequence(0, 10).toString();
					time2Txt.setText(edate);
				}
			}, "2000-01-01 00:00", "2020-12-31 00:00");
			timeSelector2.setMode(MODE.YMD);
			timeSelector2.show(Calendar.getInstance().getTime());
			break;
		default:
			break;
		}
	}

	private void getData(int page) {
		String url = Constants.ADDR_shouyi;
		String cookie = PreferenceHelper.getCookie();
		Map<String, String> param = new HashMap<String, String>();
		param.put("busType", "01");
		param.put("pageSize", "" + pageSize);
		param.put("currentPages", "" + page);
		param.put("stdate", sdate);
		param.put("endate", edate);
		PostJsonObjectRequest request = new PostJsonObjectRequest(url, cookie, param, new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				listView.onRefreshComplete();
				try {
					JSONObject REP_BODY = response.optJSONObject("REP_BODY");
					String RSPCOD = REP_BODY.optString("RSPCOD");
					if ("000000".equals(RSPCOD)) {
						JSONArray profitInfoList = REP_BODY.optJSONArray("profitInfoList");
						Gson gson = new Gson();
						List<Shouyi> list = gson.fromJson(profitInfoList.toString(), new TypeToken<List<Shouyi>>() {
						}.getType());
						if (currentPages == 1) {
							dataList.clear();
						}
						dataList.addAll(list);
						adapter.notifyDataSetChanged();
					} else {
						String RSPMSG = REP_BODY.optString("RSPMSG");
						ToastHelper.toast(ShouyiSearchActivity.this,RSPMSG);
					}
				} catch (Exception e) {
					ToastHelper.toast(ShouyiSearchActivity.this,"服务器错误");
				}
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError arg0) {
				listView.onRefreshComplete();
				ToastHelper.toast(ShouyiSearchActivity.this,"无法连接服务器");
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
				convertView = mInflater.inflate(R.layout.listitem_shouyi, null);
				holder.levelTxt = (TextView) convertView.findViewById(R.id.levelTxt);
				holder.nameTxt = (TextView) convertView.findViewById(R.id.nameTxt);
				holder.timeTxt = (TextView) convertView.findViewById(R.id.timeTxt);
				holder.amtTxt = (TextView) convertView.findViewById(R.id.amtTxt);
				holder.shouyiTxt = (TextView) convertView.findViewById(R.id.shouyiTxt);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			Shouyi item = dataList.get(position);
			holder.levelTxt.setText(item.getLevelName());
			holder.nameTxt.setText(item.getCustName().replaceAll("(?<=.).", "*"));
			holder.timeTxt.setText(ToolUtil.formatTime(item.getOrdTime()));
			holder.shouyiTxt.setText(ToolUtil.getMoney(item.getSharAmt(), true));
			holder.amtTxt.setText(ToolUtil.getMoney(item.getOrderAmt(), false));
			return convertView;
		}

		class ViewHolder {
			public TextView levelTxt;
			public TextView nameTxt;
			public TextView timeTxt;
			public TextView amtTxt;
			public TextView shouyiTxt;
		}
	}
}
